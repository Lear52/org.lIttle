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
              String txt_response="";
              String type_req    =httpCmdRequest.getType();
              String req_apk_node=httpCmdRequest.getAPK();
              String req_cmd_id   =httpCmdRequest.getCMD();
              logger.trace("request apk:"+req_apk_node+" dionis cmd:"+req_cmd_id);

              
              commonAPK.get().init(commonHTTP.get().getNode());
              
              rAPK apk=commonAPK.get().getAPK(req_apk_node);

              if(apk==null) {
                 txt_response="no find node name apk dionis:"+req_apk_node;
                 logger.error(txt_response);
              }
              else 
              try{
                   boolean  is_correct=apk.checkCMD(req_cmd_id);
                   if(is_correct==false){
                       txt_response="node apk dionis"+req_apk_node+" unknow command"+req_cmd_id;     
                       logger.error(txt_response);
                   }
                   else{
                      boolean ret=apk.open();
                      if(ret==false) {
                         txt_response="apk.open return:"+ret;     
                         logger.error(txt_response);
                      }
                      else{
                         String[] arr_res_apk=apk.runCMD(req_cmd_id);
                         if(arr_res_apk==null){
                            txt_response="node apk dionis"+req_apk_node+" return null";     
                            logger.error(txt_response);
                         }
                         else{
                              logger.trace("type:"+type_req);
                              if("js".equals(type_req))txt_response=getCMD2JSON(arr_res_apk,req_cmd_id);
                              else                     txt_response=getCMD2TXT(arr_res_apk,req_cmd_id);
                         }
                      }
                   }

               }
               finally{
                        apk.close();  
               }

               if("js".equals(type_req))sendJSON(ctx,httpCmdRequest,txt_response);
               else                     sendTxt(ctx,httpCmdRequest,txt_response,HttpResponseStatus.OK,true);

       }
       public void runReceive(ChannelHandlerContext ctx, HttpCmdRequest httpCmdRequest) {


                   getFile(ctx,httpCmdRequest,httpCmdRequest.getFilename());

       }
       public void runSend(ChannelHandlerContext ctx, HttpCmdRequest httpCmdRequest) {


       }

       public void runList(ChannelHandlerContext ctx, HttpCmdRequest httpCmdRequest) {
              String  txt_response="";
              //boolean ret;
              String type_req=httpCmdRequest.getType();
              commonAPK.get().init(commonHTTP.get().getNode());
              rAPK[] r_list=commonAPK.get().getAPK();
                 logger.trace("type:"+type_req);
              
              if(r_list!=null){
                 logger.trace("load config for apk dionis size:"+r_list.length);
                 if(r_list.length>0){
                      rAPK apk=r_list[0];
                      String[] list_cmd=apk.listCMD();
                      if("js".equals(type_req))txt_response=getList2JSON(r_list,list_cmd);
                      else                     txt_response=getList2TXT(r_list,list_cmd);
                  } 
               }
               if("js".equals(type_req)){
                  sendJSON(ctx,httpCmdRequest,txt_response);
               }
               else{
                  sendTxt(ctx,httpCmdRequest,txt_response,HttpResponseStatus.OK,true);
               }
       }
       //-----------------------------------------------------------------------------------------
       //-----------------------------------------------------------------------------------------
       //-----------------------------------------------------------------------------------------
       private String getList2JSON(rAPK[] r_list,String[] list_cmd) {
               StringWriter out = new StringWriter();
           
               JSONArray n_list=new JSONArray();
               for(int i=0;i<r_list.length;i++){
                  JSONObject obj=new JSONObject();
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
              
               String txt_response=out.toString();
               logger.trace("txt:"+txt_response);
              
               return txt_response;
       }
       
       private String getList2TXT(rAPK[] r_list,String[] list_cmd) {
               StringBuilder out_response=new StringBuilder();
               out_response.append("<pre>");           
               for(int i=0;i<list_cmd.length;i++) out_response.append(list_cmd[i]).append("\n");     
               out_response.append("</pre>");           
               String txt_response=out_response.toString();           
               logger.trace("txt:"+txt_response);
               return txt_response;
       }       

       private String getCMD2JSON(String[] _ret,String cmd_id) {
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
               root.put("name","cmd dionis "+cmd_id);
               root.write(out);
               String txt=out.toString();
               logger.trace("txt:"+txt);
               return txt;
      }
      private String getCMD2TXT(String[] _ret,String cmd_id) {
              StringBuilder out = new StringBuilder();
              out.append("<pre>");
              for(int i=0;i<_ret.length;i++){
                     out.append(_ret[i]).append('\n');
              }
              out.append("</pre>");
              String txt=out.toString();
              logger.trace("txt:"+txt);
              return txt;
      }
       

}


