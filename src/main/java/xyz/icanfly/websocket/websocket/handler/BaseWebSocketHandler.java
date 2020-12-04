package xyz.icanfly.websocket.websocket.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import xyz.icanfly.websocket.config.WebSocketResource;
import xyz.icanfly.websocket.websocket.NettyWebSocketConnector;
import xyz.icanfly.websocket.websocket.attribute.Attribute;
import xyz.icanfly.websocket.websocket.status.ChannelState;
import xyz.icanfly.websocket.websocket.status.HandshakeStateEvent;

/**
 * the sampleHandler with channel Event
 *
 * @author yang
 */
public abstract class BaseWebSocketHandler<T> extends SimpleChannelInboundHandler<T> {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(BaseWebSocketHandler.class);

    /**
     * Is called for each message of type T
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     */
    protected abstract void onMessage(ChannelHandlerContext ctx, T msg);

    /**
     * Calls onOpen after channel handshake success!
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     */
    protected abstract void onOpen(ChannelHandlerContext ctx);

    /**
     * Calls onError to handled  after an error occurred
     *
     * @param ctx   the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *              belongs to
     * @param cause Throwable
     */
    void onError(ChannelHandlerContext ctx, Throwable cause) {}

    /**
     * Calls onError to handled  after channel closed
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     */
    void onClose(ChannelHandlerContext ctx) {}

    protected static WebSocketResource getChannelResource(Channel channel) {
        return channel.attr(Attribute.WEBSOCKET_URI).get();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) {
        try {
            onMessage(ctx, msg);
        } catch (Exception e) {
            LOGGER.error("the channel:{} happen while handle message", getChannelResource(ctx.channel()), e);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (isHandShakerComplete(evt)) {
            LOGGER.info("the connection of {} has already connected !", getChannelResource(ctx.channel()).getUri());
            success(ctx.channel());
            onOpen(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        onClose(ctx);
        closeStatus(ctx);
        LOGGER.error("websocket closed, channelType:{}", getChannelResource(ctx.channel()));
        switchOrRetry(ctx);
    }

    private void closeStatus(ChannelHandlerContext ctx){
        WebSocketResource webSocketResource = getChannelResource(ctx.channel());
        webSocketResource.setStatus(ChannelState.INVALID);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("channel:{} happened an error", getChannelResource(ctx.channel()), cause);
        onError(ctx, cause);
    }

    private void success(Channel channel) {
        WebSocketResource channelMark = getChannelResource(channel);
        channelMark.setStatus(ChannelState.ALIVE);
    }

    private synchronized void switchOrRetry(ChannelHandlerContext ctx) {
        WebSocketResource webSocketResource = getChannelResource(ctx.channel());
        if (webSocketResource.getStatus() == ChannelState.INVALID) {
            LOGGER.error("try to reconnect,channelType:{},channel address:{}", webSocketResource.getType(), webSocketResource.getUri());
            NettyWebSocketConnector.connection(webSocketResource);
        }
    }

    protected boolean isHandShakerComplete(Object evt) {
        return isHandshakeStateEvent(evt) && isStateEventComplete(evt);
    }

    protected boolean isHandshakeStateEvent(Object evt) {
        return (evt instanceof HandshakeStateEvent);
    }

    protected boolean isStateEventComplete(Object evt) {
        return evt == HandshakeStateEvent.SUCCESS;
    }

}
