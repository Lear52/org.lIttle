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
 * Handles processeing for the RENAME imap command.
 *
 */
public class RenameCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(RenameCommand.class);

       public static final String NAME = "RENAME";
       public static final String ARGS = "existing-mailbox-name SPACE new-mailbox-name";

       public RenameCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------

              //--------------------------------------------------------------------------------------------------------------------------------------
              ImapResponse ret=null;
              ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);  responase.add(ret);
              logger.trace("response:"+ret);

              return responase;
       }
}

/*
6.3.5.  RENAME Command

   Arguments:  existing mailbox name
               new mailbox name

   Responses:  no specific responses for this command

   Result:     OK - rename completed
               NO - rename failure: can't rename mailbox with that name,
                    can't rename to mailbox with that name
               BAD - command unknown or arguments invalid

      The RENAME command changes the name of a mailbox.  A tagged OK
      response is returned only if the mailbox has been renamed.  It is
      an error to attempt to rename from a mailbox name that does not
      exist or to a mailbox name that already exists.  Any error in
      renaming will return a tagged NO response.

      If the name has inferior hierarchical names, then the inferior
      hierarchical names MUST also be renamed.  For example, a rename of
      "foo" to "zap" will rename "foo/bar" (assuming "/" is the
      hierarchy delimiter character) to "zap/bar".

      The value of the highest-used unique identifier of the old mailbox
      name MUST be preserved so that a new mailbox created with the same
      name will not reuse the identifiers of the former incarnation,
      UNLESS the new incarnation has a different unique identifier
      validity value.  See the description of the UID command for more
      detail.

      Renaming INBOX is permitted, and has special behavior.  It moves
      all messages in INBOX to a new mailbox with the given name,
      leaving INBOX empty.  If the server implementation supports
      inferior hierarchical names of INBOX, these are unaffected by a
      rename of INBOX.

   Examples:   C: A682 LIST "" *
               S: * LIST () "/" blurdybloop
               S: * LIST (\Noselect) "/" foo
               S: * LIST () "/" foo/bar
               S: A682 OK LIST completed
               C: A683 RENAME blurdybloop sarasoop
               S: A683 OK RENAME completed
               C: A684 RENAME foo zowie
               S: A684 OK RENAME Completed
               C: A685 LIST "" *
               S: * LIST () "/" sarasoop
               S: * LIST (\Noselect) "/" zowie
               S: * LIST () "/" zowie/bar
               S: A685 OK LIST completed

               C: Z432 LIST "" *
               S: * LIST () "." INBOX
               S: * LIST () "." INBOX.bar
               S: Z432 OK LIST completed
               C: Z433 RENAME INBOX old-mail
               S: Z433 OK RENAME completed
               C: Z434 LIST "" *
               S: * LIST () "." INBOX
               S: * LIST () "." INBOX.bar
               S: * LIST () "." old-mail
               S: Z434 OK LIST completed
*/
