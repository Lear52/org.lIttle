package org.little.mail.imap.command.cmd;

import java.util.ArrayList;
import java.util.List;

import org.little.mail.imap.SessionContext;
import org.little.mail.imap.command.ImapCommand;
import org.little.mail.imap.command.ImapCommandParameter;
import org.little.mail.imap.command.ImapConstants;
import org.little.mail.imap.response.EmptyResponse;
import org.little.mail.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


/**
 * Handles processeing for the NOOP imap command.
 *
 */
public class NoopCommand   extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(NoopCommand.class);

       public static final String NAME = "NOOP";

       public NoopCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------

              //--------------------------------------------------------------------------------------------------------------------------------------
              ImapResponse ret=null;
              ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              logger.trace("response:"+ret);

              return responase;
       }
}

/*
6.1.2.  NOOP Command

   Arguments:  none

   Responses:  no specific responses for this command (but see below)

   Result:     OK - noop completed
               BAD - command unknown or arguments invalid

      The NOOP command always succeeds.  It does nothing.

      Since any command can return a status update as untagged data, the
      NOOP command can be used as a periodic poll for new messages or
      message status updates during a period of inactivity.  The NOOP
      command can also be used to reset any inactivity autologout timer
      on the server.

   Example:    C: a002 NOOP
               S: a002 OK NOOP completed
                  . . .
               C: a047 NOOP
               S: * 22 EXPUNGE
               S: * 23 EXISTS
               S: * 3 RECENT
               S: * 14 FETCH (FLAGS (\Seen \Deleted))
               S: a047 OK NOOP completed
*/
