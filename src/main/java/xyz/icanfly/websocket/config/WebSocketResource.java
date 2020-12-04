package xyz.icanfly.websocket.config;


import xyz.icanfly.websocket.websocket.status.ChannelState;

import java.net.URI;
import java.util.Objects;

/**
 * @author : yangbing
 * @version : v1.0
 * @description an url with mark
 * @date : 2020/8/24 17:08
 */
public class WebSocketResource {
    private String type;
    private URI uri;
    private ChannelState status;
    private Boolean overSsl;

    public WebSocketResource() {
        super();
    }

    public WebSocketResource(String type, URI uri, Boolean overSsl) {
        this.type = type;
        this.uri = uri;
        this.overSsl = overSsl;
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

    public boolean isOverSsl(){
        return overSsl;
    }

    @Override
    public String toString() {
        return "WebSocketResource{" +
                "type='" + type + '\'' +
                ", uri=" + uri +
                ", overSsl=" + overSsl +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketResource webSocketResource = (WebSocketResource) o;
        return Objects.equals(type, webSocketResource.type) &&
                Objects.equals(uri, webSocketResource.uri) &&
                Objects.equals(overSsl, webSocketResource.overSsl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, uri, overSsl);
    }
}
