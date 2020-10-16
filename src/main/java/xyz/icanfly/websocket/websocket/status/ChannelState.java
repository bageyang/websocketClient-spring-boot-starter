package xyz.icanfly.websocket.websocket.status;

/**
 * @author : yangbing
 * @version : v1.4
 * @description the status of channel
 * @date : 2020/10/15 13:47
 */
public enum ChannelState {
    /**
     * the channel still alive
     */
    ALIVE,
    /**
     * the channel is completing,this status is between ALIVE and INVALID
     */
    COMPLETING,
    /**
     * the channel is invalid
     */
    INVALID
}
