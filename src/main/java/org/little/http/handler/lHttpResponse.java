package org.little.http.handler;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.little.http.commonHTTP;
import org.little.http.auth.HttpAuthResponse;
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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

public class lHttpResponse {
       private static final Logger  logger = LoggerFactory.getLogger(lHttpResponse.class);
       
       public void sendAuthRequired(ChannelHandlerContext ctx,HttpRequest request,HttpAuthResponse res) {

              
              if(res.getStatus().equals(HttpResponseStatus.OK))return;

              String msg=res.getBodyMsg();
              if(msg==null)msg=" ";

              ByteBuf buf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);

              FullHttpResponse response;

              response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, res.getStatus(), buf);

              response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
              response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

              if(res!=null)
              if(res.getAuthicationHeader()!=null)
              response.headers().set(res.getAuthicationHeader(), res.getAuthicationData());
           
              boolean keepAlive = HttpUtil.isKeepAlive(request) ;
           
              if(!keepAlive) {
                 response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
              } 
              else 
              if(request.protocolVersion().equals(HttpVersion.HTTP_1_0)) {
                 response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
              }
              ChannelFuture future = ctx.channel().writeAndFlush(response);
              if (!keepAlive) future.addListener(ChannelFutureListener.CLOSE);
   
       }

       public void sendError(ChannelHandlerContext ctx,lHttpRequest req,String err_txt) {
              sendTxt(ctx,req,_getError("ERROR: "+err_txt),HttpResponseStatus.OK,false);
       }
       public void sendOk(ChannelHandlerContext ctx,lHttpRequest req,String txt) {
              sendTxt(ctx,req,txt,HttpResponseStatus.OK,true);
       }
       
       protected String _getError(String err) {
           StringBuilder buf=new StringBuilder(10240);
           buf.append("<html>");
           buf.append("<head>");
           buf.append("<title>littlehttp:ERROR</title>\r\n");
           buf.append("</head>\r\n");
           buf.append("<body bgcolor=white><style>td{font-size: 12pt;}</style>");
      
           buf.append("<table border=\"1\">");
           buf.append("<tr>");
           buf.append("<td>");
           buf.append("<h1>ERROR</h1>");
           buf.append("</td>");
           buf.append("</tr>");
           buf.append("<tr>");
           buf.append("<td>");
           buf.append(err);
           buf.append("</td>");
           buf.append("</tr>");
           buf.append("</table>\r\n");
      
           buf.append("</body>");
           buf.append("</html>");
      
           return buf.toString();
       }

       public void sendTxt(ChannelHandlerContext ctx,lHttpRequest req,String txt,boolean forceClose) {
              sendTxt(ctx,req,txt,HttpResponseStatus.OK,forceClose);
       }
       protected void sendTxt(ChannelHandlerContext ctx,lHttpRequest req,String txt,HttpResponseStatus ret,boolean forceClose) {
      
              ByteBuf buf = Unpooled.copiedBuffer(txt, CharsetUtil.UTF_8);
              FullHttpResponse response;
              response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, ret, buf);
              response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
              response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

              // Decide whether to close the connection or not.

              boolean keepAlive = req.isKeepAlive() && !forceClose;
              if(!keepAlive) {
                 response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
              } 
              else 
              if(req.protocolVersion().equals(HttpVersion.HTTP_1_0)) {
                 response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
              }
              ChannelFuture future = ctx.channel().writeAndFlush(response);
              if (!keepAlive) future.addListener(ChannelFutureListener.CLOSE);
      
       }
       public void sendJSON(ChannelHandlerContext ctx,lHttpRequest req,String txt) {
      
              ByteBuf buf = Unpooled.copiedBuffer(txt, CharsetUtil.UTF_8);
              FullHttpResponse response;
              response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
              response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
              response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

              // Decide whether to close the connection or not.

              boolean keepAlive = req.isKeepAlive();
      
              if(!keepAlive) {
                 response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
              } 
              else 
              if(req.protocolVersion().equals(HttpVersion.HTTP_1_0)) {
                 response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
              }
              ChannelFuture future = ctx.channel().writeAndFlush(response);
              if (!keepAlive) future.addListener(ChannelFutureListener.CLOSE);
      
       }

       
       public String getInfo(lHttpRequest req) {
              StringBuilder buf=new StringBuilder(10240);
              buf.append("VERSION: " + req.protocolVersion().text() + "\r\n");
              buf.append("REQUEST_URI: " + req.getURI() + "\r\n\r\n");
              for (Entry<String, String> entry : req.getHeaders()) {
                   buf.append("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
              }
              buf.append("\r\n");
      
              Set<Cookie> cookies;
              String value = req.getHeaders().get(HttpHeaderNames.COOKIE);
              if (value == null)  cookies = Collections.emptySet();
              else                cookies = ServerCookieDecoder.STRICT.decode(value);
              for (Cookie cookie : cookies) buf.append("COOKIE: " + cookie + "\r\n");
              
              buf.append("\r\n");
      
              if (HttpMethod.GET.equals(req.getMethod())) {
                  buf.append("\r\n\r\nEND OF GET CONTENT\r\n");
              }
              return buf.toString();
       }
       public void getFile(ChannelHandlerContext ctx,lHttpRequest req) {
                   //logger.trace("set 111 req:"+req);

                   String path0=req.getPath();

                   //logger.trace("set 112 path:"+path0);

                   if(path0==null       )path0="/index.html";
                   if(""  .equals(path0))path0="/index.html";
                   if("/" .equals(path0))path0="/index.html";
                   if("\\".equals(path0))path0="/index.html";

                   //logger.trace("set 113 path:"+path0);

                   getFile(ctx,req,path0);

       }
       public void getFile(ChannelHandlerContext ctx,lHttpRequest req,String path0) {

                   String path=decodePath(path0); 

                   logger.trace("GET url:" +path0+" file:"+path);

                   File file = new File(path);
                   int file_size=(int)file.length();

                   logger.trace("GET url:" +path0+" file:"+path +" file size:"+file_size);

                   if(file.isHidden() || !file.exists()) {
                      String err="GET url:" +path0+" file:"+path+" not exists"; 
                      logger.error(err);
                      sendTxt(ctx,req,err,HttpResponseStatus.NOT_FOUND,true);
                      return ;
                   }
                   if(!file.isFile()) {
                      String err="GET url:" +path0+" file:"+path+" FORBIDDEN"; 
                      logger.error(err);
                      sendTxt(ctx,req,err,HttpResponseStatus.FORBIDDEN,true);
                      return ;
                   }
                   RandomAccessFile source_file;
                   long fileLength =0;
                   try {
                       source_file = new RandomAccessFile(file, "r");
                       fileLength = source_file.length();
                   } 
                   catch (Exception ignore) {
                      String err="GET url:" +path0+" file:"+path+" not exists"; 
                      logger.error(err);
                      sendTxt(ctx,req,err,HttpResponseStatus.NOT_FOUND,true);
                      return;
                   }
                   if(fileLength != file_size) {
                      logger.error("fileLength("+fileLength+") != file_size("+file_size+")");
                   }
                   
                   HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                   //-----------------------------------------------------------------------------------------------
                   HttpUtil.setContentLength(response, fileLength);
                   logger.trace("GET url:" +path0+" file:"+path +" fileLength:"+fileLength);

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
                   //int HTTP_CACHE_SECONDS = 600;

                   SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
                   dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));
                   // Date header
                   Calendar time = new GregorianCalendar();
                   response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
                   // Add cache headers
                   //time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);

                   //response.headers().set(HttpHeaderNames.EXPIRES      , dateFormatter.format(time.getTime()));
                   //response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
                   response.headers().set(HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(file.lastModified())));


                   if (!req.isKeepAlive()) {
                       response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                       logger.trace("HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE !!!!!  file:"+path);
                   } 
                   else{ 
                       HttpVersion ver=req.protocolVersion();       
                       if (ver!=null)
                       if (ver.equals(HttpVersion.HTTP_1_0)) {
                           response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                       }
                   }
                   response.headers().set("Server", "org.little.http");
                   //logger.trace("set header response");
                   //--------------------------------------------------------------------------------------------------
                   ctx.write(response);
                   //logger.trace("send header response");
                   // Write the content.
                   ChannelFuture sendFileFuture;
                   ChannelFuture lastContentFuture;
                   
                   try {
                         if (ctx.pipeline().get(SslHandler.class) == null) {
                           logger.trace("sendFileFuture no SslHandler url:" +path0+" file:"+path +" fileLength:"+fileLength);
                           
                           DefaultFileRegion  _f=new DefaultFileRegion(source_file.getChannel(), 0, fileLength);
                           sendFileFuture    = ctx.write        (_f,ctx.newProgressivePromise());
                           // Write the end marker.
                           lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                         } 
                         else {
                             logger.trace("sendFileFuture is SslHandler url:" +path0+" file:"+path +" fileLength:"+fileLength);
                             HttpChunkedInput _f=new HttpChunkedInput(new ChunkedFile(source_file, 0, fileLength, 8192));
                             sendFileFuture    = ctx.writeAndFlush(_f,ctx.newProgressivePromise());
                             // HttpChunkedInput will write the end marker (LastHttpContent) for us.
                             lastContentFuture = sendFileFuture;
                         }
                   } 
                   //catch (IOException e) {
                   catch (Exception e) {
                             String err="file:"+path+" not exists"; 
                             logger.error(err);
                             sendTxt(ctx,req,err,HttpResponseStatus.NOT_FOUND,true);
                             return;
                   }


                   logger.trace("start listenet transfer file:"+path0);

                   sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                       @Override
                       public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                           if (total < 0) { // total unknown
                               logger.error(future.channel() + " Transfer progress: " + progress);
                           } 
                           else {
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
                       logger.trace("ChannelFutureListener.CLOSE");
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
           if (uri.isEmpty() || uri.charAt(0) != '/') {
               return null;
           }
           // Convert file separators.
           uri = uri.replace('/', File.separatorChar);
           // Simplistic dumb security check.
           // You will have to do something serious in the production environment.
           if (uri.contains(File.separator + '.') ||
               uri.contains('.' + File.separator) ||
               uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.' ||
               INSECURE_URI.matcher(uri).matches()) {
               return null;
           }
           // Convert to absolute path.
           ;
           uri=commonHTTP.get().getRootDocument() + uri;
           //uri="var"+File.separatorChar+"html"  + uri;
           //logger.trace(uri0+" ->"+uri);
           return  uri;
       }

}


