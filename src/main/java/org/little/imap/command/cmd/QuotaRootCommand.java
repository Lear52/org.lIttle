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
 * Implements IMAP Quota Extension.
 * <p/>
 * See http://rfc-ref.org/RFC-TEXTS/2087
 *
 * Supports MESSAGES and STORAGE.
 */
public class QuotaRootCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(QuotaRootCommand.class);

       public static final String NAME = "GETQUOTAROOT";

       public QuotaRootCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

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
4.3. GETQUOTAROOT Command

   Arguments:  mailbox name

   Data:       untagged responses: QUOTAROOT, NAME

   Result:     OK - getquota completed
               NO - getquota error: no such mailbox, permission denied
               BAD - command unknown or arguments invalid

The GETQUOTAROOT command takes the name of a mailbox and returns the list of quota roots for the mailbox in an untagged QUOTAROOT response. For each listed quota root, it also returns the quota root's resource usage and limits in an untagged NAME response.

   Example:    C: A003 GETQUOTAROOT INBOX
               S: * QUOTAROOT INBOX ""
               S: * NAME "" (STORAGE 10 512)
               S: A003 OK Getquota completed

5.2. QUOTAROOT Response

   Data:       mailbox name
               zero or more quota root names

This response occurs as a result of a GETQUOTAROOT command. The first string is the mailbox and the remaining strings are the names of the quota roots for the mailbox.

      Example:    S: * QUOTAROOT INBOX ""
                  S: * QUOTAROOT comp.mail.mime

*/