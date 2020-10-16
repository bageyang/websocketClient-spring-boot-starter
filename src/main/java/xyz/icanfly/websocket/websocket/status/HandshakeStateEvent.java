package xyz.icanfly.websocket.websocket.status;

/**
 * @author yang
 */

public enum HandshakeStateEvent {
    /**
     * The handshake was timed out
     */
    TIMEOUT,

    /**
     * The handshake was started
     */
    ISSUED,

    /**
     * The handshake was complete successful and so the channel was upgraded to websocket
     */
    SUCCESS,

}
