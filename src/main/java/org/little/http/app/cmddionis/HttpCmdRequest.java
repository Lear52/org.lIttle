package org.little.http.app.cmddionis;

import org.little.http.handler.lHttpBuf;
import org.little.http.handler.lHttpRequest;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;


public class HttpCmdRequest extends lHttpRequest{
       private static final Logger          logger  = LoggerFactory.getLogger(HttpCmdRequest.class);

       protected String                 cmd;
       protected boolean                is_correct;
       protected HttpCmdResponse        response;
       
       public HttpCmdRequest(){
               clear();
               response=new HttpCmdResponse();
               setResponse(response);
       }

       private void r_clear(){
               cmd           =null;   
               is_correct    =false;
       }
       public void clear(){
               super.clear();
               r_clear();
       }

       public String      getCmd         (){return cmd   ;}
       public boolean     isCorrect      (){return is_correct;}

       public boolean HttpGet(ChannelHandlerContext ctx){
              String user=getUser();
              cmd  =getPath();
                  logger.trace("set 0 cmd:"+cmd+" store:"+user);
           
              if(cmd.startsWith("/get" )){cmd="get"  ;is_correct=true;}                   
              else
              if(cmd.startsWith("/list")){cmd="list" ;is_correct=true;}    
              else{
                 cmd="file" ;                                                             
                 is_correct=true;
              }
              is_correct=true;
              logger.trace("set 1 cmd:"+cmd+ " is_correct:"+is_correct);
                  
              //String _folder=getQuery().get("folder");


              if(cmd.equals("get")){
              }
              if(cmd.equals("list")){
              }

               is_correct=true;

               if("list".equals(getCmd())){
                   return false;
                }
                else
                if("get".equals(getCmd())){
                   return false;
                }
                else
                if("file".equals(getCmd())){
                   response.getFile(ctx,this);
                   return true;
                }
                else
                {
                   String txt="unknow cmd:"+getCmd(); 
                   logger.trace(txt);
                   response.sendError(ctx,this,txt);
                   return true;
                }

               //return true;
       }
       public boolean HttpUpload(ChannelHandlerContext ctx){
           
               lHttpBuf file=this.getBinBuffer().get(0); /**/
           
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

