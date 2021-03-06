package net.apexes.wsonrpc.client.support.websocket;

public class WebSocketException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(String message, Throwable t) {
        super(message, t);
    }
}
