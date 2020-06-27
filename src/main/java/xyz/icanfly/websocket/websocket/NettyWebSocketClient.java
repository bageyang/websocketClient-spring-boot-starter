package xyz.icanfly.websocket.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import xyz.icanfly.websocket.websocket.handshake.WebSocketUriMap;

import java.net.URI;
import java.util.List;

/**
 * @author yang
 */
public class NettyWebSocketClient {
    private List<URI> uris;
    private WebSocketUriMap webSocketUriMap;
    private SimpleChannelInboundHandler messageHandler;

    public NettyWebSocketClient(){}

    public Bootstrap run(){
        NioEventLoopGroup workGroup = new NioEventLoopGroup(8);
        try {
           WebSocketChannelInitializer channelInitializer = new WebSocketChannelInitializer();
           channelInitializer.handler(messageHandler);
           channelInitializer.map(webSocketUriMap);
            Bootstrap bootstrap = new Bootstrap()
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(channelInitializer);
            for (URI uri : uris) {
                ChannelFuture channelFuture = bootstrap.connect(uri.getHost(), 443).sync();
                webSocketUriMap.save(channelFuture.channel(),uri);
                channelFuture.channel().closeFuture().sync();
            }
            return bootstrap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
        }
        return null;
    }

    public NettyWebSocketClient urls(List<URI> urls)  {
        if (urls==null||urls.isEmpty()) {
            throw new IllegalArgumentException ("Invalid empty List");
        }
        this.uris=urls;
        return self();
    }

    public NettyWebSocketClient handler(SimpleChannelInboundHandler handler){
        this.messageHandler=handler;
        return self();
    }

    public NettyWebSocketClient map(WebSocketUriMap map){
        this.webSocketUriMap=map;
        return self();
    }

    private NettyWebSocketClient self() {
        return this;
    }



}
