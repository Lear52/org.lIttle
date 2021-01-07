package org.little.https;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;


public final class HttpsAppFile {
    

    public static void main(String[] args) throws InterruptedException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException, IOException {
        KeyStoreData keyStoreData;
        String keystoreFilePath="var/ssl/certificates.p12";
        char[] keystorePassword={'1','2','3','4','5','6'};
        String alias="lear";
        char[] entryKeyPassword={'1','2','3','4','5','6'};

        keyStoreData=KeyStoreData.from(keystoreFilePath, keystorePassword, alias,entryKeyPassword);

        SslContext context=SslContextBuilder.forServer(keyStoreData.getKey(),keyStoreData.getCertificateChain()).build();

        HttpsServer.start(context, 8443);
    }



}