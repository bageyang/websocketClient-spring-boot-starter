package xyz.icanfly.websocket.websocket.handshake;

import io.netty.channel.Channel;

import java.net.URI;

/**
 * create a mapping with uri and channel
 * @author yang
 */
public interface WebSocketUriMap {
    /**
     * Provides the uri of channel connected
     * @param channel the channel of connection
     * @return URI
     */
    URI getChannelClientUri(Channel channel);

    /**
     *
     * @param channel channel of connection
     * @param uri save the map of channel and uri
     */
    void save(Channel channel, URI uri);
}
