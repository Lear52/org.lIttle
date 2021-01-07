package org.little.ssl;

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

import org.little.util.Logger;

import io.netty.handler.ssl.SslHandler;


public class SSLHandlerProvider0 {
    private static final Logger logger = Logger.getLogger(SSLHandlerProvider.class);

    private static final String KEYSTORE         = "certificates.jks";
    private static final String KEYSTORE_TYPE    = "JKS";
    private static final String KEYSTORE_PASSWORD= "123456";
    private static final String CERT_PASSWORD    = "123456";
    private static SSLContext serverSSLContext   = null;

    public static SslHandler getSSLHandler(){
        SSLEngine sslEngine;
        sslEngine = serverSSLContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        sslEngine.setNeedClientAuth(false);

        return new SslHandler(sslEngine);
    }

    public static void initSSLContext () {

        logger.info("Initiating SSLcontext ...");
        //String algorithm = Security.getProperty(ALGORITHM);
        //if (algorithm == null) {
        //    algorithm = ALGORITHM_SUN_X509;
        //}
        KeyStore    ks = null;
        InputStream inputStream=null;
        try {
            inputStream = new FileInputStream(KEYSTORE);
            ks = KeyStore.getInstance(KEYSTORE_TYPE);
            ks.load(inputStream,KEYSTORE_PASSWORD.toCharArray());
        } catch (IOException e) {
            logger.error("Cannot load the keystore file",e);
            return;
        } catch (CertificateException e) {
            logger.error("Cannot get the certificate",e);
            return;
        }  catch (NoSuchAlgorithmException e) {
            logger.error("Somthing wrong with the SSL algorithm",e);
            return;
        } catch (KeyStoreException e) {
            logger.error("Cannot initialize keystore",e);
            return;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error("Cannot close keystore file stream ",e);
            }
        }
        try {

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks,CERT_PASSWORD.toCharArray());
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

            serverSSLContext = SSLContext.getInstance("TLS");
            //SSLContext.setCipherSuite(serverSSLContext, "ALL", false);
            serverSSLContext.init(keyManagers, trustManagers, null);


        } catch (Exception e) {
            logger.error("Failed to initialize the server-side SSLContext",e);
            return;
        }
        logger.info("Initiating SSLcontext OK");


    }


}
