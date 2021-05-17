package org.little.mailWeb.folder;
             
import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONObject;
import org.little.lmsg.lMessage;

public interface folderARH{

       public void                open();
       public boolean             create();
       public void                close();
       public void                save(lMessage msg);
       public ArrayList<lMessage> loadArray(String    _type);   //load all object with  _type = "X509 CRL" "CERTIFICATE REQUEST" "CERTIFICATE"
       public lMessage            loadArray(int       _uid);    //load all object with  _type = "X509 CRL" "CERTIFICATE REQUEST" "CERTIFICATE"
       public JSONObject          loadJSON(String     _type);   //load all object with  _type = "X509 CRL" "CERTIFICATE REQUEST" "CERTIFICATE"
       public JSONObject          loadJSON(int        _uid);    //load all object with  _type = "X509 CRL" "CERTIFICATE REQUEST" "CERTIFICATE"

       public JSONObject          loadJSONX509(String _type   );//load all object with  _type = "X509 CRL" "CERTIFICATE REQUEST" "CERTIFICATE"
       public JSONObject          loadJSONX509(int    _x509_id);//load object with  x509_id
       public lMessage            loadArrayX509(int   _x509_id);//load object with  x509_id

       public JSONObject          loadJSONAlarm (Timestamp alarm,Timestamp curent);//load all object "CERTIFICATE" 
       public ArrayList<lMessage> loadArrayAlarm(Timestamp alarm);//load all object "CERTIFICATE" 
       public void                setSend(int   _x509_id);

       public lMessage            loadSRL(int   _id);

}
