package xyz.icanfly.websocket.websocket.status;

import io.netty.channel.Channel;
import xyz.icanfly.websocket.websocket.WebSocketConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author yang
 */
public class ObjectManager {
    private static volatile Boolean market = false;
    private static Channel CURRENT_CHANNEL = null;
    private static List<Channel> CHANNEL_COLLECTION = new ArrayList<>();
    private static WebSocketConnector WEBSOCKET_CONNECTOR = null;

    public static void add(Channel channel) {
        CHANNEL_COLLECTION.add(channel);
    }

    public static Optional<Channel> getAnySuitAbleChannel() {
        return CHANNEL_COLLECTION.stream().filter(ObjectManager::isSuitable).findAny();
    }

    private static boolean isSuitable(Channel channel) {
        return channel.isActive() && (!channel.equals(CURRENT_CHANNEL));
    }

    public static void setCurrentChannel(Channel channel) {
        CURRENT_CHANNEL = channel;
    }

    public static Channel getCurrentChannel() {
        return CURRENT_CHANNEL;
    }

    public static synchronized Boolean tryInit() {
        return !market ? market = true : false;
    }

    public static void reSetAndClearChannel() {
        CHANNEL_COLLECTION.clear();
        CURRENT_CHANNEL = null;
        market = false;
    }

    public static void setWebSocketConnector(WebSocketConnector client) {
        WEBSOCKET_CONNECTOR = client;
    }

    public static WebSocketConnector getWebsocketConnector() {
        return WEBSOCKET_CONNECTOR;
    }
}
