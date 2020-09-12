package org.little.imap.command.cmd;

//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeUtility;

import org.little.imap.IMAPTransaction;
import org.little.imap.SessionContext;
import org.little.imap.commonIMAP;
import org.little.imap.command.ImapCommand;
import org.little.imap.command.ImapCommandParameter;
import org.little.imap.command.ImapConstants;
import org.little.imap.command.cmd.fetch.FetchElement;
import org.little.imap.command.cmd.fetch.FetchRequest;
import org.little.imap.command.cmd.fetch.IdRange;
import org.little.imap.command.cmd.fetch.ImapDate;
import org.little.imap.response.EmptyResponse;
import org.little.imap.response.ImapResponse;
import org.little.store.lMessage;
import org.little.store.lMessage2ELM;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.stringParser;



/**
 * Handles processing for the FETCH imap command.
 * <p/>
 * https://tools.ietf.org/html/rfc3501#section-6.4.5
 *
 */
public class FetchCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(FetchCommand.class);

       public static final String NAME = "FETCH";
       public static final String ARGS = "<message-set> <fetch-profile>";
       public boolean isUID ;

       public FetchCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);isUID =false;}
       public FetchCommand(String _tag, String _command, List<ImapCommandParameter> _parameters,boolean _isUID ) { super(_tag,_command,_parameters);isUID =_isUID;}

       private List<IdRange> parseFetch(FetchRequest fetch){
              boolean is_id_range=true;
              List<IdRange>id_range=null;//=IdRange.parseRange(str_id_range);
              
              for(int i=0;i<getParameters().size();i++){
                  String vol_param  = getParameters().get(i).toString();
                  vol_param=vol_param.toUpperCase();
                  logger.trace("IMAP fetch cmd vol_param:"+vol_param);
                  
                  if(vol_param.startsWith("FAST")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.FLAGS);                          
                     fetch.add(FetchRequest.INTERNALDATE);                          
                     fetch.add(FetchRequest.SIZE);                          
                     fetch.flags       =true;
                     fetch.internaldate=true;
                     fetch.size        =true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else 
                  if(vol_param.startsWith("FULL")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.FLAGS);                          
                     fetch.add(FetchRequest.INTERNALDATE);                          
                     fetch.add(FetchRequest.SIZE);                          
                     fetch.add(FetchRequest.ENVELOPE);                          
                     fetch.add(FetchRequest.BODY);                          
                     fetch.flags       =true;
                     fetch.internaldate=true;
                     fetch.size        =true;
                     fetch.envelope    =true;
                     fetch.body        =true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else 
                  if(vol_param.startsWith("ALL")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.FLAGS);                          
                     fetch.add(FetchRequest.INTERNALDATE);                          
                     fetch.add(FetchRequest.SIZE);                          
                     fetch.add(FetchRequest.ENVELOPE);                          
                     fetch.flags       =true;
                     fetch.internaldate=true;
                     fetch.size        =true;
                     fetch.envelope    =true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else 
                  if(vol_param.startsWith("FLAGS")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.FLAGS);                          
                     fetch.flags       =true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else 
                  if(vol_param.startsWith("RFC822.SIZE")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.SIZE);                          
                     fetch.size        =true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else 
                  if(vol_param.startsWith("ENVELOPE")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.ENVELOPE);                          
                     fetch.envelope    =true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else 
                  if(vol_param.startsWith("INTERNALDATE")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.INTERNALDATE);                          
                     fetch.internaldate=true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else 
                  if(vol_param.startsWith("BODYSTRUCTURE")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.BODYSTRUCTURE);                          
                     fetch.bodystructure=true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else 
                  if(vol_param.startsWith("BODY.PEEK")) {
                     is_id_range=false;
                     String body_section=null;
                     String size_section=null;
                     //-------------------------------------------------------------------------------
                	 //logger.trace("find all  BODY.PEEK");
                     if(vol_param.indexOf('[')>=0){
                    	 //logger.trace("BODY.PEEK find [");
                         if(vol_param.indexOf(']')<0){
                        	//logger.trace("BODY.PEEK no find ]");
                        	 vol_param+=" "; 
                            i++;
                            while(i<getParameters().size()){
                                 String _vol_param  = getParameters().get(i).toString();
                                 //logger.trace("add parametr:"+_vol_param);
                                 vol_param+=_vol_param;
                              
                                 if(_vol_param.indexOf(']')<0) {
                                	 i++;
                                	 if(_vol_param.equals("(")==false && _vol_param.equals(")")==false){
                                		 vol_param+=" ";
                                		 //logger.trace("add sp");
                                	 }
                                 }
                                 else{
                                    //logger.trace("find ] in parametrs:"+vol_param);
                                    break;
                                 }
                            }
                         }
                     }
                     //-------------------------------------------------------------------------------
                     try {                          
                           stringParser parser=new stringParser(vol_param,"<>[]");
                           String param_body=parser.get();
                           body_section=parser.get();
                           size_section=parser.get();
                           logger.trace("parth:"+body_section);
                     }
                     catch(Exception ex) {body_section=null;}
                      
                     fetch.add(FetchRequest.BODY,body_section);                          
                     fetch.body_section=body_section;
                     fetch.size_section=size_section;
                     fetch.body_peek=true;
                     logger.trace("IMAP fetch cmd param name:"+"BODY.PEEK[" + body_section + "]  ");
                  }
                  else 
                  if(vol_param.startsWith("BODY")){
                     is_id_range=false;
                     String body_section=null;
                     String size_section=null;
                     //-------------------------------------------------------------------------------
                	 //logger.trace("find all  BODY");
                     if(vol_param.indexOf('[')>=0){
                    	 //logger.trace("BODY find [");
                         if(vol_param.indexOf(']')<0){
                        	//logger.trace("BODY no find ]");
                        	 vol_param+=" "; 
                            i++;
                            while(i<getParameters().size()){
                                 String _vol_param  = getParameters().get(i).toString();
                                 //logger.trace("add parametr:"+_vol_param);
                                 vol_param+=_vol_param;
                              
                                 if(_vol_param.indexOf(']')<0) {
                                	 i++;
                                	 if(_vol_param.equals("(")==false && _vol_param.equals(")")==false){
                                		 vol_param+=" ";
                                		 //logger.trace("add sp");
                                	 }
                                 }
                                 else{
                                    //logger.trace("find ] in parametrs:"+vol_param);
                                    break;
                                 }
                            }
                         }
                     }
                     //-------------------------------------------------------------------------------
                     
                     try {                          
                           stringParser parser=new stringParser(vol_param,"<>[]");
                           String param_body=parser.get();
                           body_section=parser.get();
                           size_section=parser.get();
                           logger.trace("parth:"+body_section);
                     }
                     catch(Exception ex) {body_section=null;}
                      
                     fetch.add(FetchRequest.BODY,body_section);                          
                     fetch.body_section=body_section;
                     fetch.size_section=size_section;
                     fetch.body=true;
                     logger.trace("IMAP fetch cmd param name:"+"BODY[" + body_section + "]  ");
                  } 
                  else 
                  if(vol_param.startsWith("UID")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.UID);                          
                     fetch.uid=true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else 
                  if(vol_param.startsWith("RFC822.HEADER")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.RFC822_HEADER);                          
                     fetch.rfc822_header=true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else 
                  if(vol_param.startsWith("RFC822.TEXT")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.RFC822_TEXT);                          
                     fetch.rfc822_text=true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  }
                  else 
                  if(vol_param.startsWith("RFC822")) {
                     is_id_range=false;
                     fetch.add(FetchRequest.RFC822);                          
                     fetch.rfc822=true;
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 
                  else
                  if (vol_param.startsWith("(")) {
                      is_id_range=false;
                  } 

                  if(is_id_range) {
                     id_range=IdRange.parseRangeSequence(vol_param,id_range);
                     logger.trace("IMAP fetch cmd param name:"+vol_param);
                  } 

              }
              return id_range;

       }
