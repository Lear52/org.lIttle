package org.little.rcmd;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.rcmd.rsh.rCP;
import org.little.rcmd.rsh.rCP_Remote2Buffer;
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

              
              if(commonAPK.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              
              //rAPK r1=commonAPK.get().getAPK("main");
              //if(r1==null) {        }

              logger.info("START LITTLE.RCMD config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");

              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }

       @Override
       public String getServletInfo() {
              return "Show state queue";
       }
       private void doReceive(HttpServletRequest request, HttpServletResponse response,String req_apk_node,String req_cmd_id,String filename) throws ServletException, IOException{
           String txt_response="";
           byte[] filebuffer=null;
           rAPK apk=commonAPK.get().getAPK(req_apk_node);

           if(apk==null) {
              txt_response="no find node name apk dionis:"+req_apk_node;
              logger.error(txt_response);
           }
           else 
           try{
          	   rCP cmd=new rCP_Remote2Buffer("HTTP",1,filename,"");
          	   boolean ret=apk.runCMD(cmd);
               logger.trace("apk.runCMD("+cmd+") ret:"+ret);
          	   filebuffer=cmd.getBuffer();
               response.setContentType("application/octet-stream");
               response.addHeader("Accept-Ranges","bytes");
               response.setHeader("Content-Type","application/octet-stream");
               response.setHeader("Content-Transfer-Encoding", "Binary");
               response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
               response.setContentLength(filebuffer.length);
               logger.trace("set header");

               response.getOutputStream().write(filebuffer,0,filebuffer.length);
               logger.trace("write buf");
               response.getOutputStream().flush();;

           
           }
           finally{
                  apk.close();  
           }
    	   
    	   
       }
       private void doRunCmd(HttpServletRequest request, HttpServletResponse response,String req_apk_node,String req_cmd_id) throws ServletException, IOException{
           logger.trace("begin doRunCmd req_apk_node:"+req_apk_node+" req_cmd_id:"+req_cmd_id);
           String      txt_response;
           JSONObject  root=new JSONObject();;

           response.setContentType("application/json");
           response.setContentType("text/html; charset=UTF-8");

           rAPK apk=commonAPK.get().getAPK(req_apk_node);

           if(apk==null) {
              txt_response="no find node name apk dionis:"+req_apk_node;
              logger.error(txt_response);
           }
           else 
           try{
                boolean  is_correct=apk.checkCMD(req_cmd_id);
                if(is_correct==false){
                    txt_response="node apk dionis"+req_apk_node+" unknow command:"+req_cmd_id;     
                    logger.error(txt_response);
                }
                else{
                   boolean ret=apk.open();
                   if(ret==false) {
                      txt_response="apk.open return:"+ret;     
                      logger.error(txt_response);
                   }
                   else{
                      String[] arr_res_apk=apk.runCMD(req_cmd_id);
                      if(arr_res_apk==null){
                         txt_response="node apk dionis"+req_apk_node+" return null";     
                         logger.error(txt_response);
                      }
                      else{
                          JSONArray list=new JSONArray();
                          for(int i=0;i<arr_res_apk.length;i++){
                              JSONObject obj;
                              obj=new JSONObject();
                              obj.put("id"   ,i);
                              obj.put("txt" ,arr_res_apk[i]);
                              list.put(obj);
                          }
                          root=new JSONObject();
                          root.put("list",list);
                          root.put("size",arr_res_apk.length);
                          root.put("name","cmd dionis "+req_cmd_id);
                      }
                   }
                }

            }
            finally{
                     apk.close();  
            }

           
           root.write(response.getWriter());

           logger.trace("end doRunCmd req_apk_node:"+req_apk_node+" req_cmd_id:"+req_cmd_id);
    	   
       }
       private void doGetList(HttpServletRequest request, HttpServletResponse response,String _type) throws ServletException, IOException{
           logger.trace("begin doGetList type:"+_type);

           JSONObject  root=null;

           response.setContentType("application/json");
           response.setContentType("text/html; charset=UTF-8");
           
           rAPK[] r_list=commonAPK.get().getAPK();

           JSONArray n_list=new JSONArray();
           JSONArray c_list=new JSONArray();
           int n_list_size=0;
           int c_list_size=0;
           for(int i=0;i<r_list.length;i++){
              JSONObject obj=new JSONObject();
              obj.put("id"   ,r_list[i].getID());
              obj.put("host" ,r_list[i].getHostAddr());
              n_list.put(obj);
              n_list_size++;
           }
           if(r_list.length>0) {
              logger.trace("load config for apk dionis size:"+r_list.length);
              rAPK apk=r_list[0];
              String[] list_cmd=apk.listCMD();
          
              for(int i=0;i<list_cmd.length;i++){
                  JSONObject obj=new JSONObject();
                  obj.put("id" ,list_cmd[i]);
                  c_list.put(obj);
                  c_list_size++;
              }
           }
           root=new JSONObject();
           root.put("list_node",n_list);
           root.put("size_node",n_list_size);
          
           root.put("list_cmd" ,c_list);
           root.put("size_cmd" ,c_list_size);
          
           root.put("name","list cmd dionis");
           
           root.write(response.getWriter());

           logger.trace("end doGetList type:"+_type);
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webAddr.doRun() path:"+path);
              if(path.startsWith("/rcmd/list")){
                 String    _apk = (String) request.getParameter("apk");
                 doGetList(request,response,_apk);
                 return;
              }
              if(path.startsWith("/rcmd/cmd")){
                 String    _apk = (String) request.getParameter("apk");
                 String    _c = (String) request.getParameter("cmd");
                 doRunCmd(request,response,_apk,_c);
                 return;
              }
              if(path.startsWith("/rcmd/receive")){
                 String    _apk = (String) request.getParameter("apk");
                 String    _c = (String) request.getParameter("cmd");
                 String    _f = (String) request.getParameter("file");
                 doReceive(request,response,_apk,_c,_f);
                 return;
              }
  
              if(page==null)page = "/index.html";

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

