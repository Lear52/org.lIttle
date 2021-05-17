package org.little.mq.controlStream;

import java.util.ArrayList;

import org.little.util.run.task;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class fc_groupL extends task implements fc_group{
       private static final Logger logger = LoggerFactory.getLogger(fc_groupL.class);
       private ArrayList<fc_flow> flow_list;
       private String             id;
       private String             name;
       private boolean            state_group;
       private fc_mngr            mngr;

       public fc_groupL(fc_mngr _mngr){
              clear();
              mngr=_mngr;
       }

       @Override
       public void clear() {
              state_group      =false;
              flow_list=new ArrayList<fc_flow>();
              id         =null;  
              name       =null;
       }

       @Override
       public String             getID() {return id;}
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
              state_group      =false;
              for(int i=0;i<flow_list.size();i++) {
                  fc_flow flow=flow_list.get(i);
                  logger.trace("group id:"+id+" name:"+name+" flow:"+i);
                  flow.work();  
              }
              state_group      =true;
              
       }
       @Override
       public JSONObject getStat(){

              JSONObject root=new JSONObject();
              JSONArray  list=new JSONArray();
              root.put("type", "group");
              root.put("id", getID());
              root.put("name", getName());
              root.put("state", getStateGroup());
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

              if(node_cfg.getAttributes().getNamedItem("id")==null) {
                 logger.error("The configuration group id:noname");
                 return;
              }
              else  id=node_cfg.getAttributes().getNamedItem("id").getNodeValue();

              if(node_cfg.getAttributes().getNamedItem("name")==null) {
                   name="";
              }
              else name=node_cfg.getAttributes().getNamedItem("name").getNodeValue();

              flow_list=new ArrayList<fc_flow>();
              logger.info("The configuration node:"+node_cfg.getNodeName()+" id:"+id+" name:"+name);

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("flow".equals(n.getNodeName()) ){           
                     fc_flow flow=new fc_flowL(mngr);
                     flow.init(n);
                     flow_list.add(flow);
                  }
              }
              
       }

       @Override
       public JSONObject setFlag(String flow_id, boolean is_flag) {

              JSONObject root=new JSONObject();

              for(int i=0;i<flow_list.size();i++){
                  if(flow_list.get(i).getID().equals(flow_id)) {
                     JSONObject ret=flow_list.get(i).setFlag(is_flag); 
                     root.put("resp", ret);
                     break;
                  }
              }
              
              return root;
       }

       @Override
       public JSONObject ClearQ(String flow_id,String mngr_id,String q_id){
              JSONObject root=new JSONObject();

              logger.trace("group.ClearQ(group:"+id+",flow:"+flow_id+",mngr:"+mngr_id+",q:"+q_id+")");
              for(int i=0;i<flow_list.size();i++){
                  if(flow_list.get(i).getID().equals(flow_id)) {
                     JSONObject ret=flow_list.get(i).ClearQ(mngr_id,q_id); 
                     root.put("resp", ret);
                     logger.trace("group.ClearQ(group:"+id+",flow:"+flow_id+",mngr:"+mngr_id+",q:"+q_id+") ret:"+ret);
                     break;
                  }
              }
              
              return root;

       }
       @Override
       public void close() {
              if(flow_list==null)return;
              for(int i=0;i<flow_list.size();i++){
                  flow_list.get(i).close();
              }
              flow_list.clear();
              flow_list=null;
       }

}