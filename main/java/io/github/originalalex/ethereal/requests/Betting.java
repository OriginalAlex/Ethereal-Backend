package io.github.originalalex.ethereal.requests;

import io.github.originalalex.ethereal.database.DatabaseCommunicator;
import io.github.originalalex.ethereal.random.Generator;
import io.github.originalalex.ethereal.random.Manager;
import io.github.originalalex.ethereal.utils.NumberUtils;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Deque;
import java.util.Map;

import static io.github.originalalex.ethereal.utils.QueryUtils.getQuery;
import static io.github.originalalex.ethereal.utils.QueryUtils.isValid;

public class Betting {

    private static DatabaseCommunicator db;
    private static final double houseEdge = 0.01;

    private static Map<String, Integer> nonces;
    /**
     * Returns 5 possible things:
     * 0: the user lost the bet and the money has been deducted from their account
     * 1: the user won the bet and the money has been added to their account
     * 2: the user doesn't exist
     * 3: the bet is larger than the user's bankroll
     * -1: invalid query somehow
     */
    public static void placebet(HttpServerExchange exchange) {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        String user = getQuery("user", params);
        String bet = getQuery("bet", params);
        double betValue = NumberUtils.getValue(bet);
        if (!success(db.fetch(user), exchange, betValue)) return;
        if (!nonces.containsKey(user)) nonces.put(user, 0);
        else nonces.put(user, nonces.get(user)+1);
        String rollunder = getQuery("rollunder", params);
        String client = getQuery("clientkey", params);
        if (!isValid(user) || isValid(rollunder) || isValid(bet) || isValid(client)) {
            exchange.getResponseSender().send("-1");
            return;
        }
        double rollUnderValue = NumberUtils.getValue(rollunder);
        if (betValue <= 0 || rollUnderValue <= 0.05) {
            exchange.getResponseSender().send("-1");
            return;
        }
        double number = Generator.generate(Manager.getServerSecret(), client, nonces.get(user));
        if (number < rollUnderValue) { // they win!
            double chance = ((rollUnderValue-0.1)/100); // chance of them scoring this roll
            double perfectPayout = ((betValue / chance) - betValue);
            double payout = perfectPayout * (1 - houseEdge);
            db.update(user, payout);
            exchange.getResponseSender().send("1");
        } else { // they lose!
            db.update(user, -betValue);
            exchange.getResponseSender().send("0");
        }
    }

    private static boolean success(ResultSet row, HttpServerExchange exchange, double betSize) {
        Sender sender = exchange.getResponseSender();
        try {
            if (!row.next()) { // should only be one row!
                sender.send("2");
                return false;
            }
            double balance = row.getDouble("balance");
            if (balance < betSize) {
                sender.send("3");
                return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
