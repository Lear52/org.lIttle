package org.little.http;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;


public class lHttpCLN {
       private static final Logger logger = LoggerFactory.getLogger(lHttpCLN.class);


       private String  url     ;
       private boolean debug   ;

       public lHttpCLN(){
              debug   = true;
              url       ="http://localhost:8080/upload.php";
       }
       public lHttpCLN(String _url){
              debug   = false;
              url     =_url;
       }
       public void setURL(String url) {
              this.url = url;
       }
       public String get(ByteArrayOutputStream os) throws Exception{
              CloseableHttpClient httpclient =null;
              try {
                   httpclient = HttpClientBuilder.create().build();
                   HttpGet               http_get = new HttpGet(url);
                   CloseableHttpResponse response = null;
                   InputStream is=null;
                   try {
                        response = httpclient.execute(http_get);
                        HttpEntity ent = response.getEntity();
                        if(ent==null) {
                            return null;	
                        }
                        is = response.getEntity().getContent();
                        while(true) {
                                byte [] buf=new byte [1024];
                                int ret=is.read(buf);
                                if(ret<0) {
                                        break;
                                }
                                os.write(buf, 0, ret);
                        }
                        http_get.abort();
                   } 
                   finally {
                       if(is!=null)is.close();
                       response.close();
                   }
      
              } finally {
                 httpclient.close();

                 os.close();
              }
              return null;

       }
       public JSONObject getJSON() throws Exception{
              ByteArrayOutputStream os        =new ByteArrayOutputStream();
              JSONObject            json_root =null;
              CloseableHttpClient   httpclient=null;

              logger.trace("begin get json");

              try {
                   httpclient = HttpClientBuilder.create().build();
                   HttpGet               http_get = new HttpGet(url);
                   CloseableHttpResponse response = null;
                   InputStream           is=null;

                   logger.trace("get httpclient");

                   try {
                        response = httpclient.execute(http_get);
                        if(response==null){
                           return null;
                        }
                        int status = response.getStatusLine().getStatusCode();
                        if (status < 200 || status > 300) {
                            logger.error("httpclient execute code:"+status);
                        	return null;
                        }
                        logger.trace("httpclient execute code:"+status);
                        HttpEntity ent = response.getEntity();
                        if(ent==null) {
                           return null;	
                        }
                        is = ent.getContent();
                        while(true) {
                                byte [] buf=new byte [10240];
                                int ret=is.read(buf);
                                if(ret<0) {break;}
                                os.write(buf, 0, ret);
                        }
                   } 
                   finally {
                       http_get.abort();
                       if(is!=null)is.close();
                       try {
                           if(response!=null)response.close();
                       }
                       catch(Exception e1){
                             logger.trace("ex: "+new Except("httpclient.close",e1));
                             return null;
                       }
                   }
      
              }
              catch(Exception e){
                 logger.trace("ex: "+new Except("get json httpclient",e));
                 return null;
              } 
              finally {
                 try {
                     if(httpclient!=null)httpclient.close();
                 }
                 catch(Exception e1){
                    logger.trace("ex: "+new Except("httpclient.close",e1));
                    return null;
                 }
              }

              logger.trace("httpclient close");
              byte [] b_buf=os.toByteArray();
              if(b_buf==null){
                  logger.error("httpclient get is null");
                  return null;
              }
              if(b_buf.length<3){
                  logger.error("httpclient get is null");
                  return null;
              }
              
              String s_buf=new String(os.toByteArray(),Charset.forName("UTF-8")); 

              logger.trace("httpclient get:"+s_buf);
              if(s_buf==null){
                 logger.error("httpclient get is null");
                 return null;
              }
              //byte[] out = os.toByteArray();
              try {
                  JSONTokener tokener=new JSONTokener(s_buf);

                  json_root=new JSONObject(tokener);
              }
              catch(Exception e){
                 logger.error("ex:"+new Except("JSONTokener",e));
                 return null;
              } 
              logger.trace("get json object");
              os.close();
              

              logger.trace("end get json:"+json_root);
              
              return json_root;
       }
       public void sent(ByteArrayOutputStream os,String filename) throws Exception{
              ByteArrayInputStream is=new ByteArrayInputStream(os.toByteArray());
              
              if(debug)System.out.println("ok!");
              
              HttpClient httpclient = HttpClientBuilder.create().build();
              //httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

              HttpPost http_post = new HttpPost(url);
              File file = new File("zaba_1.jpg");

              MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
              builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
              builder.addBinaryBody(filename,is);              
              FileBody fileBody = new FileBody(file);
              builder.addPart("my_file", fileBody);
              
              HttpEntity entity = builder.build();
              http_post.setEntity(entity);
              
              if(debug)System.out.println("executing request " + http_post.getRequestLine());

              HttpResponse response = httpclient.execute(http_post);
              HttpEntity response_entity = response.getEntity();

              System.out.println(response.getStatusLine());
              
              if (response_entity != null) {
                System.out.println(EntityUtils.toString(response_entity));
              }
              if (response_entity != null) {
                     EntityUtils.consume(response_entity);
              }              
              
              //httpclient.getConnectionManager().shutdown();
              
       }
       public static void main1(String[] args) throws Exception {
              System.setProperty("java.net.preferIPv4Stack","true");
              lHttpCLN cln=new lHttpCLN();
              String  f_name;
              String  url;

              if(args.length>0)url=args[0];
              else             url="http://sa5lear1.vip.cbr.ru:8080/main/doc/law_cb.pdf"; 

              System.out.println("executing request :" + url);

              cln.setURL(url);

              ByteArrayOutputStream os=new ByteArrayOutputStream();
              f_name=cln.get(os);
              byte[] out = os.toByteArray();
              System.out.println("file:"+f_name);
              System.out.write(out);
              
       }
       public static void main(String[] args) throws Exception {
              System.setProperty("java.net.preferIPv4Stack","true");
              String url_state="http://127.0.0.1:8080/control/cmd/2list";
              lHttpCLN cln=new lHttpCLN(url_state);

              logger.trace("new lHttpCLN("+url_state+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+url_state+")",ex));
                    return;
              }

              logger.trace("getJSON("+url_state+") json:"+root);

       }
}
