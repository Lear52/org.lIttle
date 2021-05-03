package org.little.monitor;

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
import org.little.web.webRun;
/**
 * @author av
 *  
 */
public class webMngr extends webRun{
       private static final Logger logger = LoggerFactory.getLogger(webMngr.class);
       private static final long serialVersionUID = -3616757490430537836L;
       //private ImapClient client;

       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              
              //client=new ImapClient();
              
              String xpath=this.getServletContext().getRealPath("");

              String _xpath=getParametr("config");
              xpath+=_xpath;

              //if(client.loadCFG(xpath)==false){
             //    logger.error("error read config file:"+xpath);
             //    return;
             // }

              logger.info("START LITTLE.IMAPWEB config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");

              //client.setDelay(client.getTimeout());

              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }

       @Override
       public String getServletInfo() {
              return "Show key data";
       }
       private void doGetList(HttpServletRequest request, HttpServletResponse response,String _type) throws ServletException, IOException{
               logger.trace("begin doGetList type:"+_type);
               
               JSONObject  root=new JSONObject();
               //JSONObject  root=client.getLog().loadJSON(_type);
               
               logger.trace("getStatAll() :"+root);
               
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");
               
               root.write(response.getWriter());
               
               logger.trace("end doGetList type:"+_type);
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webAddr.doRun() path:"+path);
              if(path.startsWith("/list")){
                  String    _type = (String) request.getParameter("type");
                  doGetList(request,response,_type);
                  return;
              }
  
              ///if(page==null)page = client.getDefPage();

              //-----------------------------------------------------------------------------------------
              //if(page!=null)
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

