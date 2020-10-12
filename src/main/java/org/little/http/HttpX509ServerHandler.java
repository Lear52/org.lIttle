package org.little.http;

import org.little.http.auth.HttpAuthResponse;
import org.little.http.internal.HttpX509Request;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;


public class HttpX509ServerHandler extends SimpleChannelInboundHandler<HttpObject> {
       private static final Logger  logger = LoggerFactory.getLogger(HttpX509ServerHandler.class);

       private HttpX509Request      req;
      
       @Override
       public void channelInactive(ChannelHandlerContext ctx) throws Exception {
              req.clearDecoder();
       }
      
       public HttpX509ServerHandler(){
             req =new HttpX509Request(); 
       }

       @Override
       public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
      
           if (msg instanceof HttpRequest) {
               HttpRequest      request  = null;
               HttpAuthResponse ret_auth = null;
               request = (HttpRequest) msg;
               req.runRequest(ctx,request);
               logger.trace("runRequest(ctx,request)");
           }
           if (req.isDecoder()) {
              if (msg instanceof HttpContent) {
                   HttpContent chunk = (HttpContent) msg;
                   int ret=req.decodeChunk(ctx,chunk);
                   logger.trace("decodeChunk(chunk) ret:"+ret);
               }
           } 
           else {
               req.responseOk(ctx);
               logger.trace("return empty OK");
           }
       }
    
       @Override
       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
               Except ex=new Except(cause);
               logger.error("err:"+ ex);
               req.clearDecoder();
               ctx.channel().close();
       }

}
