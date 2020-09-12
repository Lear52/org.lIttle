package org.little.imap.command.cmd;

import java.util.ArrayList;
import java.util.List;

import org.little.imap.IMAPTransaction;
import org.little.imap.SessionContext;
import org.little.imap.command.ImapCommand;
import org.little.imap.command.ImapCommandParameter;
import org.little.imap.command.ImapConstants;
import org.little.imap.response.EmptyResponse;
import org.little.imap.response.ImapResponse;
import org.little.store.lFolder;
import org.little.store.lMessage;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


/**
 * Handles processeing for the STATUS imap command.
 *
 */
public class StatusCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(StatusCommand.class);

       public static final String NAME = "STATUS";
       public static final String ARGS = "<mailbox> ( <status-data-item>+ )";

       private static final String MESSAGES    = "MESSAGES";
       private static final String RECENT      = "RECENT";
       private static final String UIDNEXT     = "UIDNEXT";
       private static final String UIDVALIDITY = "UIDVALIDITY";
       private static final String UNSEEN      = "UNSEEN";


       public StatusCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              ImapResponse ret=null;
              //--------------------------------------------------------------------------------------------------------------------------------------
              IMAPTransaction txSession     = sessionContext.imapTransaction;

              String org_folder_name   = getParameters().get(0).toString();
              String folder_name   = org_folder_name.toLowerCase();

              lFolder folder=txSession.getStore().getFolder(folder_name);
              //-------------------------------------------------------------------------------------------------
              // to lFolder
              //-------------------------------------------------------------------------------------------------
              
              int unread_msg      =0;            
              int recent          =0;            
              int all_msg         =0;
              int max_uid         =0;
              int first_unread_msg=1;
              int valid_uid       =0;
              int next_uid       =0;
              if(folder!=null){
                  ArrayList<lMessage> list_msg=folder.getMsg();
                  if(list_msg==null){
                     logger.error("IMAP error read store:"+txSession.getUserName()+" folder:"+folder_name);
                     ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                     
                     responase.add(ret);
                     logger.trace("IMAP:response:"+ret);
                     return responase;
                  }

            	  for(int i=0;i<list_msg.size();i++)if(list_msg.get(i).getReceiveDate()==null)unread_msg++;
                  recent    =recent; 
                  all_msg   =list_msg.size();
                
                 for(int i=0;i<list_msg.size();i++)max_uid=Math.max(list_msg.get(i).getUID(),max_uid);
                
                 next_uid=max_uid+1;
                
                 for(int i=0;i<list_msg.size();i++)if(list_msg.get(i).getReceiveDate()==null){first_unread_msg=list_msg.get(i).getUID();break;}
                
                 valid_uid=folder.getUID();
              }
              //--------------------------------------------------------------------------------------------------

              StringBuilder buf=new StringBuilder();
              
              buf.append(NAME).append(' ').append(org_folder_name).append(" (");
              
              for(int i=0;i<getParameters().size();i++) {
                  ImapCommandParameter p=getParameters().get(i);
                  if(p==null)continue;
                  String arg1   = p.toString();
                  if(arg1==null)continue;

            	  if(arg1.equalsIgnoreCase("MESSAGES"   )) {buf.append("MESSAGES "   ).append(all_msg)  .append(" ");}
            	  else
            	  if(arg1.equalsIgnoreCase("RECENT"     )) {buf.append("RECENT "     ).append(recent)   .append(" ");}
            	  else
            	  if(arg1.equalsIgnoreCase("UIDNEXT"    )) {buf.append("UIDNEXT "    ).append(next_uid) .append(" ");}
            	  else
            	  if(arg1.equalsIgnoreCase("UIDVALIDITY")) {buf.append("UIDVALIDITY ").append(valid_uid).append(" ");}
            	  else
            	  if(arg1.equalsIgnoreCase("UNSEEN"     )) {buf.append("UNSEEN "     ).append(unread_msg).append(" ");}
              }
              buf.append(")");


              //--------------------------------------------------------------------------------------------------------------------------------------
              if(folder!=null){
                 ret=new EmptyResponse(buf.toString());                                                   responase.add(ret);
                 ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              }
              else{
                 ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);   responase.add(ret);
              }
              logger.trace("IMAP:response:"+ret);

              return responase;
       }
}

/*
6.3.10. STATUS Command

   Arguments:  mailbox name
               status data item names

   Responses:  untagged responses: STATUS

   Result:     OK - status completed
               NO - status failure: no status for that name
               BAD - command unknown or arguments invalid

      The STATUS command requests the status of the indicated mailbox.
      It does not change the currently selected mailbox, nor does it
      affect the state of any messages in the queried mailbox (in
      particular, STATUS MUST NOT cause messages to lose the \Recent
      flag).

      The STATUS command provides an alternative to opening a second
      IMAP4rev1 connection and doing an EXAMINE command on a mailbox to
      query that mailbox's status without deselecting the current
      mailbox in the first IMAP4rev1 connection.

      Unlike the LIST command, the STATUS command is not guaranteed to
      be fast in its response.  In some implementations, the server is
      obliged to open the mailbox read-only internally to obtain certain
      status information.  Also unlike the LIST command, the STATUS
      command does not accept wildcards.

      The currently defined status data items that can be requested are:

      MESSAGES       The number of messages in the mailbox.

      RECENT         The number of messages with the \Recent flag set.

      UIDNEXT        The next UID value that will be assigned to a new
                     message in the mailbox.  It is guaranteed that this
                     value will not change unless new messages are added
                     to the mailbox; and that it will change when new
                     messages are added even if those new messages are
                     subsequently expunged.



Crispin                     Standards Track                    [Page 33]

RFC 2060                       IMAP4rev1                   December 1996


      UIDVALIDITY    The unique identifier validity value of the
                     mailbox.

      UNSEEN         The number of messages which do not have the \Seen
                     flag set.


      Example:    C: A042 STATUS blurdybloop (UIDNEXT MESSAGES)
                  S: * STATUS blurdybloop (MESSAGES 231 UIDNEXT 44292)
                  S: A042 OK STATUS completed

*/
