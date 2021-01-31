package org.little.imap;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.wrapper.iWrapper;

public class runWrapper implements iWrapper{

       private static final Logger LOG = LoggerFactory.getLogger(runWrapper.class);
      
       private ImapServer          server;
       private boolean             m_mainComplete;
      
       public runWrapper(){
              clear();  
       }
      
       @Override
       public void clear(){
              server     =null;
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
      
              LOG.info("START LITTLE.IMAP "+commonIMAP.ver());
              commonIMAP.get().init();
              commonIMAP.get().initMBean();
              //---------------------------------------------------------------------------------
              server   = new ImapServer(commonIMAP.get().getCfgServer().getPort());             
              LOG.trace("create server");
              server.start();
              
              LOG.trace("start server");
              //---------------------------------------------------------------------------------
              new Thread(this).start();
              LOG.trace("end start(String args[])");
      
              return 0;
       }
      
       @Override
       public int stop(int exitCode){
              LOG.info("STOP LITTLEPROXY "+commonIMAP.ver());
              m_mainComplete = false;
      
              if(server     !=null)server.stop();
              return exitCode;
       }
       @Override
       public int restart() {
              if(server     !=null)server.stop();
              commonIMAP.get().reinit();
              server   = new ImapServer(commonIMAP.get().getCfgServer().getPort());             
              LOG.trace("create imap server port:"+commonIMAP.get().getCfgServer().getPort());
              server.start();
              LOG.trace("start imap server port:"+commonIMAP.get().getCfgServer().getPort());
             
              return 0;
       }
    
       public static void main(String args[]){
             
              commonIMAP.get().preinit();
             
              String xpath=iWrapper.getFileanme(args);
              if(xpath==null)return;
             
              if(commonIMAP.get().loadCFG(xpath)==false){
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

