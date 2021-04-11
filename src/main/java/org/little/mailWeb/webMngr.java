package org.little.mailWeb;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.little.lmsg.lMessage;
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
       private void doGetFileID(HttpServletRequest request, HttpServletResponse response,int _uid) throws ServletException, IOException{
           logger.trace("begin doGetFileID:"+_uid);

           lMessage  msg=client.getLog().loadArray(_uid);
           byte [] buf=null;
           int     buf_size=0;
           if(msg!=null){
              buf=msg.getBodyBin();
              buf_size=buf.length;
              logger.trace("load buf:"+buf.length);
           }

           response.setContentType("application/octet-stream");
           response.addHeader("Accept-Ranges","bytes");
           response.setHeader("Content-Type","application/octet-stream");
           response.setHeader("Content-Transfer-Encoding", "Binary");
           response.setHeader("Content-Disposition", "inline; filename=\"" + msg.getFilename() + "\"");
           response.setContentLength(buf.length);
           logger.trace("set header");

           response.getOutputStream().write(buf,0,buf.length);
           logger.trace("write buf");
           response.getOutputStream().flush();;

           logger.trace("end doGetFileID:"+_uid);
       }
       private void doGetX509ID(HttpServletRequest request, HttpServletResponse response,int _x509_id) throws ServletException, IOException{
           logger.trace("begin doGetX509ID:"+_x509_id);

           lMessage  msg=client.getLog().loadArrayX509(_x509_id);
           byte [] buf=null;
           int     buf_size=0;
           if(msg!=null){
              buf=msg.getBodyBin();
              buf_size=buf.length;
              logger.trace("load buf:"+buf.length);
           }
           response.setContentType("application/octet-stream");
           response.addHeader("Accept-Ranges","bytes");
           response.setHeader("Content-Type","application/octet-stream");
           response.setHeader("Content-Transfer-Encoding", "Binary");
           response.setHeader("Content-Disposition", "inline; filename=\"" + msg.getFilename() + "\"");
           response.setContentLength(buf_size);
           logger.trace("set header");

           response.getOutputStream().write(buf,0,buf_size);
           logger.trace("write buf");
           response.getOutputStream().flush();;

           logger.trace("end doGetX509ID:"+_x509_id);
       }
       private void doGetList(HttpServletRequest request, HttpServletResponse response,String _type) throws ServletException, IOException{
           logger.trace("begin doGetList type:"+_type);

           JSONObject  root=client.getLog().loadJSON(_type);

           logger.trace("getStatAll() :"+root);

           response.setContentType("application/json");
           response.setContentType("text/html; charset=UTF-8");

           root.write(response.getWriter());

           logger.trace("end doGetList type:"+_type);
       }
       private void doGetX509(HttpServletRequest request, HttpServletResponse response,String _type) throws ServletException, IOException{
           logger.trace("begin doGetX509 type:"+_type);

           JSONObject  root=client.getLog().loadJSONX509(_type);

           logger.trace("getStatAll() :"+root);

           response.setContentType("application/json");
           response.setContentType("text/html; charset=UTF-8");

           root.write(response.getWriter());

           logger.trace("end doGetX509 type:"+_type);
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
              if(path.startsWith("/x509")){
                  String    _type = (String) request.getParameter("type");
                  doGetX509(request,response,_type);
                  return;
              }
              if(path.startsWith("/get")){
                  String    _uid     = (String) request.getParameter("uid");
                  String    _x509_id = (String) request.getParameter("x509_id");
                  int uid    =-1;
                  int x509_id=-1;
                  if(_uid    !=null)try {uid    =Integer.parseInt(_uid, 10);    } catch(Exception e) {uid=-1;}
                  if(_x509_id!=null)try {x509_id=Integer.parseInt(_x509_id, 10);} catch(Exception e) {x509_id=-1;}

                  if(uid>0 || x509_id>0) {
                     if(x509_id>0)doGetX509ID(request,response,x509_id);
                     else         doGetFileID(request,response,uid);
                     return;
                  }
                  logger.trace("error request uid:"+_uid);
                  page =client.getErrorPage();;
              }
  
              if(page==null)page = client.getDefPage();

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

