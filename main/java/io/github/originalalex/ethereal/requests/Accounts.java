package io.github.originalalex.ethereal.requests;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonObject;
import io.github.originalalex.ethereal.database.DatabaseCommunicator;
import io.github.originalalex.ethereal.math.SHA256;
import io.github.originalalex.ethereal.tokens.TokenHandling;
import io.github.originalalex.ethereal.utils.JSONUtils;
import io.github.originalalex.ethereal.utils.QueryUtils;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static io.github.originalalex.ethereal.utils.QueryUtils.getQuery;

public class Accounts {

    private static DatabaseCommunicator db;

    private static final String PASSWORD_SECRET = "DJKHFGQ31sdfgsdB48T DEFHG";

    public static void setDatabse(DatabaseCommunicator database) {
        db = database;
    }

    /**
     * Server returns 2 things:
     * 0: Somebody is already registered with that username
     * A token: Successful registration
     */
    public static void createAccount(HttpServerExchange exchange) {
        QueryUtils.enableCORS(exchange);

        String dataStr = QueryUtils.extrapilateBody(exchange);
        JsonObject data = JSONUtils.getJson(dataStr);
        String username = JSONUtils.getField(data, "username");
        String password = JSONUtils.getField(data, "password");

        Sender sender = exchange.getResponseSender();
        if (db.accountExists(username)) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            sender.send("0");
            return;
        }
        String hash = SHA256.hash(password, PASSWORD_SECRET);
        db.createAccount(username, hash);

        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, String> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("admin", "false");

        String token = TokenHandling.generateToken(header, payload);
        exchange.setStatusCode(StatusCodes.OK);
        exchange.getResponseHeaders().put(Headers.SET_COOKIE, "token=" + token + "; Domain=localhost; Path=/");
    }

    /**
     * Server returns 2 things:
     * 0: username or password is incorrect
     * A token: Successful registration
     *
     */
    public static void signIn(HttpServerExchange exchange) {
        QueryUtils.enableCORS(exchange);
        Sender sender = exchange.getResponseSender();

        String dataStr = QueryUtils.extrapilateBody(exchange);
        JsonObject data = JSONUtils.getJson(dataStr);
        String user = JSONUtils.getField(data, "username");
        String pass = JSONUtils.getField(data, "password");

        String passwordHash = SHA256.hash(pass, PASSWORD_SECRET);
        if (!db.validCredentials(user, passwordHash)) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            sender.send("0");
            return;
        }

        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, String> payload = new HashMap<>();
        payload.put("username", user);
        payload.put("role", "user");

        String token = TokenHandling.generateToken(header, payload);
        exchange.setStatusCode(StatusCodes.OK);
        exchange.getResponseHeaders().put(Headers.SET_COOKIE, "token=" + token + "; Domain=localhost; Path=/");
    }

    /**
     * Server returns 2 things:
     * -2: Invalid token?
     * -1: invalid name? how?
     *  balance
     */
    public static void fetchInfo(HttpServerExchange exchange) {
        QueryUtils.enableCORS(exchange);
        Sender sender = exchange.getResponseSender();

        String token = QueryUtils.getTokenFromCookies(exchange);

        if (token == null) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            sender.send("-3");
            return;
        }

        DecodedJWT jwt = TokenHandling.verifyToken(token);

        if (jwt == null) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            sender.send("-2");
            return;
        }

        String username = jwt.getClaim("username").asString();

        double balance = db.fetchBalance(username);
        if (balance == -1) { // invalid username or something
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            sender.send("-1");
            return;
        }
        exchange.setStatusCode(StatusCodes.OK);
        sender.send("{\"username\":\"" + username + "\",\"balance\":\"" + balance + "\"}");
    }
}
