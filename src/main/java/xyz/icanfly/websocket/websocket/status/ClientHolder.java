package xyz.icanfly.websocket.websocket.status;

import xyz.icanfly.websocket.websocket.NettyWebSocketClient;

/**
 * @author yang
 */
public class ClientHolder {
    private static NettyWebSocketClient WEBSOCKET_CLIENT = null;

    public static void setWebsocketClient(NettyWebSocketClient client) {
        WEBSOCKET_CLIENT = client;
    }

    public static NettyWebSocketClient getWebsocketClient() {
        return WEBSOCKET_CLIENT;
    }
}
