package org.little.http;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.little.auth.listUser;


public class HttpAuth  {
       private static final Logger  logger = LoggerFactory.getLogger(HttpAuth.class);

       protected listUser   list;
       private int          type_auth;
       private String       realm;

       public HttpAuth() {
              list=new listUser();
              list.load();
       }
       
       
       public static HttpAuth getInstatce() {
              int _type_auth=commonHTTP.get().getTypeAuthenticateClients();
    	      HttpAuth ret;
    	      switch(_type_auth) {
    	      case 1: ret=new HttpAuthBasic    (_type_auth);break;
    	      case 2: ret=new HttpAuthDigest   (_type_auth);break;
    	      case 3: ret=new HttpAuthNegotiate(_type_auth);break;
    	      default:ret= new HttpAuthNo(0); break;
    	      }
    	      ret.setRealm(commonHTTP.get().getRealm());
    	      return ret;
       }
       
       public int    getTypeAuth()             {return type_auth;}
       public void   setTypeAuth(int type_auth){this.type_auth = type_auth;}
       public String getRealm()                {return realm;}
       public void   setRealm(String realm)    {this.realm = realm;}

       protected String getBody(String type,HttpResponseStatus response_status) {
               return "<!DOCTYPE HTML \"-//IETF//DTD HTML 2.0//EN\">\n"
               + "<html><head>\n"
               + "<title>Authentication Required</title>\n"
               + "</head><body>\n"
               + "<h1>"+type+" authentication Required</h1>\n"
               + "<p>This server could not verify that you\n"
               + "are authorized to access the document\n"
               + "requested.  Either you supplied the wrong\n"
               + "credentials (e.g., bad password), or your\n"
               + "browser doesn't understand how to supply\n"
               + "the credentials required.</p>\n "+getRealm()+" :"  +response_status+ "</body></html>\n";
       }

       
       public String getFieldName() {return null;} 
       
       public HttpAuthResponse authParse(String str_auth,String request_method){
              HttpAuthResponse ret=new HttpAuthResponse();
              logger.trace("auth ret:"+ret.getStatus());
              return ret;
      }
       
       public HttpAuthResponse authParse(HttpRequest  request){
    	   String str_auth=null;
    	   String request_method=request.method().name();
    	   if(getFieldName()==null)str_auth=null;
    	   else {                  str_auth = request.headers().get(getFieldName());}
           return authParse(str_auth,request_method);
   }

}
