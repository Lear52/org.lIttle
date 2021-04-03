package org.little.mail.imap.command.cmd;

import java.util.ArrayList;
import java.util.List;

import org.little.lmsg.store.*;
import org.little.mail.imap.IMAPTransaction;
import org.little.mail.imap.SessionContext;
import org.little.mail.imap.commonIMAP;
import org.little.mail.imap.command.ImapCommand;
import org.little.mail.imap.command.ImapCommandParameter;
import org.little.mail.imap.command.ImapConstants;
import org.little.mail.imap.response.EmptyResponse;
import org.little.mail.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

/**
 * Handles processeing for the LOGIN imap command.
 *
 */
public class LoginCommand extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(LoginCommand.class);

       public static final String NAME = "LOGIN";
       public static final String ARGS = "<userid> <password>";

       public LoginCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters())+" size:"+getParameters().size());
              //--------------------------------------------------------------------------------------------------------
              String          userid    = getParameters().get(0).toString();
              String          password  = getParameters().get(1).toString();
              ImapResponse    ret       = null;
              lStore          store     = null;
              IMAPTransaction txSession = sessionContext.imapTransaction;
              //--------------------------------------------------------------------------------------------------------
              if(userid.startsWith("\"") && userid.endsWith("\"")){
                 userid=userid.substring(1, userid.length()-1);
                 password=password.substring(1, password.length()-1);
              }

              if(txSession!=null){
                 if(txSession.getUserName()!=null){
                    logger.error("IMAP transaction already open for user:"+txSession.getUserName()+" userid:"+userid+" no login");
                 }
                 else{
                    logger.error("IMAP transaction already open  userid:"+userid+" no login");
                 }
                 ret=new EmptyResponse(getTag(),ImapConstants.BAD+" "+NAME+" "+ImapConstants.UNCOMPLETED);
                 responase.add(ret);
                 logger.trace("IMAP:response:"+ret);
                 return responase;
              }
              else{
                 sessionContext.imapTransaction=new IMAPTransaction();
                 txSession = sessionContext.imapTransaction;

              }

              //--------------------------------------------------------------------------------------------------------

              boolean checkUser=commonIMAP.get().getCfgAuth().getAuthUser().checkUser(userid, password);

              logger.trace("IMAP Login:"+userid+" p:"+password+" check:"+checkUser);

              if(checkUser){
                 store=lRoot.getStore(userid);
                 if(store==null){
                    checkUser=false;
                    sessionContext.imapTransaction=null;
                    logger.error("IMAP error open store for user:"+userid);
                 }
                 else{
                     txSession.setUserName(userid);
                     txSession.setStore(store);
                 }
              }

              if(checkUser)ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);                    
              else         ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                    
              responase.add(ret);
              logger.trace("IMAP:response:"+ret);
              return responase;
       }
}

/*
6.2.2.  LOGIN Command

   Arguments:  user name
               password

   Responses:  no specific responses for this command

   Result:     OK - login completed, now in authenticated state
               NO - login failure: user name or password rejected
               BAD - command unknown or arguments invalid

      The LOGIN command identifies the client to the server and carries
      the plaintext password authenticating this user.

   Example:    C: a001 LOGIN SMITH SESAME
               S: a001 OK LOGIN completed
*/
