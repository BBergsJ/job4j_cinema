package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;

public interface Store {
//    void saveAccount(Account account);
//    void saveTicket(Ticket ticket);

    Optional<Account> save(Account account);
    Optional<Ticket> save(Ticket ticket);

    Collection<Account> findAllAccounts();
    Collection<Ticket> findAllTickets();

    Optional<Account> findAccountById(int id);
    Optional<Account> findAccountByEmail(String email);
}
