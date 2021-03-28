package org.little.lmsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

//import org.little.imap.commonIMAP;
import org.little.util._Base64;

public class lMessage{
    private static final Random GENERATOR = new Random();

    private   String             msg_from        ;
    private   ArrayList<String>  msg_to          ;
    private   String             msg_id          ;
    private   String             msg_subject     ;
    private   String             msg_filename    ;
    private   String             msg_mime        ;
    private   Date               msg_create_date ;
    private   Date               msg_sent_date   ;
    private   Date               msg_receive_date;
    private   Date               msg_answer_date ;
    private   Date               msg_del_date    ;


    private   String             body_txt        ;
    private   byte  []           body_bin        ;
    private   String             body_bin_txt    ;
    private   String             body_bin_txt76  ;
    private   int                msg_size        ;
    //private   int                msg_count_str   ;

    private   int                msg_num         ;
    private   int                msg_uid         ;
    private   boolean            expunged        ;
                                                 
    private   String             x509_type       ;
    private   String             x509_type_file  ;
    private   Date               x509_begin_date ;
    private   Date               x509_end_date   ;
    private   String             x509_serial     ;
    private   String             x509_subject    ;
    private   String             x509_issuer     ;
    
    


    public    lMessage() {clear();}
    public    lMessage(lMessage m) {clear();set(m);}

    public    lMessage(int msg_num) {
              clear();
              this.msg_num = msg_num;
    }

    protected void clear(){
              msg_from         ="";         
              msg_to           =new ArrayList<String>(10);           
              msg_id           ="";           
              msg_subject      ="";      
              msg_filename     ="";      
              msg_create_date  =new Date();    
              msg_sent_date    =null;    
              msg_receive_date =null; 
              msg_del_date     =null; 
              msg_answer_date  =null;
              msg_mime         ="";

              body_txt        ="";
              body_bin        =null;
              body_bin_txt    ="";
              body_bin_txt76  ="";
              msg_size         = 0;
              //msg_count_str    = 0;

              x509_type        ="";      
              x509_type_file   ="PEM"; //"PEM"-чисто текстовый ,"DER"    
              x509_begin_date  =getSentDate();
              x509_end_date    =getSentDate();  
              x509_serial      ="";    
              x509_subject     ="";   
              x509_issuer      ="";    
              
              msg_num          = 0;
              msg_uid          = 0;
              expunged         = false;

    }
    public static String getNewID(String id){
        SimpleDateFormat sfd =new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
        return sfd.format(new Date())+GENERATOR.nextLong()+"@"+id;
    }

    public boolean  isExpunged      () {return expunged;   }
    public void     setExpunged     (boolean expunged) {this.expunged = expunged;   }

    public String[] getTO(){
                 String[] arr_s=(String[])msg_to.toArray(new String[msg_to.size()]);
                 return arr_s;
    }
    public String getTOs(){
                  StringBuilder buf=new StringBuilder();
                  for(int i=0;i<msg_to.size();i++){if(i>0)buf.append(","); buf.append(msg_to.get(i));}
                  return buf.toString();
    }
    public String getTOsInet(String _domain){
        StringBuilder buf=new StringBuilder();
        //for(int i=0;i<msg_to.size();i++){if(i>0)buf.append(","); buf.append(msg_to.get(i)).append("@").append(commonIMAP.get().getDefaultDomain());}
        for(int i=0;i<msg_to.size();i++){if(i>0)buf.append(","); buf.append(msg_to.get(i)).append("@").append(_domain);}
        return buf.toString();
    }
    public String   getFromInet     (String _domain){
           //return msg_from+"@"+commonIMAP.get().getDefaultDomain();
           return msg_from+"@"+_domain;
    }
    
    //public int      getMessageNumber(){return msg_num;         }
    public int      getNum          (){return msg_num;         }
    public int      getUID          (){return msg_uid;         }
    public String   getFrom         (){return msg_from;        }
    public String   getId           (){return msg_id;          }
    public String   getSubject      (){return msg_subject;     }
    public String   getFilename     (){return msg_filename;    }
    public Date     getCreateDate   (){return msg_create_date; }
    public Date     getSentDate     (){return msg_sent_date;   }
    public Date     getReceiveDate  (){return msg_receive_date;}
    public Date     getDelDate      (){return msg_del_date;    }
    public Date     getAnswerDate   (){return msg_answer_date; }
    public String   getMime         (){return msg_mime;        }
    public int      getSize         (){return msg_size;        }

    public String   getX509Type     (){return x509_type        ;}    
    public String   getX509TypeFile (){return x509_type_file   ;}    
    public Date     getX509BeginDate(){return x509_begin_date  ;}
    public Date     getX509EndDate  (){return x509_end_date    ;}
    public String   getX509Serial   (){return x509_serial      ;}  
    public String   getX509Subject  (){return x509_subject     ;} 
    public String   getX509Issuer   (){return x509_issuer      ;}  

    public String   getBodyTxt      (){return body_txt;         }
    public byte[]   getBodyBin      (){return body_bin;         }
    public String   getBodyBin76    (){return body_bin_txt76;   }
    public String   getBodyBin64    (){return body_bin_txt;     }
    
    public boolean  isSEEN() {return !isUNSEEN();}
    public boolean  isUNSEEN() {return msg_receive_date==null;}
    public boolean  isDELETE() {return msg_del_date==null;}
    public boolean  isDRAFT() {return false;}
    public boolean  isANSWERED() {return false;}
    

    public void     setTO           (String address) {msg_to=new ArrayList<String>();addTO(address);   }
    public void     addTO           (String address) {msg_to.add(address);       }

