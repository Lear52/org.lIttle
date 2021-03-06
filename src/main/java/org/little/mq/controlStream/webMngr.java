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

              String xpath=this.getServletContext().getRealPath("")+getParametr("config");

              if(mngr.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.CONTROLSTREAM "+Version.getVer()+"("+Version.getDate()+")");

              mngr.init();
              mngr.setDelay(1);

              ArrayList<task> _task=mngr.getListTask();
              runner.add(mngr);
              for(int i=0;i<_task.size();i++){
                  task t=_task.get(i);
                  t.setDelay(mngr.getTimeout());
                  runner.add(t);
              }

              logger.info("RUN LITTLE.CONTROLSTREAM "+Version.getVer()+"("+Version.getDate()+")");

              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }
       @Override
       public void destroy() {
              mngr.KILL();
              ArrayList<task> _task=mngr.getListTask();
              runner.add(mngr);
              for(int i=0;i<_task.size();i++){
                  task t=_task.get(i);
                  t.KILL();
                  runner.del(t);
              }
              _task.clear();

              runner.stop();
              runner=null;
              mngr.close();
              mngr=null;
              super.destroy();
              logger.info("STOP LITTLE.CONTROLSTREAM "+Version.getVer()+"("+Version.getDate()+")");
       }

       @Override
       public String getServletInfo() {
              return "Show state queue";
       }
       /**
          processing JSON request: get all state group 
        */
       private void doGetList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               JSONObject  root=mngr.getStatAll();
               logger.trace("mngr.getStatAll() :"+root);
               root.write(response.getWriter());

               logger.trace("webMngr.doGetList()");
       }
       /**
       processing JSON request: get local group 
       */
       private void doSetState(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               JSONObject  root=mngr.getStat();
               logger.trace("mngr.getStat() :"+root);
               root.write(response.getWriter());

               logger.trace("webMngr.doSetState()");
       }
       /**
         processing JSON request: set control flag  
       */
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
       private void doClearQ(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               String group_id=(String) request.getParameter("group");
               String flow_id =(String) request.getParameter("flow");
               String mngr_id =(String) request.getParameter("mngr");
               String q_id    =(String) request.getParameter("q");

               logger.trace("mngr.ClearQ(group:"+group_id+",flow:"+flow_id+",mngr:"+mngr_id+",q:"+q_id+")");

               JSONObject  root=mngr.ClearQ(group_id,flow_id,mngr_id,q_id);

               logger.trace("mngr.ClearQ(group:"+group_id+",flow:"+flow_id+",mngr:"+mngr_id+",q:"+q_id+") return:"+root);

               mngr.work();/**/

               root.write(response.getWriter());

               logger.trace("mngr.ClearQ()");
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webAddr.doRun() path:"+path);
                
              /**
                 JSON request get all group 
               */
              if(path.startsWith("/list")){
                 doGetList(request,response);
                 return;
              }
              /**
                 JSON request get local group from remote mngr
               */
              else 
              if(path.startsWith("/state")){
                 doSetState(request,response);
                 return;
              }  
              /**
                 JSON request set control flag
               */
              else 
              if(path.startsWith("/cntrl")){
                 doSetFlag(request,response);
                 return;
              }  
              /**
                 JSON request set control flag
               */
              else 
              if(path.startsWith("/clr")){
                 doClearQ(request,response);
                 return;
              }  
              else{
                 page =mngr.getDefaulPage();
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

