package org.little.mq.controlStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class fc_flowL  extends fc_flow{
       private static final Logger logger = LoggerFactory.getLogger(fc_flowL.class);
       private Object  lock;
       private fc_mngr mngr;

       public fc_flowL(fc_mngr _mngr) {
              clear();
              lock=new Object();
              flow_contrl=new fc_control();              
              mngr=_mngr;
       }
       @Override
       public JSONObject getStat() {
              JSONObject root=new JSONObject();
              root.put("type", "flow");
              root.put("id", getID());
              root.put("name", getName());
              JSONArray  list=new JSONArray();
              for(int i=0;i<q_list.size();i++) {
                  fc_Q q=q_list.get(i);
                  list.put(i,q.getState());
              }
              root.put("alarm"  , isAlarm());
              root.put("list"   , list);
              root.put("size"   , q_list.size());
              root.put("control", flow_contrl.getState());

              logger.trace("fc_flowL getStat() size:"+q_list.size());

              return root;
       }


       @Override
       public void work() {
              synchronized (lock){
                   for(int i=0;i<q_list.size();i++) {
                       fc_Q q=q_list.get(i);
                       q.work();
                   }

                   flow_contrl.work();

                   if(isAlarm()){
                      int len=0;
                      for(int i=0;i<q_list.size();i++) {
                          fc_Q q=q_list.get(i);
                          len+=q.getDeepQ();
                      }
                      if(len==0){
                         isAlarm(false);
                         if(mngr.isControlStream())flow_contrl.setFlag(fc_control.RUN);
                      }
                   }
                   else{
                      boolean is_alarm=false;
                      for(int i=0;i<q_list.size();i++) {
                          fc_Q q=q_list.get(i);
                          if(q.isAlarm()){is_alarm=true;break;}
                      }
                      isAlarm(is_alarm);

                      if(is_alarm && mngr.isControlStream()){
                         flow_contrl.setFlag(fc_control.STOP);
                      }
                   }
                   
              }

       }
       
       @Override
       protected JSONObject setFlag(boolean flag) {JSONObject ret=flow_contrl.setFlag(flag); work();return ret;}

       @Override
       public void init(Node node_cfg) {
              clear();
              if(node_cfg==null)return;

              if(node_cfg.getAttributes().getNamedItem("id")==null) {
                 logger.error("The configuration group id:noname");
                 return;
              }
              else  setID(node_cfg.getAttributes().getNamedItem("id").getNodeValue());

              if(node_cfg.getAttributes().getNamedItem("name")==null) {
                 setName("");
              }
              else setName(node_cfg.getAttributes().getNamedItem("name").getNodeValue());

              logger.info("The configuration node:"+node_cfg.getNodeName()+" id:"+getID()+" name:"+getName());

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("q".equals(n.getNodeName()) ){           
                     fc_Q q=new fc_QL();
                     q.init(n);
                     q_list.add(q);
                  }
                  else
                  if("cntrl".equals(n.getNodeName()) ){           
                     flow_contrl=new fc_controlL();
                     flow_contrl.init(n);
                  }
                         
              }
                 
                 
                 
       }
       
}