package org.little.imap.command.cmd;

import java.util.ArrayList;
import java.util.List;

import org.little.imap.IMAPTransaction;
import org.little.imap.SessionContext;
import org.little.imap.commonIMAP;
import org.little.imap.command.ImapCommand;
import org.little.imap.command.ImapCommandParameter;
import org.little.imap.command.ImapConstants;
import org.little.imap.response.EmptyResponse;
import org.little.imap.response.ImapResponse;
import org.little.store.lFolder;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


import com.sun.mail.imap.protocol.BASE64MailboxDecoder; // NOSONAR
import com.sun.mail.imap.protocol.BASE64MailboxEncoder; // NOSONAR


/**
 * Handles processeing for the CREATE imap command.
 *
 */
public class CreateCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(CreateCommand.class);

       public static final String NAME = "CREATE";
       public static final String ARGS = "<mailbox>";

       public CreateCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

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
              if(org_folder_name.startsWith("\"") && org_folder_name.endsWith("\"")){
            	  org_folder_name=org_folder_name.substring(1, org_folder_name.length()-1);
              }

              if(org_folder_name.toUpperCase().equals("INBOX"))folder_name=org_folder_name.toLowerCase();
              else                                             folder_name=BASE64MailboxDecoder.decode(org_folder_name);
              //else folder_name=org_folder_name;

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
              folder=txSession.getStore().getFolder(folder_name);
              if(folder==null)folder=txSession.getStore().createFolder(folder_name);
              //--------------------------------------------------------------------------------------------------------------------------------------
              if(folder!=null) {
            	  ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              }
              else {
            	  ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED); responase.add(ret);
              }
              logger.trace("IMAP:response:"+ret);

              return responase;
       }
}

/*
6.3.3.  CREATE Command

   Arguments:  mailbox name

   Responses:  no specific responses for this command

   Result:     OK - create completed
               NO - create failure: can't create mailbox with that name
               BAD - command unknown or arguments invalid

      The CREATE command creates a mailbox with the given name.  An OK
      response is returned only if a new mailbox with that name has been
      created.  It is an error to attempt to create INBOX or a mailbox
      with a name that refers to an extant mailbox.  Any error in
      creation will return a tagged NO response.

      If the mailbox name is suffixed with the server's hierarchy
      separator character (as returned from the server by a LIST
      command), this is a declaration that the client intends to create
      mailbox names under this name in the hierarchy.  Server
      implementations that do not require this declaration MUST ignore
      it.

      If the server's hierarchy separator character appears elsewhere in
      the name, the server SHOULD create any superior hierarchical names
      that are needed for the CREATE command to complete successfully.
      In other words, an attempt to create "foo/bar/zap" on a server in
      which "/" is the hierarchy separator character SHOULD create foo/
      and foo/bar/ if they do not already exist.

      If a new mailbox is created with the same name as a mailbox which
      was deleted, its unique identifiers MUST be greater than any
      unique identifiers used in the previous incarnation of the mailbox
      UNLESS the new incarnation has a different unique identifier
      validity value.  See the description of the UID command for more
      detail.

   Example:    C: A003 CREATE owatagusiam/
               S: A003 OK CREATE completed
               C: A004 CREATE owatagusiam/blurdybloop
               S: A004 OK CREATE completed

      Note: the interpretation of this example depends on whether "/"
      was returned as the hierarchy separator from LIST.  If "/" is the
      hierarchy separator, a new level of hierarchy named "owatagusiam"
      with a member called "blurdybloop" is created.  Otherwise, two
      mailboxes at the same hierarchy level are created.
*/
