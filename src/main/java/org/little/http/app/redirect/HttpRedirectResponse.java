package org.little.http.app.redirect;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.little.http.commonHTTP;
import org.little.http.handler.lHttpResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpRedirectResponse extends lHttpResponse{
       private static final Logger  logger = LoggerFactory.getLogger(HttpRedirectResponse.class);

       public void redirect(ChannelHandlerContext ctx,HttpRedirectRequest req) {

              String url=commonHTTP.get().getRedirectHost();//"http://sa5portal.vip.cbr.ru";
              int port=commonHTTP.get().getRedirectPort();
              String  r_url=url+":"+port+""+req.getPath();

              String msg="<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">";
                     msg+="<body><a href=\""+r_url+"\">go to HOME</a></body>";
                     msg+="</head></html>";


              ByteBuf buf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);

              FullHttpResponse response;
              
              response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.MOVED_PERMANENTLY, buf);

              response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
              response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
              response.headers().set(HttpHeaderNames.LOCATION, r_url);
              String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
              String HTTP_DATE_GMT_TIMEZONE = "GMT";
              int HTTP_CACHE_SECONDS = 1;
              SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
              dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));
              // Date header
              Calendar time = new GregorianCalendar();
              response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
              // Add cache headers
              time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);

              response.headers().set(HttpHeaderNames.EXPIRES      , dateFormatter.format(time.getTime()));
              response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
              response.headers().set(HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date()));
           
              response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
              ChannelFuture future = ctx.channel().writeAndFlush(response);
              future.addListener(ChannelFutureListener.CLOSE);
              logger.trace("set "+HttpHeaderNames.LOCATION+ " "+r_url);
       }
}


