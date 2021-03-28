package org.little.mail.smtp.element;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
       
import java.util.HashMap;
import java.util.Map;

public class SmtpCommand {

       public static final SmtpCommand EHLO  = new SmtpCommand(AsciiString.cached("EHLO"));
       public static final SmtpCommand HELO  = new SmtpCommand(AsciiString.cached("HELO"));
       public static final SmtpCommand AUTH  = new SmtpCommand(AsciiString.cached("AUTH"));
       public static final SmtpCommand MAIL  = new SmtpCommand(AsciiString.cached("MAIL"));
       public static final SmtpCommand RCPT  = new SmtpCommand(AsciiString.cached("RCPT"));
       public static final SmtpCommand DATA  = new SmtpCommand(AsciiString.cached("DATA"));
       public static final SmtpCommand NOOP  = new SmtpCommand(AsciiString.cached("NOOP"));
       public static final SmtpCommand RSET  = new SmtpCommand(AsciiString.cached("RSET"));
       public static final SmtpCommand EXPN  = new SmtpCommand(AsciiString.cached("EXPN"));
       public static final SmtpCommand VRFY  = new SmtpCommand(AsciiString.cached("VRFY"));
       public static final SmtpCommand HELP  = new SmtpCommand(AsciiString.cached("HELP"));
       public static final SmtpCommand QUIT  = new SmtpCommand(AsciiString.cached("QUIT"));
       public static final SmtpCommand STLS  = new SmtpCommand(AsciiString.cached("STARTTLS"));
       public static final SmtpCommand EMPTY = new SmtpCommand(AsciiString.cached(""));

       public static final SmtpCommand CONTENT     = new SmtpCommand(AsciiString.cached("CONTENT"));
       public static final SmtpCommand LASTCONTENT = new SmtpCommand(AsciiString.cached("LASTCONTENT"));
      
       private static final Map<String, SmtpCommand> COMMANDS = new HashMap<String, SmtpCommand>();


       static {
           COMMANDS.put(EHLO.getName().toString(),  EHLO);
           COMMANDS.put(HELO.getName().toString(),  HELO);
           COMMANDS.put(AUTH.getName().toString(),  AUTH);
           COMMANDS.put(MAIL.getName().toString(),  MAIL);
           COMMANDS.put(RCPT.getName().toString(),  RCPT);
           COMMANDS.put(DATA.getName().toString(),  DATA);
           COMMANDS.put(NOOP.getName().toString(),  NOOP);
           COMMANDS.put(RSET.getName().toString(),  RSET);
           COMMANDS.put(EXPN.getName().toString(),  EXPN);
           COMMANDS.put(VRFY.getName().toString(),  VRFY);
           COMMANDS.put(HELP.getName().toString(),  HELP);
           COMMANDS.put(QUIT.getName().toString(),  QUIT);
           COMMANDS.put(STLS.getName().toString(),  STLS);
           //COMMANDS.put(EMPTY.name().toString(), EMPTY);
       }


       private final AsciiString name;
      
       /**
        * Returns the {@link SmtpCommand} for the given command name.
        */
       public static SmtpCommand valueOf(CharSequence commandName) {
              ObjectUtil.checkNotNull(commandName, "commandName");

              SmtpCommand command = COMMANDS.get(commandName.toString());
              return command != null ? command : new SmtpCommand(AsciiString.of(commandName));
       }
      
      
       private SmtpCommand(AsciiString name) {
               this.name = name;
       }
      
       /**
        * Return the command name.
        */
       public AsciiString getName() {
             return name;
       }
      
       public void encode(ByteBuf buffer) {
              if(name.toString().equals("")==false && name.toString().equals("LASTCONTENT")==false && name.toString().equals("CONTENT")==false) {
                 ByteBufUtil.writeAscii(buffer, name);
              }
       }
      
       public boolean isContentExpected() {
              return this.equals(DATA);// || this.equals(AUTH);
       }
      
       @Override
       public int hashCode() {
              return name.hashCode();
       }
      
       @Override
       public boolean equals(Object obj) {
              if (obj == this) {
                  return true;
              }
              if (!(obj instanceof SmtpCommand)) {
                  return false;
              }
              return name.contentEqualsIgnoreCase(((SmtpCommand) obj).getName());
       }
      
       @Override
       public String toString() {
              return "SmtpCommand{name=" + name + '}';
       }
}     

