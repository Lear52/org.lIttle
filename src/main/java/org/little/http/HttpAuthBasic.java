package org.little.http;

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
               HttpAuthResponse ret=new HttpAuthResponse();
               boolean          is_empty=false;
               String           username        = null;
               String           password        = null;

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
                  ret.setStatus(HttpResponseStatus.UNAUTHORIZED);
                  ret.setUser(null);
                  ret.setBodyMsg(getBody("Basic",HttpResponseStatus.UNAUTHORIZED));
                  ret.setAuthicationData("Basic realm=\"" + (getRealm() == null ? "Restricted Files" : getRealm()) + "\"");        
                  ret.setAuthicationHeader("WWW-Authenticate");        
               }
               else {
                    boolean is_user;
                    is_user=list.checkUser(username, password);
                    if(is_user==false) {
                        ret.setStatus(HttpResponseStatus.UNAUTHORIZED);
                        ret.setUser(null);
                        ret.setBodyMsg(getBody("Basic",HttpResponseStatus.UNAUTHORIZED));
                        ret.setAuthicationData("Basic realm=\"" + (getRealm() == null ? "Restricted Files" : getRealm()) + "\"");        
                        ret.setAuthicationHeader("WWW-Authenticate");        
                    }
                    else {
                        ret.setStatus(HttpResponseStatus.OK);
                        ret.setUser(username);
                        ret.setBodyMsg("");
                        ret.setAuthicationData("");        
                        ret.setAuthicationHeader(null);
                    }
               }  
               logger.trace("request:"+request_method+" auth basic ret:"+ret.getStatus());
               return ret;
    }
}
