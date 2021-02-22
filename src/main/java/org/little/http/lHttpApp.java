package org.little.http;

import org.little.http.app.keystore.HttpX509Request;
import org.little.http.app.cmddionis.HttpCmdRequest;
import org.little.http.app.file.HttpFileRequest;
import org.little.http.app.redirect.HttpRedirectRequest;
import org.little.http.handler.lHttpRequest;


public class lHttpApp{

       public static lHttpRequest create(){

           if("appkeystore".equals(commonHTTP.get().getAppName())) return  new HttpX509Request();
           else
           if("appcmddionis".equals(commonHTTP.get().getAppName()))return  new HttpCmdRequest();
           else 
           if("appfile".equals(commonHTTP.get().getAppName()))     return  new HttpFileRequest();
           else 
           if("redirect".equals(commonHTTP.get().getAppName()))    return  new HttpRedirectRequest();
           else 
           return  new lHttpRequest();
       }


}
