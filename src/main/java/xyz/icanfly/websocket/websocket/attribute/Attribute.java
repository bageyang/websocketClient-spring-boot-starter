package xyz.icanfly.websocket.websocket.attribute;

import io.netty.util.AttributeKey;

import java.net.URI;

/**
 * save the attribute of channel also used
 * @author yang
 */
public class Attribute {
    public static final AttributeKey<URI> WEBSOCKET_URI=AttributeKey.valueOf("WEBSOCKET_URI");
}
