package org.little.web;

import  java.security.MessageDigest;
import  java.util.Random;

import  javax.servlet.http.Cookie;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;

import org.little.util.Except;

class  webCookie{

       protected static final int    SESSION_ID_BYTES = 64;
       protected static final String DEFAULT_ALGORITHM = "MD5";
       protected String              cookie_url;
       protected int                 cookie_timeout=-1;
       protected String              cookie_name="PRJ0-COOKIE-ID";
       protected Random              random;

       protected synchronized String generateSessionId() throws Except{
               // Generate a byte array containing a session identifier
               byte bytes[] = new byte[SESSION_ID_BYTES];

               random.nextBytes(bytes);

               try{
                   MessageDigest d=MessageDigest.getInstance(DEFAULT_ALGORITHM);
                   bytes = d.digest(bytes);
               }
               catch(Exception ex){
                   throw  new Except("MD5",ex);
               }
               // Render the result as a String of hexadecimal digits
               StringBuffer result = new StringBuffer();
               for(int i = 0; i < bytes.length; i++) {
                   byte b1 = (byte) ((bytes[i] & 0xf0) >> 4);
                   byte b2 = (byte) (bytes[i] & 0x0f);
                   if(b1<10)result.append((char) ('0' + b1));
                   else     result.append((char) ('A' + (b1 - 10)));
                   if(b2<10)result.append((char) ('0' + b2));
                   else     result.append((char) ('A' + (b2 - 10)));
               }
       return result.toString();
       }

       public webCookie(){
            cookie_url="/";
            random = new java.util.Random(System.currentTimeMillis());
       }
       public webCookie(String url){
            cookie_url=url;
            random = new java.util.Random(System.currentTimeMillis());
       }


       public String get(HttpServletRequest request,HttpServletResponse response) throws Except { 
       
            try{
               Cookie[] cookies = request.getCookies();
               if(cookies==null)return null;
       
               for (int i = 0; i < cookies.length; i++) {
                   Cookie c = cookies[i];
                   String name = c.getName();
                   if(cookie_name.equals(name)){
                      return c.getValue();
                   }
               }
            }
            catch(Exception ex){
                  throw new Except("Can't get auth cookie",ex);
            }
       
       return null;
       }
 

       public String set(HttpServletRequest request,HttpServletResponse response) throws Except{ 
       String cookie_value=null;
       
            try{
               cookie_value=generateSessionId();
            }
            catch(Except e){
                  throw new Except(e);
            }
       
            try{
               Cookie c;
               c = new Cookie(cookie_name,cookie_value);
               c.setPath(cookie_url);
               c.setMaxAge(cookie_timeout); 
               response.addCookie(c);
            }
            catch(Exception ex){
                  throw new Except("Can't set auth cookie",ex);
            }
       
       return cookie_value;
       }


//----------------------------------------------------------------------------
}