/*
       private String parseMsg0(lMessage msg,FetchRequest fetch){
               String body=lMessage2ELM.parse(msg);
               StringBuilder buf=new StringBuilder();
               for(int j=0;j<fetch.get().size();j++) {
                    FetchElement e=fetch.get().get(j);
                         
                    if(e.getType()==FetchRequest.FLAGS) {
                       buf.append("FLAGS (\\RESENT \\FLAGGED) "); 
                       logger.trace("append:"+"FLAGS () ");
                    }
                    else
                    if(e.getType()==FetchRequest.UID) {
                       buf.append("UID "+msg.getUID()+" "); 
                       logger.trace("append:"+"UID "+msg.getUID()+" ");
                    }
                    else
                    // INTERNALDATE response
                    if (e.getType()==FetchRequest.INTERNALDATE) {
                        buf.append("INTERNALDATE \""+date2prn(msg.getCreateDate())+"\""+" ");   
                        logger.trace("append:"+"INTERNALDATE \""+date2prn(msg.getCreateDate())+"\""+" ");
                    }
                    else                 
                    //   RFC822.SIZE response
                    if(e.getType()==FetchRequest.SIZE) {
                       int _size=msg.getSize();
                   if(true){//--------------------------------------------------------
                            body=BODY_TEST;
                   }
                       if(body!=null)_size=body.length();                                 
                       buf.append("RFC822.SIZE "+_size+" ");   
                       logger.trace("append:"+"RFC822.SIZE "+msg.getSize()+" ");
                    }
                    else
                    //  ENVELOPE response
                    if(e.getType()==FetchRequest.ENVELOPE) {
                       buf.append("ENVELOPE "+printEnvelope(msg)+" ");   
                       logger.trace("append:"+"ENVELOPE "+printEnvelope(msg));
                    }
                    else
                    //   BODYSTRUCTURE response
                    if(e.getType()==FetchRequest.BODYSTRUCTURE) {
                       buf.append("BODYSTRUCTURE "+printBodyStructure(msg));   
                       logger.trace("append:"+"BODYSTRUCTURE "+printBodyStructure(msg));
                    }
                    else {}
               }
                     
               for(int k=0;k<fetch.get().size();k++) {
                   FetchElement e=fetch.get().get(k);
                   // BODY response
                   if (e.getType()==FetchRequest.BODY_PEEK || e.getType()==FetchRequest.BODY) {
                      //buf.append("BODY"+printBody(msg,e.getArg1()));   
                      int part=0;
                      String s_part=e.getArg1();
                      if(s_part!=null) {
                         if(s_part.startsWith("TEXT")) part=1;
                         else
                         if(s_part.startsWith("APPLICATION")) part=2;
                         else {
                            //logger.trace("set parth1:"+s_part);
                            try {part=Integer.parseInt(s_part);} catch(Exception ex) {part=0;}
                            //logger.trace("set parth2:"+part);
                            if(part!=1 && part!=2)part=0;
                            //logger.trace("set parth3:"+part);
                         }
                      }
                      //logger.trace("set parth4:"+part);
                      int len_txt=1;
                      if(part==1){
                         if(e.getType()==FetchRequest.BODY_PEEK)buf.append("BODY.PEEK");
                         if(e.getType()==FetchRequest.BODY)buf.append("BODY");
                         if(msg.getBodyTxt()!=null)len_txt=msg.getBodyTxt().length();
                         buf.append("[1]<0> {"+len_txt+"} ");
                         buf.append(msg.getBodyTxt());
                         buf.append(SP);
                      }
                      if(part==2){
                               if(e.getType()==FetchRequest.BODY_PEEK)buf.append("BODY.PEEK");
                               if(e.getType()==FetchRequest.BODY)buf.append("BODY");
                         if(msg.getBodyBin76()!=null)len_txt=msg.getBodyBin76().length();
                         buf.append("[2]<0> {"+len_txt+"}");
                         buf.append("\r\n");
                         buf.append(msg.getBodyBin76());
                         
                     }
                     if(part==0){
                        //String body=lMessage2ELM.parse(msg);
                        if(e.getType()==FetchRequest.BODY_PEEK)buf.append("BODY.PEEK");
                        if(e.getType()==FetchRequest.BODY)buf.append("BODY");
             if(true){//--------------------------------------------------------
                      body=BODY_TEST;
             }
                        if(body!=null)len_txt=body.length();
                        buf.append("[] {"+len_txt+"}");
                        buf.append("\r\n");
                        buf.append(body);
                     }
                   }
               }

               return buf.toString();

       }
*/
       private String parseMsg(lMessage msg,FetchRequest fetch){
               String body=lMessage2ELM.parse(msg);
               StringBuilder buf=new StringBuilder();
                         
                    if(fetch.uid) {
                       buf.append("UID "+msg.getUID()+" "); 
                       logger.trace("append:"+"UID "+msg.getUID()+" ");
                    }
                    if(fetch.flags) {
                       String _flag="\\Flagged";
                       buf.append("FLAGS ("+_flag+") "); 
                       logger.trace("append:"+"FLAGS ("+_flag+") ");
                    }
                    //   RFC822.SIZE response
                    if(fetch.size) {
                       int _size=msg.getSize();
          //if(true){//--------------------------------------------------------
          //                  body=BODY_TEST;
          //}
                       if(body!=null)_size=body.length();                                 /**/                                  /**/// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                       buf.append("RFC822.SIZE "+_size+" ");   
                       logger.trace("append:"+"RFC822.SIZE "+msg.getSize()+" ");
                    }
                    // INTERNALDATE response
                    if(fetch.internaldate) {
                       buf.append("INTERNALDATE \""+date2prn(msg.getCreateDate())+"\""+" ");   
                       logger.trace("append:"+"INTERNALDATE \""+date2prn(msg.getCreateDate())+"\""+" ");
                    }
                    //  ENVELOPE response
                    if(fetch.envelope) {
                       buf.append("ENVELOPE "+printEnvelope(msg)+" ");   
                       logger.trace("append:"+"ENVELOPE "+printEnvelope(msg));
                    }
                    //   BODYSTRUCTURE response
                    if(fetch.bodystructure) {
                       buf.append("BODYSTRUCTURE "+printBodyStructure(msg));   
                       logger.trace("append:"+"BODYSTRUCTURE "+printBodyStructure(msg));
                    }
                    // BODY response
                    if(fetch.body_peek || fetch.body) {
                      //buf.append("BODY"+printBody(msg,e.getArg1()));   
                      int part=0;
                      if(fetch.body_section!=null) {
                         if(fetch.body_section.startsWith("HEADER.FIELDS")){     
                              part=3;
                         }
                         else
                         if(fetch.body_section.startsWith("HEADER")){     
                            part=4;
                         }
                         else
                         if(fetch.body_section.startsWith("TEXT"))        part=1;
                         else
                         if(fetch.body_section.startsWith("APPLICATION")) part=2;
                         else {
                            //logger.trace("set parth1:"+s_part);
                            try {part=Integer.parseInt(fetch.body_section);} catch(Exception ex) {part=0;}
                            //logger.trace("set parth2:"+part);
                            if(part!=1 && part!=2)part=0;
                            //logger.trace("set parth3:"+part);
                         }
                      }
                      //logger.trace("set parth4:"+part);
                      int len_txt=1;
                      if(part==1){
                         if(fetch.body_peek)buf.append("BODY");//buf.append("BODY.PEEK");
                         if(fetch.body)buf.append("BODY");
                         if(msg.getBodyTxt()!=null)len_txt=msg.getBodyTxt().length();
                         buf.append("[1]<0> {"+len_txt+"} \r\n");
                         buf.append(msg.getBodyTxt());
                         buf.append(SP);
                      }
                      else
                      if(part==2){
                         if(fetch.body_peek)buf.append("BODY");//buf.append("BODY.PEEK");
                         if(fetch.body)buf.append("BODY");
                         if(msg.getBodyBin76()!=null)len_txt=msg.getBodyBin76().length();
                         buf.append("[2]<0> {"+len_txt+"}\r\n");
                         buf.append(msg.getBodyBin76());
                         
                     }
                     else
                     if(part==0){
                         if(fetch.body_peek)buf.append("BODY");//buf.append("BODY.PEEK");
                         if(fetch.body)buf.append("BODY");
            // if(true){//--------------------------------------------------------
            //          body=BODY_TEST;
            // }
                        if(body!=null)len_txt=body.length();
                        buf.append("[] {"+len_txt+"}\r\n");
                        buf.append(body);
                     }
                     else
                     if(part==3){
                        if(fetch.body_peek)buf.append("BODY");//buf.append("BODY.PEEK");
                        if(fetch.body)buf.append("BODY");
                         
                        StringBuilder buf_header=new StringBuilder(); 
                        buf_header.append("Date: ").append(ImapDate.toDateEnvelope(msg.getCreateDate())).append("\r\n");
                        buf_header.append("From: ").append(msg.getFromInet()).append("\r\n");
                        buf_header.append("To: ").append(msg.getTOsInet()).append("\r\n");
                        buf_header.append("Subject: ").append(msg.getSubject()).append("\r\n");
                        buf_header.append("Message-ID: ").append(msg.getId()).append("\r\n");
                        
                        body=buf_header.toString();
                        len_txt=body.length();
                        buf.append("["+fetch.body_section+"] {"+len_txt+"}\r\n");
                        buf.append(body);
                     }
                     else
                     if(part==4){
                        if(fetch.body_peek)buf.append("BODY");//buf.append("BODY.PEEK");
                        if(fetch.body)buf.append("BODY");
                         
                        StringBuilder buf_header=new StringBuilder(); 
                        buf_header.append("Date: ").append(ImapDate.toDateEnvelope(msg.getCreateDate())).append("\r\n");
                        buf_header.append("From: ").append(msg.getFromInet()).append("\r\n");
                        buf_header.append("To: ").append(msg.getTOsInet()).append("\r\n");
                        buf_header.append("Subject: ").append(msg.getSubject()).append("\r\n");
                        buf_header.append("Message-ID: ").append(msg.getId()).append("\r\n");
                        
                        body=buf_header.toString();
                        len_txt=body.length();
                        buf.append("["+fetch.body_section+"] {"+len_txt+"}\r\n");
                        buf.append(body);
                     }
                   }

               return buf.toString();

       }
       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------
              IMAPTransaction txSession     = sessionContext.imapTransaction;
              ImapResponse    ret=null;

              //--------------------------------------------------------------------------------------------------------------------------------------
              // parse field  id-range
              //String          str_id_range    = getParameters().get(0).toString();
              List<IdRange>id_range=null;//=IdRange.parseRange(str_id_range);
              FetchRequest fetch   =new FetchRequest();
              //---------------------------------------------------------------------------------------------------------------------------------------
              id_range=parseFetch(fetch);
              //--------------------------------------------------------------------------------------------------------------------------------------
              // get all msg
              ArrayList<lMessage> all_msg=txSession.getFolder().getMsg();
              logger.trace("all msg size:"+all_msg.size());
              // get  msg in id-range
              ArrayList<lMessage> fetch_msg=new ArrayList<lMessage>();
              for(int i=0;i<all_msg.size();i++){
                  long id;
                  if(isUID) {
                     id=all_msg.get(i).getUID();
                  }
                  else {
                     id=all_msg.get(i).getNum();
                  }
                  //logger.trace("id:"+id);
                  if(IdRange.includes(id,id_range))fetch_msg.add(all_msg.get(i));
              }
              // set list fech msg  to sesion ????
              txSession.setMsg(fetch_msg);

              logger.trace("fetch msg size:"+fetch_msg.size());

              //---------------------------------------------------------------------------------------------------------------------------------------
              for(int i=0;i<fetch_msg.size();i++){
                      //logger.trace("begin MSG:"+i+"----------------------------------------------------------------------------------------------------");

                  lMessage  msg  = fetch_msg.get(i);
                  if(msg==null){
                      logger.error("null msg");
                      break;
                  }else{
                      String body=parseMsg(msg,fetch);
                      if(isUID) {
                         ret=new EmptyResponse(""+msg.getUID()+" "+NAME+" ("+body+")");  responase.add(ret);
                       }
                       else {
                         ret=new EmptyResponse(""+msg.getNum()+" "+NAME+" ("+body+")");  responase.add(ret);
                       }
                  }
              }
              
              if(isUID==false) {
                      ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
                      logger.trace("IMAP:response:"+ret);
              }

              //logger.trace("fetch responase size:"+responase.size());
              
              return responase;
       }
       private static final String SP = " ";
       private static final String NIL = "NIL";
       private static final String Q = "\"";
       private static final String LB = "(";
       private static final String RB = ")";
       //private static final String MULTIPART = "MULTIPART";
      // private static final String MESSAGE = "MESSAGE";
       private  static String date2prn(Date d){
                   if(d==null)d=new Date();
                   return ImapDate.toIMAPDateTime(d);
       }
       /*
       private static  Partial parsePartial(String command){
            stringParser parser=new stringParser(command,".");
            int start=0;
            int size=0;
            String _start=parser.get();
            String _size=parser.get();
            if(_size==null){
               _size="1";
            }
            else{
               try{start=Integer.parseInt(_start,10);}catch(Exception e){start=0;}
            }
            try{size=Integer.parseInt(_size,10);}catch(Exception e){size=1;}
            return Partial.as(start, size);
        }
        */
       private static String escapeHeader(String str){
           return MimeUtility.unfold(str).replace("\\", "\\\\").replace("\"", "\\\"");
       }
       private static String canonicalAddr(String addr){
                       if(addr.indexOf("@")==-1)addr+=("@"+commonIMAP.get().getDefaultDomain());
                       if(addr.indexOf("@")==(addr.length()-1))addr+=(commonIMAP.get().getDefaultDomain());
               return addr;
       }
       private static StringBuilder printAddr(StringBuilder buf,String addr){
                      if(addr==null){buf.append(SP+NIL + SP);}
                      else{
                           addr=canonicalAddr(addr);
                           buf.append(LB);
                           buf.append(Q+addr+Q);
                           buf.append(SP+NIL + SP);
                           String user;
                           try{user=addr.substring(0,addr.indexOf("@"));}catch(Exception e){user=" ";}
                           buf.append(Q+user+Q);
                           buf.append(SP);
                           String domain;
                           try{domain=addr.substring(addr.indexOf("@")+1);}catch(Exception e){domain=" ";}
                           buf.append(Q+domain+Q);
                           buf.append(RB);
                      }
               return buf;
       }
       private static String printBodyStructure(lMessage msg){
               StringBuilder buf=new StringBuilder();
               buf.append(LB);
               //---------------------------------------------------------------------------------------------
               buf.append(LB);
               int len_txt=1;
               if(msg.getBodyTxt()!=null)len_txt=msg.getBodyTxt().length();
               buf.append("\"TEXT\" \"PLAIN\" (\"CHARSET\" \"us-ascii\") NIL NIL \"7bit\" "+len_txt+" 8");
               buf.append(RB);

               buf.append(LB);

               if(msg.getBodyBin76()!=null)len_txt=msg.getBodyBin76().length();
               buf.append("\"APPLICATION\" \"OCTET-STREAM\" (\"NAME\" \""+msg.getFilename()+"\") NIL NIL \"base64\" "+len_txt+" NIL (\"attachment\" (\"FILENAME\" \""+msg.getFilename()+"\"))");

               buf.append(RB);

               buf.append(" \"MIXED\" (\"BOUNDARY\" \"----=_Part_0_1637070917.1594301407736\") NIL NIL");
               //---------------------------------------------------------------------------------------------
               buf.append(RB);
               return buf.toString();
       }
       private static String printEnvelope(lMessage msg){
               StringBuilder buf=new StringBuilder();

               buf.append(LB);
               {                
                 //buf.append(Q + "Fri, 11 Sep 2020 11:29:17 +0300" + Q + SP); /**/
                 //1. DATE RFC-822 ---------------
                 buf.append(Q + ImapDate.toDateEnvelope(msg.getSentDate()) + Q + SP);
                
                 //2. Subject ---------------
                 if (msg.getSubject() != null && (msg.getSubject().length() != 0)) {
                     buf.append(Q + escapeHeader(msg.getSubject()) + Q);
                 } else {
                     buf.append(NIL);
                 }
                 //3. From ---------------
                 buf.append(SP);
                 {
                  buf.append(LB);
                  printAddr(buf,msg.getFrom());
                  buf.append(RB);
                 }
                 //4. Sender ---------------
                 buf.append(SP);
                 {
                  buf.append(LB);
                  printAddr(buf,msg.getFrom());
                  buf.append(RB);
                 }
                 //5. reply-to ---------------
                 buf.append(SP);
                 {
                  buf.append(LB);
                  printAddr(buf,msg.getFrom());
                  buf.append(RB);
                 }
                 //6. to ---------------
                 buf.append(SP);
                 String [] to=msg.getTO();
                 {
                  buf.append(LB);
                  for(int i=0;i<to.length;i++){if(i>1)buf.append(SP); printAddr(buf,to[i]); }
                  buf.append(RB);
                 }
                 //7. cc ---------------
                 buf.append(SP);
                 buf.append(NIL);
                 //8. bcc ---------------
                 buf.append(SP);
                 buf.append(NIL);
                 //9. in-reply-to ---------------
                 buf.append(SP);
                 buf.append(NIL);
                 //
                 // 10 ID MSG ---------------------------------------
                 buf.append(SP);
                 if (msg.getId() != null && msg.getId().length() > 0) {
                     buf.append(Q + escapeHeader(msg.getId()) + Q);
                 } else {
                     buf.append(NIL);
                 }

               }
               buf.append(RB);


               return buf.toString();
       }
