package io.github.originalalex.ethereal;

import io.github.originalalex.ethereal.requests.Accounts;
import io.github.originalalex.ethereal.requests.Betting;
import io.github.originalalex.ethereal.database.DatabaseCommunicator;
import io.github.originalalex.ethereal.random.Manager;
import io.github.originalalex.ethereal.server.SimpleServer;
import io.github.originalalex.ethereal.websockets.Server;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;

public class Main {

    private static DatabaseCommunicator db;
    private static final HttpHandler ROOTS;

    static {
        ROOTS =  Handlers.routing()
                .get("/api/getInfo", Accounts::fetchInfo)
                .get("/api/database/placebet", Betting::placebet)
                .post("/api/users/createaccount", Accounts::createAccount)
                .post("/api/users/signin", Accounts::signIn)
                .post("/api/database/wipedb", e -> db.wipedb(e))
                .get("/api/users/fetchInformation", Accounts::fetchInfo);
    }

    private static void beginDatabase() {
        DatabaseCommunicator communicator = DatabaseCommunicator.access("localhost", "3300", "root", "root");
        db = communicator;
        Accounts.setDatabse(communicator);
    }

    private static void beginRESTfulAPI() {
        SimpleServer server = SimpleServer.establishServer(ROOTS);
        server.start();
    }

    public static void main(String[] args) {
        //Manager.begin();
        beginRESTfulAPI();
        beginDatabase();
        Server.begin();
    }
}
