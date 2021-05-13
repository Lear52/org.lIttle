package org.little.mail.imap.command.cmd;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.little.lmsg.ELM2lMessage;
import org.little.lmsg.lMessage;
import org.little.lmsg.lMessageX509;
import org.little.lmsg.store.lFolder;
import org.little.lmsg.store.lUID;
import org.little.mail.imap.IMAPTransaction;
import org.little.mail.imap.SessionContext;
import org.little.mail.imap.command.ImapCommand;
import org.little.mail.imap.command.ImapCommandParameter;
import org.little.mail.imap.command.ImapConstants;
import org.little.mail.imap.response.EmptyResponse;
import org.little.mail.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;

/**
 * Handles processing for the APPEND imap command.
 *
 */
public class AppendCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(AppendCommand.class);

       public static final String NAME = "APPEND";
       public static final String ARGS = "<mailbox> [<flag_list>] [<date_time>] literal";
       private byte [] buffer;
       private int     point;
       private int     _size;

       public AppendCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { 
    	   super(_tag,_command,_parameters);
    	   buffer=null;
    	   _size=0;
    	   point=0;
       }

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------
              ImapResponse ret        =null;
              String       folder_name=null;
              String       date       =null;
              String       flag       =null;
              String       size       =null;
              lFolder      folder        = null;
              IMAPTransaction txSession     = sessionContext.imapTransaction;
              
              if(getParameters().size()>0) {
                 String arg= getParameters().get(0).toString();
                 if(arg.startsWith("\"") && arg.endsWith("\"")){folder_name=arg.substring(1, arg.length()-1);}
                 else folder_name   = arg;
              }
              else {
                  ret=new EmptyResponse(getTag(),ImapConstants.BAD+" "+NAME+" "+ImapConstants.BADCOMMAND);   
                  responase.add(ret);
                  return responase; 
              }
              int n=1;
              while(getParameters().size()>n) {
                 String arg= getParameters().get(n).toString();
                 if(arg.startsWith("(") && arg.endsWith(")")){flag=arg.substring(1, arg.length()-1);}
                 if(arg.startsWith("{") && arg.endsWith("}")){size=arg.substring(1, arg.length()-1);}
                 if(arg.startsWith("\"") && arg.endsWith("\"")){date=arg.substring(1, arg.length()-1);}
                 n++;
              }

              if(flag!=null) {}
              if(size!=null) {try{_size=Integer.parseInt(size);}catch(Exception e) {_size=0;}}
              if(date!=null) {}

              if(_size==0){
                 logger.error("size literal data is 0");
                 ret=new EmptyResponse(getTag(),ImapConstants.BAD+" "+NAME+" "+ImapConstants.BADCOMMAND);                     
                 responase.add(ret);
                 logger.trace("response:"+ret);
                 return responase;

              }
              //byte[] mail = consumeLiteralAsBytes(request);
              //--------------------------------------------------------------------------------------------------------------------------------------
              if(buffer==null) {
            	  ret=new EmptyResponse("+","Ready for literal data");   responase.add(ret);
            	  buffer=new byte[_size];
                  logger.trace("alloc buffer for liter data :"+_size);
              }
              else {
                  ByteArrayInputStream in_byte=new ByteArrayInputStream(buffer);
                  BufferedInputStream  in_stream=new BufferedInputStream(in_byte);

                  lMessage[] buf_message=ELM2lMessage.parse(in_stream);
                  folder=txSession.getStore().getFolder(folder_name);
                  if(folder==null){
                     logger.error("IMAP error open store:"+txSession.getUserName()+" folder:"+folder_name);
                     ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                     
                     responase.add(ret);
                     logger.trace("response:"+ret);
                     return responase;
                  }

                  if(buf_message==null){
                      ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                     
                      responase.add(ret);
                      logger.trace("response:"+ret);
                      return responase;
                   }
                   for(int i=0;i<buf_message.length;i++){
                       if(buf_message[i]==null)continue;
                       buf_message[i]=lMessageX509.parse(buf_message[i]);
                       if(buf_message[i]==null)continue;

                       lMessage  msg  =buf_message[i];
                       msg.setUID(lUID.get());
                       folder.save(msg);
                   }
                   folder.close();
            	  ret=new EmptyResponse(3+" EXISTS");   responase.add(ret);
            	  ret=new EmptyResponse(1+" RECENT");   responase.add(ret);
            	  ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              }
              logger.trace("IMAP:response:"+ret);

              return responase;
       }
       @Override
       public boolean appendBuf(ByteBuf in){
    	   if(buffer==null)return false;

    	   int _sz=Math.min((_size-point),in.readableBytes());
    	   
    	   in.readBytes(buffer, point, _sz);
    	   point+=_sz;
    	   
    	   if(point==_size){
              in.skipBytes(2);  /**/
              return true;
    	   }
    	   
    	   return false;
       }
       @Override
       public boolean isAppend() {return true;}


}

