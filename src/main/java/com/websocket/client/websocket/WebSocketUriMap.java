package com.websocket.client.websocket;

import io.netty.channel.Channel;

import java.net.URI;

/**
 * @author yang
 *
 */
public interface WebSocketUriMap {
    /**
     * Provides the uri of channel connected
     * @param channel
     * @return URI
     */
    URI getChannelClientUri(Channel channel);

    /**
     * saven the map of channel and uri
     * @param channel
     * @param uri
     */
    void save(Channel channel, URI uri);
}
