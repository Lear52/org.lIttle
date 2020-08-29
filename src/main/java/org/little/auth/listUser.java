package org.little.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.utilTransform;

public class listUser implements authUser {
        private static final Logger  logger = LoggerFactory.getLogger(listUser.class);

        public void load() {
                logger.trace("load ok");            
        }

        public String  getFullUserName(String username){
               return getFullUserName(username,"cbr.ru");
        }
        public String  getFullUserName(String username,String domain){
               return username+"@"+domain;
        }
        public String  getShortUserName(String username){
               return username;
        }

        public boolean isUser(String user) {
               if("av".equals(user)) return true;
               if("iap".equals(user)) return true;
                
                return false;
        }
        public boolean checkUser(String user,String passwd) {
               if(isUser(user) && "123".equals(passwd)) return true;
               return false;
        }

        private static final String DIGEST_REALM = "FESBLoginService";

        public String getDigestUser(String user) {
               return getDigestUser(user,DIGEST_REALM);
        }

        public String getDigestUser(String user,String realm) {
               if(!isUser(user)) return null;
               String password="123";
               String ha1 = utilTransform.getMD5Hash(user + ":" + realm + ":" + password);
               return ha1;

        }
        public boolean checkVisible(String user1,String user2) {
               return true;
        }
            
}
