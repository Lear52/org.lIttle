package org.little.http.app.cmddionis;

import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.http.commonHTTP;
import org.little.http.handler.lHttpResponse;
import org.little.rcmd.rAPK;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpCmdResponse extends lHttpResponse{
       private static final Logger  logger = LoggerFactory.getLogger(HttpCmdResponse.class);

	public void runCmd(ChannelHandlerContext ctx, HttpCmdRequest httpCmdRequest) {
	       String txt="";
	       boolean ret;
               String type_req=httpCmdRequest.getType();
               rAPK apk = new rAPK();
               ret=apk.loadCFG(commonHTTP.get().getNode());
               logger.trace("load config for apk dionis ret:"+ret);

               if(ret==false) {
                  txt="apk.loadCFG return:"+ret;     
                  logger.error("load config for apk dionis ret:"+ret);
               }
               else{
                   boolean  is_correct=true;
                   //String[] a=apk.listCMD();

                   //for(int i=0;i<a.length;i++) if(a[i].equals("")){is_correct=true; break;}

                   if(is_correct){
                      ret=apk.open();
                      if(ret==false) {
                         txt="apk.open return:"+ret;     
                      }
                      else{
                         logger.trace("open apk dionis");
                         //-------------------------------------------------------------------
                         ret=true;
                        
                         String c=httpCmdRequest.getCMD();
                         logger.trace("open apk dionis cmd:"+c);
                         String[] _ret=apk.runCMD(c);
                         
                         if(_ret==null){
                            txt="apk.run ret:null";     
                            logger.error("apk dionis ret:"+txt);
                            logger.trace("apk dionis ret:"+txt);
                         }
                         else{
                              logger.trace("type:"+type_req);

                              if("js".equals(type_req)){
                                 StringWriter out = new StringWriter();
                                 JSONArray list=new JSONArray();
                                 for(int i=0;i<_ret.length;i++){
                                     JSONObject obj;
                                     obj=new JSONObject();
                                     obj.put("id"   ,i);
                                     obj.put("txt" ,_ret[i]);
                                     list.put(obj);
                                 }
                                 JSONObject root=new JSONObject();
                                 root.put("list",list);
                                 root.put("size",_ret.length);
                                 root.put("name","cmd dionis "+c);
                                 root.write(out);
                                 txt=out.toString();
                                 logger.trace("txt:"+txt);
                              }
                              else{
                                  StringBuilder buf=new StringBuilder();  
                                  for(int i=0;i<_ret.length;i++)buf.append(_ret[i]);
                                  txt=buf.toString();                         
                                  txt="<pre>apk.run ret:"+txt.length()+"\r\n"+"---------------------------------------\r\n"+txt+"---------------------------------------\r\n</pre>";
                                  logger.trace("txt:"+txt);
                              }
                         }
                         //-------------------------------------------------------------------
                         apk.close();
                      }
                   }

               } 

               if("js".equals(type_req)){
                  sendJSON(ctx,httpCmdRequest,txt);
               }
               else{
                  sendTxt(ctx,httpCmdRequest,txt,HttpResponseStatus.OK,true);
               }
	}
	public void runReceive(ChannelHandlerContext ctx, HttpCmdRequest httpCmdRequest) {


                   getFile(ctx,httpCmdRequest,httpCmdRequest.getFilename());

        }
	public void runSend(ChannelHandlerContext ctx, HttpCmdRequest httpCmdRequest) {


        }

	public void runList(ChannelHandlerContext ctx, HttpCmdRequest httpCmdRequest) {
	       String  txt="";
	       boolean ret;
               rAPK apk = new rAPK();
               String type_req=httpCmdRequest.getType();

               //logger.trace("set 3 cmd:list");

               ret=apk.loadCFG(commonHTTP.get().getNode());
               logger.trace("load config for apk dionis ret:"+ret);
               if(ret==false) {
                  txt="apk.loadCFG return:"+ret;     
                  type_req="txt";
                  logger.error(txt);
               }
               else{
                   String[] a=apk.listCMD();

                   if("js".equals(type_req)){
                      logger.trace("type:"+type_req);
                      StringWriter out = new StringWriter();
                      JSONArray list=new JSONArray();
                      for(int i=0;i<a.length;i++){
                         JSONObject obj=new JSONObject();
                         obj.put("name" ,a[i]);
                         list.put(obj);
                      }
                      JSONObject root=new JSONObject();
                      root.put("list",list);
                      root.put("size",a.length);
                      root.put("name","list cmd dionis");
                      root.write(out);
                      txt=out.toString();
                      logger.trace("txt:"+txt);
                   }
                   else{
                        logger.trace("type:"+type_req);
                        txt="<pre>list apk.cmd ------------------------------\r\n";           
                        for(int i=0;i<a.length;i++) txt+=(a[i]+"\r\n");     
                        txt+="-------------------------------------------</pre>";           
                        logger.trace("txt:"+txt);
                   }
               } 

               if("js".equals(type_req)){
                  sendJSON(ctx,httpCmdRequest,txt);
               }
               else{
                  sendTxt(ctx,httpCmdRequest,txt,HttpResponseStatus.OK,true);
               }
	}

}


