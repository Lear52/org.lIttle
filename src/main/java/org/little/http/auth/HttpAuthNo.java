package org.little.http.auth;

import org.little.proxy.util.statChannel;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpAuthNo extends HttpAuth {
    private static final Logger  logger = LoggerFactory.getLogger(HttpAuthNo.class);

    private   HttpAuthNo() {super(HttpAuth.NOAUTH,null);}
    
    protected HttpAuthNo(int _type_auth,statChannel _stat) {
              super(_type_auth,_stat);
    }
       
       
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
