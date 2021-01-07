package org.little.http;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.wrapper.iWrapper;

public class runWrapper implements iWrapper{

    private static final Logger      LOG = LoggerFactory.getLogger(runWrapper.class);

    private lHttpServer           server;
    private boolean                  m_mainComplete;

    public runWrapper(){
              clear();  
    }
    public void clear(){
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

           LOG.info("start httpServer(LITTLE.HTTP) "+commonHTTP.ver());
           commonHTTP.get().init();
           commonHTTP.get().initMBean();
           //---------------------------------------------------------------------------------
           server   = new lHttpServer(commonHTTP.get().getCfgServer().getPort());             
           LOG.trace("create httpServer port:"+commonHTTP.get().getCfgServer().getPort());
           server.start();
           
           LOG.trace("start httpServer port:"+commonHTTP.get().getCfgServer().getPort());
           //---------------------------------------------------------------------------------
           new Thread(this).start();
           LOG.trace(" start thread server");

           return 0;
    }

    public int stop(int exitCode){
           LOG.info("STOP httpServer(LITTLE.HTTP) "+commonHTTP.ver());
           m_mainComplete = false;

           if(server     !=null)server.stop();
           return exitCode;
    }

    public static void main(String args[]){
          
           commonHTTP.get().preinit();
          
           String xpath=iWrapper.getFileanme(args);
           if(xpath==null)return;
          
           if(commonHTTP.get().loadCFG(xpath)==false){
              LOG.error("httpServer(LITTLE.HTTP) error read config file:"+xpath);
              return;
           }
          
           LOG.trace("START wrapper httpServer(LITTLE.HTTP)");
          
           runWrapper w=new runWrapper();
           w.start(args);
           w.run();
           w.stop(0);
          
           LOG.trace("EXIT wrapper httpServer(LITTLE.HTTP)");
    }
}

