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

       public boolean    getStateGroup();

       public JSONObject getStat();

       public void       work();
       
       public JSONObject setFlag(String flow_id,boolean is_flag);

       public JSONObject ClearQ(String flow_id,String mngr_id,String q_id);

       public void       close();
       

}
