package org.little.proxy.Null.handler;
import java.net.SocketAddress;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

//import io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
//import io.netty.util.internal.StringUtil.NEWLINE;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class NullProxyLogHandler  extends LoggingHandler {
    private static final Logger      LOG = LoggerFactory.getLogger(NullProxyLogHandler.class);

    public NullProxyLogHandler(LogLevel level){
           super(level);
    }


    public NullProxyLogHandler(){
    }

    private String _format(ChannelHandlerContext ctx, String eventName) {
        String chStr = ctx.channel().toString();
        return new StringBuilder(chStr.length() + 1 + eventName.length()).append(chStr).append(' ').append(eventName).toString();
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
           LOG.trace(_format(ctx, "*ACTIVE"));
           ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
           LOG.trace(_format(ctx, "*INACTIVE"));
           ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
           LOG.trace(_format(ctx, "*EXCEPTION")+" Throwable:"+cause.toString());
           ctx.fireExceptionCaught(cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
           LOG.trace(_format(ctx, "*USER_EVENT")+" EVT:"+evt.toString());
           ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
           LOG.trace(_format(ctx, "*BIND") +" localAddress:"+ localAddress);
           ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx,SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
           LOG.trace(_format(ctx, "*CONNECT")+ " remoteAddress:"+remoteAddress +" localAddress:"+ localAddress);
           ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
           LOG.trace(_format(ctx, "*DISCONNECT"));
           ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
           LOG.trace(_format(ctx, "*CLOSE"));
           ctx.close(promise);
    }
}
