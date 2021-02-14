package org.little.smtp.element.command;

import java.util.ArrayList;

import org.little.smtp.element.SmtpCommand;
import org.little.smtp.element.SmtpRequest;
//import org.little.util.Logger;
//import org.little.util.LoggerFactory;


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
