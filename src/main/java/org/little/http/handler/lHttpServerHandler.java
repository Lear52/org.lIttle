package org.little.http.handler;

//import java.util.List;
//import java.util.Map.Entry;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.stringCase;
import org.little.util.string.stringWildCard;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public class lHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
       private static final Logger  logger = LoggerFactory.getLogger(lHttpServerHandler.class);

       @Override
       public void channelInactive(ChannelHandlerContext ctx) throws Exception {
              lHttpRequest req = ctx.channel().attr(lHttpRequest.ATTRIBUTE_KEY).get();
              req.clearContent();/**/ // ?????????
       }
      

       public lHttpServerHandler(){
              logger.info("create new lHttpServerHandler");
       }

       @Override
       public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

              lHttpRequest req = ctx.channel().attr(lHttpRequest.ATTRIBUTE_KEY).get();

              logger.trace("object:"+ msg.getClass().getName());

              if (msg instanceof HttpRequest) {
                  HttpRequest request=(HttpRequest) msg; 
                  req.processHttpRequest(ctx,request);
                  logger.trace("runRequest(ctx,request)");
              }
              
              if (msg instanceof HttpContent) {
                  HttpContent chunk = (HttpContent) msg;
                  int ret=req.addContent(ctx,chunk);
                  logger.trace("addContent(chunk) ret:"+ret);
              } 
      
              /*
              else {
                 req.responseOk(ctx);
                 logger.trace("return empty OK");
              }
              */
       }
    
       @Override
       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
              if(cause==null)return;

              if(cause.getMessage()==null){
                 logger.error("get exception class:"+cause.getClass()+" cause_message:"+ cause.getMessage());
                 if(stringWildCard.wildcardMatch(cause.getMessage(), "*certificate_unknown*", stringCase.INSENSITIVE))return ;
              }
              Except ex=new Except(cause);
              logger.error("get exception exceptionCaught ex:"+ ex);

              lHttpRequest req = ctx.channel().attr(lHttpRequest.ATTRIBUTE_KEY).get();

              req.clearContent();/**/ // ?????????

              ctx.channel().close();
              
       }

}
