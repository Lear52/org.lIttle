package org.little.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class authUserNegotiate implements authUser {

       private static final Logger logger = LoggerFactory.getLogger(authUserNegotiate.class);
    
       private String   realm;
       private String   ldap_url;
       private String   domain;
    
       public authUserNegotiate(commonAUTH cfg_auth){
              realm=cfg_auth.getRealm();
              ldap_url=cfg_auth.getLdapUrl();
              domain=cfg_auth.getDefaultDomain();
              logger.info("create authUserNegotiate");
       }
       /*
       public authUserNegotiate(String r){
              logger.info("create authUserNegotiate");
       }
       */
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
        public String  getFullUserName(String username){
               return getFullUserName(username,domain);
        }
        public String  getFullUserName(String username,String domain){
               if(username==null)return null;
               if(username.indexOf('@')>= 0)return username;
               return username+"@"+domain;
        }
        public String  getShortUserName(String username){
               if(username==null)return null;
               int p=username.indexOf('@');
               if(p>= 0){
                 return username.substring(0,p);
               }
               return username;
        }
       @Override
       public String getDigestUser(String user) {return null;}
    
       @Override
       public String getDigestUser(String user,String realm) {return null;}
       @Override
       public boolean checkVisible(String user1,String user2) {
                  return true;
       }

  
}

