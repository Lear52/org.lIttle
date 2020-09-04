package org.little.http;

import org.little.http.auth.HttpAuthResponse;
import org.little.http.internal.HttpX509Request;
import org.little.http.internal.HttpX509Response;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;


public class HttpX509ServerHandler extends SimpleChannelInboundHandler<HttpObject> {
       private static final Logger  logger = LoggerFactory.getLogger(HttpX509ServerHandler.class);

       private HttpX509Request      req;
       private HttpX509Response     res;
       //private HttpAuth             auth;
      
       @Override
       public void channelInactive(ChannelHandlerContext ctx) throws Exception {
              req.clearDecoder();
       }
      
       public HttpX509ServerHandler(){
             req =new HttpX509Request(); 
             res =new HttpX509Response();
             //auth=HttpAuth.getInstatce();
       }
       @Override
       public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
      
           if (msg instanceof HttpRequest) {
               HttpRequest      request  = null;
               HttpAuthResponse ret_auth = null;

               logger.trace("channelRead0 msg instanceof HttpRequest");

               request = (HttpRequest) msg;

               req.setHttpReq(request);
               //--------------------------------------------------------------
               // 
               //--------------------------------------------------------------
               ret_auth = req.Auth();

               if(ret_auth==null) {
            	  HttpX509Response.sendAuthRequired(ctx,request,ret_auth);
            	  return;
               }
               if(ret_auth.isAuth()==false) {
            	  HttpX509Response.sendAuthRequired(ctx,request,ret_auth);
            	  return;
               }
               //--------------------------------------------------------------
               //
               //--------------------------------------------------------------
               req.parsePath();
               logger.trace("cmd:"+req.getCmd()+" user:"+ret_auth.getUser()+" store:"+req.getStore());
               //--------------------------------------------------------------------------------------
               if(HttpMethod.GET.equals(request.method())) {
                  if("list".equals(req.getCmd())){
                     res.getList(ctx,req);
                     return;
                  }
                  else
                  if("get".equals(req.getCmd())){
                     res.getMsg(ctx,req);
                     return;
                  }
                  else
                  if("info".equals(req.getCmd())){
                     res.getInfo(ctx,req);
                     return;
                  }
                  else
                  if("file".equals(req.getCmd())){
                     res.getFile(ctx,req);
                     return;
                  }
                  else
                  {
                	 String txt="unknow cmd:"+req.getCmd(); 
                     logger.trace(txt);
                     res.sendError(ctx,req,txt);
                     return;
                  }
               }
               else
               if(HttpMethod.POST.equals(request.method())) {
                  if(req.createPostRequestDecoder()==false){
                	  String txt= "createPostRequestDecoder==false";
                     logger.error(txt);
                     res.sendError(ctx,req,txt);
                     return;
                  }
               }
               else
                  res.sendOk(ctx,req," ");
           }
      
           if (req.isDecoder()) {
              if (msg instanceof HttpContent) {
                   HttpContent chunk = (HttpContent) msg;
                   int ret=req.decodeChunk(chunk);
                   logger.trace("decodeChunk(chunk) ret:"+ret);
                   if(ret<=0){
                      res.saveMsg(ctx,req); 
                      //res.sendTxt(ctx,req," ",false);// return OK
                      //req.clearDecoder();
                      return;
                   }
               }
           } 
           else {
               res.sendOk(ctx,req," ");
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
