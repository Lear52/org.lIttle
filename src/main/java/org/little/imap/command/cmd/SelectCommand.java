package org.little.imap.command.cmd;

import java.util.List;
import java.util.ArrayList;

import org.little.imap.IMAPTransaction;
import org.little.imap.SessionContext;
import org.little.imap.command.ImapCommand;
import org.little.imap.command.ImapCommandParameter;
import org.little.imap.command.ImapConstants;
import org.little.imap.response.EmptyResponse;
import org.little.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.imap.command.param.*;
import org.little.store.*;


/**
 * Handles processeing for the SELECT imap command.
 *
 */
public class SelectCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(SelectCommand.class);

       public static final String NAME = "SELECT";
       public static final String ARGS = "mailbox";

       public SelectCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              
              String          folder_name   = null;
              ImapResponse    ret           = null;
              lFolder         folder        = null;
              IMAPTransaction txSession     = sessionContext.imapTransaction;

              if(getParameters().size()>0)folder_name   = getParameters().get(0).toString();
              if(txSession==null){
                 logger.error("txSession is null");
                 ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                     
                 responase.add(ret);
                 logger.trace("response:"+ret);
                 return responase;
              }
              if(txSession.getStore()==null){
                 logger.error("txSession store is null");
                 ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                     
                 responase.add(ret);
                 logger.trace("response:"+ret);
                 return responase;
              }

              folder=txSession.getStore().getFolder(folder_name);
              if(folder==null){
                 logger.error("error open store:"+txSession.getUserName()+" folder:"+folder_name);
                 ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                     
                 responase.add(ret);
                 logger.trace("response:"+ret);
                 return responase;
              }
              ArrayList<lMessage> list_msg=folder.getMsg();
              if(list_msg==null){
                 logger.error("error read store:"+txSession.getUserName()+" folder:"+folder_name);
                 ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                     
                 responase.add(ret);
                 logger.trace("response:"+ret);
                 return responase;
              }
              txSession.setFolderName(folder_name);
              txSession.setFolder(folder);
              txSession.setMsg(list_msg);
              int all_msg   =lFolder.getAllMsg(list_msg);
              int unread_msg=lFolder.getUnreadMsg(list_msg);
              int first_unread_msg=lFolder.getFirstUnreadMsg(list_msg);
              int next_uid=all_msg+1;

              ret=new EmptyResponse(all_msg+" EXISTS");                                                                            responase.add(ret);
              ret=new EmptyResponse(unread_msg+" RECENT");                                                                         responase.add(ret);
              ret=new EmptyResponse("FLAGS (\\Deleted \\Seen \\Draft \\Answered \\Flagged)");                                      responase.add(ret);
              ret=new EmptyResponse(ImapConstants.OK+" [UIDVALIDITY 1594491108] current uidvalidity");                             responase.add(ret);
              ret=new EmptyResponse(ImapConstants.OK+" [UNSEEN "+first_unread_msg+"] unseen messages");                            responase.add(ret);
              ret=new EmptyResponse(ImapConstants.OK+" [UIDNEXT "+next_uid+"] next uid");                                          responase.add(ret);
              ret=new EmptyResponse(ImapConstants.OK+" [PERMANENTFLAGS (\\Deleted \\Seen \\Draft \\Answered \\Flagged)] limited"); responase.add(ret);
              ret=new EmptyResponse(getTag(),ImapConstants.OK+" [READ-WRITE] "+NAME+" "+ImapConstants.COMPLETED);                  responase.add(ret);

              return responase;

              //mailbox.getPermanentFlags()
              //mailbox.getMessageCount()
              //mailbox.getRecentCount(resetRecent)
              //"UIDVALIDITY " + mailbox.getUidValidity()
              //"UIDNEXT " + mailbox.getUidNext()
              //"UNSEEN " + firstUnseen, "Message " + firstUnseen + " is the first unseen"
              //"No messages unseen"
/*
	public void respond(SelectResponse response) {
		untagged("FLAGS (\\Answered \\Flagged \\Deleted \\Seen \\Draft)\r\n");
		untagged(response.getMessageCount() + " EXISTS\r\n");
		untagged(response.getRecentMessageCount() + " RECENT\r\n");
		if (response.getFirstUnseen() > 0) {
			untaggedOK("[UNSEEN " + response.getFirstUnseen()
					+ "] First unseen");
		}
		untaggedOK("[UIDNEXT " + response.getNextUid() + "] Next UID");
		untaggedOK("[UIDVALIDITY " + response.getUidValidity() + "] UID Valid");
		if (response.isReadOnly()) {
			untaggedOK("[PERMANENTFLAGS ()] Read-only mailbox");
		} else {
			untaggedOK("[PERMANENTFLAGS (\\Answered \\Flagged \\Deleted \\Seen \\Draft \\*)] Limited");
		}
	}

*/

