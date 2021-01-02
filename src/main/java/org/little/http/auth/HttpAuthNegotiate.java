package org.little.http.auth;

import java.nio.charset.Charset;
import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import org.little.proxy.commonProxy;
import org.little.proxy.util.statChannel;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util._Base64;
import org.little.util.LoggerFactory;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.SslEngineSource;


import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
//import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaderNames;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.Future;
import jcifs.util.Base64;


public class HttpAuthNegotiate extends HttpAuth {
       private static final Logger  logger = LoggerFactory.getLogger(HttpAuthDigest.class);
      
       /** Factory for GSS-API mechanism. */
       private static final GSSManager     MANAGER          = GSSManager.getInstance();
       /** GSS-API mechanism "1.3.6.1.5.5.2". */
       //private static final Oid            SPNEGO_OID       = getOid();
       private static final Lock           LOCK             = new ReentrantLock();
       //private        AtomicBoolean        authenticated    ;
       private        String               username         ; 
       //private        boolean              is_auth_disable  ; 
       private        String               host_addr_port   ;
       private        statChannel          channel_info     ;
       
       //private        LoginContext         loginContext     ;         /** Login Context server uses for pre-authentication. */
       private        GSSCredential        serverCredentials;    /** Credentials server uses for authenticating requests. */



       //private       KerberosPrincipal    serverPrincipal  ;      /** Server Principal used for pre-authentication. */

       @SuppressWarnings("unused")
       private   HttpAuthNegotiate() {}

