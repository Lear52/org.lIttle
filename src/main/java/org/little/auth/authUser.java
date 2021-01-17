package org.little.auth;

public interface authUser {

        public String    getFullUserName(String username);
        public String    getFullUserName(String username,String domain);
        public String    getShortUserName(String username);
        public boolean   isUser(String user);
        public boolean   checkUser(String user,String passwd);
        public String    getDigestUser(String user);
        public String    getDigestUser(String user,String realm);
        public boolean   checkVisible(String user1,String user2);

}

