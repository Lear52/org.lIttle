package org.little.mq.controlStream;

import org.json.JSONObject;
import org.little.mq.mqapi.commonMQ;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;


public class fc_Q {
       private static final Logger logger = LoggerFactory.getLogger(fc_Q.class);

       protected commonMQ  cfg;
       //private String mq_mngr;
       //private String mq_queue;
       private int    deepQ;
       private boolean is_alarm;

       public fc_Q() {
    	      clear();
       }
       protected void   clear() {
      	       cfg=new commonMQ(); 
  
    	       //this.mq_mngr = "noname_mngr";
    	       //this.mq_queue= "noname_queue";
    	       this.deepQ   = 0;
    	       this.is_alarm=false;
       }
       
       public String  getNameQ()           {return cfg.getNameQ();}
       //public void    setNameQ(String q)   {this.mq_queue = q;}
       public String  getNameMngr()        {return cfg.getNameMngr();}
       //public void    setNameMngr(String m){this.mq_mngr = m;}
                     
       public int     getDeepQ()           {return deepQ;}
       public void    setDeepQ(int _deepQ) {this.deepQ = _deepQ;}

       public boolean isAlarm()            {return is_alarm;}
       public void    isAlarm(boolean a)   {is_alarm=a;}

       public JSONObject getState() {
              JSONObject root=new JSONObject();

              root.put("type" ,"q");
              root.put("queue",cfg.getNameQ());
              root.put("mngr" ,cfg.getNameMngr());
              root.put("len"  ,getDeepQ());
              root.put("alarm",isAlarm());

              return root;
       }
       protected JSONObject ClearQ(){JSONObject root=new JSONObject();return root;}

       protected void setState(JSONObject root) {
                 logger.trace("setStat json:"+root); 
                 try{
                    cfg.setNameMngr(root.getString ("mngr"  ));
                    cfg.setNameQ   (root.getString ("queue" ));
                    setDeepQ   (root.getInt    ("len"   ));
                    isAlarm    (root.getBoolean("alarm" ));
                 }
                 catch(Exception e){
                       logger.trace("setStat json:"+root+" ex:"+new Except("",e)); 
                 }

       }
       public void work(){}
       public void init(Node n){}
       public void close() {
              clear();
       }

}