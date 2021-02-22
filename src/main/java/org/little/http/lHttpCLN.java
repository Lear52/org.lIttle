package org.little.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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


public class lHttpCLN {


       private String  url     ;
       private boolean debug   ;

       public lHttpCLN(){
              debug   = true;
              url       ="http://localhost:8080/upload.php";
       }
       public void setURL(String url) {
              this.url = url;
       }
       public String get(ByteArrayOutputStream os) throws Exception{
              String     filename=null;
              CloseableHttpClient httpclient =null;
              try {

                   httpclient = HttpClientBuilder.create().build();
              
                   HttpGet               http_get = new HttpGet(url);
                   CloseableHttpResponse response = null;
                   InputStream is=null;
                   try {
                        response = httpclient.execute(http_get);
                        System.out.println(response.getStatusLine());
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
              
              return filename;
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
       public static void main(String[] args) throws Exception {
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
}
