package org.little.http;

import org.little.http.app.keystore.HttpX509Request;
import org.little.http.handler.lHttpRequest;


public class HttpApp{

       public static lHttpRequest create(){return  new HttpX509Request();}


}
