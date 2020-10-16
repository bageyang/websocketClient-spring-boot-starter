package xyz.icanfly.websocket.config;


import xyz.icanfly.websocket.websocket.status.ChannelState;

import java.net.URI;

/**
 * @author : yangbing
 * @version : v1.0
 * @description an url with mark
 * @date : 2020/8/24 17:08
 */
public class UrlMark {
    private String type;
    private URI uri;
    private ChannelState status;

    public UrlMark() {
        super();
    }

    public UrlMark(String type, URI uri) {
        this.type = type;
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public ChannelState getStatus() {
        return status;
    }

    public void setStatus(ChannelState status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UrlMark{" +
                "type='" + type + '\'' +
                ", uri=" + uri +
                '}';
    }
}
