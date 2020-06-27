package xyz.icanfly.websocket.websocket.status;

/**
 * @author yang
 */

public enum HandshakeStateEvent {
    /**
     * The Handshake was timed out
     */
    TIMEOUT,

    /**
     * The Handshake was started
     */
    ISSUED,

    /**
     * The Handshake was complete successful and so the channel was upgraded to websocket
     */
    SUCCESS,

}
