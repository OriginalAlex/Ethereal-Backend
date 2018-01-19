package io.github.originalalex.ethereal.utils;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import org.xnio.Pooled;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Deque;
import java.util.Map;

public class QueryUtils {

    public static String getQuery(String param, Map<String, Deque<String>> params) {
        Deque<String> value = params.get(param);
        if (value == null) return null;
        return value.peekFirst();
    }

    public static boolean isValid(String str) {
        return str != null && !str.isEmpty();
    }

    public static void enableCORS(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "http://localhost");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "GET, PUT, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Credentials"), "true");
    }

    public static String getTokenFromCookies(HttpServerExchange exchange) {
        for(Map.Entry<String, Cookie> entry : exchange.getRequestCookies().entrySet()) {
            if (entry.getKey().equalsIgnoreCase("token")) {
                return entry.getValue().getValue();
            }
        }
        return null;
    }

    public static String getTokenFromCookieString(String string) {
        String[] cookies = string.split(";");
        for (String cookie : cookies) {
            cookie = cookie.trim();
            if (cookie.startsWith("token=")) {
                return cookie.replace("token=", "");
            }
        }
        return null;
    }

    public static String extrapilateBody(HttpServerExchange exchange) {
        Pooled<ByteBuffer> pooledByteBuffer = exchange.getConnection().getBufferPool().allocate();
        ByteBuffer byteBuffer = pooledByteBuffer.getResource();

        int limit = byteBuffer.limit();
        byteBuffer.clear();
        try {
            exchange.getRequestChannel().read(byteBuffer);
        } catch(IOException e) {
            e.printStackTrace();
        }
        int pos = byteBuffer.position();
        byteBuffer.rewind();
        byte[] bytes = new byte[pos];
        byteBuffer.get(bytes);
        return new String(bytes, Charset.forName("UTF-8") );
    }
}
