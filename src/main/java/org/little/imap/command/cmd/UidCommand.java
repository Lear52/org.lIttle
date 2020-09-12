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
 * Handles processing for the UID imap command.
 *
 */
public class UidCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(UidCommand.class);

       public static final String NAME = "UID";
       public static final String ARGS = "<fetch-command>|<store-command>|<copy-command>|<search-command>|<expunge-command>";


       public UidCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase ;
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------
              String command_name=null;
              ImapCommand cmd=null;
              if(getParameters().size()>0) {
            	  command_name   = getParameters().get(0).toString();
                  getParameters().remove(0);
                  if(FetchCommand.NAME.equalsIgnoreCase(command_name))  cmd=new FetchCommand(getTag(), command_name, new ArrayList<>(getParameters()),true);
                  else
                  if(SearchCommand.NAME.equalsIgnoreCase(command_name)) cmd=new SearchCommand(getTag(), command_name, new ArrayList<>(getParameters()),true);
                  else
                  if(CopyCommand.NAME.equalsIgnoreCase (command_name))  cmd=new CopyCommand (getTag(), command_name, new ArrayList<>(getParameters()),true);
                  else
                  if(StoreCommand.NAME.equalsIgnoreCase (command_name)) cmd=new StoreCommand (getTag(), command_name, new ArrayList<>(getParameters()),true);
              }
              if(cmd!=null){
                 logger.trace("IMAP:translate cmd:"+cmd);
                 responase=cmd.doProcess(sessionContext);
              }
              else responase=new ArrayList<ImapResponse>();
              //--------------------------------------------------------------------------------------------------------------------------------------
              logger.trace("1 response size:"+responase.size());
              ImapResponse ret=null;
              ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   
              responase.add(ret);
              logger.trace("IMAP:response:"+ret);

              logger.trace("2 response size:"+responase.size());

              return responase;
       }

}
