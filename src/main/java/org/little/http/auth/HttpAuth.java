package org.little.http.auth;

import java.util.List;
//import java.util.Map.Entry;

import org.little.auth.authUser;
import org.little.http.commonHTTP;
import org.little.proxy.util.statChannel;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;


public class HttpAuth  {
       private static final Logger  logger = LoggerFactory.getLogger(HttpAuth.class);

       public  static final  int NOAUTH   =0;
       public  static final  int BASIC    =1;
       public  static final  int DIGEST   =2;
       public  static final  int KERBEROS =3;
       public  static final  int SPNEGO   =3;

       protected authUser         user_list   ;
       protected HttpAuthResponse response    ;
       private   int              type_auth   ;
       private   String           realm       ;
       protected statChannel      channel_info;

       public HttpAuth(int _type_auth,statChannel _stat) {
              user_list=commonHTTP.get().getCfgAuth().getAuthUser();
              response=null;
              channel_info=_stat;
              setTypeAuth(_type_auth);
       }
       
       public void             setStatChannel(statChannel _stat) {channel_info=_stat;} 

       public int              getTypeAuth()             {return type_auth;}
       public void             setTypeAuth(int type_auth){this.type_auth = type_auth;}
       public String           getRealm()                {return realm;}
       public void             setRealm(String realm)    {this.realm = realm;}
       public HttpAuthResponse getResponse()             {return response;}

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

       public String getTypeAuthentication(){
              switch(type_auth) {
              case HttpAuth.BASIC:  return "BASIC";
              case HttpAuth.DIGEST: return "DIGEST";
              case HttpAuth.SPNEGO: return "SPNEGO";
              default:              return "NO";
              }
       } 
       
       public String getFieldName() {
              return null;
       } 
       
       public HttpAuthResponse authParse(String str_auth,String request_method){
              response=new HttpAuthResponse();
              //logger.trace("auth ret:"+response.getStatus());
              return response;
       }
       
       public HttpAuthResponse authParse(HttpRequest  request){
              String str_auth=null;
              String request_method=request.method().name();

               //List<Entry<String, String>> val = request.headers().entries();
               //if(val==null)logger.trace("headers size is null");
               //else {
               //    logger.trace("headers size:"+val.size());
            //          for(int i=0;i<val.size();i++){ logger.trace("headers("+i+"):"+val.get(i).getKey()+"="+val.get(i).getValue());}
             //  }
              
              
              if(getFieldName()==null){
                 str_auth=null;
                 logger.trace("unknown typ eautentification auth field is "+getFieldName());
              }
              else {                  

                 if(!request.headers().contains(HttpHeaderNames.AUTHORIZATION)) { //HttpHeaderNames.AUTHORIZATION
                    logger.trace("AuthenticationNegotiateRequired authorization field is null");
                    str_auth=null;
                 }
                 else{
                    List<String> values        = request.headers().getAll(HttpHeaderNames.AUTHORIZATION);
                    if(values==null)logger.trace("field size is null");
                    else{
                      logger.trace("field size:"+values.size());
                      //for(int i=0;i<values.size();i++){ logger.trace("field("+i+"):"+values.get(i));}
                      str_auth = values.iterator().next();
                    }
                 }
                 
                 //logger.trace("auth field:"+getFieldName());
                 logger.trace("auth data:"+str_auth);
              }

              return authParse(str_auth,request_method);
       }

}
