package ru.job4j.cinema.servlet;

import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.PsqlStore;
import ru.job4j.cinema.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class HallServlet extends HttpServlet {

    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        OutputStream output = resp.getOutputStream();
        String json = GSON.toJson(PsqlStore.instOf().findAllTickets());
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JSONObject jsonReq = new JSONObject(req.getParameterNames().asIterator().next());

        String username = jsonReq.getString("usernames");
        String email = jsonReq.getString("email");
        String phone = jsonReq.getString("phone");
        String sessionId = jsonReq.getString("sessionId");
        String row = jsonReq.getString("row");
        String cell = jsonReq.getString("cell");

        Store store = PsqlStore.instOf();
        Optional<Account> account = store.findAccountByEmail(email);
        if (account.isEmpty()) {
            store.save(new Account(username, email, phone));
        }

        String msg = "Место успешно оплачено!";

        Optional<Ticket> ticket = store.save(new Ticket(
                Integer.parseInt(sessionId),
                Integer.parseInt(row),
                Integer.parseInt(cell),
                account.get()));
        if (ticket.isEmpty()) {
            msg = "К сожалению, место не может быть куплено.";
        }

        JSONObject jsonResp = new JSONObject();
        jsonResp.put("message", msg);
        PrintWriter writer = resp.getWriter();
        writer.print(jsonResp);
        writer.flush();
//        OutputStream output = resp.getOutputStream();
//        output.write(GSON.toJson(ticket.get()).getBytes(StandardCharsets.UTF_8));
//        output.flush();
//        output.close();
    }
}