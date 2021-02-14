package org.little.http.auth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.little.proxy.util.statChannel;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.stringTransform;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpAuthDigest extends HttpAuth {
       private static final Logger  logger = LoggerFactory.getLogger(HttpAuthDigest.class);
   
       private static final String DIGEST_REALM = "FESBLoginService";

      private   HttpAuthDigest() {super(HttpAuth.DIGEST,null);}
      
       protected HttpAuthDigest(int _type_auth,statChannel _stat) {
                 super(_type_auth,_stat);
       }
       
       @Override
       public String getFieldName() {return HttpHeaderNames.AUTHORIZATION.toString();} 
       @Override
       public String getRealm()                {return DIGEST_REALM;}
       @Override
       public void   setRealm(String realm)    {}
    
       @Override
       public HttpAuthResponse authParse(String str_auth,String request_method){
              String authMethod="auth";
              String username  =null;
              response=new HttpAuthResponse();

              if(stringTransform.isEmpty(str_auth)) {
                 String nonce = calculateNonce();
                 response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                 response.setUser(null);
                 response.setBodyMsg(getBody("Digest",HttpResponseStatus.UNAUTHORIZED));

                 String header = "Digest realm=" + getRealm() + ", ";
                 if(!stringTransform.isEmpty(authMethod)) {
                     header += "qop=" + authMethod + ", ";
                 }
                 header += "nonce=" + nonce + ", "+ "opaque=" + getOpaque(getRealm(), nonce) + ", algoritm=\"MD5\", state=\"FALSE\"";
                 logger.trace("header is null required authorization:"+header);

                 response.setAuthicationData(header);        
                 response.setAuthicationHeader("WWW-Authenticate");        
              }
              else {
                      logger.trace("get authorization string:"+str_auth);

                      HashMap<String, String> headerValues = parseHeader(str_auth);
                      username = headerValues.get("username");
                      logger.trace("username:"+username);
                      
                      String clientResponse = headerValues.get("response"); //logger.trace("client response:"+clientResponse);
                      String qop            = headerValues.get("qop");      //logger.trace("client qop:"+qop);
                      String nonce          = headerValues.get("nonce");    //logger.trace("client nonce:"+nonce);
                      String reqURI         = headerValues.get("uri");      //logger.trace("client uri:"+reqURI);
                      String ha1;
                      String ha2;
                     
                      if(!stringTransform.isEmpty(qop) && qop.equals("auth-int")) {
                          String requestBody="";
                          String entityBodyMd5 = stringTransform.getMD5Hash(requestBody); 
                          ha2 = stringTransform.getMD5Hash(request_method + ":" + reqURI + ":" + entityBodyMd5);
                      } 
                      else {
                          ha2 = stringTransform.getMD5Hash(request_method + ":" + reqURI);
                      }
                      String pre_serverResponse;
                     
                      if(stringTransform.isEmpty(qop)) {
                         pre_serverResponse=":" + nonce + ":" + ha2;
                      } 
                      else {
                          //String domain      = headerValues.get("realm");     //logger.trace("domain(realm):"      +domain);
                          String nonceCount  = headerValues.get("nc");        //logger.trace("nonceCount(nc):"     +nonceCount);
                          String clientNonce = headerValues.get("cnonce");    //logger.trace("clientNonce(cnonce):"+clientNonce);
                          pre_serverResponse = ":" + nonce + ":" + nonceCount + ":" + clientNonce + ":" + qop + ":" + ha2;

                      }

                      ha1 =user_list.getDigestUser(username,getRealm());
                      ha2 = stringTransform.getMD5Hash(ha1 + pre_serverResponse); 

                      boolean is_auth=ha2.equals(clientResponse);

                      logger.trace("server user              :"+username);/**/
                      logger.trace("server pre_serverResponse:"+pre_serverResponse);/**/
                      logger.trace("server realm:"+getRealm());/**/
                      logger.trace("server h2:"+ha2);
                      logger.trace("client Response:"+clientResponse);
                      logger.trace("ret:"+response);


                      if(!is_auth){

                          logger.debug("no authorization! for realm:"+getRealm()+" user:"+username);
                          response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                          response.setUser(null);
                          response.setBodyMsg(getBody("Digest",HttpResponseStatus.UNAUTHORIZED));
                          String header = "Digest realm=" + getRealm() + ", ";
                          if(!stringTransform.isEmpty(authMethod)) {
                              header += "qop=" + authMethod + ", ";
                          }
                          header += "nonce=" + nonce + ", ";
                          header += "opaque=" + getOpaque(getRealm(), nonce) + ", algoritm=\"MD5\", state=\"FALSE\"";
                          response.setAuthicationData(header);        
                          response.setAuthicationHeader("WWW-Authenticate");        
                      }
                      else{
                          response.setStatus(HttpResponseStatus.OK);
                          response.setUser(username);
                          response.setBodyMsg("");
                          response.setAuthicationData("");        
                          response.setAuthicationHeader(null);        
                      }
                      
                      
              }
           
              logger.trace("auth ret:"+response.getStatus());
              return response;
       }
       private String calculateNonce() {
               Date             d         = new Date();
               SimpleDateFormat f         = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
               String           fmtDate   = f.format(d);
               Random           rand      = new Random(100000);
               Integer          randomInt = rand.nextInt();

               return stringTransform.getMD5Hash(fmtDate + randomInt.toString());
       }

       private String getOpaque(String domain, String nonce) {
               return stringTransform.getMD5Hash(domain + nonce);
       }
       private HashMap<String, String> parseHeader(String headerString) {
               // seperte out the part of the string which tells you which Auth scheme is it
               String headerStringWithoutScheme = headerString.substring(headerString.indexOf(" ") + 1).trim();
               HashMap<String, String> values = new HashMap<String, String>();
               String keyValueArray[] = headerStringWithoutScheme.split(",");
               for (String keyval : keyValueArray) {
                   if (keyval.contains("=")) {
                       String key = keyval.substring(0, keyval.indexOf("="));
                       String value = keyval.substring(keyval.indexOf("=") + 1);
                       values.put(key.trim(), value.replaceAll("\"", "").trim());
                   }
               }
               return values;
       }

}
