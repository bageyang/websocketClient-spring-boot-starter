package xyz.icanfly.websocket.websocket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.ssl.SslHandler;
import xyz.icanfly.websocket.websocket.attribute.Attribute;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.net.SocketAddress;
import java.util.List;

/**
 * @author : yangbing
 * @version : v1.0
 * @description override all method that SslHandler has override
 * @date : 2020/10/22 9:49
 */
public class SmartSslHandler extends SslHandler {
    private boolean overSsl;

    public SmartSslHandler(SSLEngine engine) {
        super(engine);
    }

    /**
     * ChannelHandler method
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (isOverSsl(ctx)) {
            super.handlerAdded(ctx);
        }else {
            // remove this handler if do not need it
            ctx.pipeline().remove(this);
        }
    }

    /**
     * ChannelHandler method
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        if (isOverSsl(ctx)) {
            super.handlerRemoved0(ctx);
        }
    }

    /**
     * ChannelHandler method
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (isOverSsl(ctx)) {
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * ChannelOutboundHandler method
     *
     * @param ctx
     * @param localAddress
     * @param promise
     * @throws Exception
     */
    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (isOverSsl(ctx)) {
            super.bind(ctx, localAddress, promise);
        }
    }

    /**
     * ChannelOutboundHandler method
     *
     * @param ctx
     * @param remoteAddress
     * @param localAddress
     * @param promise
     * @throws Exception
     */
    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (isOverSsl(ctx)) {
            super.connect(ctx, remoteAddress, localAddress, promise);
        }
    }

    /**
     * ChannelOutboundHandler method
     *
     * @param ctx
     * @param promise
     * @throws Exception
     */
    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (isOverSsl(ctx)) {
            ctx.deregister(promise);
        }
    }

    /**
     * ChannelOutboundHandler method
     *
     * @param ctx
     * @param promise
     * @throws Exception
     */
    @Override
    public void disconnect(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        if (isOverSsl(ctx)) {
            super.disconnect(ctx, promise);
        }
    }

    /**
     * ChannelOutboundHandler method
     *
     * @param ctx
     * @param promise
     * @throws Exception
     */
    @Override
    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        if (isOverSsl(ctx)) {
            super.close(ctx, promise);
        }
    }

    /**
     * ChannelOutboundHandler method
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        if (isOverSsl(ctx)) {
            super.read(ctx);
        }
    }

    /**
     * ChannelOutboundHandler method
     *
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(final ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (isOverSsl(ctx)) {
            super.write(ctx, msg, promise);
        }
    }

    /**
     * ChannelOutboundHandler method
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (isOverSsl(ctx)) {
            super.flush(ctx);
        }
    }

    /**
     * ChannelInboundHandler method
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (isOverSsl(ctx)) {
            super.channelInactive(ctx);
        }
    }

    /**
     * ByteToMessageDecoder method
     *
     * @param ctx
     * @param in
     * @param out
     * @throws SSLException
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws SSLException {
        if (isOverSsl(ctx)) {
            super.decode(ctx, in, out);
        }
    }

    /**
     * ChannelInboundHandler method
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (isOverSsl(ctx)) {
            super.channelReadComplete(ctx);
        }
    }

    /**
     * ChannelInboundHandler method
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        if (isOverSsl(ctx)) {
            overSsl = true;
            super.channelActive(ctx);
        }
    }

    private boolean isOverSsl(ChannelHandlerContext ctx) {
        return overSsl || ctx.channel().attr(Attribute.WEBSOCKET_URI).get().isOverSsl();
    }
}
