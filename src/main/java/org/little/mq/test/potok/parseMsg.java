package org.little.mq.test.potok;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_msg;
import org.little.mq.stream.ufps.u_msg;
import org.little.mq.stream.ufps.ufpsParser;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.cfg.commonPRJ;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;


public class parseMsg extends potokMsg{
       final private static String CLASS_NAME="prj0.potok.parseMsg";
       final private static int    CLASS_ID  =2403;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);


             private   connection_pool b;
             private   query           q_r;
             private   query           q_h;
             private   reportMsg       r;
             private   headerMsg       h;
             public static String node="NN";

       public parseMsg() {
              clear();
       }
       public boolean initDB(){

              try {
                  b=commonPRJ.getInstance().getDB();
              }
              catch (Except e) {
                  log.error("Error open DB ex:"+e);
                  return false;
              }
              if(b==null){
                  log.error("Error open DB");
                  return false;
              }

              try {
                  q_r=b.open(); q_r.commit(false);       
                  q_h=b.open(); q_h.commit(false);              
              }
              catch (dbExcept e) {
                  log.error("Error open DB ex:"+e);
                  return false;
              }
              r =new reportMsg();
              h =new headerMsg();
              r.initMsg();
              h.initMsg();

              try {
                   r.initQ(q_r);
                   h.initQ(q_h);
              }
              catch (dbExcept e) {
                  log.error("Error open DB ex:"+e);
                  return false;
              }
              log.trace("open DB");
              return true;
      }
       protected int init(String _qmname,String _qname_in) {
               clear();
               if(!initDB())return def.RET_ERROR;
               int ret=super.init(_qmname,_qname_in,null); 
               cmd_start();
               return ret;
       }
       protected int init(String _qmname,String _host,int _port,String _channel,String _qname_in,String _user,String _passwd) {
               clear();
               if(!initDB())return def.RET_ERROR;
               int ret=super.init(_qmname,_host,_port,_channel,_qname_in,null,_user,_passwd);
               cmd_start();
               return ret;
       }
       public  u_msg parse(mq_msg msg){
               byte[] buf=null;
               try{
                  int len=msg.getLen();
                  buf = new byte[len];
                  msg.get(buf);
               } 
               catch (mqExcept e1){
                   return null;
               }
              
               ByteArrayInputStream input = new ByteArrayInputStream(buf);
               u_msg                ufbs  = new u_msg();
               ufpsParser.is_debug  =false;

               try{
                   int ret=ufpsParser.parse(ufbs, input);
                   if(ret!=def.RET_OK)ufbs=null;
               } 
               catch (Except e1){
                   return null;
               }
               return ufbs;
       }

       public int saveReport(mq_msg m) {
              r.setID       (m.getMsgID());
              r.setDT       (new Timestamp(m.getDateTime().getTime().getTime()) ); /**/
              r.setQM       (qm.getQMName());
              String fb=null;
              if(m.getFeedback() == CMQC.MQFB_COA     ) fb = "ARRIVED";
              if(m.getFeedback() == CMQC.MQFB_ACTIVITY) fb = "CHANNEL";
              if(m.getFeedback() == CMQC.MQFB_COD     ) fb = "DELIVERED";
              if(fb==null){
                 fb="BROKER:"+m.getReportQM();
              }
              String sq=fb+":"+m.getReportQM()+":"+node;
              r.setSource(sq);



              return def.RET_OK;
       }
       public int saveHeader(mq_msg m,u_msg ufbs) {
              h.setID       (m.getMsgID());
              h.setMsgID    (ufbs.getID());
              h.setDT       (new Timestamp(m.getDateTime().getTime().getTime()) ); /**/
              h.setSize     (m.getLen());
              h.setTo       (ufbs.getTO_0());
              h.setFrom     (ufbs.getFROM());
              String p="ARRIVED:"+m.getReportQM();

              h.setProcessor(p);
              h.setToUrl    (p);
              h.setAppID    (ufbs.getAppID());
              h.setCorrID   (ufbs.getCorID());
              h.setFileID   (" ");
              int doc_format=0;if(ufbs.getDocFormat()!=null) try { doc_format=Integer.parseInt(ufbs.getDocFormat(), 10);} catch (Exception e) {doc_format=0;}
              h.setFocFormat(doc_format);
              h.setDocType  (ufbs.getDocType());
              long ed_no=0; if(ufbs.getDocEDNO()!=null) try { ed_no=Long.parseLong(ufbs.getDocEDNO(), 10);} catch (Exception e) {ed_no=0;}
              h.setFocEDNO  (ed_no);
              h.setEDDT     (ufbs.getDocEDDate());
              long ed_autor=0;if(ufbs.getDocEDAutor()!=null) try { ed_autor=Long.parseLong(ufbs.getDocEDAutor(), 10);} catch (Exception e) {ed_autor=0;}
              h.setEDAUTOR  (ed_autor);
              h.setDocID    (ufbs.getDocType());
              h.setUser     (qm.getUser());
              int priority=0; if(ufbs.getPriority()!=null) try { priority=Integer.parseInt(ufbs.getPriority(), 10);} catch (Exception e) {priority=0;}
              h.setPriority (priority);
              h.setQM       (qm.getQMName());
              return def.RET_OK;

       }
       public void run() {

              if(qm==null)    {close();return ;}
              if(q_in==null)  {close();return ;}

              log.trace("run parseMsg("+q_in.getQName()+") is_run:"+(!isStop()));

              while(!isStop()){
                    int ret;
                    mq_msg m=new mq_msg();
                    //-------------------------------------------------------------
                    try {
                        u_msg ufbs=null;
                        m.setMaxLen(100000);
                        ret = q_in.get(m);
                        if(ret==def.RET_OK){
                           log.trace("get msg processor("+q_in.getQName ()+") ");
                           if(m.getMsgType()==8){
                              ufbs=parse(m);
                              if(ufbs!=null)ret=saveHeader(m,ufbs);
                           }
                           else ret=saveReport(m);

                           if(ret==def.RET_OK){
                              qm.commit();
                           }
                           else{
                              qm.backout();
                           }
                        }
                    } 
                    catch (mqExcept ex1) {
                      log.error("run parseMsg("+q_in.getQName()+" ex:"+ex1);
                      /**/
                      cmd_close();
                      cmd_g_close();/**/

                      continue;
                    }
                    //if(ret!=def.RET_OK)break;
                    //-------------------------------------------------------------
              }
              //cmd_close();
              log.trace("stop processor("+q_in.getQName ()+") run:"+flag_state);
              close();

       }

       public static void work(String filename) {
              File            f;
              FileReader      f_r;
              BufferedReader  in;
              try {
                  f  =new File(filename);
                  f_r=new FileReader(f);
                  in =new BufferedReader(f_r,10240);
              }
              catch (Exception ex1){
                  log.error("open file:"+filename +" ex:"+Except.printException(ex1));
                  return;
              }
              int count=1;
              try{
                  String str;
                  StringTokenizer parser_line;
                  while((str = in.readLine()) != null) {
                         parser_line = new StringTokenizer(str, "\n\r");
                         //----------------------------------------------------------
                         while(parser_line.hasMoreTokens()) {
                               String s;
                               String qname_in =null;
                               String qmname   =null;
                               String host     =null;
                               int    port     =0;
                               String channel  =null;
                               String user     =null;
                               String passwd   =null;
                               s=parser_line.nextToken();
                               StringTokenizer parser_q=new StringTokenizer(s, " \t");
                               int ind=0;
                               while(parser_q.hasMoreTokens()) {
                                     String ss=parser_q.nextToken();
                                     switch(ind){
                                     case 0: qmname    =ss; break;  
                                     case 1: qname_in  =ss; host=ss;break;  
                                     case 2: if(ss!=null) try { port=Integer.parseInt(ss, 10);} catch (Exception e) {port=0;host=null;} break;  
                                     case 3: channel  =ss; break;  
                                     case 4: qname_in  =ss; break;  
                                     case 5: user      =ss; break;  
                                     case 6: passwd    =ss; break;  
                                     }
                                     ind++;
                               }
                               if(ind<6)channel=null;
                               if(qmname!=null){
                                  if(channel==null){
                                     log.info("potok:"+count+" qm:"+qmname+" q_in:"+qname_in);
                                     parseMsg r=new parseMsg();
                                     if(r.init(qmname,qname_in)<0){
                                        log.error("potok:"+count+"error init");
                                     }
                                     else r.fork();
                                  }
                                  else{
                                     log.info("potok:"+count+" qm:"+qmname+" q_in:"+qname_in+" host:"+host+":"+port+" channel:"+channel);
                                     parseMsg r=new parseMsg();
                                     if(r.init(qmname,host,port,channel,qname_in,user,passwd)<0){
                                        log.error("potok:"+count+"error init");
                                     }
                                     else r.fork();
                                  }
                                  count++;
                               }
                         }
                         //----------------------------------------------------------
                  }
                  in.close();
              } 
              catch (Exception ex2){
                  log.error("read file:"+filename +" ex:"+Except.printException(ex2));
              }
              try {in.close();} catch (IOException e1) {}
              try {f_r.close();} catch (IOException e1) {}

       }

       public static void main(String[] args) {


              if(args.length<1){
                 System.out.println("run java "+getClassName()+" config_filename");
              }
              else{
                 MQException.log=null;
                 String config_filename=args[0];
                 parseMsg.work(config_filename);
              }

       }

}
/*
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

SOURCE
 ----------------------------------------------------------
 ARRIVED:SBPBACK
 ARRIVED:SBPFRONT
 BROKER:IN.REG@SBPBACK                                  :DK
 BROKER:IN.REG@SBPBACK                                  :NN
 BROKER:IN.REG@SBPBACK                                  :TU
 BROKER:IN.REG@SBPFRONT                                 :DK
 BROKER:IN.REG@SBPFRONT                                 :NN
 BROKER:IN.REG@SBPFRONT                                 :TU
 BROKER:IN1.SLA@SBPBACK                                 :DK
 BROKER:IN1.SLA@SBPBACK                                 :NN
 BROKER:IN1.SLA@SBPBACK                                 :NU
 BROKER:IN1.SLA@SBPBACK                                 :TU
 BROKER:IN1.SLA@SBPFRONT                                :DK
 BROKER:IN1.SLA@SBPFRONT                                :NN
 BROKER:IN1.SLA@SBPFRONT                                :NU
 BROKER:IN1.SLA@SBPFRONT                                :TU
 BROKER:IN1@SBPBACK                                     :DK
 BROKER:IN1@SBPBACK                                     :NN
 BROKER:IN1@SBPBACK                                     :NU
 BROKER:IN1@SBPBACK                                     :TU
 BROKER:IN1@SBPFRONT                                    :DK
 BROKER:IN1@SBPFRONT                                    :NN
 BROKER:IN1@SBPFRONT                                    :NU
 BROKER:IN1@SBPFRONT                                    :TU
 DELIVERED:SBPBACK
 DELIVERED:SBPFRONT


 PROCESSOR
 ----------------
 ARRIVED:SBPBACK
 ARRIVED:SBPFRONT



*/