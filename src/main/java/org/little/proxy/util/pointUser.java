package org.little.proxy.util;

import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class pointUser {

    private static final Logger LOG = LoggerFactory.getLogger(pointUser.class);
    private static final char[] hexChars=new char[] {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    private String userName;
    private String password;
    private String md5_password;
    private String ha1;
    private String realm;
    private String host_port;

    public pointUser(){
                  set("","","");
                  setHost("localhost:8080");
    }
    public pointUser(String u, String p, String r){
                  set(u,p,r);
    }

    public String  getHost          (){return host_port;}
    public String  getUser          (){return userName;}
    public String  getPassword      (){return password;}
    public String  getPasswordMd5   (){return md5_password;}
    public String  getRealm         (){return realm;}
    public String  getH1            (){return ha1;}
    public String  getServerResponse(String pre_serverResponse){return DigestUtils.md5Hex(getH1() + pre_serverResponse);}

    public boolean getServerResponse(String pre_serverResponse,String clientResponse){
           //if(true){
              boolean ret;
              LOG.trace("s:"+pre_serverResponse);/**/
              String  s=getServerResponse(pre_serverResponse);
              ret=s.equals(clientResponse);
              LOG.trace("ret:"+ret+" "+s+" "+clientResponse);
              return ret;
           //}
           //else
           //return getServerResponse(pre_serverResponse).equals(clientResponse);

    }
    public boolean isPassword(String p){return password.equals(p);}

    public void    setHost(String h){ host_port=h;}
    public void    setUser(String s){ userName=s;}
    public void    setPassword(String s){
                   password=s;
                   md5_password=getMD5Hash(password.getBytes());
    }
    public void    setRealm(String r){realm=r;}
    public void    setH1(String r){setRealm(r);ha1 = DigestUtils.md5Hex(getUser() + ":" + getRealm() + ":" + getPassword());}
    public void    set(String u,String p,String r){setUser(u);setPassword(p);setH1(r);}
  
    /**
     *
     * @param buffer
     * @return MD5-
     * @throws Exception
     */
    public static String getMD5Hash(byte []buffer) {
        MessageDigest md;
        try{md = MessageDigest.getInstance("MD5");}catch(Exception ex){return null;}
        md.update(buffer);
        byte []raw = md.digest();
        return getHEXStringFromBytes(raw);
    }
    public static String getHEXStringFromBytes(byte []buffer) {
        StringBuffer result=new StringBuffer();
        for (int i=0; i<buffer.length; i++) {
            result.append(hexChars[buffer[i]>>4 & 0x0F]);
            result.append(hexChars[buffer[i] & 0x0F]);            
        }
        return result.toString();
    }

}

