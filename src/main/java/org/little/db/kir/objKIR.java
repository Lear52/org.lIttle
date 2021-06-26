package org.little.db.kir;
                    
import java.sql.Timestamp;
//import java.util.ArrayList;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
/*
�� ���, ������������ � ������ ��������, ������ ������������ ���� � �������������� ������ � ��������� ��������: 
-       ����������� � �� ��� �� (������ ��), ������������� ���������� ������; 
-       ���������� �� (������ ��) � �� ��� ��� ��������� ������.
���������� ������ ������ ���������
-       ����� ����������� �������;
-       ���������� ������������� ��;
-       ����� ���������;
-       ������������� ���������� �� (��� �������);
-       ������������� ��������� ������ ��� �������� ��;
-       ������������� ��������� ������ ���� ��������� ��;
-       ��� ��;
-       ��� ���������� �����-�� (�� ��, ���, ���)

*/


public class objKIR{

       private static final Logger logger = LoggerFactory.getLogger(objKIR.class);

       public Timestamp getTime  () {return dt;     }
       public String    getMsgID () {return msg_id; }
       public String    getDocID () {return doc_id; }
       public String    getCorID () {return cor_id; }
       public String    getTo    () {return to;     }
       public String    getFrom  () {return from;   }

       public void      setTime  (Timestamp dt    ){this.dt = dt;}
       public void      setMsgID (String    msg_id){this.msg_id = msg_id;}
       public void      setDocID (String    doc_id){this.doc_id = doc_id;}
       public void      setCorID (String    cor_id){this.cor_id = cor_id;}
       public void      setTo    (String    to    ) {this.to = to;}
       public void      setFrom  (String    from  ){this.from = from;}

       private Timestamp         dt;  
       private String            msg_id;
       private String            doc_id;
       private String            cor_id;
       //private ArrayList<String> list_to;
       private String            to;
       private String            from;
       
       public objKIR() {
              clear();
              logger.trace("create objKIR");
       }

       public void clear() {
           dt    =null;  
           msg_id=null;
           doc_id=null;
           cor_id=null;
           to    =null;
           from  =null;
       }
       
       public StringBuilder printXML(StringBuilder buf) {
              buf.append("<DT>")    .append(getTime  ()).append("</DT>");
              buf.append("<MSG_ID>").append(getMsgID ()).append("</MSG_ID>");
              buf.append("<DOC_ID>").append(getDocID ()).append("</DOC_ID>");
              buf.append("<COR_ID>").append(getCorID ()).append("</COR_ID>");
              buf.append("<TO>")    .append(getTo    ()).append("</TO>");
              buf.append("<FROM>")  .append(getFrom  ()).append("</FROM>");
              return buf;
       }
       public String printXML() {
              StringBuilder buf=new StringBuilder();
              return printXML(buf).toString();
       }

}
