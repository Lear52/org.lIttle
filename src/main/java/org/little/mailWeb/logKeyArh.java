package org.little.mailWeb;
             
import java.util.ArrayList;

import org.json.JSONObject;
import org.little.lmsg.lMessage;

public interface logKeyArh{
       public void open(commonDB cfg);
       public void close();
       public void print(lMessage msg);
       public ArrayList<lMessage> loadArrey();
       public JSONObject loadJSON();
       

}
