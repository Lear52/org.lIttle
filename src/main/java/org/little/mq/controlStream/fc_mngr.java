package org.little.mq.controlStream;

import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.scheduler;
import org.little.util.run.task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.little.util.common;

public class fc_mngr  extends common{
       private static final Logger logger = LoggerFactory.getLogger(fc_mngr.class);
       private ArrayList<fc_group> group_list;
       private ArrayList<task>     task_list;

       public fc_mngr() {
              group_list=new ArrayList<fc_group>();
              task_list=new ArrayList<task>();
              setNodeName("littlestat");
       }
       public void init() {
            init(this.getNode());
       }
       public void init(Node _node_cfg) {
              if(_node_cfg==null)return;

              group_list=new ArrayList<fc_group>();
              task_list =new ArrayList<task>();

              logger.info("The configuration node:"+_node_cfg.getNodeName());
              NodeList glist=_node_cfg.getChildNodes();

              if(glist==null) return;

              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("local".equals(n.getNodeName()) ){
                     fc_groupL fc_grp=new fc_groupL();
                     fc_grp.init(n);
                     group_list.add(fc_grp);
                     task_list.add(fc_grp);
                  }
                  else
                  if("remote".equals(n.getNodeName())){
                     fc_groupR fc_grp=new fc_groupR();
                     fc_grp.init(n);
                     group_list.add(fc_grp);
                     task_list.add(fc_grp);
                  }
              }
              
       }
       
       public void work() {
              for(int i=0;i<group_list.size();i++) {
                  fc_group gr=group_list.get(i);
                  gr.work();
              }                   
       }
       
       public ArrayList<task> getListTask() {return task_list;}


       public JSONObject setFlag(String group_id,String flow_id,boolean is_flag) {
              JSONObject root=new JSONObject();
              root.put("type", "flag");
              for(int i=0;i<group_list.size();i++){
                  if(group_list.get(i).getID().equals(group_id)) {
                	  JSONObject ret=group_list.get(i).setFlag(flow_id,is_flag); 
                      root.put("resp", ret);
                	  break;
                  }
              }


              return root;
       }
       public JSONObject getStat() {
              JSONObject root=new JSONObject();
              JSONArray  list=new JSONArray();
              root.put("type", "local state");
              int count=0;
              for(int i=0;i<group_list.size();i++) {
                      fc_group group=group_list.get(i);
                      if(group instanceof fc_groupL){list.put(count,group.getStat());count++;}
              }
              root.put("list", list);
              root.put("size", count);

              logger.trace("fc_mnmr getStat() size:"+group_list.size());
              
              return root;
       }
       public JSONObject getStatAll() {
              JSONObject root=new JSONObject();
              JSONArray  list=new JSONArray();
              root.put("type", "all");
              for(int i=0;i<group_list.size();i++) {
                      fc_group group=group_list.get(i);
                      list.put(i,group.getStat());
              }
              root.put("list", list);
              root.put("size", group_list.size());

              logger.trace("fc_mnmr getStat() size:"+group_list.size());
              
              return root;
       }

       public static void main(String args[]){
              fc_mngr mngr=new fc_mngr();
              String xpath=args[0];

              if(mngr.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.CONTROLSTREAM "+ver());
              mngr.init();
              logger.info("RUN LITTLE.CONTROLSTREAM "+ver());
              scheduler runner = new scheduler(10);

              ArrayList<task> _task=mngr.getListTask();
              for(int i=0;i<_task.size();i++)runner.add(_task.get(i));
              
              runner.fork();
              //mngr.run();

       }
}
