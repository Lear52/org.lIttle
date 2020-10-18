package org.little.http.app.keystore;

import org.little.http.handler.lHttpBuf;
import org.little.http.handler.lHttpRequest;
import org.little.store.lMessage;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class HttpX509Request extends lHttpRequest{
       private static final Logger          logger  = LoggerFactory.getLogger(HttpX509Request.class);

       protected String                 store;
       protected String                 folder;
       protected String                 msg;
       protected String                 cmd;
       protected boolean                is_correct;
       protected lMessage               upload_msg;
       protected HttpX509Response      response;
       
       public HttpX509Request(){
               clear();
               response=new HttpX509Response();
               setResponse(response);
       }

       private void r_clear(){
               store         =null;
               folder        =null;
               msg           =null;
               cmd           =null;   
               is_correct    =false;
               upload_msg    =new lMessage();
       }
       public void clear(){
               super.clear();
               r_clear();
       }

       public String      getStore       (){return store ;}
       public String      getFolder      (){return folder;}
       public String      getCmd         (){return cmd   ;}
       public boolean     isCorrect      (){return is_correct;}
       public lMessage    getUploadMsg   (){return upload_msg;}
       public String      getMsg         (){return msg   ;}

       public boolean HttpGet(ChannelHandlerContext ctx){
              store=getUser();
              cmd  =getPath();
    	      logger.trace("set 0 cmd:"+cmd+" store:"+store);
           
              if(cmd.startsWith("/get" )){cmd="get"  ;is_correct=true;}                   // get http://x.x.x.x:pppp/get?folder=name&msg=num  - get file(foldername, num_msg)
              else
              if(cmd.startsWith("/list")){cmd="list" ;folder="inbox";is_correct=true;}    // get http://x.x.x.x:pppp/list?folder=name         - get list file
              else
              if(cmd.startsWith("/del" )){cmd="del"  ; is_correct=true;}                  // get http://x.x.x.x:pppp/del?folder=name&msg=num  - del file(foldername, num_msg) 
              else
              if(cmd.startsWith("/user")){cmd="user" ; is_correct=true;}                  // get http://x.x.x.x:pppp/user                     - list user
              else
              if(cmd.startsWith("/post")){cmd="post" ; is_correct=true;}                  // http://x.x.x.x:pppp/post                         - put file(foldername=outbox)
              else                      
              if(cmd.startsWith("/env" )){cmd="env"  ; is_correct=true;}                  // http://x.x.x.x:pppp/env                          - get test (print enviroment)
              else{
                 cmd="file" ;                                                             // http://x.x.x.x:pppp/filename.html                - get http file 
                 is_correct=true;
              }
              is_correct=true;
              logger.trace("set 1 cmd:"+cmd+ " is_correct:"+is_correct);
    	      
              String _folder=getQuery().get("folder");
              if(_folder!=null)folder=_folder;

              msg=getQuery().get("msg");

              if(cmd.equals("get")){
                  //if(store==null){clear(); is_correct=false;return false;}
                  if(folder==null){clear();is_correct=false;return false;}
                  if(msg   ==null){clear();is_correct=false;return false;}
              }
              if(cmd.equals("list")){
                  //if(store==null){clear(); is_correct=false;return false;}
                  if(folder==null){clear();is_correct=false;return false;}
              }

               is_correct=true;
               logger.trace("set 4 cmd:"+cmd+ " is_correct:"+is_correct+" store:"+store+ " folder:"+folder+" msg:"+msg);

               if("list".equals(getCmd())){
                   response.getList(ctx,this);
                   return true;
                }
                else
                if("user".equals(getCmd())){
                   response.getListUser(ctx,this);
                   return true;
                }
                else
                if("get".equals(getCmd())){
                   response.getMsg(ctx,this);
                   return true;
                }
                else
                /*if("info".equals(getCmd())){
                   response.getInfo(ctx,this);
                   return true;
                }
                else*/
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
           
    	   upload_msg.setFilename(file.getName());
           upload_msg.setMime    (file.getMime());
           upload_msg.setSentDate();
           upload_msg.setFrom(store);
           String to=getQuery().get("to");
           String subject=getQuery().get("subject");

           if(to==null){
              logger.error("HttpUpload:"+file.getName()+" to is null user:"+store);
              upload_msg.addTO("");          
           }
           else upload_msg.addTO(to);

           if(subject==null)upload_msg.setSubject(subject);else upload_msg.setSubject(subject);

           upload_msg.setBodyBin(file.getBuf());
           response.saveMsg(ctx,this);
    	   
           logger.trace("HttpUpload:"+file.getName()+" to:"+to+" subj:"+subject);

    	   return false;
       }
       public boolean HttpPost(ChannelHandlerContext ctx){
    	   return HttpUpload(ctx);
    	   }
       public boolean HttpPut(ChannelHandlerContext ctx){
    	   return HttpUpload(ctx);
       }




}

