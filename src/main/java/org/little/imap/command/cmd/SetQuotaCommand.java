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
 */
public class SetQuotaCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(SetQuotaCommand.class);

       public static final String NAME = "SETQUOTA";

       public SetQuotaCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

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
4.1. SETQUOTA Command


   Arguments:  quota root
               list of resource limits

   Data:       untagged responses: QUOTA

   Result:     OK - setquota completed
               NO - setquota error: can't set that data
               BAD - command unknown or arguments invalid

      The SETQUOTA command takes the name of a mailbox quota root and a
      list of resource limits.  The resource limits for the named quota
      root are changed to be the specified limits.  Any previous
      resource limits for the named quota root are discarded.

      If the named quota root did not previously exist, an
      implementation may optionally create it and change the quota roots
      for any number of existing mailboxes in an implementation-defined
      manner.

   Example:    C: A001 SETQUOTA "" (STORAGE 512)
               S: * QUOTA "" (STORAGE 10 512)
               S: A001 OK Setquota completed

 */
