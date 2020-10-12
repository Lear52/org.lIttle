package org.littleshoot.proxy.impl;

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
import org.little.proxy.util.commonProxy;
import org.little.proxy.util.statChannel;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util._Base64;
import org.little.util.LoggerFactory;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.SslEngineSource;
//import org.little.util.HexDump;

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

/**
 * ÃŒ…  À¿—— ClientToProxyConnection
*/
public class AuthClientToProxyConnection extends ClientToProxyConnection {

    private static final Logger LOG = LoggerFactory.getLogger(AuthClientToProxyConnection.class);
    /** Factory for GSS-API mechanism. */
    private static final GSSManager     MANAGER          = GSSManager.getInstance();
    /** GSS-API mechanism "1.3.6.1.5.5.2". */
    private static final Oid            SPNEGO_OID       = getOid();
    private static final Lock           LOCK             = new ReentrantLock();

    private        AtomicBoolean        authenticated    ;
    private        String               username         ; 
    private        boolean              is_auth_disable  ; 
    private        String               host_addr_port   ;
    private        statChannel          channel_info     ;
    
    private        LoginContext         loginContext     ;         /** Login Context server uses for pre-authentication. */
    private        GSSCredential        serverCredentials;    /** Credentials server uses for authenticating requests. */
    @SuppressWarnings("unused")
	private        KerberosPrincipal    serverPrincipal  ;      /** Server Principal used for pre-authentication. */

    private final BytesReadMonitor bytesReadMonitor = new BytesReadMonitor() {
        @Override
        protected void bytesRead(int numberOfBytes) {
                  if(channel_info==null)return;
                  channel_info.addIn(numberOfBytes);
                  //LOG.trace("---------------------- addIn:"+channel_info.getString());
        }
    };
    private BytesWrittenMonitor bytesWrittenMonitor = new BytesWrittenMonitor() {
        @Override
        protected void bytesWritten(int numberOfBytes) {
                  if(channel_info==null)return;
                  channel_info.addOut(numberOfBytes);
                  //LOG.trace("---------------------- addOut:"+channel_info.getString());
        }
    };

    public AuthClientToProxyConnection(final DefaultHttpProxyServer proxyServer,SslEngineSource sslEngineSource,boolean typeauthenticateClients,ChannelPipeline pipeline,GlobalTrafficShapingHandler globalTrafficShapingHandler){

        super(proxyServer,sslEngineSource,true,pipeline,globalTrafficShapingHandler);

        pipeline.remove("bytesReadMonitor");
        pipeline.remove("bytesWrittenMonitor");
        pipeline.remove("requestReadMonitor");
        pipeline.remove("responseWrittenMonitor");

        pipeline.addFirst("bytesReadMonitor", bytesReadMonitor);
        pipeline.addFirst("bytesWrittenMonitor", bytesWrittenMonitor);

        clear();
        //username       ="";
        //is_auth_disable=false;//  
        //host_addr_port ="0.0.0.0";
        //channel_info   = null;
        //SPNEGO_OID     = getOid();

        if(commonProxy.get().getTypeAuthenticateClients()==3){
           LOG.debug("type_authenticateClients:Kerberos");

           try{
              // set auth info 
               CallbackHandler handler = new CallbackHandler() {
                              public void handle(final Callback[] callback) {
                                  for (int i=0; i<callback.length; i++) {
                                      if(callback[i] instanceof NameCallback) {
                                         NameCallback nameCallback = (NameCallback) callback[i];
                                         nameCallback.setName(commonProxy.get().getLdapUsername());
                                      } 
                                      else 
                                      if(callback[i] instanceof PasswordCallback) {
                                         PasswordCallback passCallback = (PasswordCallback) callback[i];
                                         passCallback.setPassword(commonProxy.get().getLdapPassword().toCharArray());
                                      } else {
                                         LOG.info("Unsupported Callback i=" + i + "; class=" + callback[i].getClass().getName());
                                      }
                                  }
                              }
               };

               loginContext       = new LoginContext("spnego-server", handler);
               loginContext.login();
               Subject subject=loginContext.getSubject();
               PrivilegedExceptionAction<GSSCredential> action =  new PrivilegedExceptionAction<GSSCredential>() {
                                                     public GSSCredential run() throws GSSException {
                                                           return MANAGER.createCredential(null,GSSCredential.INDEFINITE_LIFETIME,SPNEGO_OID,GSSCredential.ACCEPT_ONLY);
                                                      } 
              };
              serverCredentials = Subject.doAs(subject, action);
              serverPrincipal   = new KerberosPrincipal(serverCredentials.getName().toString());
           }
           catch(Exception ex){
               Except ex1=new Except(ex.toString(),ex);
               LOG.error("AuthClientToProxyConnection() type_authenticateClients:kerberos  ex:"+ex1);
               LOG.info("type_authenticateClients:"+commonProxy.get().getTypeAuthenticateClients());
           }
        }

    }
    protected void clear(){
              authenticated    = new AtomicBoolean();

              loginContext     =null; 
              serverCredentials=null; 
              serverPrincipal  =null; 

              username        =  "";
              is_auth_disable=false;//  
              host_addr_port ="0.0.0.0";
              channel_info   = null;
              //SPNEGO_OID     = getOid();
    }

