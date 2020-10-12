package org.little.http.auth;

import java.nio.charset.Charset;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util._Base64;
import org.little.util.utilTransform;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpAuthBasic extends HttpAuth {
       private static final Logger  logger = LoggerFactory.getLogger(HttpAuthBasic.class);
      
       @SuppressWarnings("unused")
       private   HttpAuthBasic() {}
       protected HttpAuthBasic(int _type_auth) {setTypeAuth(_type_auth);}
        
       @Override
       public String getFieldName() {return HttpHeaderNames.AUTHORIZATION.toString();} 

       @Override   
       public HttpAuthResponse authParse(String auth,String request_method){
              boolean is_empty= false;
              String  username= null;
              String  password= null;

              response=new HttpAuthResponse();

              if(utilTransform.isEmpty(auth)) is_empty=true;
              else {
                   String       value           = utilTransform.substringAfter(auth, "Basic ").trim();        
                   if(utilTransform.isEmpty(value)) is_empty=true;
                   else {
                        byte[] decodedValue   = _Base64.base64ToByteArray(value);
                        String decodedString   = new String(decodedValue, Charset.forName("UTF-8"));
                        if(utilTransform.isEmpty(decodedString))is_empty=true;
                        else {
                              username        = utilTransform.substringBefore(decodedString, ":");
                              password        = utilTransform.substringAfter(decodedString, ":");
                              if(utilTransform.isEmpty(username))is_empty=true;
                              if(utilTransform.isEmpty(password))is_empty=true;
                        }
                          
                   }
                   
              }
              if(is_empty) {
                 response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                 response.setUser(null);
                 response.setBodyMsg(getBody("Basic",HttpResponseStatus.UNAUTHORIZED));
                 response.setAuthicationData("Basic realm=\"" + (getRealm() == null ? "Restricted Files" : getRealm()) + "\"");        
                 response.setAuthicationHeader("WWW-Authenticate");        
              }
              else {
                   boolean is_user;
                   is_user=list.checkUser(username, password);
                   if(is_user==false) {
                       response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                       response.setUser(null);
                       response.setBodyMsg(getBody("Basic",HttpResponseStatus.UNAUTHORIZED));
                       response.setAuthicationData("Basic realm=\"" + (getRealm() == null ? "Restricted Files" : getRealm()) + "\"");        
                       response.setAuthicationHeader("WWW-Authenticate");        
                   }
                   else {
                       response.setStatus(HttpResponseStatus.OK);
                       response.setUser(username);
                       response.setBodyMsg("");
                       response.setAuthicationData("");        
                       response.setAuthicationHeader(null);
                   }
              }  
              logger.trace("request:"+request_method+" auth basic ret:"+response.getStatus());
              return response;
       }
}
