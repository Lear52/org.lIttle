package org.little.http;

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
      	   HttpAuthResponse ret=new HttpAuthResponse();
     	   ret.setStatus(HttpResponseStatus.OK);
     	   ret.setUser("noname");
     	   ret.setBodyMsg(null);
     	   ret.setAuthicationData(null);	
     	   ret.setAuthicationHeader(null);
           logger.trace("request:"+request_method+" auth no ret:"+ret.getStatus());
           return ret;
    }

	
	
}
