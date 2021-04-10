package org.little.mailWeb;
             
import java.util.ArrayList;

import org.json.JSONObject;
import org.little.lmsg.lMessage;

public interface folderARH{


       public void                open();
       public void                close();
       public void                save(lMessage msg);
       public ArrayList<lMessage> loadArray(String _type);
       public lMessage            loadArray(int _uid);
       public JSONObject          loadJSON(String _type);
       public JSONObject          loadJSON(int _uid);
       

}
