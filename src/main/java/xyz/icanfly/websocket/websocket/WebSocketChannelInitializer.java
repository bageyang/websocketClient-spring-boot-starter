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
    private WebSocketUriMap map;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        SSLContext sslContext = SSLContextFactory.getInstance();
        SSLEngine engine = sslContext.createSSLEngine();
        engine.setUseClientMode(true);
        engine.setWantClientAuth(false);
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addFirst("sslHandler", new SslHandler(engine));
        //1. http 解码器
        pipeline.addLast("sttpClientCodec", new HttpClientCodec());
        //2.http 数据在传输过程中是分段的,需要 HttpObjectAggregator ,将多段数据聚合
        pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(8192));
        //3.websocket升级处理器
        pipeline.addLast("webSocketClientProtocolHandler", new WebSocketClientHelper(map));
        pipeline.addLast("websocketHandler", handler);
    }

    protected void handler(SimpleChannelInboundHandler handler){
        this.handler=handler;
    }

    protected void map(WebSocketUriMap map){
        this.map=map;
    }
}
