package org.little.web;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONObject;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class webUp2 extends HttpServlet{
       private static final long serialVersionUID = 0x5f49ff6f8980813L;
       private static final Logger logger = LoggerFactory.getLogger(webUp2.class);
      
       public webUp2(){}
      
       protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
           String SAVE_DIR = "upload";
           String fname = request.getParameter("fname");
           String appPath = request.getServletContext().getRealPath("");
           String savePath = (new StringBuilder(String.valueOf(appPath))).append(File.separator).append(SAVE_DIR).toString();
           File   fileSaveDir = new File(savePath);
           if(!fileSaveDir.exists())fileSaveDir.mkdir();

           String full_file_name;

           Iterator<Part> iterator = request.getParts().iterator();

           while(iterator.hasNext()) {
                 Part   part     = (Part)iterator.next();
                 String fileName = extractFileName(part);
                 File   f_cur    = new File(fileName);

                 fileName        = f_cur.getName();
                 if(fname != null)fileName = fname;

                 full_file_name = String.valueOf(savePath)+File.separator+fileName;
                 part.write(full_file_name);
                 logger.trace("upload file:"+full_file_name);
           }
      
           JSONObject json_root = new JSONObject();
           try{
               json_root.put("cmd", "uploadfile");
               json_root.put("filename", " ");
               json_root.write(response.getWriter());
           }
           catch(IOException ex2){
               logger.error("writeJSON error ex:"+ex2);
               return;
           }
           logger.info((new StringBuilder("webUp cmd:upload file:")).append(fname).toString());
       }
      
       private String extractFileName(Part part){
           String contentDisp = part.getHeader("content-disposition");
           String items[] = contentDisp.split(";");
           String as[];
           int j = (as = items).length;
           for(int i = 0; i < j; i++){
               String s = as[i];
               if(s.trim().startsWith("filename"))return s.substring(s.indexOf("=") + 2, s.length() - 1);
           }
           return "";
       }


}
