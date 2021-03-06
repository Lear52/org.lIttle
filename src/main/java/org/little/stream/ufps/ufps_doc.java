package org.little.stream.ufps;

public class ufps_doc {

       private String format      ;
       private String type        ;
       private String id          ;

       private String ed_no       ;
       private String ed_date     ;
       private String ed_autor    ;
     



       public ufps_doc() {
    	      clear();
    	    
       }
       public void clear() {
              format      ="";
              type        ="";
              id          ="";
              ed_no       ="";
              ed_date     ="";
              ed_autor    ="";
       }

       public void   setFormat    (String arg){format      =arg;}     
       public void   setType      (String arg){type        =arg;}      
       public void   setID        (String arg){id          =arg;}        
       public void   setEDNO      (String arg){ed_no       =arg;}     
       public void   setEDDate    (String arg){ed_date     =arg;}     
       public void   setEDAutor   (String arg){ed_autor    =arg;}     

       public String getFormat    (){return format      ;}     
       public String getType      (){return type        ;}      
       public String getID        (){return id          ;}        
       public String getEDNO      (){return ed_no       ;}     
       public String getEDDate    (){return ed_date     ;}     
       public String getEDAutor   (){return ed_autor    ;}     


       public String toString(){
                               return 
                                " doc_format:"+         format        
                               +" doc_type:"+           type        
                               +" doc_id:"+             id          
                               +" ed_no:"+              ed_no   
                               +" ed_date:"+            ed_date 
                               +" ed_autor:"+           ed_autor




                               ;
       }

}



