package io.github.originalalex.ethereal.server;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;

public class SimpleServer {

    private static final int DEFAULT_PORT = 81;
    private static final String DEFAULT_HOST = "0.0.0.0";

    private Undertow.Builder builder;

    private SimpleServer(Undertow.Builder builder) {
        this.builder = builder;
    }


    public void start() {
        Undertow undertow = builder.build();
        undertow.start();
        System.out.println("RESTful API started on 127.0.0.1:" + DEFAULT_PORT);
    }

    public static SimpleServer establishServer(HttpHandler handler) {
        Undertow.Builder builder = Undertow.builder()
                .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true)
                .addHttpListener(DEFAULT_PORT, DEFAULT_HOST, handler);
        return new SimpleServer(builder);
    }

}
