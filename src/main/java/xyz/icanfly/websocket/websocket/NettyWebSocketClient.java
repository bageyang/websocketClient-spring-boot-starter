package xyz.icanfly.websocket.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import xyz.icanfly.websocket.websocket.attribute.Attribute;
import xyz.icanfly.websocket.websocket.status.ObjectHolder;

import java.net.URI;
import java.util.List;

/**
 * @author yang
 */
public class NettyWebSocketClient {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NettyWebSocketClient.class);
    private List<URI> uris;
    private SimpleChannelInboundHandler messageHandler;
    private static Bootstrap holderStrap;

    public NettyWebSocketClient() {}

    public void run() {
        NioEventLoopGroup workGroup = new NioEventLoopGroup(8);
        try {
            WebSocketChannelInitializer channelInitializer = new WebSocketChannelInitializer();
            channelInitializer.handler(messageHandler);
            holderStrap = new Bootstrap()
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(channelInitializer);
            connection();
        } catch (Exception e) {
            logger.error("error with start websocket client :", e);
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                workGroup.shutdownGracefully().syncUninterruptibly();
            }));
        }
    }

    public void connection() {
        for (URI uri : uris) {
            ChannelFuture channelFuture = holderStrap.connect(uri.getHost(), 443);
            Channel channel = channelFuture.channel();
            channel.attr(Attribute.WEBSOCKET_URI).set(uri);
            ObjectHolder.add(channel);
        }
    }


    public NettyWebSocketClient urls(List<URI> urls) {
        if (urls == null || urls.isEmpty()) {
            throw new IllegalArgumentException("Invalid empty List");
        }
        this.uris = urls;
        return self();
    }

    public NettyWebSocketClient handler(SimpleChannelInboundHandler handler) {
        this.messageHandler = handler;
        return self();
    }

    private NettyWebSocketClient self() {
        return this;
    }

}
