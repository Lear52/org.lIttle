package org.little.imap.command.cmd;

import java.util.ArrayList;
import java.util.List;

import org.little.imap.IMAPTransaction;
import org.little.imap.SessionContext;
import org.little.imap.command.ImapCommand;
import org.little.imap.command.ImapCommandParameter;
import org.little.imap.command.ImapConstants;
import org.little.imap.response.EmptyResponse;
import org.little.imap.response.ImapResponse;
import org.little.store.lFolder;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.stringCase;
import org.little.util.string.stringWildCard;

import com.sun.mail.imap.protocol.BASE64MailboxDecoder; // NOSONAR
import com.sun.mail.imap.protocol.BASE64MailboxEncoder; // NOSONAR

public class LsubCommand extends ListCommand {
       private static final Logger logger = LoggerFactory.getLogger(LsubCommand.class);

       public static final String NAME = "LSUB";

       public LsubCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------
              String arg1 = null;
              String arg2 = null;
              if(getParameters().size()>1) {
            	 arg1 = getParameters().get(0).toString();
                 arg2 = getParameters().get(1).toString();
                 if(arg1.startsWith("\"") && arg1.endsWith("\"")){arg1=arg1.substring(1, arg1.length()-1);}
                 if(arg2.startsWith("\"") && arg2.endsWith("\"")){arg2=arg2.substring(1, arg2.length()-1);}
              }
              //--------------------------------------------------------------------------------------------------------------------------------------
              ImapResponse ret=null;
              if(arg2!=null) {
                 IMAPTransaction txSession     = sessionContext.imapTransaction;
                 ArrayList<lFolder >  list_folder=txSession.getStore().getListFolder();
                 if("".equals(arg1) || ".".equals(arg1))
                 for(int i=0;i<list_folder.size();i++) {
            	     String name_folder=list_folder.get(i).getName();//.toUpperCase();

            	     if(stringWildCard.wildcardMatch(name_folder,arg2, stringCase.INSENSITIVE)) {                   
            	        String _name_folder=BASE64MailboxEncoder.encode(name_folder);
            	        ret=new EmptyResponse(NAME+" (\\HasNoChildren) \".\" \""+ _name_folder+"\"");responase.add(ret);
            	     }

                 }
                 ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              }
              else {
                 ret=new EmptyResponse(NAME+" (\\NoSelect) \".\" \"\"");responase.add(ret);
                 ret=new EmptyResponse(getTag(),ImapConstants.BAD+" "+NAME+" "+ImapConstants.BADCOMMAND);   responase.add(ret);
              } 

              logger.trace("response:"+ret);

              return responase;
       }

}
