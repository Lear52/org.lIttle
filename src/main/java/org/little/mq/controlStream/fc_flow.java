package org.little.mq.controlStream;

import java.util.ArrayList;

//import org.json.JSONArray;
import org.json.JSONObject;
//import org.little.util.Logger;
//import org.little.util.LoggerFactory;
import org.w3c.dom.Node;

public class fc_flow{
       //private static final Logger logger = LoggerFactory.getLogger(fc_flow.class);

       private   String          id;
       private   String          name;
       protected ArrayList<fc_Q> q_list;
       protected fc_control      flow_contrl;
       private   boolean         state;
       private   boolean         is_alarm;

       public fc_flow() {
              clear();
              flow_contrl=new fc_control(); 
       }
       protected void clear() {
              id=null;
              name=null;
              q_list=new ArrayList<fc_Q>();
              state=false;
       }
       protected ArrayList<fc_Q> getFlow(){return q_list;}
       public String             getID() {return id;}
       public void               setID(String id) {this.id = id;       }
       public String             getName() {return name;}
       public void               setName(String name) {this.name = name;}
       public boolean            isAlarm()            {return is_alarm;}
       public void               isAlarm(boolean a)   {is_alarm=a;}
       
       public    JSONObject      getStat(){return null;}
                                
       public    void            work(){}
                                
       protected void            setState(JSONObject root){}
                                
       protected JSONObject      setFlag(boolean flag) {return null;}

       protected JSONObject      ClearQ(String mngr_id,String q_id){return null;}
                                
       public    void            init(Node n) {}
                                
       public    void            setState(boolean s) {state=s;}
                                
       public    boolean         getState()          {return state;}

       public    void            close() {
                 if(q_list==null)return;
                 for(int i=0;i<q_list.size();i++){
                     fc_Q q=q_list.get(i);
                     q.close();
                 }
                 q_list.clear();
                 q_list=null;
       }

       
}