package org.little.http.app.keystore;

import java.io.StringWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.http.handler.lHttpResponse;
import org.little.store.lFolder;
import org.little.store.lMessage;
import org.little.store.lMessage2JSON;
import org.little.store.lMessageX509;
import org.little.store.lRoot;
import org.little.store.lStore;
import org.little.store.lUID;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpX509Response extends lHttpResponse{
       private static final Logger  logger = LoggerFactory.getLogger(HttpX509Response.class);

       public void getMsg(ChannelHandlerContext ctx,HttpX509Request req) {
              if(req.getCmd().equals("get")==false){ 
                 sendTxt(ctx,req,_getError("cmd:"+req.getCmd()+" no op"),true);return;
              }
              lStore store=lRoot.getStore(req.getStore());
              if(store==null){
                 String txt="cmd:"+req.getCmd()+" user:"+req.getStore()+" unknow";
                 sendError(ctx,req,txt);
                 return;
              }
              lFolder folder=store.getFolder(req.getFolder());
              if(folder==null){
            	 String txt= "cmd:"+req.getCmd()+" user:"+req.getStore()+" folder:"+req.getFolder()+" unknow"; 
                 sendError(ctx,req,txt);
                 return;
              }

              folder.open(lFolder.READ_ONLY);
      
              lMessage msg=folder.getMsg().get(0);/**/
              if(msg==null){
            	 String txt= "cmd:"+req.getCmd()+" user:"+req.getStore()+" folder:"+req.getFolder()+" msg:"+req.getMsg()+" unknown";
                 sendError(ctx,req,txt);
                 return;
              }
              //logger.trace(msg);
              FullHttpResponse response;
              
              if(msg.getBodyBin()!=null){
                  ByteBuf buf  = Unpooled.copiedBuffer(msg.getBodyBin());
                  response     = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
                  String  mime = lMessage.getMIME(msg.getFilename());
                  response.headers().set(HttpHeaderNames.CONTENT_TYPE, mime);
                  response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
              }
              else
              if(msg.getBodyTxt()!=null){
                 ByteBuf buf = Unpooled.copiedBuffer(msg.getBodyTxt()          , CharsetUtil.UTF_8);
                 response    = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
                 response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
                 response.headers().set("Content-Disposition","attachment; filename="+msg.getFilename());
                 response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
              }
              else{
            	 String txt="cmd:"+req.getCmd()+" user:"+req.getStore()+" folder:"+req.getFolder()+" empty"; 
            	 sendOk(ctx,req,txt);
                 return;
              }
      
              if(!req.isKeepAlive()) {
                 response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
              } 
              else 
              if(req.protocolVersion().equals(HttpVersion.HTTP_1_0)) {
                 response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
              }
              ChannelFuture future = ctx.channel().writeAndFlush(response);
              if (!req.isKeepAlive()) future.addListener(ChannelFutureListener.CLOSE);
      
              folder.close();
       }
       public void getList(ChannelHandlerContext ctx,HttpX509Request req) {
              ArrayList<lMessage>  list_msg=null;
              String buf=null;

              if(req.getCmd().equals("list")==false){ 
                 String txt="cmd:"+req.getCmd()+" no op";
                 logger.error(txt);
              }
              else{
                 lStore store=lRoot.getStore(req.getStore());
                 if(store==null){
                    String txt="cmd:"+req.getCmd()+" user:"+req.getStore()+" unknow";
                    logger.error(txt);
                 }
                 else{
                    lFolder folder;
                    String folder_name=req.getFolder();
                    if(folder_name.toUpperCase().equals("INBOX"))folder=store.getInboxFolder();
                    else
                    if(folder_name.toUpperCase().equals("OUTBOX"))folder=store.getOutboxFolder();
                    else
                    if(folder_name.toUpperCase().equals("TRASHBOX") || folder_name.toUpperCase().equals("DELBOX"))folder=store.getDelboxFolder();
                    else
                    if(folder_name.toUpperCase().equals("COMMONBOX"))folder=store.getCommonFolder();
                    else  folder=store.getFolder(req.getFolder());

                    if(folder==null){
                       String txt="cmd:"+req.getCmd()+" user:"+req.getStore()+" folder:"+req.getFolder()+" unknow";
                       logger.error(txt);
                    }
                    else{
                       list_msg=folder.getMsg();
                       if(list_msg==null){
                          logger.info("cmd:"+req.getCmd()+" user:"+req.getStore()+" folder:"+req.getFolder()+" count msg:null");
                       }
                       else{ 
                          logger.info("cmd:"+req.getCmd()+" user:"+req.getStore()+" folder:"+req.getFolder()+" count msg:"+list_msg.size());
                       }
                       folder.close();
                    }
                    store.close();
                 }
              }

              buf=lMessage2JSON.parse(list_msg);

              if(buf==null)return;
              sendJSON(ctx,req,buf);
      
       }
       public void getListUser(ChannelHandlerContext ctx,HttpX509Request req) {
              String buf=null;

              if(req.getCmd().equals("user")==false){ 
                 String txt="cmd:"+req.getCmd()+" no op";
                 logger.error(txt);
              }
              else{
                  JSONArray list=new JSONArray();

		// TODO Auto-generated method stub
                  list.put("av");
		// TODO Auto-generated method stub
                  list.put("iap");

                  JSONObject root_object=new JSONObject();
                  root_object.put("type" ,"user");
                  root_object.put("state",true);
                  root_object.put("list" ,list);
                  StringWriter out = new StringWriter();
                  root_object.write(out);
                  buf=out.toString();
              }

              if(buf==null)return;
              sendJSON(ctx,req,buf);
      
       }
       
       public void saveMsg(ChannelHandlerContext ctx,HttpX509Request req) {
              
    	      //req.clearDecoder();
              
              lMessage  msg=req.getUploadMsg();
              if(msg==null){
                 logger.trace("no msg");
                 return;
              }
              msg=lMessageX509.parse(msg);
              if(msg==null){
                 logger.trace("no msg");
                 return;
              }
              String [] to   =msg.getTO();
              String    from =msg.getFrom();
              for(int j=0;j<to.length;j++){
                  String  store_name=to[j];
                  lStore  store     =lRoot.getStore(store_name);  if(store ==null)continue;
                  lFolder folder    =store.getInboxFolder();      if(folder==null)continue;
                  msg.setUID(lUID.get());
                  folder.save(msg);
                  logger.trace("msg save store:"+store.getName()+" folder:"+folder.getName()+" msg->"+msg);
                  folder.close();
              }

              while(true){
                String  store_name=from;
                lStore  store     =lRoot.getStore(store_name);  if(store ==null)break;
                lFolder folder    =store.getOutboxFolder();     if(folder==null)break;
                msg.setUID(lUID.get());
                folder.save(msg);
                logger.trace("msg save store:"+store.getName()+" folder:"+folder.getName()+" msg->"+msg);
                folder.close();
                break;
              }
              getFile(ctx,req,"/redirect.html");

       }
}


