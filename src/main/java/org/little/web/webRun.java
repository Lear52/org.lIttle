package org.little.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

/**
 * @author av
 *  
 */
public class webRun extends HttpServlet {
       final private static int    CLASS_ID  =403;
             public  static int    getClassId(){return CLASS_ID;}
       private static final Logger logger = LoggerFactory.getLogger(webRun.class);

        
        @Override
        public void init() throws ServletException {
                    super.init();
        }

        @Override
        public void destroy() {
               super.destroy();
        }
        
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                doRun(request,response);
        }

        @Override
        public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                doRun(request,response);
        }

        @Override
        public void doPut(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
                doRun(request,response);
        }

        public void doRun(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
                String page = null;
                prerun(request, response);
                page=run(request, response);
                postrun(request, response,page);
                
        }
        @Override
        public String getServletInfo() {
               return "Base servlet";
        }

        public void prerun(HttpServletRequest request, HttpServletResponse response) {
                try {
                    request.setCharacterEncoding("UTF-8");
                    response.setCharacterEncoding("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    /**/
                    e1.printStackTrace();
                }
        
        }
        public void postrun(HttpServletRequest request, HttpServletResponse response,String page) {
                // ������� ��������� � ������ �� �������� �������
                try {
                     request.setAttribute("servletName", page);
                     response.setContentType("text/html; charset=UTF-8");
                     response.getWriter().println("<html><head><title>prj0.org.webAccess</title></head><body>" + page + "</body>");
                } catch (Exception ex) {
                     logger.error("error output"+" Exception:"+ex);
                }
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
        public String run(HttpServletRequest request, HttpServletResponse response) {
                //HttpSession h_session;
                //util u = null;//������� ������������
                String cmd = null;
                String page = null;

                //u=getUserSession(request);/**/

                cmd = (String) request.getParameter(webDef.request_cmd);
                logger.trace("webaccess cmd:"+cmd);
                //-------------------------------------------------------------------------------------
                if (cmd == null) {
                        logger.error("error cmd:null");
                        page = "/";
                } 
                        page = "/";
                //-------------------------------------------------------------------------------------
                return page;

        }

        /**
         * 
         * 
         * @return util
         */

        static public String getErrorSession(HttpServletRequest request) {
                String error = null;
                try{
                   HttpSession h_session;
                   h_session = request.getSession();
                   error = (String) h_session.getAttribute(webDef.session_error);
                } catch (Exception e) {
                        error = null;
                }
                return error;
        }
        /**
         * 
         * @param request
         * @param error
         */
        static public void setErrorSession(HttpServletRequest request, String error) {
                HttpSession h_session;
                try{
                   h_session = request.getSession();
                   h_session.setAttribute(webDef.session_error, error);
                } catch (Exception e) {
                        logger.error("set error:" + error);
                        return;
                }

        }


        static public String toHtml(String str) {
               //return str==null?"&nbsp;":str;
               return str==null?" ":str;
        }

}

