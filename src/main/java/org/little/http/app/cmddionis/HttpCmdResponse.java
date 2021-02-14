package org.little.http.app.cmddionis;

import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.http.commonHTTP;
import org.little.http.handler.lHttpResponse;
import org.little.rcmd.commonAPK;
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

              String apk_req=httpCmdRequest.getAPK();
              String c=httpCmdRequest.getCMD();
              logger.trace("request apk:"+apk_req+" dionis cmd:"+c);

              commonAPK.get().init(commonHTTP.get().getNode());
              rAPK apk=commonAPK.get().getAPK(apk_req);
              if(apk==null) {
                 txt="noname apk dionis:"+apk_req;
                 logger.error(txt);
              }
              else {
                   boolean  is_correct=apk.checkCMD(c);

                   if(is_correct){
                      ret=apk.open();
                      if(ret==false) {
                         txt="apk.open return:"+ret;     
                         logger.error(txt);
                      }
                      else{
                         logger.trace("open apk dionis");
                         //-------------------------------------------------------------------
                         ret=true;
                        
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
              String type_req=httpCmdRequest.getType();

              commonAPK.get().init(commonHTTP.get().getNode());

              rAPK[] r_list=commonAPK.get().getAPK();
              if(r_list!=null){
                 logger.trace("load config for apk dionis size:"+r_list.length);
                 if(r_list.length>0){
                      rAPK apk=r_list[0];
                      String[] list_cmd=apk.listCMD();
                
                      if("js".equals(type_req)){
                         logger.trace("type:"+type_req);
                         StringWriter out = new StringWriter();
                
                         JSONArray n_list=new JSONArray();
                         for(int i=0;i<r_list.length;i++){
                            JSONObject obj=new JSONObject();
                            logger.trace("id:"+r_list[i].getID()+" host:"+r_list[i].getHost());
                            obj.put("id"   ,r_list[i].getID());
                            obj.put("host" ,r_list[i].getHostAddr());
                            n_list.put(obj);
                         }
                
                         JSONArray c_list=new JSONArray();
                         for(int i=0;i<list_cmd.length;i++){
                            JSONObject obj=new JSONObject();
                            obj.put("id" ,list_cmd[i]);
                            c_list.put(obj);
                         }

                         JSONObject root=new JSONObject();
                         root.put("list_node",n_list);
                         root.put("size_node",r_list.length);

                         root.put("list_cmd" ,c_list);
                         root.put("size_cmd" ,list_cmd.length);

                         root.put("name","list cmd dionis");
                         root.write(out);

                         txt=out.toString();
                         logger.trace("txt:"+txt);
                      }
                      else{
                           logger.trace("type:"+type_req);
                           txt="<pre>list apk.cmd ------------------------------\r\n";           
                           for(int i=0;i<list_cmd.length;i++) txt+=(list_cmd[i]+"\r\n");     
                           txt+="-------------------------------------------</pre>";           
                           logger.trace("txt:"+txt);
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

}


