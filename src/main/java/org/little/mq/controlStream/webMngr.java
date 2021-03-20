package org.little.mq.controlStream;

import java.io.IOException;
import java.util.ArrayList;

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
import org.little.util.run.task;
import org.little.web.webThread;
/**
 * @author av
 *  
 */
public class webMngr extends webThread{
       final private static int    CLASS_ID  =605;
       public  static int    getClassId(){return CLASS_ID;}
       private static final long serialVersionUID = 19690401L+CLASS_ID;
       private static final Logger logger = LoggerFactory.getLogger(webMngr.class);

       private fc_mngr mngr;

       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              mngr=new fc_mngr();

              String xpath=this.getServletContext().getRealPath("");
              //xpath+=File.separator;
              xpath+="littleproxy_mq.xml";

              if(mngr.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.CONTROLSTREAM "+Version.getVer()+"("+Version.getDate()+")");
              mngr.init();
              ArrayList<task> _task=mngr.getListTask();
              for(int i=0;i<_task.size();i++){
                  task t=_task.get(i);
                  t.setDelay(10);
                  runner.add(t);
              }


              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }

       @Override
       public String getServletInfo() {
              return "Show state queue";
       }
       /**
          отдает JSON запрос 
        */
       private void doGetList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               JSONObject  root=mngr.getStatAll();
               logger.trace("mngr.getStatAll() :"+root);
               root.write(response.getWriter());

               logger.trace("webMngr.doGetList()");
       }
       private void doSetState(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               JSONObject  root=mngr.getStat();
               logger.trace("mngr.getStat() :"+root);
               root.write(response.getWriter());

               logger.trace("webMngr.doSetState()");
       }
       private void doSetFlag(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               String group_id=(String) request.getParameter("group");
               String flow_id =(String) request.getParameter("flow");
               String _is_flag =(String) request.getParameter("state");
               boolean is_flag=false;
               try{ is_flag=Boolean.parseBoolean(_is_flag);}catch(Exception e){is_flag=false;}

               logger.trace("mngr.SetFlag(group:"+group_id+",flow:"+flow_id+",flag:"+is_flag+")");

               JSONObject  root=mngr.setFlag(group_id,flow_id,is_flag);

               logger.trace("mngr.SetFlag(group:"+group_id+",flow:"+flow_id+",flag:"+is_flag+") return:"+root);

               root.write(response.getWriter());

               logger.trace("webMngr.doSetFlag()");
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              //String    cmd = (String) request.getParameter("c");
              //if(cmd == null){cmd="";}
              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webAddr.doRun() path:"+path);
                
              /**
                 JSON запрос: 
               */
              if(path.startsWith("/list")){
                 doGetList(request,response);
                 return;
              }
              /**
                 JSON запрос:
               */
              else 
              if(path.startsWith("/state")){
                 doSetState(request,response);
                 return;
              }  
              /**
                 JSON запрос:
               */
              else 
              if(path.startsWith("/cntrl")){
                 doSetFlag(request,response);
                 return;
              }  
              else{
                 page ="/index.jsp";
              }

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

