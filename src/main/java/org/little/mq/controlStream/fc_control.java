package org.little.mq.controlStream;
        
import org.json.JSONObject;
import org.w3c.dom.Node;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class fc_control {
       private static final Logger logger = LoggerFactory.getLogger(fc_control.class);

       private String mq_mngr;
       private String mq_queue;
       private boolean is_manual;
       private boolean is_set_flag;

       public fc_control(){
              clear();
       }
       protected void   clear() {
              mq_mngr   =null;
              mq_queue  =null;
              is_manual  =false;
              is_set_flag=false;
       }

       protected String getNameQ   ()         {return mq_queue;}
       protected void   setNameQ   (String q) {this.mq_queue = q;}
       protected String getNameMngr()         {return mq_mngr;}
       protected void   setNameMngr(String m) {this.mq_mngr = m;}


       public void setState(JSONObject root) {
           isManual(root.optBoolean("is_manual"));
           isFlag  (root.optBoolean("is_flag"  ));
       }
       public JSONObject    getState() {
              JSONObject root=new JSONObject();
              root.put("type"     ,"cntrl");
              root.put("is_manual",isManual());
              root.put("is_flag"  ,isFlag());
              root.put("queue"    ,getNameQ());
              root.put("mngr"     ,getNameMngr());

              return root;
       };
       public void controlFlag(boolean is_flag) {
              if(isManual())return;
              isFlag(is_flag);
       }

       protected boolean isManual() {return is_manual;}
       protected void    isManual(boolean is_manual) {this.is_manual = is_manual;}
       protected boolean isFlag() {return is_set_flag;}
       protected void    isFlag(boolean is_set_flag) {this.is_set_flag = is_set_flag;}

       protected JSONObject setFlag(boolean flag) {
                 JSONObject root=new JSONObject();
                 root.put("control", "ERROR");
                 logger.error("can't set local flag for remote control");
                 return root;
       }
       public void init(Node node_cfg) {}
       public    void       work(){}

}