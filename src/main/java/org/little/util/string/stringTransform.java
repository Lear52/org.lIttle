package org.little.util.string;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util._Base64;

public class stringTransform {
       private static final Logger  logger = LoggerFactory.getLogger(stringTransform.class);
            
       /**
        *
        * @param str_buffer
        * @return MD5-hash
        * @throws Exception
        */
       private static final char[] hexChars=new char[] {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

       public  static String getMD5Hash(String str_buffer) {
               return getMD5Hash(str_buffer.getBytes());
       }
       public static String toMD5(String password){
              if(isEmpty(password))return "0";
              MessageDigest mdig;
              try {mdig = MessageDigest.getInstance("MD5");} catch (NoSuchAlgorithmException e) {Except ex=new Except(e);logger.error(ex);return null;}
              mdig.reset();
              mdig.update(password.getBytes(Charset.forName("UTF-8")));
        return getHex(mdig.digest());
       }

       public static String getMD5Hash(byte []byte_buffer) {
              MessageDigest md;
              try{md = MessageDigest.getInstance("MD5");}catch(Exception e){Except ex=new Except(e);logger.error(ex);return null;}
              md.update(byte_buffer);
              byte []md5_buffer = md.digest();
              return getHex(md5_buffer);
       }
       public static String getHEX(String str_buffer) {
              //if(false)return getHex(str_buffer.getBytes());
              //else     
              return getHex(str_buffer.toCharArray());
       }
       
       public static String getHex(byte []byte_buffer) {
              StringBuffer result=new StringBuffer();
              for (int i=0; i<byte_buffer.length; i++) {
                  result.append(hexChars[byte_buffer[i]>>4 & 0x0F]);
                  result.append(hexChars[byte_buffer[i] & 0x0F]);            
              }
              return result.toString();
       }
       public static String getHex(char []char_buffer) {
              StringBuffer result=new StringBuffer();
              for (int i=0; i<char_buffer.length; i++) {
                  char c=char_buffer[i];
                  int  i_c=Character.digit(c, 16);
                  if(i_c<0)return null;
                  result.append(hexChars[i>>4 & 0x0F]);
                  result.append(hexChars[i    & 0x0F]);            
              }
              return result.toString();
       }

       public static String PasswordToLdapMd5(String password){
              char [] char_passwd=password.toCharArray();
              String hex_passwd    = getHex(char_passwd);
              String base64_passwd = _Base64.byteArrayToBase64(hex_passwd.getBytes());
              return "{md5}"+base64_passwd;
              /*
              @SneakyThrows
              public static String hexToLdapMd5(String password){
                  return "{md5}"+Base64.encodeBase64String(Hex.decodeHex(password.toCharArray()));
              }
              */
       }

       public static String LdapMd5ToPassword(String password){
              String hex_password       = password.substring("{md5}".length());
              byte[] decoded_password   = _Base64.base64ToByteArray(hex_password);
              String str_password       = new String(decoded_password, Charset.forName("UTF-8"));
                  /*
               public static String ldapMd5ToHex(String password){
               return Hex.encodeHexString(Base64.decodeBase64(password.substring("{md5}".length())));
              */
               return str_password;
       }

       public static boolean isEmpty(String cs){
              int strLen;
              if (cs == null || (strLen = cs.length()) == 0) {
                  return true;
              }
              for (int i = 0; i < strLen; i++) {
                  if (!Character.isWhitespace(cs.charAt(i))) {
                      return false;
                  }
              }
              return true;
           
       }
      
       public static String substringBefore(String str, String separator){
              if(isEmpty(str) || separator == null)return str;
              if(separator.isEmpty()              )return "";
              int pos = str.indexOf(separator);
              if(pos == -1)return str;
              else         return str.substring(0, pos);
       }
      
       public static String substringAfter(String str, String separator){
              if(isEmpty(str)     )return str;
              if(separator == null)return "";
              int pos = str.indexOf(separator);
              if(pos == -1)return "";
              else         return str.substring(pos + separator.length());
       }
        
}