public String BODY_TEST=""
+"X-Account-Key: account1\r\n"
+"X-UIDL: 000002cc59faed7d\r\n"
+"X-Mozilla-Status: 0001\r\n"
+"X-Mozilla-Status2: 00000000\r\n"
+"X-Mozilla-Keys:\r\n"
+"Return-Path: <lear@factor-ts.ru>\r\n"
+"X-Original-To: lear@factor-ts.ru\r\n"
+"Delivered-To: lear@factor-ts.ru\r\n"
+"To: lear@factor-ts.ru\r\n"
+"DKIM-Signature: v=1; a=rsa-sha256; c=relaxed/simple; d=factor-ts.ru; s=10;\r\n"
+"	t=1599902162; bh=i0QP7X7ADTgTY8llG4UeY5Dnal1PGxvfE7uKPgHSKoM=;\r\n"
+"	h=To:From:Subject:Date;\r\n"
+"	b=DlwVvJ7K3ZsWbK1TQZq4G+dxFJa49wzacburkjMKRbYEyIOjVkm6WTAtINWg0k+B8\r\n"
+"	 y5KmemIawNwko4hCmR/BTB6GYmGUsYCoUs9XKgvW6d9ho82AOTjjErNjcC44KC3DJX\r\n"
+"	 XcKGZD2nPSVQDtP/omlpVLOZRqxPYZvQZRHliSPQ=\r\n"
+"From: Andrey <lear@factor-ts.ru>\r\n"
+"Subject: subject:123\r\n"
+"Message-ID: <c660a47e-18f4-d38f-f3a7-1c4e60e80d85@factor-ts.ru>\r\n"
+"Date: Sat, 12 Sep 2020 12:15:58 +0300\r\n"
+"MIME-Version: 1.0\r\n"
+"Content-Type: text/plain; charset=utf-8; format=flowed\r\n"
+"Content-Transfer-Encoding: 7bit\r\n"
+"Content-Language: ru\r\n"
+"X-MailScanner-ID: 11654349FA6.A7E67\r\n"
+"X-MailScanner: Found to be clean\r\n"
+"X-MailScanner-From: lear@factor-ts.ru\r\n"
+"X-Spam-Status: No\r\n"
+"\r\n"
+"test123\r\n"
+"\r\n";

