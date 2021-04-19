package org.little.mailWeb;

import javax.servlet.ServletException;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.web.webThread;
/**
 * @author av
 *  
 */
public class loadMngr extends webThread{
	private static final long serialVersionUID = -8857423924701221546L;
	private static final Logger logger = LoggerFactory.getLogger(loadMngr.class);
       private ImapClient client;

       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              
              client=new ImapClient();
              
              String xpath=this.getServletContext().getRealPath("");

              String _xpath=getParametr("config");
              xpath+=_xpath;

              if(client.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }

              logger.info("START LITTLE.IMAPWEB config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");

              client.setDelay(client.getTimeout());

              runner.add(client);
              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }

       @Override
       public String getServletInfo() {
              return "Load key data";
       }

}

