package xyz.icanfly.websocket.websocket.status;

import xyz.icanfly.websocket.websocket.NettyWebSocketConnector;

/**
 * @author yang
 */
public class ClientHolder {
    private static NettyWebSocketConnector WEBSOCKET_CLIENT = null;

    public static void setWebsocketClient(NettyWebSocketConnector client) {
        WEBSOCKET_CLIENT = client;
    }

    public static NettyWebSocketConnector getWebsocketClient() {
        return WEBSOCKET_CLIENT;
    }
}
