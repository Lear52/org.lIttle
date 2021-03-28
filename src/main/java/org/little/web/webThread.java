package org.little.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.scheduler;
import org.little.util.run.task;

/**
 * @author av
 */

public class webThread extends HttpServlet{
       final private static int    CLASS_ID  =404;
             public  static int    getClassId(){return CLASS_ID;}
       private static final Logger logger = LoggerFactory.getLogger(webThread.class);

        //     —оздание нового пула потоков 
        protected scheduler runner = new scheduler(10);
     
        @Override
        public void init() throws ServletException {
        //     «апуск нового пула потоков 
               runner.fork();
               logger.trace("web scheduller is run");
        }
     
        @Override
        public void destroy() {
        //      остановка пула потоков 
               if(runner!=null)runner.shutdown();
               runner = null;
               super.destroy();
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

