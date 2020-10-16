package xyz.icanfly.websocket.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.WebSocketScheme;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import xyz.icanfly.websocket.config.UrlMark;
import xyz.icanfly.websocket.websocket.attribute.Attribute;
import xyz.icanfly.websocket.websocket.status.ChannelState;

import java.net.URI;
import java.util.List;

/**
 * @author yang
 */
public class NettyWebSocketClient {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NettyWebSocketClient.class);
    private List<UrlMark> marks;
    private SimpleChannelInboundHandler messageHandler;
    private static Bootstrap holderStrap;

    public NettyWebSocketClient() {
    }

    public void run() {
        NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
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
        for (UrlMark mark : marks) {
            connection(mark);
        }
    }

    public void connection(UrlMark urlMark) {
        urlMark.setStatus(ChannelState.COMPLETING);
        URI uri = urlMark.getUri();
        holderStrap.connect(getHost(uri), getPort(uri));
    }


    private static int getPort(URI uri) {
        int port = uri.getPort();
        if (-1 == port) {
            String scheme = uri.getScheme();
            if (WebSocketScheme.WSS.name().contentEquals(scheme)) {
                port = WebSocketScheme.WSS.port();
            } else if (WebSocketScheme.WS.name().contentEquals(scheme)) {
                port = WebSocketScheme.WS.port();
            }
        }
        return port;
    }

    private static String getHost(URI uri) {
        return uri.getHost();
    }


    public NettyWebSocketClient marks(List<UrlMark> urls) {
        if (urls == null || urls.isEmpty()) {
            throw new IllegalArgumentException("Invalid empty List");
        }
        this.marks = urls;
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
