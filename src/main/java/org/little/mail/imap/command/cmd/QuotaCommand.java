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
 * Implements IMAP Quota Extension.
 *
 * See http://rfc-ref.org/RFC-TEXTS/2087
 *
 */
public class QuotaCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(QuotaCommand.class);

       public static final String NAME = "QUOTA";

       public QuotaCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

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
4.2. GETQUOTA Command

   Arguments:  quota root

   Data:       untagged responses: NAME

   Result:     OK - getquota completed
               NO - getquota  error:  no  such  quota  root,  permission
               denied
               BAD - command unknown or arguments invalid

The GETQUOTA command takes the name of a quota root and returns the quota root's resource usage and limits in an untagged NAME response.

   Example:    C: A003 GETQUOTA ""
               S: * NAME "" (STORAGE 10 512)
               S: A003 OK Getquota completed

5.1. NAME Response

   Data:       quota root name
               list of resource names, usages, and limits

This response occurs as a result of a GETQUOTA or GETQUOTAROOT command. The first string is the name of the quota root for which this quota applies.

The name is followed by a S-expression format list of the resource usage and limits of the quota root. The list contains zero or more triplets. Each triplet conatins a resource name, the current usage of the resource, and the resource limit.

Resources not named in the list are not limited in the quota root. Thus, an empty list means there are no administrative resource limits in the quota root.

      Example:    S: * NAME "" (STORAGE 10 512)

*/