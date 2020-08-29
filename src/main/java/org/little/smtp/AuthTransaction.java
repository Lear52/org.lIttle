package org.little.smtp;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util._Base64;

/**
 * models an ongoing mail transaction
 */
public class AuthTransaction {

        private static final Logger logger = LoggerFactory.getLogger(AuthTransaction.class);
        private String type;
        private String username;
        private String password;
 
        public AuthTransaction() {
               logger.info("Begin new authTransaction");
               type="";
               username=null;
               password=null;
        }

        public String   getType(){return type;}
        
        public boolean  isLogin() {return username!=null;}
        
        public void     setType(CharSequence _type){if(_type!=null)type=_type.toString();}

        public boolean  isTypePlain() {return type.startsWith("PLAIN");}

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

        public boolean  isTypeLogin() {return type.startsWith("LOGIN");}

        public void     setLogin(CharSequence arg_plain) {
                        username=_Base64.Base64ToStr(arg_plain.toString());
                        logger.trace("login:"+username);
        }        
        public void     setPasswd(CharSequence arg_plain) {
                        password=_Base64.Base64ToStr(arg_plain.toString());
                        logger.trace("password:"+password);
        }        
                
        public boolean  isAuth() {
                        logger.trace("check auth state:"+(username!=null&&password!=null)+" login:"+username+" password:"+password);
                        return (username!=null&&password!=null);
        }


}
