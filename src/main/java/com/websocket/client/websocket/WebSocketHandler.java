package com.websocket.client.websocket;

import io.netty.channel.ChannelHandlerContext;

/**
 * create a interface to accpet BinaryWebSocketFrame and do some when websokcet Upgrade successful
 *  it should has two method
 *  1: onMessage(ChannelHandlerContext ctx, byte[] msg) or onMessage(Channel channel, byte[] msg)
 *  2: onOpen(ChannelHandlerContext ctx)
 *  3. onClose(ChannelHandlerContext ctx,Exception e) or onClose(Channel channel,Exception e)
 *  4. onError(ChannelHandlerContext ctx,Exception e) or onError(Channel channel,Exception e)
 * @author yang
 */
public interface WebSocketHandler {
    void onMessage(ChannelHandlerContext ctx, byte[] msg);

    void onOpen(ChannelHandlerContext ctx);

    void onClose(ChannelHandlerContext ctx, Exception e);

    void onError(ChannelHandlerContext ctx, Exception e);
}
