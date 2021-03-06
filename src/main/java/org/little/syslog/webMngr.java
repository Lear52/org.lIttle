package org.little.syslog;

import java.io.IOException;
//import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.syslog.impl.commonSyslog;
import org.little.syslog.impl.printEventMQ;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.web.webRun;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
/**
 * @author av
 *  
 */
public class webMngr extends webRun{
       private static final long serialVersionUID = -8820420158454598488L;
       private static final Logger logger = LoggerFactory.getLogger(webMngr.class);
       private static Server server;

       public webMngr(){
              server=null;
       }

       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              
              
              String xpath=this.getServletContext().getRealPath("");

              String _xpath=getParametr("config");
              xpath+=_xpath;

              if(server==null){
                 if(commonSyslog.loadCFG(xpath)==false){
                    logger.error("error read config file:"+xpath);
                    return;
                 }
                 server=new Server();
                 server.set(commonSyslog.get().getPort());

                 if(commonSyslog.get().isForwardMQ()){
                    printEventMQ _log=new printEventMQ();
                    _log.init(commonSyslog.get().getMQNode());
                    _log.open();
                    server.addPrintEvent(_log);
                 }
                 else{
                     logger.info("START LITTLE.SYSLOG forward mq close");
                 }
                 server.fork();
                 logger.info("START LITTLE.SYSLOG config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");
              }
              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }

       @Override
       public String getServletInfo() {
              return "syslog server";
       }

       @Override
       public void destroy() {
              if(server!=null){
                 server.stop();
              }
              server=null;
              super.destroy();
              logger.info("STOP LITTLE.SYSLOG "+Version.getVer()+"("+Version.getDate()+")");
       }

       private void doGetList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
           logger.trace("begin doGetList");

           JSONObject  root=new JSONObject();
           JSONArray evn_list=new JSONArray();

           response.setContentType("application/json");
           response.setContentType("text/html; charset=UTF-8");
           
           if(server!=null){
              Iterator<SyslogServerEventIF> list;
              list = server.print().getEvent();
              while(list.hasNext()) {
        	   JSONObject  p=new JSONObject();
        	   SyslogServerEventIF evn = list.next();
        	   p.put("date", evn.getDate());
        	   p.put("host", evn.getHost());
        	   p.put("level", evn.getLevel());
        	   p.put("facility", evn.getFacility());
        	   p.put("msg", evn.getMessage());
        	   evn_list.put(p);
              }
              
           }

           root.put("list_msg",evn_list);
           root.put("name","list last message");
           root.write(response.getWriter());

           logger.trace("end doGetList");
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webMngr.doRun() path:"+path);

              if(path.startsWith("/list")){
                 doGetList(request,response);
                 return;
              }
  
              if(page==null)page = commonSyslog.get().getDefPage();
              logger.trace("webMngr.doRun() page:"+page);
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

