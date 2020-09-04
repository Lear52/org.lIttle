package org.little.http.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderNames;

public class HttpAuthNegotiate extends HttpAuth {
    private static final Logger  logger = LoggerFactory.getLogger(HttpAuthDigest.class);

	@SuppressWarnings("unused")
	private   HttpAuthNegotiate() {}
	protected HttpAuthNegotiate(int _type_auth) {setTypeAuth(_type_auth);}
        @Override
        public String getFieldName() {return HttpHeaderNames.AUTHORIZATION.toString();} 

    
        @Override
        public HttpAuthResponse authParse(String auth,String request_method){
               HttpAuthResponse ret=new HttpAuthResponse();


               logger.trace("auth ret:"+ret.getStatus());
               return ret;
        }

}
