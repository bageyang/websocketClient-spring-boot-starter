package xyz.icanfly.websocket.websocket.status;

import io.netty.channel.Channel;
import xyz.icanfly.websocket.websocket.NettyWebSocketClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author yang
 */
public class ObjectHolder {
    private static volatile Boolean market = false;
    private static Channel CURRENT_CHANNEL = null;
    private static List<Channel> CHANNEL_COLLECTION = new ArrayList<>();
    private static NettyWebSocketClient WEBSOCKET_CLIENT = null;

    public static void add(Channel channel) {
        CHANNEL_COLLECTION.add(channel);
    }

    public static Optional<Channel> get() {
        return CHANNEL_COLLECTION.stream().filter(ObjectHolder::isSuitable).findAny();
    }

    public static void setCurrentChannel(Channel channel){
        CURRENT_CHANNEL=channel;
    }

    static boolean isSuitable(Channel channel){
        return channel.isActive()&&(!channel.equals(CURRENT_CHANNEL));
    }

    public static synchronized Boolean tryInit() {
        return !market ? market = true : false;
    }

    public static void reSet() {
        CHANNEL_COLLECTION.clear();
        market = false;
    }

    public static void setWebsocketClient(NettyWebSocketClient client){
        WEBSOCKET_CLIENT = client;
    }

    public static NettyWebSocketClient getWebsocketClient() {
        return WEBSOCKET_CLIENT;
    }
}
