package org.little.wrapper;

import org.little.proxy.util.commonProxy;
import org.little.proxy.util.runWrapper;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.iWrapper;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;


public class wrapper implements WrapperListener{

    private static final Logger      LOG = LoggerFactory.getLogger(wrapper.class);

    private runWrapper               server;


    protected      wrapper(String args[]){
                   server=new runWrapper();
                   WrapperManager.start(this, args);
    }

    public Integer start(String args[]){ server.start(args);return null; } 
    public int     stop(int exitCode){return server.stop(exitCode);}

    public void controlEvent(int event){
         
           LOG.trace("Event:"+event);

           if(event == 202 && WrapperManager.isLaunchedAsService()){
               if(WrapperManager.isDebugEnabled())System.out.println("wrapper: controlEvent(" + event + ") Ignored");
           } 
           else {
               if(WrapperManager.isDebugEnabled())System.out.println("wrapper: controlEvent(" + event + ") Stopping");
               WrapperManager.stop(0);
           }
         
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
           new wrapper(args);
           LOG.trace("EXIT wrapper");
    }

}
