package xyz.icanfly.websocket.websocket.attribute;

import io.netty.util.AttributeKey;
import xyz.icanfly.websocket.config.WebSocketResource;

/**
 * save the attribute of channel also used
 * @author yang
 */
public class Attribute {
    public static final AttributeKey<WebSocketResource> WEBSOCKET_URI=AttributeKey.valueOf("WEBSOCKET_URI");

}
