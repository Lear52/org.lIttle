package org.little.ssl;

import javax.net.ssl.SSLContext;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * 
 */
public class commonSSL{

       private static final Logger logger = LoggerFactory.getLogger(commonSSL.class);


       private String keystore_filename   ;
       private String keystore_type       ;
       private String keystore_password   ;
       private String certificate_password;

       private String certificate         ;
       private String privatekey          ;

       private boolean is_ssl             ;
       private boolean is_use_client_mode ;
       private boolean is_need_client_auth;
       private boolean is_use_keystore    ;
       private String  ssl_context;
       private String  key_alias;

       private SSLContext serverSSLContext   = null;


       public commonSSL(){clear();}

       public void clear(){
              keystore_filename    = "certificates.jks";
              keystore_type        = "JKS";
              keystore_password    = "123456";
              certificate_password = "123456";
              key_alias            = "lear";
              is_ssl               = false;
              is_use_keystore      = true;
              is_use_client_mode   = false;
              is_need_client_auth  = false;
              ssl_context          = "TLS";         
              serverSSLContext     = null;
              certificate          ="certificate.pem";
              privatekey           ="privateKey.key";
       }
       public void init(NodeList glist){
              if(glist==null) return;
              logger.info("The configuration ssl");
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("ssl"                 .equals(n.getNodeName())){String s=n.getTextContent(); try{is_ssl=Boolean.parseBoolean(s);             }catch(Exception e){ is_ssl=false;             logger.error("ssl:"+s);}              logger.info("ssl:"+is_ssl);}
                  else
                  if("use_client_mode"     .equals(n.getNodeName())){String s=n.getTextContent(); try{is_use_client_mode=Boolean.parseBoolean(s); }catch(Exception e){ is_use_client_mode=false; logger.error("use_client_mode:"+s);}  logger.info("use_client_mode:"+is_use_client_mode);}
                  else
                  if("use_keystore"        .equals(n.getNodeName())){String s=n.getTextContent(); try{is_use_keystore=Boolean.parseBoolean(s);    }catch(Exception e){ is_use_keystore=true;     logger.error("use_keystore:"+s);}     logger.info("use_keystore:"+is_use_keystore);}
                  else
                  if("need_client_auth"    .equals(n.getNodeName())){String s=n.getTextContent(); try{is_need_client_auth=Boolean.parseBoolean(s);}catch(Exception e){ is_need_client_auth=false;logger.error("need_client_auth:"+s);} logger.info("need_client_auth:"+is_use_client_mode);}
                  else
                  if("keystore_filename"   .equals(n.getNodeName())){keystore_filename=n.getTextContent();    logger.info("keystore_filename:"+keystore_filename);}
                  else
                  if("keystore_type"       .equals(n.getNodeName())){keystore_type=n.getTextContent();        logger.info("keystore_type:"+keystore_type);}
                  else
                  if("keystore_password"   .equals(n.getNodeName())){keystore_password=n.getTextContent();    logger.info("keystore_password:**************");}
                  else
                  if("certificate_password".equals(n.getNodeName())){certificate_password=n.getTextContent(); logger.info("certificate_password:**************");}
                  else
                  if("type_ssl_context"    .equals(n.getNodeName())){ssl_context=n.getTextContent(); logger.info("type_ssl_context:"+ssl_context);}
                  else
                  if("key_alias"           .equals(n.getNodeName())){key_alias=n.getTextContent();   logger.info("key_alias:"+key_alias);}
                  else
                  if("certificate"         .equals(n.getNodeName())){certificate=n.getTextContent(); logger.info("certificate:"+certificate);}
                  else
                  if("privatekey"          .equals(n.getNodeName())){privatekey=n.getTextContent();  logger.info("privatekey:"+privatekey);}
              }
       }
       public String     getKeyStore        () {return keystore_filename;   }
       public String     getKeyStoreType    () {return keystore_type;       }
       public String     getKeyStorePassword() {return keystore_password;   }
       public String     getCertPassword    () {return certificate_password;}

       public String     getCertificate     () {return certificate;         }
       public String     getPrivateKey      () {return privatekey;          }

       public boolean    getUseClientMode   () {return is_use_client_mode;  }
       public boolean    getNeedClientAuth  () {return is_need_client_auth; }
       public boolean    isSSL              () {return is_ssl;              }
       public boolean    getUseKeystore     () {return is_use_keystore;     }

       public String     getTypeSSLContext  () {return ssl_context;         }
       public String     getKeyAlias        () {return key_alias;           }
       public SSLContext getSSLContext      () {return serverSSLContext;    }
       public void       setSSLContext(SSLContext serverSSLContext) {this.serverSSLContext = serverSSLContext;}


}