/*
        ImapSessionFolder mailbox = session.getSelected();
        response.flagsResponse(mailbox.getPermanentFlags());
        response.existsResponse(mailbox.getMessageCount());
        final boolean resetRecent = !isExamine;
        response.recentResponse(mailbox.getRecentCount(resetRecent));
        response.okResponse("UIDVALIDITY " + mailbox.getUidValidity(), null);
        response.okResponse("UIDNEXT " + mailbox.getUidNext(), null);

        int firstUnseen = mailbox.getFirstUnseen();
        if (firstUnseen > 0) {
            response.okResponse("UNSEEN " + firstUnseen,  "Message " + firstUnseen + " is the first unseen");
        } else {
            response.okResponse(null, "No messages unseen");
        }

        response.permanentFlagsResponse(mailbox.getPermanentFlags());

        if (mailbox.isReadonly()) {
            response.commandComplete(this, "READ-ONLY");
        } else {
            response.commandComplete(this, "READ-WRITE");
        }


/*

RECEIVED: A3 SELECT inbox
SENT: * 1 EXISTS[nl]
SENT: * 1 RECENT[nl]
SENT: * FLAGS (\Deleted \Seen \Draft \Answered \Flagged)[nl]
SENT: * OK [UIDVALIDITY 1594491108] current uidvalidity[nl]
SENT: * OK [UNSEEN 1] unseen messages[nl]
SENT: * OK [UIDNEXT 2] next uid[nl]
SENT: * OK [PERMANENTFLAGS (\Deleted \Seen \Draft \Answered \Flagged)] limited[nl]
SENT: A3 OK [READ-WRITE] SELECT completed
                
*/

       }
}

/*
6.3.1.  SELECT Command

   Arguments:  mailbox name

   Responses:  REQUIRED untagged responses: FLAGS, EXISTS, RECENT
               OPTIONAL OK untagged responses: UNSEEN, PERMANENTFLAGS

   Result:     OK - select completed, now in selected state
               NO - select failure, now in authenticated state: no
                    such mailbox, can't access mailbox
               BAD - command unknown or arguments invalid

   The SELECT command selects a mailbox so that messages in the
   mailbox can be accessed.  Before returning an OK to the client,
   the server MUST send the following untagged data to the client:

      FLAGS       Defined flags in the mailbox.  See the description
                  of the FLAGS response for more detail.

      <n> EXISTS  The number of messages in the mailbox.  See the
                  description of the EXISTS response for more detail.

      <n> RECENT  The number of messages with the \Recent flag set.
                  See the description of the RECENT response for more
                  detail.

      OK [UIDVALIDITY <n>]
                  The unique identifier validity value.  See the
                  description of the UID command for more detail.

   to define the initial state of the mailbox at the client.

   The server SHOULD also send an UNSEEN response code in an OK
   untagged response, indicating the message sequence number of the
   first unseen message in the mailbox.

   If the client can not change the permanent state of one or more of
   the flags listed in the FLAGS untagged response, the server SHOULD
   send a PERMANENTFLAGS response code in an OK untagged response,
   listing the flags that the client can change permanently.

   Only one mailbox can be selected at a time in a connection;
   simultaneous access to multiple mailboxes requires multiple
   connections.  The SELECT command automatically deselects any
   currently selected mailbox before attempting the new selection.
   Consequently, if a mailbox is selected and a SELECT command that
   fails is attempted, no mailbox is selected.




Crispin                     Standards Track                    [Page 23]

RFC 2060                       IMAP4rev1                   December 1996


   If the client is permitted to modify the mailbox, the server
   SHOULD prefix the text of the tagged OK response with the
         "[READ-WRITE]" response code.

      If the client is not permitted to modify the mailbox but is
      permitted read access, the mailbox is selected as read-only, and
      the server MUST prefix the text of the tagged OK response to
      SELECT with the "[READ-ONLY]" response code.  Read-only access
      through SELECT differs from the EXAMINE command in that certain
      read-only mailboxes MAY permit the change of permanent state on a
      per-user (as opposed to global) basis.  Netnews messages marked in
      a server-based .newsrc file are an example of such per-user
      permanent state that can be modified with read-only mailboxes.

   Example:    C: A142 SELECT INBOX
               S: * 172 EXISTS
               S: * 1 RECENT
               S: * OK [UNSEEN 12] Message 12 is first unseen
               S: * OK [UIDVALIDITY 3857529045] UIDs valid
               S: * FLAGS (\Answered \Flagged \Deleted \Seen \Draft)
               S: * OK [PERMANENTFLAGS (\Deleted \Seen \*)] Limited
               S: A142 OK [READ-WRITE] SELECT completed
*/
