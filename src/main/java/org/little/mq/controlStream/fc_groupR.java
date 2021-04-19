package org.little.mq.controlStream;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.http.lHttpCLN;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.task;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class fc_groupR  extends task implements fc_group{
       private static final Logger logger = LoggerFactory.getLogger(fc_groupR.class);

       private ArrayList<fc_flow> flow_list;
       private String             url_state;
       private String             url_control;
       private String             userURL;
       private String             passwordURL;
       private String             id;
       private String             remote_id;
       private String             name;
       private boolean            state_group;

       public fc_groupR(){
              clear();
       }

       @Override
       public void clear() {
              flow_list=new ArrayList<fc_flow>();
              state_group      =false;
              url_state  =null;  
              url_control=null;
              userURL    =null;    
              passwordURL=null;
              id         =null;  
              remote_id =null;
              name       =null;
       }
       @Override
       public String             getID() {return id;}
       public String             getRID() {return remote_id;}
       @Override
       public void               setID(String id) {this.id = id;       }
       @Override
       public String             getName() {return name;}
       @Override
       public void               setName(String name) {this.name = name;}
       @Override
       public boolean            getStateGroup(){return state_group;}

       @Override
       public void work(){

              logger.trace("begin run group");

              state_group      =false;

              lHttpCLN cln=new lHttpCLN(url_state);

              logger.trace("new lHttpCLN("+url_state+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+url_state+")",ex));
                    return;
              }

              logger.trace("getJSON("+url_state+")");

              if(root==null)return;

              JSONArray list=root.getJSONArray("list");//group 
              if(list.length()<1){

                 return;
              }

              root=list.getJSONObject(0);

              //setID  (root.getString("id"));
              //setName(root.getString("name"));
              list=root.getJSONArray("list");// flow

              logger.trace("getJSON("+url_state+") group id:"+getID()+" name:"+getName() +" list flow json:"+list);

              for(int i=0;i<list.length();i++) {
                  fc_flow    flow;
                  if(i>=flow_list.size()){
                     flow=new fc_flowR();
                     flow_list.add(flow);
                  }
                  else{
                     flow=flow_list.get(i);
                  }
                  JSONObject f_json=list.getJSONObject(i);
                  flow.setState(f_json);
                  flow.work();
              }

              state_group      =true;

              logger.trace("end run group id:"+getID()+" name:"+getName());
              
       }
       @Override
       public JSONObject setFlag(String flow_id, boolean is_flag) {
              String _url_control=url_control+"?group="+getRID()+"&flow="+flow_id+"&state="+is_flag;
              state_group      =false;

              lHttpCLN cln=new lHttpCLN(_url_control);

              logger.trace("new lHttpCLN("+_url_control+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+_url_control+")",ex));
                    return null;
              }

              logger.trace("getJSON("+_url_control+")");

              if(root==null)return null;


              logger.trace("setFlag("+_url_control+" group id:"+getID()+" flow:"+flow_id +" flag:"+is_flag+") ret:"+root);


              state_group      =true;

              logger.trace("end setFlag groupR id:"+getID()+" name:"+getName());

              return root;
       }
       @Override
       public JSONObject ClearQ(String flow_id,String mngr_id,String q_id){
              String _url_control=url_control+"?group="+getRID()+"&mngr="+mngr_id+"&q="+q_id;
              state_group      =false;

              lHttpCLN cln=new lHttpCLN(_url_control);

              logger.trace("new lHttpCLN("+_url_control+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+_url_control+")",ex));
                    return null;
              }

              logger.trace("getJSON("+_url_control+")");

              if(root==null)return null;


              logger.trace("ClearQ("+_url_control+" group id:"+getID()+" flow:"+flow_id + " ret:"+root);


              state_group      =true;

              logger.trace("end ClearQ groupR id:"+getID()+" name:"+getName());

              return root;
       }
       @Override
       public JSONObject getStat(){

              JSONObject root=new JSONObject();
              JSONArray  list=new JSONArray();
              root.put("type", "group");
              root.put("id" , getID());
              root.put("rid", getRID());
              root.put("state", getStateGroup());
              root.put("name", getName());
              for(int i=0;i<flow_list.size();i++) {
                  fc_flow flow=flow_list.get(i);
                  list.put(i,flow.getStat());
              }
              root.put("list", list);
              root.put("size", flow_list.size());

              logger.trace("fc_group getStat() id:"+id+" name:"+name+" size:"+flow_list.size());
              
              return root;

       }

       @Override
       public void init(Node node_cfg) {
              if(node_cfg==null)return;
              //
              if(node_cfg.getAttributes().getNamedItem("id")==null) {
                 logger.error("The configuration group id:noname");
                 return;
              }
              else  id=node_cfg.getAttributes().getNamedItem("id").getNodeValue();
              //
              if(node_cfg.getAttributes().getNamedItem("name")==null) {
                 name="";
              }
              else name=node_cfg.getAttributes().getNamedItem("name").getNodeValue();
              //
              if(node_cfg.getAttributes().getNamedItem("remote_id")==null) {
                 remote_id="";
              }
              else remote_id=node_cfg.getAttributes().getNamedItem("remote_id").getNodeValue();
              //
              flow_list=new ArrayList<fc_flow>();
              logger.trace("The configuration node:"+node_cfg.getNodeName()+" id:"+id+" name:"+name);

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("url_state" .equals(n.getNodeName())){url_state  =n.getTextContent(); logger.info("url_state:" +url_state  );}else
                  if("url_contrl".equals(n.getNodeName())){url_control=n.getTextContent(); logger.info("url_contrl:"+url_control);}else
                  if("url_user"  .equals(n.getNodeName())){userURL    =n.getTextContent(); logger.info("url_user:"  +userURL    );}else
                  if("url_passwd".equals(n.getNodeName())){passwordURL=n.getTextContent(); logger.info("url_passwd:"+passwordURL);}
                  if("remote_id" .equals(n.getNodeName())){remote_id=n.getTextContent();   logger.info("remote_id:"+remote_id);}
              }
              
       }


}

