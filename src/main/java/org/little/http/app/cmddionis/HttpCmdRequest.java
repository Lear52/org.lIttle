package org.little.http.app.cmddionis;

import org.little.http.handler.lHttpRequest;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;


public class HttpCmdRequest extends lHttpRequest{
       private static final Logger      logger  = LoggerFactory.getLogger(HttpCmdRequest.class);

       protected HttpCmdResponse        response;
       private   String                 rcommand;
       private   String                 rapk;
       private   String                 filename;
       private   String                 type_output;
       
       public HttpCmdRequest(){
              clear();
              response=new HttpCmdResponse();
              setResponse(response);
       }

       @Override
       public void clear(){
              super.clear();
              rcommand   =null;
              rapk       =null;    
              filename   =null;
              type_output=null;
       }

       public String getAPK     (){return rapk;}
       public String getCMD     (){return rcommand;}
       public String getFilename(){return filename;}
       public String getType    (){return type_output;}

       @Override
       public boolean HttpGet(ChannelHandlerContext ctx){
              String cmd;
              String user=getUser();

              cmd  =getPath();
              logger.trace("set 0 cmd:"+cmd+" user:"+user);
           
              if(cmd.startsWith("/get" )){cmd="get"  ;}                   
              else
              if(cmd.startsWith("/send" )){cmd="send"  ;}                   
              else
              if(cmd.startsWith("/receive" )){cmd="receive";}                   
              else
              if(cmd.startsWith("/list" )){cmd="list";}                   
              else{
                 cmd="file";
              }
              //logger.trace("set 1 cmd:"+cmd);


              if("get".equals(cmd)){

                  rcommand    =getQuery().get("cmd");
                  rapk        =getQuery().get("apk");
                  type_output =getQuery().get("type");
                  if(type_output==null)type_output="txt";
                  if(type_output.startsWith("js" ))type_output="js";
                  else                             type_output="txt";

                  //logger.trace("set 2 cmd:get");
                  response.runCmd(ctx,this);
                  return RequestProcessOk;
              }
              else
              if("list".equals(cmd)){
                  type_output=getQuery().get("type");
                  rapk=getQuery().get("apk");
                  if(type_output==null)type_output="txt";
                  if(type_output.startsWith("js" ))type_output="js";
                  else                             type_output="txt";
                  response.runList(ctx,this);
                  return RequestProcessOk;
              }
              else
              if("receive".equals(cmd)){
                  rapk=getQuery().get("apk");
                  filename=getQuery().get("file");
                  response.runReceive(ctx,this);
                  return RequestProcessOk;
              }
              else
              if("file".equals(cmd)){
                  response.getFile(ctx,this);
                  return RequestProcessOk;
              }
              else
              {
                  String txt="unknow cmd:"+cmd; 
                  logger.trace(txt);
                  response.sendError(ctx,this,txt);
                  return RequestProcessBad;
              }

       }
       @Override       
       public boolean HttpPost(ChannelHandlerContext ctx){
              String cmd;
              String user=getUser();

              cmd  =getPath();
              logger.trace("set 0 cmd:"+cmd+" user:"+user);
           
              if(cmd.startsWith("/send" )){cmd="send"  ;}                   
              if("send".equals(cmd)){
                  rcommand=getQuery().get("cmd");
                  rapk=getQuery().get("apk");
                  logger.trace("set 2 cmd:send");
                  response.runSend(ctx,this);
                  return RequestProcessOk;
              }
              return RequestProcessBad;
       }
       @Override
       public boolean HttpPut(ChannelHandlerContext ctx){
              String cmd;
              String user=getUser();

              cmd  =getPath();
              logger.trace("set 0 cmd:"+cmd+" user:"+user);
           
              if(cmd.startsWith("/send" )){cmd="send"  ;}                   
              if("send".equals(cmd)){
                  rcommand=getQuery().get("cmd");
                  rapk=getQuery().get("apk");
                  logger.trace("set 2 cmd:send");
                  response.runSend(ctx,this);
                  return RequestProcessOk;
              }
              return RequestProcessBad;
       }




}

