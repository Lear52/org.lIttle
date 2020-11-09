package org.little.smtp;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.iWrapper;

public class runWrapper implements iWrapper{

    private static final Logger      LOG = LoggerFactory.getLogger(runWrapper.class);

    private SmtpServer               server;
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

           LOG.info("START LITTLE.PROXY "+commonSMTP.ver());
           commonSMTP.get().init();
           commonSMTP.get().initMBean();
           //---------------------------------------------------------------------------------
           server   = new SmtpServer(commonSMTP.get().getPort());             
           LOG.trace("create server");
           server.start();
           
           LOG.trace("start server");
           //---------------------------------------------------------------------------------
           new Thread(this).start();
           LOG.trace("end start(String args[])");

           return 0;
    }

    public int stop(int exitCode){
           LOG.info("STOP LITTLEPROXY "+commonSMTP.ver());
           m_mainComplete = false;

           if(server     !=null)server.stop();
           return exitCode;
    }

    public static void main(String args[]){
          
           commonSMTP.get().preinit();
          
           String xpath=iWrapper.getFileanme(args);
           if(xpath==null)return;
          
           if(commonSMTP.get().loadCFG(xpath)==false){
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
