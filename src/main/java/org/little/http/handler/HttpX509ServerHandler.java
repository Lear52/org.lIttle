package org.little.http.handler;

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

       //private lHttpRequest      req;
      
       @Override
       public void channelInactive(ChannelHandlerContext ctx) throws Exception {
              lHttpRequest req = ctx.channel().attr(lHttpRequest.ATTRIBUTE_KEY).get();
              req.clearDecoder();
       }
      
       //public HttpX509ServerHandler(lHttpRequest _req){
       //      req =_req; 
       //}

       public HttpX509ServerHandler(){
              //req =new HttpX509Request(); 
              logger.info("create HttpX509ServerHandler");
       }

       @Override
       public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
              lHttpRequest req = ctx.channel().attr(lHttpRequest.ATTRIBUTE_KEY).get();
      
              if (msg instanceof HttpRequest) {
                  //HttpRequest      request  = null;
                  //HttpAuthResponse ret_auth = null;
                  //request = (HttpRequest) msg;
                  req.runRequest(ctx,(HttpRequest) msg);
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
              lHttpRequest req = ctx.channel().attr(lHttpRequest.ATTRIBUTE_KEY).get();
              Except ex=new Except(cause);
              logger.error("err:"+ ex);
              req.clearDecoder();
              ctx.channel().close();
       }

}
