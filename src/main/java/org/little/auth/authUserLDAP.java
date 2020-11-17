package org.little.auth;

//import org.little.util.Logger;
//import org.little.util.LoggerFactory;


public class authUserLDAP implements authUser {

       //private static final Logger LOG = LoggerFactory.getLogger(authUser.class);
    
       private String   realm;
       private String   ldap_url;
       private String   domain;
    
       public authUserLDAP(){
              realm="vip.cbr.ru";
              ldap_url="ldap://rdc22-vip01.vip.cbr.ru:3268";
              domain="vip.cbr.ru";
       }
       public authUserLDAP(String r){
              ldap_url="ldap://rdc22-vip01.vip.cbr.ru:3268";
              loadUsers4Realm(r);
       }
       public String    getRealm(){return realm;}
       public void      setRealm(String r){realm=r;}
       public String    getDomain(){return domain;}
       public void      setDomain(String r){domain=r;}

       private void      loadUsers4Realm(String _realm){
              setRealm(_realm);
       }
    
       @Override
       public boolean   checkUser(String user,String passwd){
              boolean ret= serviceLDAP.auth(user,passwd,getRealm(),ldap_url);
              return ret;
       }
       public boolean   isUser(String user){
              boolean ret= false;
              return ret;
       }
       @Override
       public String getFullUserName(String username){
              return serviceLDAP.getFullName(username,getRealm());
       }
       @Override
       public String    getFullUserName(String username,String domain){
              return serviceLDAP.getFullName(username,domain);
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

