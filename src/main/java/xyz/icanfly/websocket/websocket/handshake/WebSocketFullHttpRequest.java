package xyz.icanfly.websocket.websocket.handshake;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author yang
 */
public class WebSocketFullHttpRequest {
    private FullHttpRequest request;
    private String expectedResponseString;

    public WebSocketFullHttpRequest(FullHttpRequest request, String expectedResponseString) {
        this.request = request;
        this.expectedResponseString = expectedResponseString;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public String getExpectedResponseString() {
        return expectedResponseString;
    }

    public void setExpectedResponseString(String expectedResponseString) {
        this.expectedResponseString = expectedResponseString;
    }
}
