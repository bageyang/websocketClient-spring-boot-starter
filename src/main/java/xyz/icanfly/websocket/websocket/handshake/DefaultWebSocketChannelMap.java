package xyz.icanfly.websocket.websocket.handshake;

import io.netty.channel.Channel;
import xyz.icanfly.websocket.websocket.handshake.WebSocketUriMap;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yang
 */
public class DefaultWebSocketChannelMap implements WebSocketUriMap {
    private static ConcurrentHashMap<String, URI> uriMap = new ConcurrentHashMap<>();

    @Override
    public URI getChannelClientUri(Channel channel) {
        String s = channel.id().asLongText();
        return uriMap.get(s);
    }

    @Override
    public void save(Channel channel, URI uri) {
        uriMap.put(channel.id().asLongText(), uri);
    }
}
