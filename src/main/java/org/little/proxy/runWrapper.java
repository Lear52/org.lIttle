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

    private NullProxyServer          server;
    private HttpProxyServerBootstrap bootstrap;
    private HttpProxyServer          proxyserver;
    private boolean                  m_mainComplete;

    public runWrapper(){
              clear();  
    }
    public void clear(){
              bootstrap  =null;  
              proxyserver=null;
              server     =null;
              m_mainComplete=true;
    }

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
        
    public int start(String args[]){

           LOG.info("START LITTLE.PROXY "+commonProxy.ver());
           commonProxy.get().init();
           commonProxy.get().initMBean();
           //---------------------------------------------------------------------------------
           if(commonProxy.get().getType()==1){
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
              bootstrap = DefaultHttpProxyServer.bootstrap()
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
             
              proxyserver=bootstrap.start();
             
              LOG.trace("start  HttpProxyServer");
             
              mainThread.start();
           }                                              
           else                                               
           if(commonProxy.get().getType()==0){            
              server   = new NullProxyServer();             
              LOG.trace("create NullProxyMain");
              server.init();
              server.start();
              LOG.trace("start NullProxyMain");
           }
           //---------------------------------------------------------------------------------
           new Thread(this).start();
           LOG.trace("end start(String args[])");

           return 0;
    }

    public int stop(int exitCode){
           LOG.info("STOP LITTLEPROXY "+commonProxy.ver());
           m_mainComplete = false;

           if(proxyserver!=null)proxyserver.stop();
           if(bootstrap  !=null)bootstrap.stop();
           if(server     !=null)server.stop();
           return exitCode;
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

