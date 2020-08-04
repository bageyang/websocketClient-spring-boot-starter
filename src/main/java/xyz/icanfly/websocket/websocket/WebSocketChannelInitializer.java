package xyz.icanfly.websocket.websocket;

import xyz.icanfly.websocket.ssl.SSLContextFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslHandler;
import xyz.icanfly.websocket.websocket.handshake.WebSocketClientHelper;
import xyz.icanfly.websocket.websocket.handshake.WebSocketUriMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * @author yang
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    private SimpleChannelInboundHandler handler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        SSLContext sslContext = SSLContextFactory.getInstance();
        SSLEngine engine = sslContext.createSSLEngine();
        engine.setUseClientMode(true);
        engine.setWantClientAuth(false);
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addFirst("sslHandler", new SslHandler(engine));
        pipeline.addLast("httpClientCodec", new HttpClientCodec());
        pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(8192));
        pipeline.addLast("webSocketClientProtocolHandler", new WebSocketClientHelper());
        pipeline.addLast("websocketHandler", handler);
    }

    protected void handler(SimpleChannelInboundHandler handler){
        this.handler=handler;
    }
}
