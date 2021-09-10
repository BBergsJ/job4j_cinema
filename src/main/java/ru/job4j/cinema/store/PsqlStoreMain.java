package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;

public class PsqlStoreMain {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
        Account account = new Account("username", "email", "phone");
        store.save(account);
        Ticket ticket = new Ticket(1, 2, 3, account);
        store.save(ticket);
        for (Account account1 : store.findAllAccounts()) {
            System.out.println(account1.getUsername());
        }
    }
}
