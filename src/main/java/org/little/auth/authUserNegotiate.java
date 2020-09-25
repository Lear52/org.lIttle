package org.little.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class authUserNegotiate implements authUser {

       private static final Logger LOG = LoggerFactory.getLogger(authUserNegotiate.class);
    
       private String   realm;
       private String   domain;
    
       public authUserNegotiate(){
              realm="vip.cbr.ru";
       }
       public authUserNegotiate(String r){

       }
       public String    getRealm(){return realm;}
       public void      setRealm(String r){realm=r;}

    
       @Override
       public boolean   checkUser(String user,String passwd){
              return false;
       }
       public boolean   isUser(String user){
              boolean ret= false;
              return ret;
       }
       @Override
       public String getFullUserName(String username){
              return null;
       }
       @Override
       public String    getFullUserName(String username,String domain){
              return null;
       }
       @Override
       public String    getShortUserName(String username){
              return username;
       }
       @Override
       public String getDigestUser(String user) {
              return null;
       }
    
       @Override
       public String getDigestUser(String user,String realm) {
              return null;
       }
       @Override
       public boolean checkVisible(String user1,String user2) {
                  return true;
       }

  
}

