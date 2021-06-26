package org.little.db.kir;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

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
       /**
	 * 
	 */
	private static final long serialVersionUID = -3163620482303427340L;
	private static final Logger logger = LoggerFactory.getLogger(webMngr.class);
       private commonKIR    cfg;
       //private ImapView client;

       public webMngr(){
              cfg = new commonKIR();
              logger.info("create webMngr");
       } 

       @Override
       public void init() throws ServletException {

              if(cfg==null)return;

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


              logger.info("START LITTLE.KIRWEB(VIEW) config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");
              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
        }
        @Override
        public void destroy() {
               if(cfg!=null)cfg.clear();
               cfg=null;
               super.destroy();
               //client=null;
               logger.info("STOP LITTLE.KIRWEB(VIEW) "+Version.getVer()+"("+Version.getDate()+")");
        }

       @Override
       public String getServletInfo() {
              return "Show KIR data";
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              if(cfg==null)return;

              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webAddr.doRun() path:"+path);

              if(path.startsWith("/stat")){
                  return;
              }
              else
              if(path.startsWith("/load")){
                  return;
              }
  
              if(page==null)page = cfg.getDefPage();

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

