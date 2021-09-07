package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

public class PsqlStore implements Store {

    private final BasicDataSource pool = new BasicDataSource();
    private static final Logger LOGGER = LoggerFactory.getLogger(PsqlStore.class.getName());

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(new FileReader("db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            LOGGER.error("Exception occurred: " + e.getMessage(), e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred: " + e.getMessage(), e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl("jdbc.url");
        pool.setUsername("jdbc.username");
        pool.setPassword("jdbc.password");
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Optional<Account> save(Account account) {
        Optional<Account> rsl = Optional.empty();
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("INSERT INTO account(username, email, phone)"
                + " VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    account.setId(rs.getInt(1));
                    rsl = Optional.of(account);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred: " + e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        Optional<Ticket> rsl = Optional.empty();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO ticket(session_id, row, cell, account_id)"
                     + " VALUES (?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, ticket.getSessionId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getAccount().getId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setId(rs.getInt(1));
                    rsl = Optional.of(ticket);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred: " + e.getMessage(), e);
        }
        return rsl;
    }

//    @Override
//    public void saveAccount(Account account) {
//        if (account.getId() == 0) {
//            create(account);
//        } else {
//            update(account);
//        }
//    }
//
//    public void saveTicket(Ticket ticket) {
//        if (ticket.getId() == 0) {
//            create(ticket);
//        } else {
//            update(ticket);
//        }
//    }
//
//    private Account create(Account account) {
//        try (Connection cn = pool.getConnection();
//             PreparedStatement ps =  cn.prepareStatement("INSERT INTO account(username, email, phone) VALUES (?, ?, ?)",
//                     PreparedStatement.RETURN_GENERATED_KEYS)
//        ) {
//            ps.setString(1, account.getUsername());
//            ps.setString(2, account.getEmail());
//            ps.setString(3, account.getPhone());
//            ps.executeUpdate();
//            try (ResultSet id = ps.getGeneratedKeys()) {
//                if (id.next()) {
//                    account.setId(id.getInt(1));
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error("Exception occurred: " + e.getMessage(), e);
//        }
//        return account;
//    }
//
//    private Ticket create(Ticket ticket) {
//        try (Connection cn = pool.getConnection();
//             PreparedStatement ps =  cn.prepareStatement("INSERT INTO ticket(session_id, row, cell, account_id) VALUES (?, ?, ?, ?)",
//                     PreparedStatement.RETURN_GENERATED_KEYS)
//        ) {
//            ps.setInt(1, ticket.getSessionId());
//            ps.setInt(2, ticket.getRow());
//            ps.setInt(3, ticket.getCell());
//            ps.setInt(4, ticket.getAccount().getId());
//            ps.execute();
//            try (ResultSet id = ps.getGeneratedKeys()) {
//                if (id.next()) {
//                    ticket.setId(id.getInt(1));
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error("Exception occurred: " + e.getMessage(), e);
//        }
//        return ticket;
//    }
//
//    private void update(Account account) {
//        try (Connection cn = pool.getConnection();
//             PreparedStatement ps = cn.prepareStatement("UPDATE account SET username = ?, email = ?, phone = ? WHERE id = ?")) {
//            ps.setString(1, account.getUsername());
//            ps.setString(2, account.getEmail());
//            ps.setString(3, account.getPhone());
//            ps.setInt(4, account.getId());
//            ps.executeUpdate();
//        } catch (Exception e) {
//            LOGGER.error("Exception occurred: " + e.getMessage(), e);
//        }
//    }
//
//    private void update(Ticket ticket) {
//        try (Connection cn = pool.getConnection();
//             PreparedStatement ps = cn.prepareStatement("UPDATE ticket SET session_id = ?, row = ?, cell = ?, account_id = ? WHERE id = ?")) {
//            ps.setInt(1, ticket.getSessionId());
//            ps.setInt(2, ticket.getRow());
//            ps.setInt(3, ticket.getCell());
//            ps.setInt(4, ticket.getAccount().getId());
//            ps.setInt(5, ticket.getId());
//            ps.executeUpdate();
//        } catch (Exception e) {
//            LOGGER.error("Exception occurred: " + e.getMessage(), e);
//        }
//    }

    @Override
    public Collection<Account> findAllAccounts() {
        Collection<Account> accounts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM account")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(new Account(rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("phone")));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred: " + e.getMessage(), e);
        }
        return accounts;
    }

    @Override
    public Collection<Ticket> findAllTickets() {
        Collection<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("SELECT * FROM ticket")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(new Ticket(rs.getInt("id"),
                            rs.getInt("session_id"),
                            rs.getInt("row"),
                            rs.getInt("cell"),
                            findAccountById(rs.getInt("account_id"))
                                    .orElse(null)));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred: " + e.getMessage(), e);
        }
        return tickets;
    }

    @Override
    public Optional<Account> findAccountById(int id) {
        Optional<Account> account = Optional.empty();
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("SELECT * FROM account WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    account = Optional.of(new Account(rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("phone")));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred: " + e.getMessage(), e);
        }
        return account;
    }

    @Override
    public Optional<Account> findAccountByEmail(String email) {
        Optional<Account> account = Optional.empty();
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("SELECT * FROM account WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    account = Optional.of(new Account(rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("phone")));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred: " + e.getMessage(), e);
        }
        return account;
    }
}