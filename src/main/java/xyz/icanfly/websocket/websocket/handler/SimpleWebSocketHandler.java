package xyz.icanfly.websocket.websocket.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import xyz.icanfly.websocket.websocket.status.HandshakeStateEvent;
import xyz.icanfly.websocket.websocket.status.ObjectHolder;

/**
 *
 * @author yang
 */
public class SimpleWebSocketHandler<T> extends SimpleChannelInboundHandler<T> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SimpleWebSocketHandler.class);

    protected void onMessage(ChannelHandlerContext ctx, T msg)throws Exception{}

    protected void onOpen(Channel ctx){};

    protected void onError(ChannelHandlerContext ctx,Throwable cause){};

    protected void onClose(ChannelHandlerContext ctx){};

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
        onMessage(ctx,msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt){
        if (isHandShakerComplete(evt)) {
            if (ObjectHolder.tryInit()) {
                onOpen(ctx.channel());
                ObjectHolder.setCurrentChannel(ctx.channel());
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        onClose(ctx);
        switchOrRetry(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        onError(ctx,cause);
        switchOrRetry(ctx);
    }

    private void switchOrRetry(ChannelHandlerContext ctx) {
        if (ObjectHolder.get().isPresent()) {
            logger.info("switch to another channel");
            Channel channel = ObjectHolder.get().get();
            onOpen(channel);
        } else {
            logger.info("can not find suitable data sources for check out,try to reconnect");
            ObjectHolder.reSet();
            ObjectHolder.getWebsocketClient().connection();
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
