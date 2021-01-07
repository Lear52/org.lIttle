package org.little.http.app.file;

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

public class HttpFileResponse extends lHttpResponse{
       private static final Logger  logger = LoggerFactory.getLogger(HttpFileResponse.class);

       public void saveMsg(ChannelHandlerContext ctx,HttpFileRequest req) {
              
              getFile(ctx,req,"/redirect.html");

       }
}


