package org.little.smtp.element;

import org.little.auth.authUser;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util._Base64;
import org.little.smtp.commonSMTP;

/**
 * models an ongoing mail transaction
 */
public class SmtpAuthTransaction {
       private static final Logger logger = LoggerFactory.getLogger(SmtpAuthTransaction.class);

       private String   type;
       private String   username;
       private String   password;
       private authUser auth_user;
       private boolean  is_auth_user;
 
       public SmtpAuthTransaction() {
              clear(false);

       }
       public SmtpAuthTransaction(boolean preset) {
              clear(preset);
       }
       private void clear(boolean preset) {
              logger.info("Begin new SNMP auth transaction pre set:"+preset);
               if(preset){
                 type="PRESET";
                 username="nouser";
                 password="";
                 auth_user=commonSMTP.get().getCfgAuth().getAuthUser();
                 is_auth_user=true;
               }
               else{
                 type="";
                 username=null;
                 password=null;
                 auth_user=commonSMTP.get().getCfgAuth().getAuthUser();
                 is_auth_user=false;
               }

       }
       public boolean  isLogin() {return username!=null;}
       public boolean  isTypePlain() {return type.startsWith("PLAIN");}
       public boolean  isTypeLogin() {return type.startsWith("LOGIN");}
       public boolean  isTypePreset() {return type.startsWith("PRESET");}      

       public boolean  isAuth() {return is_auth_user;}
       public void     setAuth(boolean _auth) {is_auth_user=_auth;}

       public String   getType(){return type;}
       public String   getLogin(){return username;}
       
       public void     setType(CharSequence _type){if(_type!=null)type=_type.toString();}


       public void     setPlain(CharSequence arg_plain) {
              byte [] b_plain=_Base64.base64ToByteArray(arg_plain.toString());
              logger.trace("PLAIN:"+new String(b_plain));
              int n_1=0;
              int n_2=0;
              int i;
              for(i=0;i<b_plain.length;i++) if(b_plain[i]==0) {n_1=i;break;}
              for(i=n_1+1;i<b_plain.length;i++) if(b_plain[i]==0) {n_2=i;break;}
              if(n_2==0)return;                                            // 012345678
              if(n_2==(b_plain.length-1))return; // password is null          av0av0123

              username=new String(b_plain,n_1+1,n_2-n_1-1);
              password=new String(b_plain,n_2+1,b_plain.length-n_2-1);

              logger.trace("user:"+username+" passwd:"+password);
                  
       }


       public void     setLogin(CharSequence arg_plain) {
              username=_Base64.Base64ToStr(arg_plain.toString());
              logger.trace("login:"+username);
       }        
       public void     setPasswd(CharSequence arg_plain) {
              password=_Base64.Base64ToStr(arg_plain.toString());
              logger.trace("password:"+password);
       }        
               

       public boolean  checkUser() {
              boolean  ret;
              ret=(username!=null&&password!=null);
              if(ret!=false){
                 if(commonSMTP.get().isProxy()==false){
                    ret=auth_user.checkUser(username, password); 
                 }
              }
              is_auth_user=ret;

              logger.trace("check auth state:"+isAuth()+" login:"+username+" password:"+password);

              return isAuth();
       }


}