       protected HttpAuthNegotiate(int _type_auth) {setTypeAuth(_type_auth);}
       @Override
       public String getFieldName() {return HttpHeaderNames.AUTHORIZATION.toString();} 

    
       @Override
       public HttpAuthResponse authParse(String authorization,String request_method){
              response=new HttpAuthResponse();
              String  principal="unknown_principal";
              //String  realm=commonProxy.get().getRealm();//"vip.cbr.ru";/**/
              
              if(authorization.startsWith("Negotiate")) {
                  String sub_auth=authorization.substring(10);
                    //---------------------------------------------------------------------------------------
                    logger.trace("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" client sent to server negotiate token:"+sub_auth);
                    byte[] _gss = Base64.decode(sub_auth);
                    if(0 == _gss.length) {
                       logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" GSS data was NULL.");
                       //writeAuthenticationRequired("",realm,"",null);
                       response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                       response.setUser(null);
                       response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                       response.setAuthicationData("Negotiate");        
                       response.setAuthicationHeader("WWW-Authenticate");        
                       return response;
                    }
                    //---------------------------------------------------------------------------------------
                    {
                       String hdr="NTLMSSP";
                       byte [] _ntlmspp={(byte)0x4E,(byte)0x54,(byte)0x4C,(byte)0x4D,(byte)0x53,(byte)0x53,(byte)0x50,(byte)0x0};
                       //try{ HexDump.dump(_ntlmspp); }catch(Exception ex113){}
                       boolean is_ntlmspp=true;
                       for(int i=0;(i<_ntlmspp.length && i<_gss.length);i++){
                           if(_ntlmspp[i]!=_gss[i]){
                              is_ntlmspp=false;
                              break;
                          }
                       }
                       if(is_ntlmspp){
                          logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" GSS data begin with :"+hdr);
                          //writeAuthenticationRequired("",realm,"",null);
                          response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                          response.setUser(null);
                          response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                          response.setAuthicationData("Negotiate");        
                          response.setAuthicationHeader("WWW-Authenticate");        
                          return response;
                       }
                    }
                  
                  //------------------------------------------------------------------------------------------
                  byte[]        token2    = null;
                  GSSContext    context   = null;
                  try {
                      //-----------------------------------------------------------------------------------------------
                      LOCK.lock();
                      try {
                          logger.trace("client_ip:"+channel_info.getSrc()+" crete context");
                          context = MANAGER.createContext(serverCredentials);
                      }
                      catch(Exception ex){
                          String msg=ex.toString();
                          Except ex1=new Except(msg,ex);
                          logger.error("client_ip:"+channel_info.getSrc()+" error createContext ex:"+ex1);
                          //writeAuthenticationRequired("",realm,"",null);
                          response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                          response.setUser(null);
                          response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                          response.setAuthicationData("Negotiate");        
                          response.setAuthicationHeader("WWW-Authenticate");        
                          return response;
                      }
                      finally {
                          LOCK.unlock();
                      }
                      //-----------------------------------------------------------------------------------------------
                      LOCK.lock();
                      try {
                          token2 = context.acceptSecContext(_gss, 0, _gss.length);
                      }
                      catch(GSSException ex){
                          String msg=ex.toString()+" | major:"+ex.getMajorString()+" | minor:"+ex.getMinorString();
                          Except ex1=new Except(msg,ex);
                          logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error acceptSecContext ex:"+ex1);
                          //writeAuthenticationRequired("",realm,"",null);
                          response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                          response.setUser(null);
                          response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                          response.setAuthicationData("Negotiate");        
                          response.setAuthicationHeader("WWW-Authenticate");        
                          return response;
                      }
                      finally {
                          LOCK.unlock();
                      }
                      //-----------------------------------------------------------------------------------------------
                      try {
                          if (!context.isEstablished()) {
                               logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" context not established");
                               //writeAuthenticationRequired("",realm,"",null);
                               response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                               response.setUser(null);
                               response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                               response.setAuthicationData("Negotiate");        
                               response.setAuthicationHeader("WWW-Authenticate");        
                               return response;
                          }
                          else{
                               try {
                                   principal = context.getSrcName().toString();
                               }
                               catch(Exception ex){
                                    Except ex1=new Except(ex.toString(),ex);
                                    logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error context.getSrcName().toString() ex:"+ex1);
                                    //writeAuthenticationRequired("",realm,"",null);
                                    response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                                    response.setUser(null);
                                    response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                                    response.setAuthicationData("Negotiate");        
                                    response.setAuthicationHeader("WWW-Authenticate");        
                                    return response;
                               }
                               finally {
                                 //SpnegoAuthenticator.LOCK.unlock();
                               }
                          }
          
                          logger.debug("client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" principal:" + principal);
                          //------------------------------------------------------------------------------
                          
                          if(token2!=null){
                             String _token2=Base64.encode(token2);
                             logger.trace("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" context.acceptSecContext(_gss, 0, _gss.length) return:" + _token2);
                          }
                          //------------------------------------------------------------------------------
                      }
                      catch(Exception ex){
                          String msg=ex.toString();
                          Except ex1=new Except(msg,ex);
                          logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error get Context ex:"+ex1);
                          //writeAuthenticationRequired("",realm,"",null);
                          response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                          response.setUser(null);
                          response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                          response.setAuthicationData("Negotiate");        
                          response.setAuthicationHeader("WWW-Authenticate");        
                          return response;
                      }
                      finally {
                          //SpnegoAuthenticator.LOCK.unlock();
                      }
                      //------------------------------------------------------------------------------------
                  } 
                  finally {
                      if (null != context) {
                          //SpnegoAuthenticator.LOCK.lock();
                          try {
                              logger.info("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" context dispose");
                              context.dispose();
                          }
                          catch(Exception ex){
                                Except ex1=new Except(ex.toString(),ex);
                                logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error context.dispose() ex:"+ex1);
                                //writeAuthenticationRequired("",realm,"",null);
                                response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                                response.setUser(null);
                                response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                                response.setAuthicationData("Negotiate");        
                                response.setAuthicationHeader("WWW-Authenticate");        
                          }
                          finally {
                              //SpnegoAuthenticator.LOCK.unlock();
                          }
                      }
                  }
                  //-----------------------------------------------------------------------------------------
                }
                     
              response.setStatus(HttpResponseStatus.OK);
              response.setUser(username);
              response.setBodyMsg("");
              response.setAuthicationData("");        
              response.setAuthicationHeader(null);
              logger.trace("auth ret:"+response.getStatus());
              return response;
       }
       private static Oid getOid() {
           Oid oid = null;
           try {
               oid = new Oid("1.3.6.1.5.5.2");
           } catch (GSSException gsse) {
               logger.error("Unable to create OID 1.3.6.1.5.5.2 !", gsse);
           }
           return oid;
       }

}