public String BODY_TEST1=""
+"Return-Path: av@vip.cbr.ru\r\n"
+"Received: from d-shadrinav.vip.cbr.ru (d-shadrinav.vip.cbr.ru [127.0.0.1])\r\n"
+"	by D-SHADRINAV\r\n"
+"	; Wed, 26 Aug 2020 16:21:20 +0300\r\n"
+"Date: Wed, 26 Aug 2020 16:21:19 +0300 (MSK)\r\n"
+"From: av@vip.cbr.ru\r\n"
+"To: av@vip.cbr.ru, av@vip.cbr.ru\r\n"
+"Message-ID: <3711894.1.1598448080015@d-shadrinav.vip.cbr.ru>\r\n"
+"Subject: subject:Sending a file\r\n"
+"MIME-Version: 1.0\r\n"
+"Content-Type: multipart/mixed; \r\n"
+"	boundary=\"----=_Part_0_22249666.1598448079984\"\r\n"
+"\r\n"
+"------=_Part_0_22249666.1598448079984\r\n"
+"Content-Type: text/plain; charset=us-ascii\r\n"
+"Content-Transfer-Encoding: 7bit\r\n"
+"\r\n"
+"text:Sending a file.\r\n"
+"\r\n"
+"------=_Part_0_22249666.1598448079984\r\n"
+"Content-Type: application/octet-stream; name=92svc-CA-test.cer\r\n"
+"Content-Transfer-Encoding: base64\r\n"
+"Content-Disposition: attachment; filename=92svc-CA-test.cer\r\n"
+"\r\n"
+"MIII1zCCCIKgAwIBAgIQQFAXsNJia97B7Xx2XFwA2jAMBggqhQMHAQEDAgUAMHkxEjAQBgoJkiaJ\r\n"
+"k/IsZAEZFgJydTETMBEGCgmSJomT8ixkARkWA2NicjEWMBQGCgmSJomT8ixkARkWBnJlZ2lvbjEM\r\n"
+"MAoGA1UECxMDUEtJMQ4wDAYDVQQLEwVHVUJaSTEYMBYGA1UEAxMPUk9PVHN2Yy1DQS10ZXN0MB4X\r\n"
+"DTE5MDIwNzA5NTY0NVoXDTM1MDUwNDIzNTkwMFowezESMBAGCgmSJomT8ixkARkWAnJ1MRMwEQYK\r\n"
+"CZImiZPyLGQBGRYDY2JyMRYwFAYKCZImiZPyLGQBGRYGcmVnaW9uMRIwEAYDVQQLEwlUYXRhcnN0\r\n"
+"YW4xDDAKBgNVBAsTA1BLSTEWMBQGA1UEAxMNOTJzdmMtQ0EtdGVzdDBmMB8GCCqFAwcBAQEBMBMG\r\n"
+"ByqFAwICIwEGCCqFAwcBAQICA0MABEBRMaqF0iAEez5Otfz1XUC9qWkVKGAjdUOVJnJegOCfqmpN\r\n"
+"/wrSXJPojhcT7TfmZx4qvh7OjnmpiYPWh+y8HQNLo4IG2TCCBtUwgbgGCSsGAQQBnFYEDwSBqjCB\r\n"
+"p4AU6T1BXNyIQwvWMqVz84qxC7YdeM+hfaR7MHkxEjAQBgoJkiaJk/IsZAEZFgJydTETMBEGCgmS\r\n"
+"JomT8ixkARkWA2NicjEWMBQGCgmSJomT8ixkARkWBnJlZ2lvbjEMMAoGA1UECxMDUEtJMQ4wDAYD\r\n"
+"VQQLEwVHVUJaSTEYMBYGA1UEAxMPUk9PVHN2Yy1DQS10ZXN0ghBANhC30yJWuQvsFzhRZ8ouMB0G\r\n"
+"A1UdDgQWBBTt42bjUFGiGPZ6XFIMJj5/I9Cg3TCBuAYJKwYBBAHQBAQGBIGqMIGngBSi6BTIG8yB\r\n"
+"+elKEcNJlXdZ8HkUFqF9pHsweTESMBAGCgmSJomT8ixkARkWAnJ1MRMwEQYKCZImiZPyLGQBGRYD\r\n"
+"Y2JyMRYwFAYKCZImiZPyLGQBGRYGcmVnaW9uMQwwCgYDVQQLEwNQS0kxDjAMBgNVBAsTBUdVQlpJ\r\n"
+"MRgwFgYDVQQDEw9ST09Uc3ZjLUNBLXRlc3SCEEBQFMBDpXWTXUPmKlp6y4gwKAYJKwYBBAHQBAQD\r\n"
+"BBswGQYJKwYBBAHQBAUDBAwyMzgwVEgzNVIxMDEwWAYFKoUDZG8ETwxN0JDQn9CaICLQodGA0LXQ\r\n"
+"tNGB0YLQstC+INCa0JfQmCDQodCa0JDQlCAi0KHQuNCz0L3QsNGC0YPRgNCwIiIg0LLQtdGA0YHQ\r\n"
+"uNGPIDUwCwYDVR0PBAQDAgGGMHYGA1UdEQRvMG2gSgYDVQQKoEMMQdCe0YLQtNC10LvQtdC90LjQ\r\n"
+"tSAtINCd0JEg0KDQtdGB0L/Rg9Cx0LvQuNC60LAg0KLQsNGC0LDRgNGB0YLQsNC9oB8GA1UEDaAY\r\n"
+"DBbQodC10YDQuNGPINCY0JDQoSDQojA3MCsGA1UdEAQkMCKADzIwMTkwMjA3MDk1NjQ1WoEPMjAy\r\n"
+"MDA1MDQyMzU5MDBaMBAGCSsGAQQB0AQECwQDAgEBMIHVBggrBgEFBQcBAQSByDCBxTBaBggrBgEF\r\n"
+"BQcwAoZObGRhcDovL3JlZ2lvbi5jYnIucnUvQ049Uk9PVHN2Yy1DQS10ZXN0LE9VPUdVQlpJLE9V\r\n"
+"PVBLSSxEQz1yZWdpb24sREM9Y2JyLERDPXJ1MGcGCCsGAQUFBzAChltsZGFwOi8vcmVnaW9uLmNi\r\n"
+"ci5ydS9DTj1ST09Uc3ZjLUNBLTU2MDFJUENUNU4wMS10ZXN0LE9VPUdVQlpJLE9VPVBLSSxEQz1y\r\n"
+"ZWdpb24sREM9Y2JyLERDPXJ1MIGyBgNVHSMEgaowgaeAFEH+BGZ+4g6vtPMfo7uEiXcUwYltoX2k\r\n"
+"ezB5MRIwEAYKCZImiZPyLGQBGRYCcnUxEzARBgoJkiaJk/IsZAEZFgNjYnIxFjAUBgoJkiaJk/Is\r\n"
+"ZAEZFgZyZWdpb24xDDAKBgNVBAsTA1BLSTEOMAwGA1UECxMFR1VCWkkxGDAWBgNVBAMTD1JPT1Rz\r\n"
+"dmMtQ0EtdGVzdIIQQFAXsAthaGZpfvSsXFgEijAPBgNVHRMECDAGAQH/AgEAMIIBIgYFKoUDZHAE\r\n"
+"ggEXMIIBEwxN0JDQn9CaICLQodGA0LXQtNGB0YLQstC+INCa0JfQmCDQodCa0JDQlCAi0KHQuNCz\r\n"
+"0L3QsNGC0YPRgNCwIiIg0LLQtdGA0YHQuNGPIDUMP9CQ0J/QmiAi0KHQuNCz0L3QsNGC0YPRgNCw\r\n"
+"LdGB0LXRgNGC0LjRhNC40LrQsNGCIiDQstC10YDRgdC40Y8gNQxU0JLRi9C/0LjRgdC60LAg0LjQ\r\n"
+"tyDQl9Cw0LrQu9GO0YfQtdC90LjRjyDihJYxNDkvMy8yLzItMTAyMiDQvtGCIDMwINC40Y7QvdGP\r\n"
+"IDIwMTUg0LMuDCvQodCkLzEyOC0yNzQ2INC+0YIgMTcg0L3QvtGP0LHRgNGPIDIwMTUg0LMuMIHE\r\n"
+"BgNVHR8EgbwwgbkwVKBSoFCGTmxkYXA6Ly9yZWdpb24uY2JyLnJ1L0NOPVJPT1RzdmMtQ0EtdGVz\r\n"
+"dCxPVT1HVUJaSSxPVT1QS0ksREM9cmVnaW9uLERDPWNicixEQz1ydTBhoF+gXYZbbGRhcDovL3Jl\r\n"
+"Z2lvbi5jYnIucnUvQ049Uk9PVHN2Yy1DQS01NjAxSVBDVDVOMDEtdGVzdCxPVT1HVUJaSSxPVT1Q\r\n"
+"S0ksREM9cmVnaW9uLERDPWNicixEQz1ydTBrBgNVHRIEZDBioD8GA1UECqA4DDbQmtC+0YDQvdC1\r\n"
+"0LLQvtC5INCm0KEg0JTQkdCR0KAg0JHQsNC90LrQsCDQoNC+0YHRgdC40LigHwYDVQQNoBgMFtCh\r\n"
+"0LXRgNC40Y8g0JjQkNChINCiMDcwDAYIKoUDBwEBAwIFAANBAOX7FXqYWJrcrDh4eP7q7S5yKTa5\r\n"
+"mHK3dDT8ZuLgKK6zBXdsZXphfWScQToXvjkn6N6M9F2WNzbtMu/p2jYXF/M=\r\n"
+"------=_Part_0_22249666.1598448079984--\r\n"
;

  /*     private static String printBody(lMessage msg,String body_section){
               StringBuilder buf=new StringBuilder();
               int part=0;
               if(body_section!=null) {
                       try {part=Integer.parseInt(body_section);} catch(Exception e) {part=0;}
                       if(part!=1 || part!=2)part=0;
               }
               logger.trace("set parth:"+part);
          
               int len_txt=1;
               if(part==1){
                  if(msg.getBodyTxt()!=null)len_txt=msg.getBodyTxt().length();
                  buf.append("[1]<0> {"+len_txt+"} ");
                  buf.append(msg.getBodyTxt());
                  buf.append(SP);
               }
               if(part==2){
                  if(msg.getBodyBin76()!=null)len_txt=msg.getBodyBin76().length();
                  buf.append("[2]<0> {"+len_txt+"}");
                  buf.append("\r\n");
                  buf.append(msg.getBodyBin76());
                  
              }
              if(part==0){
                     String body=lMessage2ELM.parse(msg);
                       if(body!=null)len_txt=body.length();
                   buf.append("[] {"+len_txt+"}");
                   buf.append("\r\n");
                   buf.append(body);
              }
              return buf.toString();
       }
*/
}

