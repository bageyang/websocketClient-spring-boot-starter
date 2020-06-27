package xyz.icanfly.websocket.websocket.handshake;

import xyz.icanfly.websocket.websocket.util.WebSocketUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocket13FrameEncoder;

import java.net.URI;

/**
 * @author yang
 */
public class WebsocketHandlerShaker {
    private volatile boolean handshakeComplete;
    private String expectedChallengeResponseString;

    private void setHandshakeComplete() {
        handshakeComplete = true;
    }

    public boolean isHandshakeComplete() {
        return handshakeComplete;
    }

    public ChannelFuture handShake(Channel channel, ChannelPromise promise, URI uri) {
        ChannelPipeline pipeline = channel.pipeline();
        //检测通道内是否有http编解码器
        HttpResponseDecoder decoder = pipeline.get(HttpResponseDecoder.class);
        if (decoder == null) {
            HttpClientCodec codec = pipeline.get(HttpClientCodec.class);
            if (codec == null) {
                promise.setFailure(new IllegalStateException("ChannelPipeline does not contain a HttpResponseDecoder or HttpClientCodec"));
                return promise;
            }
        }
        WebSocketFullHttpRequest upgradeRequest = WebSocketUtil.newUpgradeRequest(uri);
        FullHttpRequest request = upgradeRequest.getRequest();
        this.expectedChallengeResponseString = upgradeRequest.getExpectedResponseString();
        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    ChannelPipeline p = future.channel().pipeline();
                    ChannelHandlerContext ctx = p.context(HttpRequestEncoder.class);
                    if (ctx == null) {
                        ctx = p.context(HttpClientCodec.class);
                    }
                    if (ctx == null) {
                        promise.setFailure(new IllegalStateException("ChannelPipeline does not contain a HttpRequestEncoder or HttpClientCodec"));
                        return;
                    }
                    p.addAfter(ctx.name(), "ws-encoder", newWebSocketEncoder());
                    promise.setSuccess();
                } else {
                    promise.setFailure(future.cause());
                }
            }
        });
        return promise;
    }

    private static ChannelHandler newWebSocketEncoder() {
        return new WebSocket13FrameEncoder(true);
    }

    public void finishHandshake(Channel channel, FullHttpResponse response) {
        WebSocketUtil.verify(response, expectedChallengeResponseString);
        setHandshakeComplete();
        final ChannelPipeline p = channel.pipeline();

        HttpContentDecompressor decompressor = p.get(HttpContentDecompressor.class);
        if (decompressor != null) {
            p.remove(decompressor);
        }

        HttpObjectAggregator aggregator = p.get(HttpObjectAggregator.class);
        if (aggregator != null) {
            p.remove(aggregator);
        }

        ChannelHandlerContext ctx = p.context(HttpResponseDecoder.class);
        if (ctx == null) {
            ctx = p.context(HttpClientCodec.class);
            if (ctx == null) {
                throw new IllegalStateException("ChannelPipeline does not contain a HttpRequestEncoder or HttpClientCodec");
            }

            final HttpClientCodec codec = (HttpClientCodec) ctx.handler();
            codec.removeOutboundHandler();

            p.addAfter(ctx.name(), "ws-decoder", WebSocketUtil.newWebsocketDecoder());

            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    p.remove(codec);
                }
            });
        } else {
            if (p.get(HttpRequestEncoder.class) != null) {
                p.remove(HttpRequestEncoder.class);
            }

            final ChannelHandlerContext context = ctx;
            p.addAfter(context.name(), "ws-decoder", WebSocketUtil.newWebsocketDecoder());

            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    p.remove(context.handler());
                }
            });
        }
    }
}