    static public boolean isTransparent(){
           return commonProxy.get().isTransparent();
    }
    static public int getPort(){
           return commonProxy.get().getPort();
    }
    @Override
    protected void serverDisconnected(ProxyToServerConnection serverConnection) {
              super.serverDisconnected(serverConnection);
              LOG.warn("server_ip:"+host_addr_port+" disconnection");
    }
    @Override
    protected void serverConnectionSucceeded(ProxyToServerConnection serverConnection,boolean shouldForwardInitialRequest) {
              super.serverConnectionSucceeded(serverConnection,shouldForwardInitialRequest);
              LOG.warn("server_ip:"+host_addr_port+" connection:OK");
    }
    @Override
    protected boolean serverConnectionFailed(ProxyToServerConnection serverConnection,ConnectionState lastStateBeforeFailure,Throwable cause){
              boolean ret=super.serverConnectionFailed(serverConnection,lastStateBeforeFailure,cause);
              LOG.error("server_ip:"+host_addr_port+" connection:Failed ret:"+ret);
              return ret;
    }
    @Override
    protected void disconnected() {

                 commonProxy.get().getChannel().remove(channel_info);

                 if(channel_info!=null)/**/
                 LOG.info("+++ Disconnected client ip:"+channel_info.getSrc()+" proxy ip:"+channel_info.getDst());
                 else
                 LOG.info("+++ Disconnected client ip:unknow proxy ip:unknow channel_info=null");

              super.disconnected();
    }
    @Override
    Future<Void> disconnect() {
                 //commonProxy.get().getChannel().remove(channel);
                 commonProxy.get().getChannel().remove(channel_info);

                 if(channel_info!=null)/**/
                 LOG.info("!!! Disconnect client ip:"+channel_info.getSrc()+" proxy ip:"+channel_info.getDst());
                 else
                 LOG.info("!!! Disconnect client ip:unknow proxy ip:unknow channel_info=null");

                 return super.disconnect();

    }
    /*
    @Override
    protected void read(Object msg) {
              LOG.trace(stat_channel.toString()+" read class:"+msg.getClass().getName());

              if(channel_info!=null)
              if (isTunneling()) {
                  // In tunneling mode, this connection is simply shoveling bytes
                  ByteBuf b=(ByteBuf) msg;
                  stat_channel.addIn(b.writerIndex());
              } else {
                  // If not tunneling, then we are always dealing with HttpObjects.
                  HttpObject o=(HttpObject) msg;
              }
              super.read(msg);
    }

    @Override
    protected void doWrite(Object msg) {

              LOG.trace(stat_channel.toString()+" write class:"+msg.getClass().getName());
              super.doWrite(msg);
              if(channel_info!=null)
                 if (msg instanceof HttpObject) {
                //writeHttp((HttpObject) msg);
                 } else {
                  ByteBuf b=(ByteBuf) msg;
                //writeRaw((ByteBuf) msg);
                 }

    }
    */
    @Override
    protected void respond(ProxyToServerConnection serverConnection, HttpFilters filters,HttpRequest currentHttpRequest, HttpResponse currentHttpResponse,HttpObject httpObject){

           if(httpObject instanceof DefaultHttpResponse){
              DefaultHttpResponse h=(DefaultHttpResponse)httpObject;
              HttpHeaders hh=h.headers();
              //-------------------------------------------------------------------------
              String location = hh.get(HttpHeaderNames.LOCATION); //"Location"
              if(location!=null){
                 String host=currentHttpRequest.headers().get("X-OLD-Host");
                 if(host==null)host="";
                 hh.remove(HttpHeaderNames.LOCATION);
                 {
                 String [] arr;
                 arr=location.split("/");       

                 String l=arr[0]+"//"+arr[2];
                 int    ln=l.length();
                 String ll=location.substring(ln);

                 location =arr[0]+"//"+host+ll;
                 }
                 hh.add(HttpHeaderNames.LOCATION,location);
              }
              //-------------------------------------------------------------------------
              if(is_auth_disable==false)
              //if(h.getStatus().code()==401){/**/
              if(h.status().code()==401){
                 if(channel_info!=null)
                 LOG.warn("AuthenticationRequired ip:"+channel_info.getSrc()+" server ip:"+host_addr_port+" return:401");
                 //hh.add("Set-Cookie","WebAccessBean_sessionTicket=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/");
                 //hh.add("Set-Cookie","WebAccessBean_sessionTicket_STENDSOK=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/");
                 writeAuthenticationRequired("",commonProxy.get().getRealm(),"",hh);
                 authenticated.set(false);
                 return;
              }
              //-------------------------------------------------------------------------
           }

           super.respond(serverConnection,filters,currentHttpRequest,currentHttpResponse,httpObject);
    }
    private boolean authenticationDigestRequired(HttpRequest request) {//,String cln_addr
        boolean ret=true;
        if (authenticated.get()) {
            ret=false;
        }


        String authHeader1 = request.headers().get(HttpHeaderNames.AUTHORIZATION); //"Authorization"

        //String method = request.getMethod().name();
        String method = request.method().name();

        if(!request.headers().contains(HttpHeaderNames.AUTHORIZATION)){
            String nonce = calculateNonce();
            writeAuthenticationRequired("auth",commonProxy.get().getRealm(),nonce,null);
            return true;
        }
        if (!authHeader1.startsWith("Digest")) {
            String nonce = calculateNonce();
            writeAuthenticationRequired("auth",commonProxy.get().getRealm(),nonce,null);
            return true;
        }
        HashMap<String, String> headerValues = parseHeader(authHeader1);
        String username = headerValues.get("username");
        LOG.trace("username:"+username);


        if(ret==true){
           String clientResponse = headerValues.get("response");
           String qop            = headerValues.get("qop");      LOG.trace("qop:"+qop);
           String nonce          = headerValues.get("nonce");    LOG.trace("nonce:"+nonce);
           String reqURI         = headerValues.get("uri");      LOG.trace("uri:"+reqURI);
           String ha2;
          
           if (!isBlank(qop) && qop.equals("auth-int")) {
               String requestBody="";
               String entityBodyMd5 = DigestUtils.md5Hex(requestBody); 
               ha2 = DigestUtils.md5Hex(method + ":" + reqURI + ":" + entityBodyMd5);
           } else {
               ha2 = DigestUtils.md5Hex(method + ":" + reqURI);
           }
           String pre_serverResponse;
          
           if (isBlank(qop)) {
               pre_serverResponse=":" + nonce + ":" + ha2;
               LOG.trace("qop is blank");
          
           } 
           else {
               //String domain      = headerValues.get("realm");     //LOG.trace("domain(realm):"      +domain);
               String nonceCount  = headerValues.get("nc");        //LOG.trace("nonceCount(nc):"     +nonceCount);
               String clientNonce = headerValues.get("cnonce");    //LOG.trace("clientNonce(cnonce):"+clientNonce);
               pre_serverResponse = ":" + nonce + ":" + nonceCount + ":" + clientNonce + ":" + qop + ":" + ha2;
           }
          
           if(!commonProxy.get().authenticate(username,pre_serverResponse,clientResponse)){
               LOG.debug("no authorization! for realm:"+commonProxy.get().getRealm()+" u:"+username);
               writeAuthenticationRequired(method,commonProxy.get().getRealm(),nonce,null);
               return true;
           }
           ret=false;
           authenticated.set(true);
        }

        //String uri = request.getUri(); LOG.trace("URL:"+uri);
        //username=commonProxy.get().getFullUserName(username);

        //host_addr_port = commonProxy.get().getHosts().getHostPort(username);
        //request.headers().add("X-Forward-User",username); 
        //request.headers().remove(Names.HOST);
        //request.headers().add(Names.HOST,host_addr_port);
        //request.headers().remove(HttpHeaders.Names.AUTHORIZATION);  //need for broker/**/
        //channel_info.setUser(username);

        //LOG.info("client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" URL:"+uri + " for user:"+username);

        return ret;//flase
    }