/*
6.4.5.  FETCH Command

   Arguments:  message set
               message data item names

   Responses:  untagged responses: FETCH

   Result:     OK - fetch completed
               NO - fetch error: can't fetch that data
               BAD - command unknown or arguments invalid

      The FETCH command retrieves data associated with a message in the
      mailbox.  The data items to be fetched can be either a single atom
      or a parenthesized list.

      The currently defined data items that can be fetched are:

      ALL            Macro equivalent to: (FLAGS INTERNALDATE
                     RFC822.SIZE ENVELOPE)

      BODY           Non-extensible form of BODYSTRUCTURE.

      BODY[<section>]<<partial>>
                     The text of a particular body section.  The section
                     specification is a set of zero or more part
                     specifiers delimited by periods.  A part specifier
                     is either a part number or one of the following:
                     HEADER, HEADER.FIELDS, HEADER.FIELDS.NOT, MIME, and
                     TEXT.  An empty section specification refers to the
                     entire message, including the header.

                     Every message has at least one part number.
                     Non-[MIME-IMB] messages, and non-multipart
                     [MIME-IMB] messages with no encapsulated message,
                     only have a part 1.

                     Multipart messages are assigned consecutive part
                     numbers, as they occur in the message.  If a
                     particular part is of type message or multipart,
                     its parts MUST be indicated by a period followed by
                     the part number within that nested multipart part.

                     A part of type MESSAGE/RFC822 also has nested part
                     numbers, referring to parts of the MESSAGE part's
                     body.

                     The HEADER, HEADER.FIELDS, HEADER.FIELDS.NOT, and
                     TEXT part specifiers can be the sole part specifier
                     or can be prefixed by one or more numeric part
                     specifiers, provided that the numeric part
                     specifier refers to a part of type MESSAGE/RFC822.
                     The MIME part specifier MUST be prefixed by one or
                     more numeric part specifiers.

                     The HEADER, HEADER.FIELDS, and HEADER.FIELDS.NOT
                     part specifiers refer to the [RFC-822] header of
                     the message or of an encapsulated [MIME-IMT]
                     MESSAGE/RFC822 message.  HEADER.FIELDS and
                     HEADER.FIELDS.NOT are followed by a list of
                     field-name (as defined in [RFC-822]) names, and
                     return a subset of the header.  The subset returned
                     by HEADER.FIELDS contains only those header fields
                     with a field-name that matches one of the names in
                     the list; similarly, the subset returned by
                     HEADER.FIELDS.NOT contains only the header fields
                     with a non-matching field-name.  The field-matching
                     is case-insensitive but otherwise exact.  In all
                     cases, the delimiting blank line between the header
                     and the body is always included.

                     The MIME part specifier refers to the [MIME-IMB]
                     header for this part.

                     The TEXT part specifier refers to the text body of
                     the message, omitting the [RFC-822] header.


                       Here is an example of a complex message
                       with some of its part specifiers:

                        HEADER     ([RFC-822] header of the message)
                        TEXT       MULTIPART/MIXED
                        1          TEXT/PLAIN
                        2          APPLICATION/OCTET-STREAM
                        3          MESSAGE/RFC822
                        3.HEADER   ([RFC-822] header of the message)
                        3.TEXT     ([RFC-822] text body of the message)
                        3.1        TEXT/PLAIN
                        3.2        APPLICATION/OCTET-STREAM
                        4          MULTIPART/MIXED
                        4.1        IMAGE/GIF
                        4.1.MIME   ([MIME-IMB] header for the IMAGE/GIF)
                        4.2        MESSAGE/RFC822
                        4.2.HEADER ([RFC-822] header of the message)
                        4.2.TEXT   ([RFC-822] text body of the message)
                        4.2.1      TEXT/PLAIN
                        4.2.2      MULTIPART/ALTERNATIVE
                        4.2.2.1    TEXT/PLAIN
                        4.2.2.2    TEXT/RICHTEXT


                     It is possible to fetch a substring of the
                     designated text.  This is done by appending an open
                     angle bracket ("<"), the octet position of the
                     first desired octet, a period, the maximum number
                     of octets desired, and a close angle bracket (">")
                     to the part specifier.  If the starting octet is
                     beyond the end of the text, an empty string is
                     returned.

                     Any partial fetch that attempts to read beyond the
                     end of the text is truncated as appropriate.  A
                     partial fetch that starts at octet 0 is returned as
                     a partial fetch, even if this truncation happened.

                          Note: this means that BODY[]<0.2048> of a
                          1500-octet message will return BODY[]<0>
                          with a literal of size 1500, not BODY[].

                          Note: a substring fetch of a
                          HEADER.FIELDS or HEADER.FIELDS.NOT part
                          specifier is calculated after subsetting
                          the header.


                     The \Seen flag is implicitly set; if this causes
                     the flags to change they SHOULD be included as part
                     of the FETCH responses.

      BODY.PEEK[<section>]<<partial>>
                     An alternate form of BODY[<section>] that does not
                     implicitly set the \Seen flag.

      BODYSTRUCTURE  The [MIME-IMB] body structure of the message.  This
                     is computed by the server by parsing the [MIME-IMB]
                     header fields in the [RFC-822] header and
                     [MIME-IMB] headers.

      ENVELOPE       The envelope structure of the message.  This is
                     computed by the server by parsing the [RFC-822]
                     header into the component parts, defaulting various
                     fields as necessary.

      FAST           Macro equivalent to: (FLAGS INTERNALDATE
                     RFC822.SIZE)

      FLAGS          The flags that are set for this message.

      FULL           Macro equivalent to: (FLAGS INTERNALDATE
                     RFC822.SIZE ENVELOPE BODY)

      INTERNALDATE   The internal date of the message.

      RFC822         Functionally equivalent to BODY[], differing in the
                     syntax of the resulting untagged FETCH data (RFC822
                     is returned).

      RFC822.HEADER  Functionally equivalent to BODY.PEEK[HEADER],
                     differing in the syntax of the resulting untagged
                     FETCH data (RFC822.HEADER is returned).

      RFC822.SIZE    The [RFC-822] size of the message.

      RFC822.TEXT    Functionally equivalent to BODY[TEXT], differing in
                     the syntax of the resulting untagged FETCH data
                     (RFC822.TEXT is returned).

      UID            The unique identifier for the message.

   Example:    C: A654 FETCH 2:4 (FLAGS BODY[HEADER.FIELDS (DATE FROM)])
               S: * 2 FETCH ....
               S: * 3 FETCH ....
               S: * 4 FETCH ....
               S: A654 OK FETCH completed


7.4.2.  FETCH Response

   Contents:   message data

      The FETCH response returns data about a message to the client.
      The data are pairs of data item names and their values in
      parentheses.  This response occurs as the result of a FETCH or
      STORE command, as well as by unilateral server decision (e.g. flag
      updates).

      The current data items are:

      BODY           A form of BODYSTRUCTURE without extension data.

      BODY[<section>]<<origin_octet>>
                     A string expressing the body contents of the
                     specified section.  The string SHOULD be
                     interpreted by the client according to the content
                     transfer encoding, body type, and subtype.

                     If the origin octet is specified, this string is a
                     substring of the entire body contents, starting at
                     that origin octet.  This means that BODY[]<0> MAY
                     be truncated, but BODY[] is NEVER truncated.

                     8-bit textual data is permitted if a [CHARSET]
                     identifier is part of the body parameter
                     parenthesized list for this section.  Note that
                     headers (part specifiers HEADER or MIME, or the
                     header portion of a MESSAGE/RFC822 part), MUST be
                     7-bit; 8-bit characters are not permitted in
                     headers.  Note also that the blank line at the end
                     of the header is always included in header data.

                     Non-textual data such as binary data MUST be
                     transfer encoded into a textual form such as BASE64
                     prior to being sent to the client.  To derive the
                     original binary data, the client MUST decode the
                     transfer encoded string.

      BODYSTRUCTURE  A parenthesized list that describes the [MIME-IMB]
                     body structure of a message.  This is computed by
                     the server by parsing the [MIME-IMB] header fields,
                     defaulting various fields as necessary.

                     For example, a simple text message of 48 lines and
                     2279 octets can have a body structure of: ("TEXT"
                     "PLAIN" ("CHARSET" "US-ASCII") NIL NIL "7BIT" 2279
                     48)

                     Multiple parts are indicated by parenthesis
                     nesting.  Instead of a body type as the first
                     element of the parenthesized list there is a nested
                     body.  The second element of the parenthesized list
                     is the multipart subtype (mixed, digest, parallel,
                     alternative, etc.).

                     For example, a two part message consisting of a
                     text and a BASE645-encoded text attachment can have
                     a body structure of: (("TEXT" "PLAIN" ("CHARSET"
                     "US-ASCII") NIL NIL "7BIT" 1152 23)("TEXT" "PLAIN"
                     ("CHARSET" "US-ASCII" "NAME" "cc.diff")
                     "<960723163407.20117h@cac.washington.edu>"
                     "Compiler diff" "BASE64" 4554 73) "MIXED"))

                     Extension data follows the multipart subtype.
                     Extension data is never returned with the BODY
                     fetch, but can be returned with a BODYSTRUCTURE
                     fetch.  Extension data, if present, MUST be in the
                     defined order.

                     The extension data of a multipart body part are in
                     the following order:

                     body parameter parenthesized list
                        A parenthesized list of attribute/value pairs
                        [e.g. ("foo" "bar" "baz" "rag") where "bar" is
                        the value of "foo" and "rag" is the value of
                        "baz"] as defined in [MIME-IMB].

                     body disposition
                        A parenthesized list, consisting of a
                        disposition type string followed by a
                        parenthesized list of disposition
                        attribute/value pairs.  The disposition type and
                        attribute names will be defined in a future
                        standards-track revision to [DISPOSITION].

                     body language
                        A string or parenthesized list giving the body
                        language value as defined in [LANGUAGE-TAGS].

                     Any following extension data are not yet defined in
                     this version of the protocol.  Such extension data
                     can consist of zero or more NILs, strings, numbers,
                     or potentially nested parenthesized lists of such
                     data.  Client implementations that do a
                     BODYSTRUCTURE fetch MUST be prepared to accept such
                     extension data.  Server implementations MUST NOT
                     send such extension data until it has been defined
                     by a revision of this protocol.

                     The basic fields of a non-multipart body part are
                     in the following order:

                     body type
                        A string giving the content media type name as
                        defined in [MIME-IMB].

                     body subtype
                        A string giving the content subtype name as
                        defined in [MIME-IMB].

                     body parameter parenthesized list
                        A parenthesized list of attribute/value pairs
                        [e.g. ("foo" "bar" "baz" "rag") where "bar" is
                        the value of "foo" and "rag" is the value of
                        "baz"] as defined in [MIME-IMB].

                     body id
                        A string giving the content id as defined in
                        [MIME-IMB].

                     body description
                        A string giving the content description as
                        defined in [MIME-IMB].

                     body encoding
                        A string giving the content transfer encoding as
                        defined in [MIME-IMB].

                     body size
                        A number giving the size of the body in octets.
                        Note that this size is the size in its transfer
                        encoding and not the resulting size after any
                        decoding.

                     A body type of type MESSAGE and subtype RFC822
                     contains, immediately after the basic fields, the
                     envelope structure, body structure, and size in
                     text lines of the encapsulated message.

                     A body type of type TEXT contains, immediately
                     after the basic fields, the size of the body in
                     text lines.  Note that this size is the size in its
                     content transfer encoding and not the resulting
                     size after any decoding.

                     Extension data follows the basic fields and the
                     type-specific fields listed above.  Extension data
                     is never returned with the BODY fetch, but can be
                     returned with a BODYSTRUCTURE fetch.  Extension
                     data, if present, MUST be in the defined order.

                     The extension data of a non-multipart body part are
                     in the following order:

                     body MD5
                        A string giving the body MD5 value as defined in
                        [MD5].

                     body disposition
                        A parenthesized list with the same content and
                        function as the body disposition for a multipart
                        body part.

                     body language
                        A string or parenthesized list giving the body
                        language value as defined in [LANGUAGE-TAGS].

                     Any following extension data are not yet defined in
                     this version of the protocol, and would be as
                     described above under multipart extension data.

      ENVELOPE       A parenthesized list that describes the envelope
                     structure of a message.  This is computed by the
                     server by parsing the [RFC-822] header into the
                     component parts, defaulting various fields as
                     necessary.

                     The fields of the envelope structure are in the
                     following order: date, subject, from, sender,
                     reply-to, to, cc, bcc, in-reply-to, and message-id.
                     The date, subject, in-reply-to, and message-id
                     fields are strings.  The from, sender, reply-to,
                     to, cc, and bcc fields are parenthesized lists of
                     address structures.

                     An address structure is a parenthesized list that
                     describes an electronic mail address.  The fields
                     of an address structure are in the following order:
                     personal name, [SMTP] at-domain-list (source
                     route), mailbox name, and host name.

                     [RFC-822] group syntax is indicated by a special
                     form of address structure in which the host name
                     field is NIL.  If the mailbox name field is also
                     NIL, this is an end of group marker (semi-colon in
                     RFC 822 syntax).  If the mailbox name field is
                     non-NIL, this is a start of group marker, and the
                     mailbox name field holds the group name phrase.

                     Any field of an envelope or address structure that
                     is not applicable is presented as NIL.  Note that
                     the server MUST default the reply-to and sender
                     fields from the from field; a client is not
                     expected to know to do this.

      FLAGS          A parenthesized list of flags that are set for this
                     message.

      INTERNALDATE   A string representing the internal date of the
                     message.

      RFC822         Equivalent to BODY[].

      RFC822.HEADER  Equivalent to BODY.PEEK[HEADER].

      RFC822.SIZE    A number expressing the [RFC-822] size of the
                     message.

      RFC822.TEXT    Equivalent to BODY[TEXT].

      UID            A number expressing the unique identifier of the
                     message.


   Example:    S: * 23 FETCH (FLAGS (\Seen) RFC822.SIZE 44827)

*/
