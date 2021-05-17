package org.little.mailWeb.folder;
                     
import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONObject;
import org.little.lmsg.lMessage;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class folderLogFile implements folderARH{
       private static final Logger logger = LoggerFactory.getLogger(folderLogFile.class);
       public folderLogFile() {}


       @Override
       public synchronized void   open          ()             {                                   }
       @Override
       public synchronized void   close         ()             {                                   }
       @Override
       public  boolean            create        ()             {return true;                       }
       @Override
       public void                save          (lMessage msg) { logger.info("-- "+msg.toString());}
       @Override
       public ArrayList<lMessage> loadArray     (String _type) {return new ArrayList<lMessage>();  }
       @Override
       public JSONObject          loadJSON      (String _type) {return new JSONObject();           }
       @Override                 
       public lMessage            loadArray     (int _uid    ) {return new lMessage();             }
       @Override                 
       public JSONObject          loadJSON      (int _uid    ) {return new JSONObject();           }
       @Override                 
       public JSONObject          loadJSONX509  (String _type) {return new JSONObject();           }
       @Override                 
       public JSONObject          loadJSONX509  (int _uid    ) {return new JSONObject();           }
       @Override                 
       public lMessage            loadArrayX509 (int _x509_id) {return new lMessage();             }
       @Override
       public JSONObject          loadJSONAlarm (Timestamp alarm,Timestamp curent){return new JSONObject();           }
       @Override
       public ArrayList<lMessage> loadArrayAlarm(Timestamp alarm){return new ArrayList<lMessage>();  }
       @Override
       public void                setSend       (int _x509_id) {                                   }    
       @Override
       public lMessage            loadSRL       (int   _id   ) {return new lMessage();             }

}
