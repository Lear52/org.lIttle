package org.little.http;

import org.little.http.app.keystore.HttpX509Request;
import org.little.http.app.cmddionis.HttpCmdRequest;
import org.little.http.handler.lHttpRequest;


public class HttpApp{

       public static lHttpRequest create(){
           if("appkeystore".equals(commonHTTP.get().getAppName()))return  new HttpX509Request();
           else
           if("appcmddionis".equals(commonHTTP.get().getAppName()))return  new HttpCmdRequest();
           else 
           return  new lHttpRequest();
       }


}
