package org.little.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONObject;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

@WebServlet("/webUp")
@MultipartConfig(fileSizeThreshold=1024*1024*100,// 10MB 
                 maxFileSize=1024*1024*1000,	// 100MB
		 maxRequestSize=1024*1024*1000)	// 100MB

public class webUp extends HttpServlet {
       /**
	 * 
	 */
	private static final long serialVersionUID = 429143747588524051L;
	private static final Logger logger = LoggerFactory.getLogger(webThread.class);
 
	/**
	 * Name of the directory where uploaded files will be saved, relative to
	 * the web application directory.
	 */
	//private static final String SAVE_DIR = "upload";

	/**
	 * handles file upload
	 */
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
                String SAVE_DIR = "upload";
                String fname = (String) request.getParameter("fname");

		// gets absolute path of the web application
		String appPath = request.getServletContext().getRealPath("");
		// constructs path of the directory to save uploaded file
		String savePath = appPath + File.separator + SAVE_DIR;

		// creates the save directory if it does not exists
		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		}

		for (Part part : request.getParts()) {
			String fileName = extractFileName(part);
			// refines the fileName in case it is an absolute path
                        File  f_cur=new File(fileName);
			fileName = f_cur.getName();

			if(fname!=null){
			   /*
                           stringParser pr= new stringParser (fileName,".");
                           //String _file_name=
                        		   pr.get();
                           String _file_ext="";
                           String _f;
                           //file_path_src=filename;
                           while((_f=pr.get())!=null){
                                 _file_ext=_f;//получаем последнюю часть имени файла отделенную .
                           }
                           _file_ext=_file_ext.toLowerCase();

                           fileName=fname+"."+_file_ext;
                           */
                           fileName=fname;
                        }


                        String full_file_name=savePath + File.separator + fileName;
			part.write(full_file_name);
			logger.trace("upload file:"+full_file_name);
		}

                JSONObject json_root = new JSONObject();
                try{
                   json_root.put("cmd"     ,"uploadfile");
                   json_root.put("filename"," ");
                   json_root.write(response.getWriter());
                } catch (IOException ex2) {
                   logger.error("writeJSON error ex:"+ex2);
                   return ;
                }
                logger.info("webUp cmd:upload file:"+fname+"");
                return ;

		//request.setAttribute("message", "Upload has been done successfully!");

		//getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
	}

	/**
	 * Extracts file name from HTTP header content-disposition
	 */
	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for(String s : items){
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length()-1);
			}
		}
		return "";
	}

}