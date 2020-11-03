package org.little.smtpsrv.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.little.smtpsrv.SessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


/**
 * https://www.iana.org/assignments/mail-parameters/mail-parameters.xhtml
 * 
 * @author thomas
 *
 */
public class SmtpRegistry {

        private static SmtpRegistry inst=null;

        private Map<String, SmtpCommand> commands;

        private static final Logger logger = LoggerFactory.getLogger(SmtpRegistry.class);
        
        private void setCommand(AbstractSmtpCommand s) { 
                if(s==null)return;
                CharSequence k=s.getCommandVerb();
                String key;
                if(k==null)key="";
                else{
                    key=k.toString();
                    if(key==null)key="";
                }
                commands.put(key, s);
        }

        private SmtpRegistry() {
                commands = new HashMap<>();
                AbstractSmtpCommand s;
                s=new MailData           (); setCommand(s);
                s=new EightBitMime       (); setCommand(s);
                s=new ExpandMailingList  (); setCommand(s);
                s=new ExtendedHello      (); setCommand(s);
                s=new Help               (); setCommand(s);
                s=new Mail               (); setCommand(s);
                s=new NoOperation        (); setCommand(s);
                s=new Quit               (); setCommand(s);
                s=new Recipent           (); setCommand(s);
                s=new Reset              (); setCommand(s);
                s=new StartTLS           (); setCommand(s);
                s=new VerifyMailbox      (); setCommand(s);
                s=new Auth               (); setCommand(s);
                //s=new AuthLogin          (); setCommand(s);
                //s=new AuthPlain          (); setCommand(s);
                //s=new AuthDigest         (); setCommand(s);
        }

        static public SmtpRegistry get(){
                    if(inst==null)inst=new SmtpRegistry();
                    return inst;
        }

        public SmtpCommand getCommand(String command) { return commands.get(command);}

        public List<SmtpCommand> getCommands() { return new ArrayList<>(commands.values()); }

        public List<SmtpCommand> getHelloKeywords(SessionContext ctxMailSession) {
                List<SmtpCommand> cmds = new ArrayList<>();
                ServiceLoader<SmtpCommand> sl = ServiceLoader.load(SmtpCommand.class);
                sl.forEach(s -> {
                        logger.trace("getHelloKeyword:"+s.getCommandVerb());
                        if(s.getHelloKeyword(ctxMailSession) != null) {
                                cmds.add(s);
                        }
                });
                return cmds;
        }
}
