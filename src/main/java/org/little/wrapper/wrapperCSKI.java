package org.little.wrapper;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.iWrapper;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;


public class wrapperCSKI implements WrapperListener{

    private static final Logger      logger = LoggerFactory.getLogger(wrapperCSKI.class);

    private org.little.http.runWrapper   serverHTTP;
    private org.little.smtp.runWrapper   serverSMTP;
    private org.little.imap.runWrapper   serverIMAP;


    protected      wrapperCSKI(String args[]){
                   serverHTTP=new org.little.http.runWrapper();
                   logger.trace("create org.little.http");
                   serverSMTP=new org.little.smtp.runWrapper();
                   logger.trace("create org.little.smtp");
                   serverIMAP=new org.little.imap.runWrapper();
                   logger.trace("create org.little.imap");
                   WrapperManager.start(this, args);
    }

    public Integer start(String args[]){ 
           serverHTTP.start(args);
           logger.trace("start org.little.http");
           serverSMTP.start(args);
           logger.trace("start org.little.smtp");
           serverIMAP.start(args);
           logger.trace("start org.little.imap");
           return null; 
    } 
    public int     stop(int exitCode){
           int ret;
           ret=serverHTTP.stop(exitCode);
           logger.trace("stop org.little.http");
           ret=serverSMTP.stop(exitCode);
           logger.trace("stop org.little.smtp");
           ret=serverIMAP.stop(exitCode);
           logger.trace("stop org.little.imap");
           return ret;
    }

    public void controlEvent(int event){
         
           logger.trace("Event CSKI:"+event);

           if(event == 202 && WrapperManager.isLaunchedAsService()){
               if(WrapperManager.isDebugEnabled())logger.trace("wrapper CSKI: controlEvent(" + event + ") Ignored");
           } 
           else {
               if(WrapperManager.isDebugEnabled())logger.trace("wrapper CSKI: controlEvent(" + event + ") Stopping");
               WrapperManager.stop(0);
           }
         
    }    

    public static void main(String args[]){
           logger.trace("start CSKI");

           org.little.http.commonHTTP.get().preinit();
           org.little.smtp.commonSMTP.get().preinit();
           org.little.imap.commonIMAP.get().preinit();

           logger.trace("preinit CSKI");

           String xpath=iWrapper.getFileanme(args);
           if(xpath==null)return;

           if(org.little.http.commonHTTP.get().loadCFG(xpath)==false){
              logger.error("error read http config file:"+xpath);
              return;
           }
           if(org.little.smtp.commonSMTP.get().loadCFG(xpath)==false){
              logger.error("error read smtp config file:"+xpath);
              return;
           }
           if(org.little.imap.commonIMAP.get().loadCFG(xpath)==false){
              logger.error("error read imap config file:"+xpath);
              return;
           }
           logger.trace("load config file:"+xpath);

           logger.trace("START wrapper CSKI");
           new wrapperCSKI(args);
           logger.trace("EXIT wrapper CSKI");
    }

}
