package org.little.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.scheduler;

/**
 * @author av
 */

public class webThread extends HttpServlet{
       private static final long serialVersionUID = -660122190631811408L;
       private static final Logger logger = LoggerFactory.getLogger(webThread.class);

        //     —оздание нового пула потоков 
        protected int       size_runner;
        protected scheduler runner;

        public webThread(){
               preinit();
        }

        protected void preinit(){
               size_runner=10;
               runner = new scheduler(size_runner);
        }
     
        @Override
        public void init() throws ServletException {
        //     «апуск нового пула потоков 
               runner.fork();
               logger.info("web scheduller is run");
        }
     
        @Override
        public void destroy() {
        //      остановка пула потоков 
               if(runner!=null)runner.shutdown();
               runner = null;
               super.destroy();
               logger.info("web scheduller is stop");
        }
     
        @Override
        public String getServletInfo() {
               return "Base servlet scheduler";
        }

        public String getParametr(String param) {
               return getInitParameter(param);
        }

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
               try {
                    request.setCharacterEncoding("UTF-8");
                    request.setAttribute("servletName",getServletInfo());
                    response.setContentType("text/html; charset=UTF-8");
               } 
               catch (UnsupportedEncodingException e1) {
                    logger.error("ex:"+e1);
               }
               doRun(request,response);
        }
        @Override
        public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                 try {
                    request.setCharacterEncoding("UTF-8");
                    request.setAttribute("servletName",getServletInfo());
                    response.setContentType("text/html; charset=UTF-8");
                 } 
                 catch (UnsupportedEncodingException e1) {
                    logger.error("ex:"+e1);
                 }
                 doRun(request,response);
        }
        @Override
        public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                 try {
                    request.setCharacterEncoding("UTF-8");
                    request.setAttribute("servletName",getServletInfo());
                    response.setContentType("text/html; charset=UTF-8");
                 } 
                 catch (UnsupportedEncodingException e1) {
                    logger.error("ex:"+e1);
                 }
                 doRun(request,response);
        }



        public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        }



}

