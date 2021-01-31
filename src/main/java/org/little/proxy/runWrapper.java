package org.little.proxy;

import java.net.InetSocketAddress;

import org.little.proxy.Null.NullProxyServer;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.wrapper.iWrapper;
import org.littleshoot.proxy.DefaultHostResolver;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.AuthClientToProxyConnection;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;



public class runWrapper implements iWrapper{

       private static final Logger      LOG = LoggerFactory.getLogger(runWrapper.class);
      
       private NullProxyServer          server_null_proxy;
       private HttpProxyServerBootstrap bootstrap_http_proxy;
       private HttpProxyServer          server_http_proxy;
       private boolean                  m_mainComplete;
      
       public runWrapper(){
              clear();  
       }

       @Override
       public void clear(){
                 bootstrap_http_proxy  =null;  
                 server_http_proxy=null;
                 server_null_proxy     =null;
                 m_mainComplete=true;
       }
      
       @Override
       public void run(){
           synchronized(this){
               m_mainComplete = true;
               notifyAll();
           }
         
           while(true){
               synchronized(this){
                   if(!m_mainComplete){
                       LOG.info("main break");
                       break;
                   }
                   notifyAll();
               }
               try{Thread.sleep(1000L); } catch (InterruptedException e){LOG.trace("ex:" + e);}
           }
       }
        
       @Override
       public int start(String args[]){

              LOG.info("START LITTLE.PROXY "+commonProxy.ver());
              commonProxy.get().init();
              commonProxy.get().initMBean();
              //---------------------------------------------------------------------------------
              if(commonProxy.get().getType()==commonProxy.PROXY_TYPE_HTTP){
                 int               port;             
                 boolean           transparent;
             
                 Thread mainThread = new Thread(this, "wrapperMainProxy");
                 transparent = AuthClientToProxyConnection.isTransparent();
                 port        = AuthClientToProxyConnection.getPort();
             
                 InetSocketAddress server_address;
                 InetSocketAddress client_address;
                 if("*".equals(commonProxy.get().getCfgServer().getLocalServerBind())){
                     server_address=new InetSocketAddress("0.0.0.0", port);
                     LOG.info("HttpProxyServer bind *");
                 }
                 else{
                     server_address=new InetSocketAddress(commonProxy.get().getCfgServer().getLocalServerBind(), port);
                     LOG.info("HttpProxyServer bind "+server_address);
                 }
                 if("*".equals(commonProxy.get().getCfgServer().getLocalClientBind())){
                     client_address=null;
                     LOG.info("HttpProxyClient bind *");
                 }
                 else{
                     client_address=new InetSocketAddress(commonProxy.get().getCfgServer().getLocalClientBind(), 0);
                     LOG.info("HttpProxyClient bind "+client_address);
                 }
                 bootstrap_http_proxy = DefaultHttpProxyServer.bootstrap()
                            .withTransparent(transparent)
                            .withAllowLocalOnly(false)
                            .withAllowRequestToOriginServer(true)
                            .withServerResolver(new DefaultHostResolver())
                            .withAuthenticateSslClients(true) 
                            .withNetworkInterface(client_address)
                            .withAddress(server_address)
                            //.withPort(port)
                            //.withThrottling(readThrottleBytesPerSecond,writeThrottleBytesPerSecond)
                            //.plusActivityTracker(new ActivityTrackerImpl())/**/
                            //.withFiltersSource(new BasicHttpFiltersSource())
             
                 ;
                 LOG.trace("create HttpProxyServer");
                
                 server_http_proxy=bootstrap_http_proxy.start();
                
                 LOG.trace("start  HttpProxyServer");
                
                 mainThread.start();
              }                                              
              else                                               
              if(commonProxy.get().getType()==commonProxy.PROXY_TYPE_NULL){            
                 server_null_proxy   = new NullProxyServer();             
                 LOG.trace("create NullProxyMain");
                 server_null_proxy.init();
                 server_null_proxy.start();
                 LOG.trace("start NullProxyMain");
              }
              //---------------------------------------------------------------------------------
              new Thread(this).start();
              LOG.trace("end start(String args[])");
             
              return 0;
       }

       @Override
       public int stop(int exitCode){
              LOG.info("STOP LITTLEPROXY "+commonProxy.ver());
              m_mainComplete = false;
      
              if(server_http_proxy!=null)server_http_proxy.stop();
              if(bootstrap_http_proxy  !=null)bootstrap_http_proxy.stop();
              if(server_null_proxy     !=null)server_null_proxy.stop();
              return exitCode;
       }

       @Override
       public int restart() {
              if(commonProxy.get().getType()==commonProxy.PROXY_TYPE_HTTP){
             
              }
              else                                               
              if(commonProxy.get().getType()==commonProxy.PROXY_TYPE_NULL){
                  if(server_null_proxy!=null)server_null_proxy.stop();
                     server_null_proxy   = new NullProxyServer();             
                  LOG.trace("create NullProxyMain");
                  server_null_proxy.init();
                  server_null_proxy.start();
                  LOG.trace("start NullProxyMain");
              }        
              return 0;
       }

       public static void main(String args[]){
             
              commonProxy.get().preinit();
             
              String xpath=iWrapper.getFileanme(args);
              if(xpath==null)return;
             
              if(commonProxy.get().loadCFG(xpath)==false){
                 LOG.error("error read config file:"+xpath);
                 return;
              }
             
              LOG.trace("START wrapper");
             
              runWrapper w=new runWrapper();
              w.start(args);
              w.run();
              w.stop(0);
             
              LOG.trace("EXIT wrapper");
       }
}

