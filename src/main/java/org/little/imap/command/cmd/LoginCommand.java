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
import org.little.store.*;
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
              logger.trace("doProcess:"+NAME+" "+ImapCommand.print(getParameters())+" size:"+getParameters().size());
              //--------------------------------------------------------------------------------------------------------
              String          userid    = getParameters().get(0).toString();
              String          password  = getParameters().get(1).toString();
              ImapResponse    ret       = null;
              lStore          store     = null;
              IMAPTransaction txSession = sessionContext.imapTransaction;
              //--------------------------------------------------------------------------------------------------------

              if(txSession!=null){
                 logger.error("error open txSession for user:"+userid);
                 ret=new EmptyResponse(getTag(),ImapConstants.BAD+" "+NAME+" "+ImapConstants.UNCOMPLETED);
                 responase.add(ret);
                 logger.trace("response:"+ret);
                 return responase;
              }
              else{
                 sessionContext.imapTransaction=new IMAPTransaction();
                 txSession = sessionContext.imapTransaction;

                 if(txSession.getUserName()!=null){
                    logger.error("txSession already open for user:"+txSession.getUserName()+" userid:"+userid);
                    ret=new EmptyResponse(getTag(),ImapConstants.BAD+" "+NAME+" "+ImapConstants.UNCOMPLETED);
                    responase.add(ret);
                    logger.trace("response:"+ret);
                    return responase;
                 }
              }

              //--------------------------------------------------------------------------------------------------------

              boolean checkUser=commonIMAP.get().getAuth().checkUser(userid, password);
              logger.trace("Login:"+userid+" p:"+password+" check:"+checkUser);

              if(checkUser){
                 store=lRoot.getStore(userid);
                 if(store==null){
                    checkUser=false;
                    logger.error("error open store for user:"+userid);
                 }
                 else{
                     txSession.setUserName(userid);
                     txSession.setStore(store);
                 }
              }

              if(checkUser)ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);                    
              else         ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);                    
              responase.add(ret);
              logger.trace("response:"+ret);
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
