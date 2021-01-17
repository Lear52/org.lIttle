package org.little.http.handler;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;


public class lHttpLog{
       private static final Logger          logger  = LoggerFactory.getLogger(lHttpLog.class);

       public static void print(HttpRequest request,HttpResponseStatus status) {
               logger.trace(request+" "+request);
       }



}

