package org.little.http.internal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.http.lHttpResponse;
import org.little.store.lFolder;
import org.little.store.lMessage;
import org.little.store.lMessage2JSON;
import org.little.store.lMessageX509;
import org.little.store.lRoot;
import org.little.store.lStore;
import org.little.store.lUID;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
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
       public void getFile(ChannelHandlerContext ctx,HttpX509Request req) {
           String path0=req.getPath();

           if(path0.equals(""))path0="/index.html";
           if(path0.equals("/"))path0="/index.html";

           getFile(ctx,req,path0);

       }
       public void getFile(ChannelHandlerContext ctx,HttpX509Request req,String path0) {

           String path=decodePath(path0); 

           logger.trace(path0+" -> "+path);
           File file = new File(path);
           if(file.isHidden() || !file.exists()) {
              String err="file:"+path+" not exists"; 
              logger.error(err);
              sendTxt(ctx,req,err,HttpResponseStatus.NOT_FOUND,true);
              return ;
           }
           if(!file.isFile()) {
              String err="file:"+path+" FORBIDDEN"; 
              logger.error(err);
              sendTxt(ctx,req,err,HttpResponseStatus.FORBIDDEN,true);
              return ;
           }
           RandomAccessFile raf;
           long fileLength =0;
           try {
               raf = new RandomAccessFile(file, "r");
               fileLength = raf.length();
           } 
           catch (Exception ignore) {
              String err="file:"+path+" not exists"; 
              logger.error(err);
              sendTxt(ctx,req,err,HttpResponseStatus.NOT_FOUND,true);
              return;
           }

           HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
           //-----------------------------------------------------------------------------------------------
           HttpUtil.setContentLength(response, fileLength);

           //MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
           //String mime_type_file=mimeTypesMap.getContentType(file.getPath());

           FileNameMap fileNameMap = URLConnection.getFileNameMap();
           String mime_type_file = fileNameMap.getContentTypeFor(file.getName());
           if(mime_type_file==null){
              mime_type_file="application/octet-stream";
              logger.error("mime_type ==null set  mime_type:"+mime_type_file);
           }
           response.headers().set(HttpHeaderNames.CONTENT_TYPE, mime_type_file);

           logger.trace("get file:"+path+" len:"+fileLength+" mime_type:"+mime_type_file);
           //-----------------------------------------------------------------------------------------------
           String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
           String HTTP_DATE_GMT_TIMEZONE = "GMT";
           int HTTP_CACHE_SECONDS = 60;

           SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
           dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));
           // Date header
           Calendar time = new GregorianCalendar();
           response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
           // Add cache headers
           time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
           response.headers().set(HttpHeaderNames.EXPIRES      , dateFormatter.format(time.getTime()));
           response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
           response.headers().set(HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(file.lastModified())));
           if (!req.isKeepAlive()) {
               response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
           } 
           else{ 
               HttpVersion ver=req.protocolVersion();	
               if (ver!=null)
               if (ver.equals(HttpVersion.HTTP_1_0)) {
                   response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
               }
           }
           logger.trace("set header response");
           //--------------------------------------------------------------------------------------------------
           ctx.write(response);
           logger.trace("send header response");
           // Write the content.
           ChannelFuture sendFileFuture;
           ChannelFuture lastContentFuture;
           
           try {
                 if (ctx.pipeline().get(SslHandler.class) == null) {
                     sendFileFuture    = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
                     // Write the end marker.
                     lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                 } 
                 else {
                     sendFileFuture    = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)),ctx.newProgressivePromise());
                     // HttpChunkedInput will write the end marker (LastHttpContent) for us.
                     lastContentFuture = sendFileFuture;
                 }
           } 
           catch (IOException e) {
                     String err="file:"+path+" not exists"; 
                     logger.error(err);
                     sendTxt(ctx,req,err,HttpResponseStatus.NOT_FOUND,true);
                     return;
           }


           logger.trace("start listenet transfer");

           sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
               @Override
               public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                   if (total < 0) { // total unknown
                       logger.error(future.channel() + " Transfer progress: " + progress);
                   } else {
                       logger.error(future.channel() + " Transfer progress: " + progress + " / " + total);
                   }
               }

               @Override
               public void operationComplete(ChannelProgressiveFuture future) {
                   logger.trace(future.channel() + " Transfer complete.");
               }
           });

           // Decide whether to close the connection or not.
           if (!req.isKeepAlive()) {
               // Close the connection when the whole content is written out.
               lastContentFuture.addListener(ChannelFutureListener.CLOSE);
           }
           //---------------------------------------------------------------------------------------------------------
       }
       private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

       private static String decodePath(String uri0) {
                // Decode the path.
                String uri;
                try {
                    uri = URLDecoder.decode(uri0, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Except ex=new Except("parse URI:"+uri0,e);
                    logger.error(ex);
                    return null;
                }
                //logger.trace(uri0+" ->"+uri);
                if (uri.isEmpty() || uri.charAt(0) != '/') {
                    return null;
                }
                //logger.trace(uri0+" ->"+uri);
               
                // Convert file separators.
                uri = uri.replace('/', File.separatorChar);
               
                //logger.trace(uri0+" ->"+uri);
               
                // Simplistic dumb security check.
                // You will have to do something serious in the production environment.
                if (uri.contains(File.separator + '.') ||
                    uri.contains('.' + File.separator) ||
                    uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.' ||
                    INSECURE_URI.matcher(uri).matches()) {
                    return null;
                }
               
                // Convert to absolute path.
                uri="var"+File.separatorChar+"html"  + uri;
                //logger.trace(uri0+" ->"+uri);
                return  uri;

       }

}


