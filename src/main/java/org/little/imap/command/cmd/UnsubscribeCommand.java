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
 * Handles processeing for the UNSUBSCRIBE imap command.
 *
 */
public class UnsubscribeCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(UnsubscribeCommand.class);

       public static final String NAME = "UNSUBSCRIBE";
       public static final String ARGS = "<mailbox>";

       public UnsubscribeCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------
              ImapResponse ret=null;
              String org_folder_name   ;
              String folder_name       ;
              
              if(getParameters().size()>0) {org_folder_name   = getParameters().get(0).toString();}
              else {
                  ret=new EmptyResponse(getTag(),ImapConstants.BAD+" "+NAME+" "+ImapConstants.BADCOMMAND);   
                  responase.add(ret);
                  return responase; 
              }
              if(org_folder_name.toUpperCase().equals("INBOX"))folder_name=org_folder_name.toLowerCase();
              else folder_name=org_folder_name;

              if(folder_name.startsWith("\"") && folder_name.endsWith("\"")){
                 folder_name=folder_name.substring(1, folder_name.length()-1);
              }

              //--------------------------------------------------------------------------------------------------------------------------------------
              
              ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              logger.trace("IMAP:response:"+ret);

              return responase;
       }

}