    private boolean authenticationBasicRequired(HttpRequest request) { //,String cln_addr
        boolean ret=true;
        if (authenticated.get()) {
            LOG.debug("client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" username:"+username+" is authenticated");
            ret=false;
        }

        if (!request.headers().contains(HttpHeaderNames.AUTHORIZATION)) {
            writeAuthenticationRequired("",commonProxy.get().getRealm(),"",null);
            return true;
        }

        List<String> values          = request.headers().getAll(HttpHeaderNames.AUTHORIZATION);
        String       fullValue       = values.iterator().next();
        String       value           = StringUtils.substringAfter(fullValue, "Basic ").trim();
        //byte[]       decodedValue    = BaseEncoding.base64().decode(value);
        byte[]       decodedValue    = _Base64.base64ToByteArray(value);
        String       decodedString   = new String(decodedValue, Charset.forName("UTF-8"));
        String       username        = StringUtils.substringBefore(decodedString, ":");
        String       password        = StringUtils.substringAfter(decodedString, ":");

        username=commonProxy.get().getFullUserName(username);
        if(ret==true){
           if (!commonProxy.get().authenticate(username, password)) {
               LOG.warn("no authorization! for realm:"+commonProxy.get().getRealm()+" u:"+username+" p:"+password);
               writeAuthenticationRequired("",commonProxy.get().getRealm(),"",null);
               return true;
           }
           LOG.debug("Got authorization!"+" u:"+username);
           ret=false;
           authenticated.set(true);
        }

        //String uri = request.getUri(); LOG.trace("URL:"+uri);
        //username=commonProxy.get().getFullUserName(username);
        //host_addr_port = commonProxy.get().getHosts().getHostPort(username);
        //request.headers().add("X-Forward-User",username); 
        //request.headers().remove(Names.HOST);
        //request.headers().add(Names.HOST,host_addr_port);
        //request.headers().remove(Names.AUTHORIZATION);
        //channel_info.setUser(username);

        //LOG.info("client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" URL:"+uri + " for user:"+username);

        return false;
    }
    private boolean authenticationNegotiateRequired(HttpRequest request) {//,String cln_addr
        String  principal="unknown_principal";
        boolean ret=true;
        String  realm=commonProxy.get().getRealm();//"vip.cbr.ru";/**/

        if (authenticated.get()) {
            LOG.debug("AuthenticationNegotiateNOTRequired client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" username:"+username+" is authenticated");
            ret=false;
        }

        if (!request.headers().contains(HttpHeaderNames.AUTHORIZATION)) {
            LOG.trace("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" authorization field is null");
            writeAuthenticationRequired("",realm,"",null);
            return true;
        }

        List<String> values        = request.headers().getAll(HttpHeaderNames.AUTHORIZATION);
        String       authorization = values.iterator().next();

        if(authorization == null) {
           LOG.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" authorization header is empty");
           writeAuthenticationRequired("",realm,"",null);
           return true;
        }
                                     
        if(ret==true) {

            LOG.trace("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" authorization:"+authorization);

            if(authorization.startsWith("Negotiate")) {
               String sub_auth=authorization.substring(10);
               //-----------------------------------------------------------------------------------------
               {
               //-----------------------------------------------------------------------------------------
               LOG.trace("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" client sent to server negotiate token:"+sub_auth);

               //LOG.trace("Authentication:"+sub_auth);
       
               byte[] _gss = Base64.decode(sub_auth);
               if(0 == _gss.length) {
                  LOG.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" GSS data was NULL.");
                  writeAuthenticationRequired("",realm,"",null);
                  return true;
               }

               //try{ HexDump.dump(_gss); }catch(Exception ex111){}

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
                     LOG.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" GSS data begin with :"+hdr);
                     writeAuthenticationRequired("",realm,"",null);
                     return true;
                  }
               }
               /*
               try{      
                   DerValue d=new DerValue(_gss);
                   LOG.info("DerValue:"+d.toString());
               }
               catch(Exception ex112){ 
                     LOG.error(" DerValue ex:"+ex112);
               }
               */
               byte[]        token2    = null;
               //byte[]        token3    = null;
               GSSContext    context   = null;
               //GSSCredential delegCred = null;
               try {
                   //-----------------------------------------------------------------------------------------------
                   LOCK.lock();
                   try {
                       LOG.trace("client_ip:"+channel_info.getSrc()+" crete context");
                       context = MANAGER.createContext(serverCredentials);
                   }
                   catch(Exception ex){
                       String msg=ex.toString();
                       Except ex1=new Except(msg,ex);
                       LOG.error("client_ip:"+channel_info.getSrc()+" error createContext ex:"+ex1);
                       writeAuthenticationRequired("",realm,"",null);
                       return true;
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
                       LOG.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error acceptSecContext ex:"+ex1);
                       writeAuthenticationRequired("",realm,"",null);
                       return true;
                   }
                   finally {
                       LOCK.unlock();
                   }
                   //-----------------------------------------------------------------------------------------------
                   try {
                       if (!context.isEstablished()) {
                            LOG.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" context not established");
                            writeAuthenticationRequired("",realm,"",null);
                            return true;
                       }
                       else{
                            try {
                                principal = context.getSrcName().toString();
                            }
                            catch(Exception ex){
                                 Except ex1=new Except(ex.toString(),ex);
                                 LOG.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error context.getSrcName().toString() ex:"+ex1);
                                 writeAuthenticationRequired("",realm,"",null);
                                 return true;
                            }
                            finally {
                              //SpnegoAuthenticator.LOCK.unlock();
                            }
                       }
       
                       LOG.debug("client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" principal:" + principal);
                       //------------------------------------------------------------------------------
                       
                       if(token2!=null){
                          String _token2=Base64.encode(token2);
                          LOG.trace("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" context.acceptSecContext(_gss, 0, _gss.length) return:" + _token2);
                       }
                       /*
                       else LOG.trace("client_ip:"+cln_addr+" context.acceptSecContext(_gss, 0, _gss.length) return:null");
       
                       if(token3!=null){
                          String _token3=Base64.encode(token3);
                          LOG.trace("client_ip:"+cln_addr+" context.export() return:" + token3);
                       }
                       else LOG.trace("client_ip:"+cln_addr+" context.export() return:null");
                       */
                       //------------------------------------------------------------------------------
                   }
                   catch(Exception ex){
                       String msg=ex.toString();
                       Except ex1=new Except(msg,ex);
                       LOG.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error get Context ex:"+ex1);
                       writeAuthenticationRequired("",realm,"",null);
                       return true;
                   }
                   finally {
       
                       //SpnegoAuthenticator.LOCK.unlock();
                   }
              
               } 
               finally {
                   if (null != context) {
                       //SpnegoAuthenticator.LOCK.lock();
                       try {
                           LOG.info("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" context dispose");
                           context.dispose();
                       }
                       catch(Exception ex){
                             Except ex1=new Except(ex.toString(),ex);
                             LOG.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error context.dispose() ex:"+ex1);
                             writeAuthenticationRequired("",realm,"",null);
                       }
                       finally {
                           //SpnegoAuthenticator.LOCK.unlock();
                       }
                   }
               }
               //-----------------------------------------------------------------------------------------
               }
               //-----------------------------------------------------------------------------------------
               ret=false;
               authenticated.set(true);

               username=commonProxy.get().getFullUserName(principal);

               LOG.trace("Authorization Negotiate :"+username+" ret:"+ret+"  !!!!!!!!!!!!");
            }
            else
            if(authorization.startsWith("Basic")) {
               LOG.info("Authorization Negotiate to Basic !!!");
               return authenticationBasicRequired(request);//,cln_addr
            }
        }

        //String uri = request.getUri(); 
        //

        //host_addr_port = commonProxy.get().getHosts().getHostPort(username);
        //request.headers().add("X-Forward-User",username); 
        //request.headers().remove(Names.HOST);
        //request.headers().add(Names.HOST,host_addr_port);
        //request.headers().remove(Names.AUTHORIZATION);
        //channel_info.setUser(username);

        //LOG.info("client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" URL:"+uri + " for user:"+username);

        return false;
    }
    
    @Override
    protected boolean authenticationRequired(HttpRequest request) {
              boolean ret=true;

              String host=request.headers().get(HttpHeaderNames.HOST);if(host==null)host="";
              //String url = request.getUri(); 
              String url = request.uri(); 
              request.headers().add("X-OLD-Host",host); 

              if(channel_info==null)channel_info=commonProxy.get().getChannel().get(channel);
              if(channel_info==null){
                 channel_info=commonProxy.get().getChannel().create();
                 channel_info.setChannel(channel);
                 channel_info.setUser(username);
              }

              request.headers().add("X-Forwarded-For",channel_info.getSrcHost()+", "+channel_info.getDstHost()); //X-Forwarded-For: 203.0.113.195, 70.41.3.18, 150.172.238.178
              //---------------------------------------------------------------------------------------------------------
              {
                 String u=commonProxy.get().getGuest().getUser4IP(channel_info.getSrcHost());
                 
                 if(u!=null){
                    is_auth_disable=true;
                    authenticated.set(true);
                    ret=false;
                    if("".equals(username))username=u;
                    LOG.trace("getUser4IP -> client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" URL:"+url + " for user:"+username);
                 }
                 else{
                    u=commonProxy.get().getGuest().getUser4URL(url);
                    
                    if(u!=null){
                       is_auth_disable=true;
                       ret=false;
                       if("".equals(username))username=u;
                       LOG.trace("getUser4URL -> client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" URL:"+url + " for user:"+username);
                    }
                    else {
                       LOG.trace("is_auth_disable=false -> client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" URL:"+url + " for user:"+username);
                       is_auth_disable=false;
                       ret=true;
                    }
                 }
              }

              LOG.trace("authenticated.get()="+authenticated.get() +" is_auth_disable="+is_auth_disable);

              if(authenticated.get()==true || is_auth_disable==true) ret=false;

              if(authenticated.get()==false && is_auth_disable==false) {
                 //LOG.trace("authenticated.get()==false && is_auth_disable==false");
                 //---------------------------------------------------------------------------------------------------------
                 if(commonProxy.get().getTypeAuthenticateClients()==0){
                    ret=false;  
                    //ret=authenticationBasicRequired (request,cln_addr);     
                 }
                 else
                 if(commonProxy.get().getTypeAuthenticateClients()==1){
                    ret=authenticationBasicRequired (request);  
                 }
                 else
                 if(commonProxy.get().getTypeAuthenticateClients()==2){
                    ret=authenticationDigestRequired(request); 
                 }
                 else
                 if(commonProxy.get().getTypeAuthenticateClients()==3){
                    ret=authenticationNegotiateRequired(request); 
                    //LOG.trace("authenticationNegotiateRequired(request) return:"+ret);
                 }
                 //---------------------------------------------------------------------------------------------------------
              }

              LOG.trace("authenticationRequired:"+ret +" client_ip:"+channel_info.getSrc());
              //else
              if(ret==false){
                 //ret=false;
                 authenticated.set(true);             

                 channel_info.setUser(username);

                 //LOG.warn("set username:"+username+" for addr:"+channel_info.getSrcHost()+" for url:"+url);
                 if("".equals(username)==false)request.headers().add("X-Forward-User",username); 

                 if(username!=null)host_addr_port = commonProxy.get().getHosts().getHostPort(username);/**/
                 else              host_addr_port = commonProxy.get().getHosts().getDefaultHostPort(); /**/

                 request.headers().remove(HttpHeaderNames.HOST);
                 request.headers().add(HttpHeaderNames.HOST,host_addr_port);
                 if(is_auth_disable==false)request.headers().remove(HttpHeaderNames.AUTHORIZATION);

                 LOG.info("client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" URL:"+url + " for user:"+username+" Ok");

              }

        return ret;
    }
    
    protected void writeAuthenticationRequired(String authMethod,String realm,String nonce,HttpHeaders hh) {
              HttpResponseStatus response_status=HttpResponseStatus.UNAUTHORIZED;
              String             header;
              String             type;


              if(commonProxy.get().getTypeAuthenticateClients()==1){
                 response_status=HttpResponseStatus.UNAUTHORIZED;
                 header = "Basic realm=\"" + (realm == null ? "Restricted Files" : realm) + "\"";
                 type   = "Basic";
              }
              else
              if(commonProxy.get().getTypeAuthenticateClients()==2){
                 response_status=HttpResponseStatus.UNAUTHORIZED;
                 header = "Digest realm=" + realm + ", ";
                 if (!isBlank(authMethod)) {
                     header += "qop=" + authMethod + ", ";
                 }
                 header += "nonce=" + nonce + ", ";
                 header += "opaque=" + getOpaque(realm, nonce) + ", algoritm=\"MD5\", state=\"FALSE\"";
                 type   = "Digest";
              }
              else
              if(commonProxy.get().getTypeAuthenticateClients()==3){
                 response_status=HttpResponseStatus.UNAUTHORIZED;
                 type    = "Kerberos";
                 //header  = "Negotiate,Basic realm=\""+realm+"\"";
                 header  = "Negotiate";
                 //response_status=HttpResponseStatus.VARIANT_ALSO_NEGOTIATES;
              }
              else                        
                      return;
              //-----------------------------------------------------------------------------------------------------------------------
              String body = "<!DOCTYPE HTML \"-//IETF//DTD HTML 2.0//EN\">\n"
                      + "<html><head>\n"
                      + "<title>Authentication Required</title>\n"
                      + "</head><body>\n"
                      + "<h1>"+type+" authentication Required</h1>\n"
                      + "<p>This server could not verify that you\n"
                      + "are authorized to access the document\n"
                      + "requested.  Either you supplied the wrong\n"
                      + "credentials (e.g., bad password), or your\n"
                      + "browser doesn't understand how to supply\n"
                      + "the credentials required.</p>\n" +response_status+ "</body></html>\n";

              FullHttpResponse response = ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1,response_status, body);
              if(hh!=null){
                 List<String> l=hh.getAll("Set-Cookie");

                 response.headers().remove("Set-Cookie");
                 for(int i=0;i<l.size();i++){
                     LOG.trace("i:"+i+" username:"+username+" for client_ip:"+channel_info.getSrc()+" server_ip:"+channel_info.getDst()+" cookie:"+l.get(i));
                     response.headers().add("Set-Cookie",l.get(i));
                 }
                 LOG.info("for return 401 set cookie username:"+username+" for client_ip:"+channel_info.getSrc()+" server_ip:"+channel_info.getDst());

              }

              if(true){/**/
                 for(int i=0;i<commonProxy.get().getCookie().size();i++){
                     LOG.trace("Set-Cookie:"+commonProxy.get().getCookie().get(i));
                     response.headers().add("Set-Cookie",commonProxy.get().getCookie().get(i));
                 }
                 //response.headers().add("Set-Cookie","WebAccessBean_sessionTicket=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/");
                 //response.headers().add("Set-Cookie","WebAccessBean_sessionTicket_STENDSOK=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/");
              }

              //HttpHeaders.setDate(response, new Date());
              response.headers().set("Date", new Date());
              response.headers().set("WWW-Authenticate", header);
              write(response);

    }
    private String calculateNonce() {
        Date             d         = new Date();
        SimpleDateFormat f         = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
        String           fmtDate   = f.format(d);
        Random           rand      = new Random(100000);
        Integer          randomInt = rand.nextInt();

        return DigestUtils.md5Hex(fmtDate + randomInt.toString());
    }

    private String getOpaque(String domain, String nonce) {
        return DigestUtils.md5Hex(domain + nonce);
    }

    private static boolean isBlank(String cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    protected HashMap<String, String> parseHeader(String headerString) {
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

    private static Oid getOid() {
        Oid oid = null;
        try {
            oid = new Oid("1.3.6.1.5.5.2");
        } catch (GSSException gsse) {
            LOG.error("Unable to create OID 1.3.6.1.5.5.2 !", gsse);
        }
        return oid;
    }

}


