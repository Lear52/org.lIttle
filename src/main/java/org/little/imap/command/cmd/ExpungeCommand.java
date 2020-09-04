package org.little.imap.command.cmd;

import java.util.ArrayList;
import java.util.List;

import org.little.imap.SessionContext;
import org.little.imap.command.ImapCommand;
import org.little.imap.command.ImapCommandParameter;
import org.little.imap.command.ImapConstants;
import org.little.imap.response.EmptyResponse;
import org.little.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


/**
 * Handles processing for the EXPUNGE imap command.
 * <p>
 * Supports also <a href="https://tools.ietf.org/html/rfc4315#section-2.1">UID EXPUNGE</a> of UIDPLUS extension.
 *
 */
public class ExpungeCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(ExpungeCommand.class);

       public static final String NAME = "EXPUNGE";
       public static final String ARGS="<message-set>";

       public ExpungeCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------

              //--------------------------------------------------------------------------------------------------------------------------------------
              ImapResponse ret=null;
              ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              logger.trace("IMAP:response:"+ret);

              return responase;
       }


}

/*
http://tools.ietf.org/html/rfc3501#page-49 :
6.4.3.  EXPUNGE Command

   Arguments:  none

   Responses:  untagged responses: EXPUNGE

   Result:     OK - expunge completed
               NO - expunge failure: can't expunge (e.g. permission
                    denied)
               BAD - command unknown or arguments invalid

      The EXPUNGE command permanently removes from the currently
      selected mailbox all messages that have the \Deleted flag set.
      Before returning an OK to the client, an untagged EXPUNGE response
      is sent for each message that is removed.

   Example:    C: A202 EXPUNGE
               S: * 3 EXPUNGE
               S: * 3 EXPUNGE
               S: * 5 EXPUNGE
               S: * 8 EXPUNGE
               S: A202 OK EXPUNGE completed

      Note: in this example, messages 3, 4, 7, and 11 had the
      \Deleted flag set.  See the description of the EXPUNGE
      response for further explanation.
*/
/*
https://tools.ietf.org/html/rfc4315#section-2.1 :

2.1.  UID EXPUNGE Command

   Arguments:  sequence set

   Data:       untagged responses: EXPUNGE

   Result:     OK - expunge completed
               NO - expunge failure (e.g., permission denied)
               BAD - command unknown or arguments invalid

      The UID EXPUNGE command permanently removes all messages that both
      have the \Deleted flag set and have a UID that is included in the
      specified sequence set from the currently selected mailbox.  If a
      message either does not have the \Deleted flag set or has a UID
      that is not included in the specified sequence set, it is not
      affected.

      This command is particularly useful for disconnected use clients.
      By using UID EXPUNGE instead of EXPUNGE when resynchronizing with
      the server, the client can ensure that it does not inadvertantly
      remove any messages that have been marked as \Deleted by other
      clients between the time that the client was last connected and
      the time the client resynchronizes.

      If the server does not support the UIDPLUS capability, the client
      should fall back to using the STORE command to temporarily remove
      the \Deleted flag from messages it does not want to remove, then
      issuing the EXPUNGE command.  Finally, the client should use the
      STORE command to restore the \Deleted flag on the messages in
      which it was temporarily removed.

      Alternatively, the client may fall back to using just the EXPUNGE
      command, risking the unintended removal of some messages.






Crispin                     Standards Track                     [Page 2]

RFC 4315                IMAP - UIDPLUS Extension           December 2005


   Example:    C: A003 UID EXPUNGE 3000:3002
               S: * 3 EXPUNGE
               S: * 3 EXPUNGE
               S: * 3 EXPUNGE
               S: A003 OK UID EXPUNGE completed
 */

