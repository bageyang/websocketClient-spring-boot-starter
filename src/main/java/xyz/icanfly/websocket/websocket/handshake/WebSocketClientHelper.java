package xyz.icanfly.websocket.websocket.handshake;

import xyz.icanfly.websocket.config.UrlMark;
import xyz.icanfly.websocket.websocket.attribute.Attribute;
import xyz.icanfly.websocket.websocket.status.HandshakeStateEvent;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.ScheduledFuture;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * help netty to connection a remoteAddress by websocket
 * @author yang
 */
public class WebSocketClientHelper extends ChannelInboundHandlerAdapter {
    private final WebsocketHandlerShaker handshaker;
    private ChannelPromise promise;

    public WebSocketClientHelper(WebsocketHandlerShaker handshaker) {
        this.handshaker = handshaker;
    }

    public WebSocketClientHelper() {
        this(new WebsocketHandlerShaker());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        promise = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        URI uri = channel.attr(Attribute.WEBSOCKET_URI).get().getUri();
        handshaker.handShake(channel, promise,uri).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    promise.tryFailure(future.cause());
                    ctx.fireExceptionCaught(future.cause());
                } else {
                    ctx.fireUserEventTriggered(HandshakeStateEvent.ISSUED);
                }
            }
        });
        applyTimeOut(ctx, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpResponse)) {
            ctx.fireChannelRead(msg);
            return;
        }

        FullHttpResponse response = (FullHttpResponse) msg;
        try {
            if (!handshaker.isHandshakeComplete()) {
                handshaker.finishHandshake(ctx.channel(), response);
                promise.trySuccess();
                ctx.fireUserEventTriggered(HandshakeStateEvent.SUCCESS);
                ctx.pipeline().remove(this);
                return;
            }
        } finally {
            response.release();
        }

    }

    private void applyTimeOut(ChannelHandlerContext ctx, ChannelPromise promise) {
        if (promise.isDone()) {
            return;
        }
        ScheduledFuture<?> timedOutFuture = ctx.executor().schedule(() -> {
            if (promise.isDone()) {
                return;
            }
            if (promise.tryFailure(new WebSocketHandshakeException("handshake timed out"))) {
                ctx.flush().fireUserEventTriggered(HandshakeStateEvent.TIMEOUT).close();
            }
        }, 10, TimeUnit.SECONDS);

        //any event happened will cancel the timedOut task
        promise.addListener(new FutureListener<Void>() {
            @Override
            public void operationComplete(Future<Void> future) throws Exception {
                timedOutFuture.cancel(false);
            }
        });
    }

}
