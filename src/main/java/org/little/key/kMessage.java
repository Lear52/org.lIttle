package org.little.key;

import java.util.Date;
 
public class kMessage{
    private   byte  []           body_bin        ;
                                                 
    private   String             x509_type       ;
    private   String             x509_type_file  ;
    private   Date               x509_begin_date ;
    private   Date               x509_end_date   ;
    private   String             x509_serial     ;
    private   String             x509_subject    ;
    private   String             x509_issuer     ;
    
    public    kMessage() {clear();}

    protected void clear(){
              body_bin        =null;

              x509_type        ="";      
              x509_type_file   ="PEM"; //"PEM"-����� ��������� ,"DER"    
              x509_begin_date  =null;
              x509_end_date    =null;  
              x509_serial      ="";    
              x509_subject     ="";   
              x509_issuer      ="";    
              

    }

    public String   getX509Type     (){return x509_type        ;}    
    public String   getX509TypeFile (){return x509_type_file   ;}    
    public Date     getX509BeginDate(){return x509_begin_date  ;}
    public Date     getX509EndDate  (){return x509_end_date    ;}
    public String   getX509Serial   (){return x509_serial      ;}  
    public String   getX509Subject  (){return x509_subject     ;} 
    public String   getX509Issuer   (){return x509_issuer      ;}  

    public byte[]   getBodyBin      (){return body_bin;         }


    public void     setX509Type     (String s)       {x509_type      =s;}    
    public void     setX509TypeFile (String s)       {x509_type_file =s;}    
    public void     setX509BeginDate(Date   s)       {x509_begin_date=s;}
    public void     setX509EndDate  (Date   s)       {x509_end_date  =s;}
    public void     setX509Serial   (String s)       {x509_serial    =s;}  
    public void     setX509Subject  (String s)       {x509_subject   =s;} 
    public void     setX509Issuer   (String s)       {x509_issuer    =s;}  

    public void     setBodyBin      (byte[] b)       {
                    body_bin=b;
    }

    public String   toString() {return  ""
                   +" "+x509_type       
                   +" "+x509_type_file  
                   +" "+x509_begin_date 
                   +" "+x509_end_date   
                   +" "+x509_serial     
                   +" "+x509_subject    
                   +" "+x509_issuer      
                   ;
    }
    public String  printx509() {return  
                    "\nx509_type      :"+x509_type       
                   +"\nx509_type_file :"+x509_type_file  
                   +"\nx509_begin_date:"+x509_begin_date 
                   +"\nx509_end_date  :"+x509_end_date   
                   +"\nx509_serial    :"+x509_serial     
                   +"\nx509_subject   :"+x509_subject    
                   +"\nx509_issuer    :"+x509_issuer 
                   +"\n"
                   ;
    }


}