/*
<?xml version="1.0" encoding="WINDOWS-1251"?>
<env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
  <env:Header>
      <props:MessageInfo xmlns:props="urn:cbr-ru:msg:props:v1.3">
           <props:To>uic:040700199900</props:To>
           <props:From>uic:049999900000</props:From>
           <props:MessageID>450295413-0000004099013</props:MessageID>
           <props:MessageType>1</props:MessageType>
           <props:Priority>5</props:Priority>
           <props:CreateTime>2015-12-16T19:50:08Z</props:CreateTime>
      </props:MessageInfo>
      <props:DocInfo xmlns:props="urn:cbr-ru:msg:props:v1.3">
           <props:DocFormat>2</props:DocFormat>
           <props:DocType>MT865</props:DocType>
           <props:DocID>450295413-0000004099013</props:DocID>
      </props:DocInfo>
   </env:Header>
   <env:Body>
   <Object xmlns="urn:cbr-ru:dc:v1.0">ezE6DQo6VFlQRTo4NjUNCjpUTzo5OTk5OTk5OTkwMDANCjpGUk9NOjA0MDQ5OTk5OTAxMw0KOklEOjA0MDQ5OTk5OTAxMzIwMTUxMjE2NDUwMjk1NDEzLTAwMDAwMDQwOTkwMTMvMDAwMDAwMDAwMQ0KOlJFRklEOjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwLzAwMDAwMDAwMDB9DQp7NDoNCjozMDA6OTYwMDAxDQo6MzQ0OjIxj66r4+eo4uwgpKCtreulfXs1OntGQUM6MzA4MjAxNjQwNjA5MkE4NjQ4ODZGNzBEMDEwNzAyQTA4MjAxNTUzMDgyMDE1MTAyMDEwMTMxMEYzMDBEMDYwOTJCMDYwMTA0MDE5QzU2MDEwMTA1MDAzMDBCMDYwOTJBODY0ODg2RjcwRDAxMDcwMTMxODIwMTJDMzA4MjAxMjgwMjAxMDEzMDU4MzA0NDMxMEIzMDA5MDYwMzU1MDQwNjEzMDI1MjU1MzEwQjMwMDkwNjAzNTUwNDA4MTMwMjMwMzQzMTBDMzAwQTA2MDM1NTA0MEExMzAzNDM0MjUyMzEwRDMwMEIwNjAzNTUwNDBCMTMwNDU1NDI1QTQ5MzEwQjMwMDkwNjAzNTUwNDAzMTMwMjQzNDEwMjEwNDAzNjEwQjc3RDBCRDVBRUI3NDI3N0QxNTUyMUYxRTAzMDBEMDYwOTJCMDYwMTA0MDE5QzU2MDEwMTA1MDBBMDY5MzAxODA2MDkyQTg2NDg4NkY3MEQwMTA5MDMzMTBCMDYwOTJBODY0ODg2RjcwRDAxMDcwMTMwMUMwNjA5MkE4NjQ4ODZGNzBEMDEwOTA1MzEwRjE3MEQzMTM1MzEzMjMxMzYzMTM5MzUzMDMxMzM1QTMwMkYwNjA5MkE4NjQ4ODZGNzBEMDEwOTA0MzEyMjA0MjAzREFBNzk0OUM2MzE0NkNGRThCQkI3MEM2NUQ5RDQ5NkUxODlFNTgyNTk1Q0ZEMzUyNjkxRDAzQjBCQUQzQTQwMzAwRDA2MDkyQjA2MDEwNDAxOUM1NjAxMDIwNTAwMDQ0MDkyOEUwRDlEOTUyOEE2Nzc2RUVGRkU5NjcwNzlFNEE2ODI2OTQ0MDcyOTFGNkMwMzRDMTU5MjVGQjAwRTUwNzM3MDQyQjVBRkE0NDYxRDJENEZDQzg0NkQzQjRBM0E4NkVGQUE0ODhDODgxRjdFOEJEODVCMEJGMEY1QjlCQUU0fX0=</Object>
   </env:Body>

</env:Envelope>
<env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
  <env:Header>
    <props:MessageInfo xmlns:props="urn:cbr-ru:msg:props:v1.3">
      <props:To>uic:451111111100</props:To>
      <props:From>uic:452222222211</props:From>
      <props:AppMessageID>guid:sat-00000094.22765.6577674.2202603.1.SYS.FAST.PAYMENT.xml-unicend</props:AppMessageID>
      <props:MessageID>guid:sat-00000094.22765.6577674.2202603.1.SYS.FAST.PAYMENT.xml-unicend</props:MessageID>
      <props:MessageType>1</props:MessageType>
      <props:Priority>5</props:Priority>
      <props:CreateTime>2018-08-10T05:07:01Z</props:CreateTime>
      <props:AckRequest>true</props:AckRequest>
    </props:MessageInfo>
    <props:DocInfo xmlns:props="urn:cbr-ru:msg:props:v1.3">
      <props:DocFormat>1</props:DocFormat>
      <props:DocType>ED701</props:DocType>
      <props:EDRefID EDNo="302146116" EDDate="2018-08-10" EDAuthor="4522222222"/>
    </props:DocInfo>
  </env:Header>
  <env:Body>
    <sen:SigEnvelope xmlns:sen="urn:cbr-ru:dsig:env:v1.1">
      <sen:SigContainer>
        <dsig:MACValue xmlns:dsig="urn:cbr-ru:dsig:v1.1">MIIBYAYJKoZIhvcNAQcCoIIBUTCCAU0CAQExDjAMBggqhQMHAQECAgUAMAsGCSqGSIb3DQEHATGCASkwggElAgEBMFcwQzELMAkGA1UEBhMCUlUxCzAJBgNVBAgTAjQ1MQwwCgYDVQQKEwNDQlIxDDAKBgNVBAsTA01DSTELMAkGA1UEAxMCQ0ECEEBQFMBruXkZhaYgSluWSYwwDAYIKoUDBwEBAgIFAKBpMBgGCSqGSIb3DQEJAzELBgkqhkiG9w0BBwEwHAYJKoZIhvcNAQkFMQ8XDTE4MTAyOTIwMzMxNFowLwYJKoZIhvcNAQkEMSIEIAaN22pzpScqBhna1r0zNf5AsTqQTnVRbIV+ngkArGrOMAwGCCqFAwcBAQEBBQAEQD25HZ3y1oLv6we0Lal8C81WnGgOCDFVxx0GOhcnOJl+5uS/duZbmi8o8wlLrXDyMObnMwblLqUK0HvbNM1XCRw=</dsig:MACValue>
      </sen:SigContainer>
      <sen:Object>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</sen:Object>
    </sen:SigEnvelope>
  </env:Body>
</env:Envelope>

*/