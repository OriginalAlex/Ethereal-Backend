package io.github.originalalex.ethereal.backend;

import io.undertow.server.HttpServerExchange;

public class Parameters {

    public static String queryParameter(HttpServerExchange exchange, String name) {
        String val = exchange.getQueryParameters().get(name).getFirst();
        return val;
    }

}

/*

I need an API to do the following tasks:
- Calculate value of next role: hash(server secret + client hash)
- Do CRUD database operations for user balances and storing games
- and more later I guess

 */
