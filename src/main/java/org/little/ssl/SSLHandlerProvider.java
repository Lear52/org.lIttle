package org.little.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;


public class SSLHandlerProvider {
       private static final Logger logger = Logger.getLogger(SSLHandlerProvider.class);

       public static SslHandler getSSLSelfHandler(commonSSL ssl_cfg){
           SslContext        sslCtx         =null;
           try {
               SelfSignedCertificate ssc = new SelfSignedCertificate();
               sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
               
               SSLEngine  engine=sslCtx.newEngine(PooledByteBufAllocator.DEFAULT);
       
               //return new SslHandler(engine, true);
               return new SslHandler(engine);
           } catch(Exception e) {
               logger.error("Failed to establish TLS!", e);
               return null;
           }
       }

       private static SslHandler getSSLHandlerKeyfile(commonSSL ssl_cfg){

              SslContext        sslCtx=null;
              SslContextBuilder context_builder =null;
              if(ssl_cfg.isSSL()==false)return null;
              if(ssl_cfg.getUseKeystore()==true)return null;

              try {
                 File _certificate=new File(ssl_cfg.getCertificate());
                 File _privatekey=new File(ssl_cfg.getPrivateKey());
                 //File _trastcert=new File("rootCA.pem");
                 context_builder = SslContextBuilder.forServer(_certificate, _privatekey);
                 context_builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                 context_builder.sslProvider(SslProvider.JDK);
                 sslCtx = context_builder.build();
                 //ssl_cfg.setSSLContext(serverSSLContext);
                 /*
                    File   key         = new File(SmtpConfig.getTlsKeyFile());
                    String keyPassword = SmtpConfig.getTlsKeyPassword();
                    File trustStore    = new File(SmtpConfig.getTlsTrustStoreFile());
             
                    SslContext sslCtx = SslContextBuilder.forServer(key, trustStore, keyPassword).build();
                 */
                 SSLEngine  engine=sslCtx.newEngine(PooledByteBufAllocator.DEFAULT);
                 //return new SslHandler(engine, true);
                 return new SslHandler(engine);
             
              } catch(Exception e) {
                    logger.error("Failed to establish TLS!", e);
                    return null;
              }

       }

       private static SslHandler getSSLHandlerKeystore(commonSSL ssl_cfg){
              if(ssl_cfg.isSSL()         ==false)return null;
              if(ssl_cfg.getSSLContext() ==null )return null;
              if(ssl_cfg.getUseKeystore()==false)return null;
              
              logger.info("create SSL handler ...");
              //javax.net.ssl.SSLEngine
              SSLEngine engine;
              //sslEngine = ssl_cfg.getSSLContext().createSSLEngine();
              engine = ssl_cfg.getSSLContext().createSSLEngine("localhost",8080);
              engine.setUseClientMode(ssl_cfg.getUseClientMode());
              engine.setNeedClientAuth(ssl_cfg.getNeedClientAuth());
              //sslEngine.
              return new SslHandler(engine);
              //return new SslHandler(engine, true);
       }
       public static SslHandler getSSLHandler(commonSSL ssl_cfg){
              
              if(ssl_cfg.getUseKeystore()==true)return getSSLHandlerKeystore(ssl_cfg);
              return getSSLHandlerKeyfile(ssl_cfg);

       }
       public static void initSSLContext (commonSSL ssl_cfg) {
              // javax.net.ssl.SSLContext
              if(ssl_cfg.isSSL()         ==false)return;
              if(ssl_cfg.getUseKeystore()==false)return;
                
              logger.info("Initiating SSLcontext ...");
              //String algorithm = Security.getProperty(ALGORITHM);
              //if (algorithm == null) {
              //    algorithm = ALGORITHM_SUN_X509;
              //}
              SSLContext serverSSLContext   = null;
              KeyStore    ks = null;
              // load KeyStore           
              InputStream inputStream=null;    
              try {
                  inputStream = new FileInputStream(ssl_cfg.getKeyStore());//KEYSTORE = "certificates.jks"
                  ks = KeyStore.getInstance(ssl_cfg.getKeyStoreType());//KEYSTORE_TYPE = "JKS"
                  String psw=ssl_cfg.getKeyStorePassword();
                  ks.load(inputStream,psw.toCharArray());//KEYSTORE_PASSWORD
                  
              } catch (IOException e) {
                  logger.error("Cannot load the keystore file:"+ssl_cfg.getKeyStore(),e);
              } catch (CertificateException e) {
                  logger.error("Cannot get the certificate",e);
              }  catch (NoSuchAlgorithmException e) {
                  logger.error("Somthing wrong with the SSL algorithm",e);
              } catch (KeyStoreException e) {
                  logger.error("Cannot initialize keystore file:"+ssl_cfg.getKeyStore(),e);
              } finally {
                  try {
                      if(inputStream!=null)inputStream.close();
                  } catch (IOException e) {
                      logger.error("Cannot close keystore file:"+ssl_cfg.getKeyStore(),e);
                  }
              }
             
              try {
                  // Set up key manager factory to use our key store
                  KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                  String psw=ssl_cfg.getCertPassword();
                  kmf.init(ks,psw.toCharArray());//CERT_PASSWORD
                  
                  KeyManager[] keyManagers = kmf.getKeyManagers();
                  
                  // Setting trust store null since we don't need a CA certificate or Mutual Authentication
                  TrustManager[] trustManagers = null;
                  {
                      trustManagers = new TrustManager[] { new X509TrustManager() {
                          // TrustManager that trusts all servers
                          @Override
                          public void checkClientTrusted(X509Certificate[] arg0,String arg1) throws CertificateException {
                          }
             
                          @Override
                          public void checkServerTrusted(X509Certificate[] arg0,String arg1) throws CertificateException {
                          }
             
                          @Override
                          public X509Certificate[] getAcceptedIssuers() {
                              return null;
                          }
                      } };
             
                  }
             
             
                  serverSSLContext = SSLContext.getInstance(ssl_cfg.getTypeSSLContext());//"TLS"
                  //SSLContext.setCipherSuite(serverSSLContext, "ALL", false);
                  serverSSLContext.init(keyManagers, trustManagers, null);
                  ssl_cfg.setSSLContext(serverSSLContext);
             
              } catch (Exception e) {
                  logger.error("Failed to initialize the server-side SSLContext",e);
              }
              logger.info("Initiating SSLcontext OK");

    }


}
