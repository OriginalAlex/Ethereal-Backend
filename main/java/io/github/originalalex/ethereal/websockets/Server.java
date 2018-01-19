package io.github.originalalex.ethereal.websockets;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.originalalex.ethereal.tokens.TokenHandling;
import io.github.originalalex.ethereal.utils.QueryUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class Server extends WebSocketServer {

    private static final int PORT = 31416;

    private Server(InetSocketAddress address) {
        super(address);
        this.start();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String tokenStr = QueryUtils.getTokenFromCookieString(handshake.getFieldValue("Cookie"));
        DecodedJWT token = TokenHandling.verifyToken(tokenStr);
        if (token == null) {
            conn.close();
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occured on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("Started WebSocket on 127.0.0.1:" + PORT);
    }

    public static void begin() {
        new Server(new InetSocketAddress(PORT));
    }

}
