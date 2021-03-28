package org.little.mq.controlStream;

import org.w3c.dom.Node;
import org.json.JSONObject;


public interface fc_group {

       public void       clear();
                        
       public void       init(Node n);
                        
       public String     getID();
       public void       setID(String id);
       public String     getName();
       public void       setName(String name);

       public boolean    getState();

       public JSONObject getStat();


       public void       work();
       
       public JSONObject setFlag(String flow_id,boolean is_flag);

       

}
