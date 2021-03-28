package org.little.mail.smtp.element.command;

import java.util.ArrayList;

import org.little.mail.smtp.element.SmtpCommand;
import org.little.mail.smtp.element.SmtpRequest;


public class Empty extends SmtpRequest{

        //private static final Logger logger = LoggerFactory.getLogger(Empty.class);

        public Empty(){
               super(SmtpCommand.EMPTY);
        }

        public Empty(CharSequence... parameters) {
               super(SmtpCommand.EMPTY,parameters);
        }

        public Empty(ArrayList<CharSequence> parameters) {
               super(SmtpCommand.EMPTY,parameters);
        }


}
