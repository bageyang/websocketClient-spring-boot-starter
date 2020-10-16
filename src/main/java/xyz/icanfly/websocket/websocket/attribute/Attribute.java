package xyz.icanfly.websocket.websocket.attribute;

import io.netty.util.AttributeKey;
import jdk.internal.loader.FileURLMapper;
import xyz.icanfly.websocket.config.UrlMark;

import java.net.URI;

/**
 * save the attribute of channel also used
 * @author yang
 */
public class Attribute {
    public static final AttributeKey<UrlMark> WEBSOCKET_URI=AttributeKey.valueOf("WEBSOCKET_URI");

}