    //public void     setMessageNumber(int msg_num)    {this.msg_num = msg_num;    }
    public void     setNum          (int n)          {msg_num=n;                 }
    public void     setUID          (int n)          {msg_uid=n;                 }
    public void     setFrom         (String address) {msg_from=address;          }
    public void     setId           (String _id)     {msg_id=_id;                }
    public void     setSubject      (String subject) {msg_subject=subject;       }
    public void     setFilename     (String filename){msg_filename=filename;     }
    public void     setCreateDate   (Date date)      {msg_create_date=date;      }
    public void     setCreateDate   ()               {setCreateDate(new Date ());}
    public void     setSentDate     (Date date)      {msg_sent_date=date;        }
    public void     setSentDate     ()               {setSentDate(new Date ());  }
    public void     setReceiveDate  (Date date)      {msg_receive_date=date;     }
    public void     setReceiveDate  ()               {setReceiveDate(new Date());}
    public void     setDelDate      (Date date)      {msg_del_date=date;         }
    public void     setAnswerDate   (Date date)      {msg_answer_date=date;      }
    public void     setDelDate      ()               {setDelDate(new Date ());   }
    public void     setAnswerDate   ()               {setAnswerDate(new Date ());}
    public void     setMime         (String mime)    {msg_mime=mime;             }
    public void     setSize         (int  _size)     {msg_size=_size;            }

    public void     setX509Type     (String s)       {x509_type      =s;}    
    public void     setX509TypeFile (String s)       {x509_type_file =s;}    
    public void     setX509BeginDate(Date   s)       {x509_begin_date=s;}
    public void     setX509EndDate  (Date   s)       {x509_end_date  =s;}
    public void     setX509Serial   (String s)       {x509_serial    =s;}  
    public void     setX509Subject  (String s)       {x509_subject   =s;} 
    public void     setX509Issuer   (String s)       {x509_issuer    =s;}  

    public void     setBodyTxt      (String b)       {body_txt=b;     }
    private void    setBodyBin76    ()       {
                    int len_txt=body_bin_txt.length();
                    StringBuilder out_buf=new StringBuilder();
                    {
                     int begin=0;
                     //msg_count_str   = 0;
                     while(len_txt>0){
                           int len=Integer.min(len_txt, 76);
                           String s=body_bin_txt.substring(begin,begin+len);
                           begin+=len;
                           len_txt-=len;
                           out_buf.append(s);
                           if(len==76)out_buf.append("\r\n");
                           //msg_count_str++;
                     }
                     //out_buf.append("\r\n");
                  }
                  body_bin_txt76=out_buf.toString();
    }

    public void     setBodyBin      (byte[] b)       {
                    if(b==null){
                       body_bin        =null;
                       body_bin_txt    ="";  
                       body_bin_txt76  ="";  
                       //msg_count_str   = 0;
                       setSize(0);
                       return;
                    }
                    body_bin=b;
                    setSize(body_bin.length);
                    body_bin_txt=_Base64.byteArrayToBase64(body_bin);
                    setBodyBin76();
    }
    public void     setBodyBin64(String b){
                    if(b!=null){
                       body_bin_txt=b;
                       body_bin=_Base64.base64ToByteArray(b);
                       setBodyBin76();
                       setSize(body_bin.length);
                    }else {
                       setBodyBin(null);
                    }  
    }

                   

    public  lMessage  clone() {
            lMessage m=new lMessage();
            m.set(this);
            return m;
    }
    public  void set(lMessage m) {

            msg_from        =m.msg_from        ;
            msg_to          =m.msg_to          ;
            msg_id          =m.msg_id          ;
            msg_subject     =m.msg_subject     ;
            msg_filename    =m.msg_filename    ;
            msg_create_date =m.msg_create_date ;
            msg_sent_date   =m.msg_sent_date   ;
            msg_receive_date=m.msg_receive_date;
            msg_del_date    =m.msg_del_date    ;
            msg_size        =m.msg_size        ;
            body_txt        =m.body_txt        ;        
            body_bin        =m.body_bin        ;        

            x509_type      =m.x509_type       ;
            x509_type_file =m.x509_type_file  ;
            x509_begin_date=m.x509_begin_date ;
            x509_end_date  =m.x509_end_date   ;
            x509_serial    =m.x509_serial     ;
            x509_subject   =m.x509_subject    ;
            x509_issuer    =m.x509_issuer     ;
    }



    public static String getMIME(String fileName){
           java.net.FileNameMap fm=java.net.URLConnection.getFileNameMap();
           String ret=fm.getContentTypeFor(fileName);
           if(ret==null)ret="application/octet-stream";
           return ret; 
    }

    public String   toString() {return  ""
                   +" "+msg_num         
                   +" "+msg_from        
                   +" "+getTOs()          
                   +" "+msg_id          
                   +" "+msg_subject     
                   +" "+msg_filename    
                   +" "+msg_create_date   
                   +" "+msg_sent_date   
                   +" "+msg_receive_date
                   +" "+msg_del_date
                   +" "+msg_size
                   +" "+x509_type       
                   +" "+x509_type_file  
                   +" "+x509_begin_date 
                   +" "+x509_end_date   
                   +" "+x509_serial     
                   +" "+x509_subject    
                   +" "+x509_issuer      
                   +" txt:"+getBodyTxt()
                   +" bin:"+getBodyBin64()
                   ;
    }
    public String  printx509() {return  
                    "\n"+x509_type       
                   +"\n"+x509_type_file  
                   +"\n"+x509_begin_date 
                   +"\n"+x509_end_date   
                   +"\n"+x509_serial     
                   +"\n"+x509_subject    
                   +"\n"+x509_issuer 
                   +"\n"
                   ;
    }


}