/*
6.3.11. APPEND Command

   Arguments:  mailbox name
               OPTIONAL flag parenthesized list
               OPTIONAL date/time string
               message literal

   Responses:  no specific responses for this command

   Result:     OK - append completed
               NO - append error: can't append to that mailbox, error
                    in flags or date/time or message text
               BAD - command unknown or arguments invalid

      The APPEND command appends the literal argument as a new message
      to the end of the specified destination mailbox.  This argument
      SHOULD be in the format of an [RFC-822] message.  8-bit characters
      are permitted in the message.  A server implementation that is
      unable to preserve 8-bit data properly MUST be able to reversibly
      convert 8-bit APPEND data to 7-bit using a [MIME-IMB] content
      transfer encoding.

      Note: There MAY be exceptions, e.g. draft messages, in which
      required [RFC-822] header lines are omitted in the message literal
      argument to APPEND.  The full implications of doing so MUST be
      understood and carefully weighed.

   If a flag parenthesized list is specified, the flags SHOULD be set in
   the resulting message; otherwise, the flag list of the resulting
   message is set empty by default.

   If a date_time is specified, the internal date SHOULD be set in the
   resulting message; otherwise, the internal date of the resulting
   message is set to the current date and time by default.

   If the append is unsuccessful for any reason, the mailbox MUST be
   restored to its state before the APPEND attempt; no partial appending
   is permitted.

   If the destination mailbox does not exist, a server MUST return an
   error, and MUST NOT automatically create the mailbox.  Unless it is
   certain that the destination mailbox can not be created, the server
   MUST send the response code "[TRYCREATE]" as the prefix of the text
   of the tagged NO response.  This gives a hint to the client that it
   can attempt a CREATE command and retry the APPEND if the CREATE is
   successful.

   If the mailbox is currently selected, the normal new mail actions
   SHOULD occur.  Specifically, the server SHOULD notify the client
   immediately via an untagged EXISTS response.  If the server does not
   do so, the client MAY issue a NOOP command (or failing that, a CHECK
   command) after one or more APPEND commands.

   Example:    C: A003 APPEND saved-messages (\Seen) {310}
               C: Date: Mon, 7 Feb 1994 21:52:25 -0800 (PST)
               C: From: Fred Foobar <foobar@Blurdybloop.COM>
               C: Subject: afternoon meeting
               C: To: mooch@owatagu.siam.edu
               C: Message-Id: <B27397-0100000@Blurdybloop.COM>
               C: MIME-Version: 1.0
               C: Content-Type: TEXT/PLAIN; CHARSET=US-ASCII
               C:
               C: Hello Joe, do you think we can meet at 3:30 tomorrow?
               C:
               S: A003 OK APPEND completed

      Note: the APPEND command is not used for message delivery, because
      it does not provide a mechanism to transfer [SMTP] envelope
      information.

*/
