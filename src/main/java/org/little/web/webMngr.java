package org.little.web;

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
/**
 * @author av
 *  
 */
public class webMngr extends webRun{
       /**
	 * 
	 */
	private static final long serialVersionUID = 2810192641705865280L;
	private static final Logger logger = LoggerFactory.getLogger(webMngr.class);
       private String xpath;
       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              
              
              xpath=this.getServletContext().getRealPath("")+getParametr("config");


              logger.info("START LITTLE.AUTH config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");

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
              if(path.startsWith("/auth/")){
                  //String    _u = (String) request.getParameter("u");
                  //String    _p1 = (String) request.getParameter("p1");
                  //String    _p2 = (String) request.getParameter("p2");


                  
                  return;
              }
  
              page = "index.html";

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

