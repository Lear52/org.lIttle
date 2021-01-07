package org.little.http.app.file;

import org.little.http.handler.lHttpBuf;
import org.little.http.handler.lHttpRequest;
import org.little.store.lMessage;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class HttpFileRequest extends lHttpRequest{
       private static final Logger   logger  = LoggerFactory.getLogger(HttpFileRequest.class);

       //protected String                 store;
       //protected String                 folder;
       //protected String                 msg;
       protected String                 cmd;
       protected boolean                is_correct;
       //protected lMessage               upload_msg;
       protected HttpFileResponse       response;
       
       public HttpFileRequest(){
               clear();
               response=new HttpFileResponse();
               setResponse(response);
       }

       private void r_clear(){
               //store         =null;
               //folder        =null;
               //msg           =null;
               cmd           =null;   
               is_correct    =false;
               //upload_msg    =new lMessage();
       }
       public void clear(){
               super.clear();
               r_clear();
       }

       //public String      getStore       (){return store ;}
       //public String      getFolder      (){return folder;}
       public String      getCmd         (){return cmd   ;}
       public boolean     isCorrect      (){return is_correct;}
       //public lMessage    getUploadMsg   (){return upload_msg;}
       //public String      getMsg         (){return msg   ;}

       public boolean HttpGet(ChannelHandlerContext ctx){
              //store=getUser();
              cmd  =getPath();
    	      logger.trace("set 0 cmd:"+cmd);
           
              if(cmd.startsWith("/post")){cmd="post" ; is_correct=true;}                  // http://x.x.x.x:pppp/post                         - put file(foldername=outbox)
              else                      
              if(cmd.startsWith("/env" )){cmd="env"  ; is_correct=true;}                  // http://x.x.x.x:pppp/env                          - get test (print enviroment)
              else{
                 cmd="file" ;                                                             // http://x.x.x.x:pppp/filename.html                - get http file 
                 is_correct=true;
              }
              is_correct=true;
              logger.trace("set 1 cmd:"+cmd+ " is_correct:"+is_correct);
    	      
              if("file".equals(getCmd())){
                   response.getFile(ctx,this);
                   return true;
              }

              return true;
       }
       public boolean HttpUpload(ChannelHandlerContext ctx){
           
    	   lHttpBuf file=this.getBinBuffer().get(0); /**/
           
    	   file.getName();

           file.getBuf();

           response.saveMsg(ctx,this);
    	   
           logger.trace("HttpUpload:"+file.getName());

    	   return false;
       }
       public boolean HttpPost(ChannelHandlerContext ctx){
    	   return HttpUpload(ctx);
    	   }
       public boolean HttpPut(ChannelHandlerContext ctx){
    	   return HttpUpload(ctx);
       }




}

