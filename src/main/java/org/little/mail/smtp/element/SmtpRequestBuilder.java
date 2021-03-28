package org.little.mail.smtp.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.little.mail.smtp.element.command.Auth;
import org.little.mail.smtp.element.command.Content;
import org.little.mail.smtp.element.command.Empty;
import org.little.mail.smtp.element.command.ExpandMailingList;
import org.little.mail.smtp.element.command.ExtendedHello;
import org.little.mail.smtp.element.command.Hello;
import org.little.mail.smtp.element.command.Help;
import org.little.mail.smtp.element.command.Mail;
import org.little.mail.smtp.element.command.MailData;
import org.little.mail.smtp.element.command.NoOperation;
import org.little.mail.smtp.element.command.Quit;
import org.little.mail.smtp.element.command.Recipent;
import org.little.mail.smtp.element.command.Reset;
import org.little.mail.smtp.element.command.StartTLS;
import org.little.mail.smtp.element.command.VerifyMailbox;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;

public class SmtpRequestBuilder {
        private static final Logger logger = LoggerFactory.getLogger(SmtpRequestBuilder.class);

       private   static final AsciiString FROM_NULL_SENDER = AsciiString.cached("FROM:<>");

       protected static SmtpRequestBuilder inst=null;

       
       private SmtpRequestBuilder() { }
/*
       public static SmtpRequest makeCommand(String command_name) {
                command_name=command_name.toUpperCase();
                //return commands.get(command_name);
                SmtpCommand cmd=SmtpCommand.valueOf(command_name);        

                if(cmd==SmtpCommand.EHLO ) return new ExtendedHello();
                if(cmd==SmtpCommand.HELO ) return new Hello();
                if(cmd==SmtpCommand.AUTH ) return new Auth();
                if(cmd==SmtpCommand.MAIL ) return new Mail();
                if(cmd==SmtpCommand.RCPT ) return new Recipent ();
                if(cmd==SmtpCommand.DATA ) return new MailData();
                if(cmd==SmtpCommand.NOOP ) return new NoOperation();
                if(cmd==SmtpCommand.RSET ) return new Reset();
                if(cmd==SmtpCommand.EXPN ) return new ExpandMailingList();
                if(cmd==SmtpCommand.VRFY ) return new VerifyMailbox();
                if(cmd==SmtpCommand.HELP ) return new Help();
                if(cmd==SmtpCommand.QUIT ) return new Quit();
                if(cmd==SmtpCommand.STLS ) return new StartTLS();
                if(cmd==SmtpCommand.EMPTY) return new Empty();

                return new Empty();
        }
*/        
       public static SmtpRequest make(ArrayList<CharSequence> list_arg) {
              String command_name=list_arg.get(0).toString();
              list_arg.remove(0);
              command_name=command_name.toUpperCase();
              SmtpCommand cmd=SmtpCommand.valueOf(command_name);        
              SmtpRequest req;
              if(cmd==SmtpCommand.EHLO ) req = new ExtendedHello();
              else
              if(cmd==SmtpCommand.HELO ) req = new Hello();
              else
              if(cmd==SmtpCommand.AUTH ) req = new Auth();
              else
              if(cmd==SmtpCommand.MAIL ) req = new Mail();
              else
              if(cmd==SmtpCommand.RCPT ) req = new Recipent ();
              else
              if(cmd==SmtpCommand.DATA ) req = new MailData();
              else
              if(cmd==SmtpCommand.NOOP ) req = new NoOperation();
              else
              if(cmd==SmtpCommand.RSET ) req = new Reset();
              else
              if(cmd==SmtpCommand.EXPN ) req = new ExpandMailingList();
              else
              if(cmd==SmtpCommand.VRFY ) req = new VerifyMailbox();
              else
              if(cmd==SmtpCommand.HELP ) req = new Help();
              else
              if(cmd==SmtpCommand.QUIT ) req = new Quit();
              else
              if(cmd==SmtpCommand.STLS ) req = new StartTLS();
              else
              if(cmd==SmtpCommand.EMPTY) req = new Empty();
              else  return null ;//req = new Empty();

              req.setParam(list_arg); 
              logger.trace("make request:"+req);
              return req;
        }

       public static SmtpRequest helo(CharSequence hostname)              {return new Hello              (hostname);}
       public static SmtpRequest ehlo(CharSequence hostname)              {return new ExtendedHello      (hostname);}
       public static SmtpRequest auth(CharSequence... parameter)          {return new Auth               (parameter);}
       public static SmtpRequest empty(CharSequence... parameter)         {return new Empty              (parameter);}
       public static SmtpRequest empty(ArrayList<CharSequence> parameter) {return new Empty              (parameter);}
       public static SmtpRequest content(CharSequence parameter)          {return new Content            (SmtpCommand.CONTENT, parameter);}
       public static SmtpRequest lastcontent(CharSequence parameter)      {return new Content            (SmtpCommand.LASTCONTENT, parameter+".\r\n");}
       public static SmtpRequest noop()                                   {return new NoOperation        ();}
       public static SmtpRequest data()                                   {return new MailData           ();}
       public static SmtpRequest rset()                                   {return new Reset              ();}
       public static SmtpRequest help(String cmd)                         {return cmd == null ? new Help () : new Help(cmd);}
       public static SmtpRequest quit()                                   {return new Quit               ();}
       public static SmtpRequest expn(CharSequence mailingList)           {return new ExpandMailingList  (ObjectUtil.checkNotNull(mailingList, "mailingList"));}
       public static SmtpRequest vrfy(CharSequence user)                  {return new VerifyMailbox      (ObjectUtil.checkNotNull(user, "nouser"));}
       public static SmtpRequest mail(CharSequence sender, CharSequence... mailParameters) {
           if (mailParameters == null || mailParameters.length == 0) {
               return new Mail(SmtpCommand.MAIL,sender != null ? "FROM:<" + sender + '>' : FROM_NULL_SENDER.toString());
           } else {
               List<CharSequence> params = new ArrayList<CharSequence>(mailParameters.length + 1);
               params.add(sender != null? "FROM:<" + sender + '>' : FROM_NULL_SENDER);
               Collections.addAll(params, mailParameters);
               return new Mail(params);
           }
       }
       public static SmtpRequest rcpt(CharSequence recipient, CharSequence... rcptParameters) {
           ObjectUtil.checkNotNull(recipient, "recipient");
           if (rcptParameters == null || rcptParameters.length == 0) {
               return new SmtpRequest(SmtpCommand.RCPT, "TO:<" + recipient + '>');
           } else {
               List<CharSequence> params = new ArrayList<CharSequence>(rcptParameters.length + 1);
               params.add("TO:<" + recipient + '>');
               Collections.addAll(params, rcptParameters);
               return new SmtpRequest(SmtpCommand.RCPT, params);
           }
       }
       


}

