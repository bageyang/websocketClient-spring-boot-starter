package xyz.icanfly.websocket.websocket.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import xyz.icanfly.websocket.config.UrlMark;
import xyz.icanfly.websocket.websocket.attribute.Attribute;
import xyz.icanfly.websocket.websocket.status.ChannelState;
import xyz.icanfly.websocket.websocket.status.HandshakeStateEvent;
import xyz.icanfly.websocket.websocket.status.ClientHolder;

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
    abstract void onMessage(ChannelHandlerContext ctx, T msg);

    /**
     * Calls onOpen after channel handshake success!
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     */
    abstract void onOpen(ChannelHandlerContext ctx);

    /**
     * Calls onError to handled  after an error occurred
     *
     * @param ctx   the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *              belongs to
     * @param cause Throwable
     */
    abstract void onError(ChannelHandlerContext ctx, Throwable cause);

    /**
     * Calls onError to handled  after channel closed
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     */
    abstract void onClose(ChannelHandlerContext ctx);

    protected static UrlMark getChannelMark(Channel channel) {
        return channel.attr(Attribute.WEBSOCKET_URI).get();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) {
        onMessage(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (isHandShakerComplete(evt)) {
            LOGGER.info("the connection of " + getChannelMark(ctx.channel()).getUri() + " has already connected !");
            success(ctx.channel());
            onOpen(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        onClose(ctx);
        LOGGER.error("websocket closed, channelType:{}", getChannelMark(ctx.channel()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("websocket happened an error:{}", getChannelMark(ctx.channel()));
        LOGGER.error("cause:{}", cause);
        onError(ctx, cause);
        fail(ctx);
        switchOrRetry(ctx);
    }

    private void success(Channel channel) {
        UrlMark channelMark = getChannelMark(channel);
        channelMark.setStatus(ChannelState.ALIVE);
    }

    protected void fail(ChannelHandlerContext ctx) {
        UrlMark channelMark = getChannelMark(ctx.channel());
        channelMark.setStatus(ChannelState.INVALID);
    }

    private synchronized void switchOrRetry(ChannelHandlerContext ctx) {
        UrlMark channelMark = getChannelMark(ctx.channel());
        LOGGER.warn("try to reconnect,channelType:{},channel address:{}", channelMark.getType(), channelMark.getUri());
        if (channelMark.getStatus() == ChannelState.INVALID) {
            ClientHolder.getWebsocketClient().connection(channelMark);
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
