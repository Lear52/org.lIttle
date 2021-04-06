package org.little.mailWeb;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.web.webThread;
/**
 * @author av
 *  
 */
public class webMngr extends webThread{
       private static final Logger logger = LoggerFactory.getLogger(webMngr.class);
       private static final long serialVersionUID = -3616757490430537836L;
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
              return "Show state queue";
       }
       private void doGetList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
           response.setContentType("application/json");
           response.setContentType("text/html; charset=UTF-8");
           
           JSONObject  root=client.getLog().loadJSON();
           logger.trace("mngr.getStatAll() :"+root);
           root.write(response.getWriter());

           logger.trace("webMngr.doGetList()");
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              //String    cmd = (String) request.getParameter("c");
              //if(cmd == null){cmd="";}
              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webAddr.doRun() path:"+path);
              if(path.startsWith("/list")){
                  doGetList(request,response);
                  return;
              }
  
              page ="/index.jsp";

              //-----------------------------------------------------------------------------------------
              if(page!=null)
              try {
                   RequestDispatcher d         = null;
                   ServletContext servlet_cntx = null;
                   ServletConfig servlet_cfg   = null;
              
                   servlet_cfg  = getServletConfig();
              
                   servlet_cntx = servlet_cfg.getServletContext();
              
                   d = servlet_cntx.getRequestDispatcher(page);
              
                   d.forward(request, response);
              } catch (Exception ex) {
                      logger.error("error forward to " + page +" Exception:"+ex);
              }
                
       }

}

