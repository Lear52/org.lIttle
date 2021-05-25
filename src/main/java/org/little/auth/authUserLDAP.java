package org.little.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class authUserLDAP implements authUser {

       private static final Logger logger = LoggerFactory.getLogger(authUserLDAP.class);
    
       private String   realm;
       private String   ldap_url;
       private String   domain;
    
       public authUserLDAP(){
              clear();
              logger.info("create authUserLDAP");
       }
       public authUserLDAP(String _ldap_url,String _realm,String _domain){
              realm   =_realm;
              ldap_url=_ldap_url;
              domain  =_domain;
              logger.info("create authUserLDAP");
       }
       public authUserLDAP(commonAUTH cfg_auth){
              realm   =cfg_auth.getRealm();
              ldap_url=cfg_auth.getLdapUrl();
              domain  =cfg_auth.getDefaultDomain();
              logger.info("create authUserLDAP");
       }
       private void clear(){
               realm="";   
               ldap_url="";
               domain="";         
       }
       public String    getRealm(){return realm;}
       public void      setRealm(String r){realm=r;}
       public String    getDomain(){return domain;}
       public void      setDomain(String r){domain=r;}

       //private void      loadUsers4Realm(String _realm){
       //       setRealm(_realm);
       //}
    
       @Override
       public boolean   checkUser(String user,String passwd){
              boolean ret = serviceLDAP._auth(getFullUserName(user,getDomain()),passwd,ldap_url);
              return ret;
       }
       public boolean   isUser(String user){
              boolean ret= false;
              return ret;
       }
       @Override
       public String getFullUserName(String username){
              return serviceLDAP.getFullName(username,getDomain());
       }
       @Override
       public String  getFullUserName(String username,String domain){
              int id=domain.indexOf("{}");
              String ret;
              if(id>=0){
                 if(id+2>=domain.length()) ret=new StringBuilder().append(domain.substring(0,id)).append(username).toString();
                 else{
                      ret=new StringBuilder().append(domain.substring(0,id)).append(username).append(domain.substring(id+2)).toString();
                 }
              }
              else ret=new StringBuilder().append(username).append(domain).toString();
              return ret;
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
       public boolean checkVisible(String user1,String user2) {
                  return true;
       }

  
}

