package org.little.http.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpAuthNo extends HttpAuth {
    private static final Logger  logger = LoggerFactory.getLogger(HttpAuthBasic.class);

    @SuppressWarnings("unused")
    private   HttpAuthNo() {}
    protected HttpAuthNo(int _type_auth) {setTypeAuth(_type_auth);}
       
       
    @Override
    public String getFieldName() {return null;} 


    @Override   
    public HttpAuthResponse authParse(String auth,String request_method){
           response=new HttpAuthResponse();
           response.setStatus(HttpResponseStatus.OK);
           response.setUser("noname");
           response.setBodyMsg(null);
           response.setAuthicationData(null);       
           response.setAuthicationHeader(null);
           logger.trace("request:"+request_method+" no auth ret:"+response.getStatus());
           return response;
    }

       
       
}
