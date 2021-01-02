package org.little.store;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.util.string.stringDate;



public class lMessage2JSON  {

	   public static String parse(lMessage msg){
              JSONObject obj=msg2obj(msg);
              StringWriter out = new StringWriter();
              obj.write(out);
              return out.toString();
       }
       public static String parse(ArrayList<lMessage> msg){
              JSONObject obj=list2obj(msg);
              StringWriter out = new StringWriter();
              obj.write(out);
              return out.toString();
       }
       public static void parse(Writer out,lMessage msg){
              JSONObject obj=msg2obj(msg);
              obj.write(out);
       }
       public static void parse(Writer out,ArrayList<lMessage> msg){
              JSONObject obj=list2obj(msg);
              obj.write(out);
       }

       private static JSONObject list2obj(ArrayList<lMessage> msg){
              JSONArray list=new JSONArray();
              boolean   state=false;
              if(msg!=null){
                 for(int i=0;i<msg.size();i++){
                     lMessage   m  =msg.get(i);
                     if(m==null)continue;
                     JSONObject obj=msg2obj(m);
                     list.put(obj);
                     state=true;
                 }
              }

              JSONObject root_object=new JSONObject();
              root_object.put("type" ,"list");
              root_object.put("state",state);
              root_object.put("list" ,list);
              return root_object;
       }

       private static JSONObject msg2obj(lMessage msg){

              JSONObject root_object=new JSONObject();

              JSONArray to=new JSONArray();

              String [] _to=msg.getTO();

              for(int i=0;i<_to.length;i++)to.put(_to[i]);

              JSONObject obj=new JSONObject();

              obj.put("Num"           ,msg.getNum                            ());
              obj.put("From"          ,msg.getFrom                           ());
              obj.put("To"            ,to                                      );                                
              obj.put("Id"            ,msg.getId                             ());
              obj.put("Subject"       ,msg.getSubject                        ());
              obj.put("Filename"      ,msg.getFilename                       ());
              if(msg.getCreateDate     ()!=null)obj.put("CreateDate" ,stringDate.date2prn(msg.getCreateDate ())); else obj.put("CreateDate" ,"");
              if(msg.getSentDate       ()!=null)obj.put("SentDate"   ,stringDate.date2prn(msg.getSentDate   ())); else obj.put("SentDate"   ,"");
              if(msg.getReceiveDate    ()!=null)obj.put("ReceiveDate",stringDate.date2prn(msg.getReceiveDate())); else obj.put("ReceiveDate","");
              if(msg.getDelDate        ()!=null)obj.put("DelDate"    ,stringDate.date2prn(msg.getDelDate    ())); else obj.put("DelDate"    ,"");
              if(msg.getAnswerDate     ()!=null)obj.put("AnswerDate" ,stringDate.date2prn(msg.getAnswerDate ())); else obj.put("AnswerDate" ,"");

              obj.put("Mime"          ,msg.getMime                           ());
              obj.put("Size"          ,msg.getSize                           ());
                                              
              obj.put("X509Type"      ,msg.getX509Type                       ());
              obj.put("X509TypeFile"  ,msg.getX509TypeFile                   ());
              obj.put("X509BeginDate" ,stringDate.date2prn(msg.getX509BeginDate()));
              obj.put("X509EndDate"   ,stringDate.date2prn(msg.getX509EndDate  ()));
              obj.put("X509Serial"    ,msg.getX509Serial                     ());
              obj.put("X509Subject"   ,msg.getX509Subject                    ());
              obj.put("X509Issuer"    ,msg.getX509Issuer                     ());

              root_object.put("type"   ,"message"       );
              root_object.put("header" ,obj             );
              root_object.put("BodyTxt",msg.getBodyTxt());

              return root_object;
       };

}
                              