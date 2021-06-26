package org.little.key;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CRLEntryHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;

import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import org.little.util.*;


public class X509 {
       //---------------------------------------------------------------------------------------------------------------
       //  X.509 Certificate
       public static void parseX509CERDER2MSG(byte [] buf){
               String          type="CERTIFICATE";
               InputStream     in  =null;
               X509Certificate cert=null;
               try{
                   try{
                       CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                       in   = new ByteArrayInputStream(buf);
                       cert = (X509Certificate) certFactory.generateCertificate(in);
                   }
                   catch (Exception e){
                             System.out.println("DER NO! e:"+e.getClass().getName());
                             Except ex=new Except(e);
                             System.out.println("DER NO! ex:"+ex);
                          return ;
                   }
                   try{
                       BigInteger n      =cert.getSerialNumber();
                       String     n_str=n.toString      ();
                       String     issuer;
                       issuer  =  cert.getIssuerX500Principal().toString();
                       issuer  =  cert.getIssuerDN().toString().toString();
                       String     subject;
                       subject =  cert.getSubjectX500Principal().toString();
                       subject =  cert.getSubjectDN().toString();
                       Date       start  =cert.getNotBefore();
                       Date       end    =cert.getNotAfter();   
                        
                   System.out.println("DER Ok!");
                   System.out.println("subject:"+subject);
                   try{LogHexDump.dump(buf);}catch (Exception e111){}
                   }
                   catch (Exception e){
                             System.out.println("DER NO! e:"+e.getClass().getName());
                             Except ex=new Except(e);
                             System.out.println("DER NO! ex:"+ex);
                         return ;
                   }

                      
               }     
               finally {
                  if(in!=null)try {in.close();     } catch (Exception e){}
               }
              
       }
       

       public static void main(String[] args) {
              FileInputStream in;

              byte[] buf=null;
              try{
                  in=new FileInputStream(args[0]);
                  buf=_ByteBuilder.toByte(in);
              }
              catch (Exception e){
                     System.out.println("ex:"+e);
                     return ;
              }

              parseX509CERDER2MSG(buf);

       }

}
