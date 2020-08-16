package xyz.icanfly.websocket.websocket.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import xyz.icanfly.websocket.websocket.status.HandshakeStateEvent;
import xyz.icanfly.websocket.websocket.status.ObjectManager;

/**
 *
 * @author yang
 */
public class SimpleWebSocketHandler<T> extends SimpleChannelInboundHandler<T> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SimpleWebSocketHandler.class);

    /**
     * 单位时间
     */
    private static final Long ALLOW_INACTIVE_TIME = 5*1000L;
    /**
     * 单位时间允许切换的次数
     */
    private static final Integer ALLOW_SWITCH_NUM = 5;
    /**
     * 上次切换时间
     */
    private static Long LAST_CHANGE_TIME = System.currentTimeMillis();
    /**
     * 切换次数
     */
    private static Long CHANGE_TIME = 0L;

    protected void onMessage(ChannelHandlerContext ctx, T msg)throws Exception{}

    protected void onOpen(Channel ctx){};

    protected void onError(ChannelHandlerContext ctx,Throwable cause){};

    protected void onClose(ChannelHandlerContext ctx){};

    protected static Channel getCurrentChannel(){
        return ObjectManager.getCurrentChannel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
        onMessage(ctx,msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt){
        if (isHandShakerComplete(evt)) {
            if (ObjectManager.tryInit()) {
                onOpen(ctx.channel());
                ObjectManager.setCurrentChannel(ctx.channel());
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

    protected boolean isHandShakerComplete(Object evt) {
        return isHandshakeStateEvent(evt) && isStateEventComplete(evt);
    }

    protected boolean isHandshakeStateEvent(Object evt) {
        return (evt instanceof HandshakeStateEvent);
    }

    protected boolean isStateEventComplete(Object evt) {
        return evt == HandshakeStateEvent.SUCCESS;
    }

    private void switchOrRetry(ChannelHandlerContext ctx) {
        if (ObjectManager.getAnySuitAbleChannel().isPresent()) {
            logger.warn("switch to another channel");
            Channel channel = ObjectManager.getAnySuitAbleChannel().get();
            onOpen(channel);
        } else {
            logger.warn("can not find suitable data sources for check out,try to reconnect");
            ObjectManager.reSetAndClearChannel();
            ObjectManager.getWebsocketConnector().connection();
        }
        checkStatus();
    }

    private void checkStatus() {
        CHANGE_TIME++;
        long now = System.currentTimeMillis();
        long s = now - LAST_CHANGE_TIME;
        if(s<ALLOW_INACTIVE_TIME){
            if(CHANGE_TIME>ALLOW_SWITCH_NUM){
                LAST_CHANGE_TIME=now;
                CHANGE_TIME=0L;
                logger.error("websocket connection has switched:"+CHANGE_TIME+" times,in "+ALLOW_INACTIVE_TIME
                        +" millisecond,please check the websocket address is stable");
            }
        }else {
            LAST_CHANGE_TIME=now;
        }
    }

}
