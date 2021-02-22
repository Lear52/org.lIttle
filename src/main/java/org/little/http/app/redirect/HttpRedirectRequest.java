package org.little.http.app.redirect;

import java.net.URI;

import org.little.http.handler.lHttpRequest;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class HttpRedirectRequest extends lHttpRequest{
       private static final Logger   logger  = LoggerFactory.getLogger(HttpRedirectRequest.class);

       private HttpRedirectResponse       response;
       private int                        port;
       private String                     path;
       
       public HttpRedirectRequest(){
              clear();
              response=new HttpRedirectResponse();
              setResponse(response);
       }


       @Override
       public void clear(){
              super.clear();
       }
       private boolean set(){
           try{
               uri = new URI(request.uri());
               port=uri.getPort();
               path=uri.getPath();
               logger.trace("host:"+uri.getHost()+" port:"+port+" path:"+path);
           }
           catch(Exception e){
                 logger.error("host:"+uri.getHost()+" port:"+port+" path:"+path +" ex:"+e);
                 return RequestProcessBad;
           }
           return RequestProcessOk;
       
       }

       @Override
       public boolean HttpGet(ChannelHandlerContext ctx){
              boolean ret=set();
              if(ret==RequestProcessBad)return RequestProcessBad;

              response.redirect(ctx,this);
              return RequestProcessOk;
       }
       @Override
       public boolean HttpPost(ChannelHandlerContext ctx){
           boolean ret=set();
           if(ret==RequestProcessBad)return RequestProcessBad;

           response.redirect(ctx,this);
           return RequestProcessOk;
       }
       @Override
       public boolean HttpPut(ChannelHandlerContext ctx){
           boolean ret=set();
           if(ret==RequestProcessBad)return RequestProcessBad;

           response.redirect(ctx,this);
           return RequestProcessOk;
       }


	   public int    getPort() {return port;}
       public String getPath() {return path;}


}

