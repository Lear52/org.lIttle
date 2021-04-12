package org.little.http;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.web.webRun;
/**
 * @author av
 *  
 */
public class webMngr extends webRun{
	private static final long serialVersionUID = -8820420158454598488L;
	private static final Logger logger = LoggerFactory.getLogger(webMngr.class);

       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              
              
              String xpath=this.getServletContext().getRealPath("");

              String _xpath=getParametr("config");
              xpath+=_xpath;

              

              logger.info("START LITTLE.HTTP config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");

              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }

       @Override
       public String getServletInfo() {
              return "Show state queue";
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webAddr.doRun() path:"+path);
              if(path.startsWith("/http/")){
                  String    _c = (String) request.getParameter("c");
                  String auth_type = request.getAuthType();
                  String user = request.getUserPrincipal().getName();
                  
                  return;
              }
  
              if(page==null)page = "index.html";

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

