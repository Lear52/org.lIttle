package org.little.mailWeb;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.web.webThread;
/**
 * @author av
 *  
 */
public class alarmMngr extends webThread{
       private static final Logger logger = LoggerFactory.getLogger(alarmMngr.class);
       private commonArh    cfg;

       public alarmMngr(){
              cfg = new commonArh();
              logger.info("create webMngr");
       } 

       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              
              String xpath =this.getServletContext().getRealPath("");
              String _xpath=getParametr("config");
              xpath+=_xpath;
              boolean ret=cfg.loadCFG(xpath);
              cfg.init();
              if(ret==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }

              logger.info("START LITTLE.IMAPWEB(ALARM) config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");

              //runner.add(cln);
              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }
       @Override
       public void destroy() {
               super.destroy();
               logger.info("STOP LITTLE.IMAPWEB(ALARM) "+Version.getVer()+"("+Version.getDate()+")");
       }


       @Override
       public String getServletInfo() {
              return "Alarm key data";
       }
       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{}

}

