package org.little.mail.imap.command.cmd;

import java.util.ArrayList;
import java.util.List;

import org.little.lmsg.store.lFolder;
import org.little.mail.imap.IMAPTransaction;
import org.little.mail.imap.SessionContext;
import org.little.mail.imap.command.ImapCommand;
import org.little.mail.imap.command.ImapCommandParameter;
import org.little.mail.imap.command.ImapConstants;
import org.little.mail.imap.response.EmptyResponse;
import org.little.mail.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


/**
 * Handles processeing for the SUBSCRIBE imap command.
 *
 */
public class SubscribeCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(SubscribeCommand.class);

       public static final String NAME = "SUBSCRIBE";
       public static final String ARGS = "<mailbox>";

       public SubscribeCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------
              ImapResponse    ret             = null;
              lFolder         folder          = null;
              IMAPTransaction txSession       = sessionContext.imapTransaction;

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
              if(txSession==null){
                 logger.error("IMAP transaction is null");
                 ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                     
                 responase.add(ret);
                 logger.trace("IMAP:response:"+ret);
                 return responase;
              }
              if(txSession.getStore()==null){
                 logger.error("IMAP transaction set current store is null");
                 ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                     
                 responase.add(ret);
                 logger.trace("IMAP:response:"+ret);
                 return responase;
              }
              //----------------------------------------------------------------------------------------------
              
              if(folder_name.startsWith("\"") && folder_name.endsWith("\"")){
                 folder_name=folder_name.substring(1, folder_name.length()-1);
              }
              if(org_folder_name.startsWith("\"") && org_folder_name.endsWith("\"")){
            	  org_folder_name=org_folder_name.substring(1, org_folder_name.length()-1);
              }
              //----------------------------------------------------------------------------------------------
              folder=txSession.getStore().getFolder(folder_name);

              //--------------------------------------------------------------------------------------------------------------------------------------
              if(folder==null) {
            	  ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              }
              else {
            	  ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);   responase.add(ret);
              }
              logger.trace("IMAP:response:"+ret);

              return responase;
       }

}
