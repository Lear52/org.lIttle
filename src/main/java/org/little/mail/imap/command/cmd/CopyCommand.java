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
 * Handles processeing for the COPY imap command.
 *
 */
public class CopyCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(CopyCommand.class);
 
       public static final String NAME = "COPY";
       public static final String ARGS = "<message-set> <mailbox>";
       public boolean isUID ;

       public CopyCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);isUID =false;}
       public CopyCommand(String _tag, String _command, List<ImapCommandParameter> _parameters,boolean _isUID) { super(_tag,_command,_parameters);isUID =_isUID ;}

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
