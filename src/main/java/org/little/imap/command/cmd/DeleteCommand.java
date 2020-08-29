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
 * Handles processeing for the DELETE imap command.
 *
 */
public class DeleteCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(DeleteCommand.class);

       public static final String NAME = "DELETE";
       public static final String ARGS = "<mailbox>";

       public DeleteCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

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
6.3.4.  DELETE Command

   Arguments:  mailbox name

   Responses:  no specific responses for this command

   Result:     OK - delete completed
               NO - delete failure: can't delete mailbox with that name
               BAD - command unknown or arguments invalid

      The DELETE command permanently removes the mailbox with the given
      name.  A tagged OK response is returned only if the mailbox has
      been deleted.  It is an error to attempt to delete INBOX or a
      mailbox name that does not exist.

      The DELETE command MUST NOT remove inferior hierarchical names.
      For example, if a mailbox "foo" has an inferior "foo.bar"
      (assuming "." is the hierarchy delimiter character), removing
      "foo" MUST NOT remove "foo.bar".  It is an error to attempt to
      delete a name that has inferior hierarchical names and also has
      the \Noselect mailbox name attribute (see the description of the
      LIST response for more details).

      It is permitted to delete a name that has inferior hierarchical
      names and does not have the \Noselect mailbox name attribute.  In
      this case, all messages in that mailbox are removed, and the name
      will acquire the \Noselect mailbox name attribute.

      The value of the highest-used unique identifier of the deleted
      mailbox MUST be preserved so that a new mailbox created with the
      same name will not reuse the identifiers of the former
      incarnation, UNLESS the new incarnation has a different unique
      identifier validity value.  See the description of the UID command
      for more detail.


   Examples:   C: A682 LIST "" *
               S: * LIST () "/" blurdybloop
               S: * LIST (\Noselect) "/" foo
               S: * LIST () "/" foo/bar
               S: A682 OK LIST completed
               C: A683 DELETE blurdybloop
               S: A683 OK DELETE completed
               C: A684 DELETE foo
               S: A684 NO Name "foo" has inferior hierarchical names
               C: A685 DELETE foo/bar
               S: A685 OK DELETE Completed
               C: A686 LIST "" *
               S: * LIST (\Noselect) "/" foo
               S: A686 OK LIST completed
               C: A687 DELETE foo
               S: A687 OK DELETE Completed


               C: A82 LIST "" *
               S: * LIST () "." blurdybloop
               S: * LIST () "." foo
               S: * LIST () "." foo.bar
               S: A82 OK LIST completed
               C: A83 DELETE blurdybloop
               S: A83 OK DELETE completed
               C: A84 DELETE foo
               S: A84 OK DELETE Completed
               C: A85 LIST "" *
               S: * LIST () "." foo.bar
               S: A85 OK LIST completed
               C: A86 LIST "" %
               S: * LIST (\Noselect) "." foo
               S: A86 OK LIST completed
*/
