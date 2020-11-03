package org.little.smtpcln.rr;

import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmtpRequests {

       private static final SmtpRequest DATA             = new SmtpRequest(SmtpCommand.DATA);
       private static final SmtpRequest NOOP             = new SmtpRequest(SmtpCommand.NOOP);
       private static final SmtpRequest RSET             = new SmtpRequest(SmtpCommand.RSET);
       private static final SmtpRequest HELP_NO_ARG      = new SmtpRequest(SmtpCommand.HELP);
       private static final SmtpRequest QUIT             = new SmtpRequest(SmtpCommand.QUIT);
       private static final AsciiString FROM_NULL_SENDER = AsciiString.cached("FROM:<>");
       
       /**
        * Creates a {@code HELO} request.
        */
       public static SmtpRequest helo(CharSequence hostname) {
           return new SmtpRequest(SmtpCommand.HELO, hostname);
       }
       
       /**
        * Creates a {@code EHLO} request.
        */
       public static SmtpRequest ehlo(CharSequence hostname) {
           return new SmtpRequest(SmtpCommand.EHLO, hostname);
       }
       
       /**
        * Creates a {@code EMPTY} request.
        */
       public static SmtpRequest empty(CharSequence... parameter) {
           return new SmtpRequest(SmtpCommand.EMPTY, parameter);
       }
       
       /**
        * Creates a {@code AUTH} request.
        */
       public static SmtpRequest auth(CharSequence... parameter) {
           return new SmtpRequest(SmtpCommand.AUTH, parameter);
       }
       
       /**
        * Creates a {@code NOOP} request.
        */
       public static SmtpRequest noop() {
           return NOOP;
       }
       
       /**
        * Creates a {@code DATA} request.
        */
       public static SmtpRequest data() {
           return DATA;
       }
       
       /**
        * Creates a {@code RSET} request.
        */
       public static SmtpRequest rset() {
           return RSET;
       }
       
       /**
        * Creates a {@code HELP} request.
        */
       public static SmtpRequest help(String cmd) {
           return cmd == null ? HELP_NO_ARG : new SmtpRequest(SmtpCommand.HELP, cmd);
       }
       
       /**
        * Creates a {@code QUIT} request.
        */
       public static SmtpRequest quit() {
           return QUIT;
       }
       
       /**
        * Creates a {@code MAIL} request.
        */
       public static SmtpRequest mail(CharSequence sender, CharSequence... mailParameters) {
           if (mailParameters == null || mailParameters.length == 0) {
               return new SmtpRequest(SmtpCommand.MAIL,sender != null ? "FROM:<" + sender + '>' : FROM_NULL_SENDER);
           } else {
               List<CharSequence> params = new ArrayList<CharSequence>(mailParameters.length + 1);
               params.add(sender != null? "FROM:<" + sender + '>' : FROM_NULL_SENDER);
               Collections.addAll(params, mailParameters);
               return new SmtpRequest(SmtpCommand.MAIL, params);
           }
       }
       
       /**
        * Creates a {@code RCPT} request.
        */
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
       
       /**
        * Creates a {@code EXPN} request.
        */
       public static SmtpRequest expn(CharSequence mailingList) {
           return new SmtpRequest(SmtpCommand.EXPN, ObjectUtil.checkNotNull(mailingList, "mailingList"));
       }
       
       /**
        * Creates a {@code VRFY} request.
        */
       public static SmtpRequest vrfy(CharSequence user) {
           return new SmtpRequest(SmtpCommand.VRFY, ObjectUtil.checkNotNull(user, "user"));
       }
       
       private SmtpRequests() { }
}

