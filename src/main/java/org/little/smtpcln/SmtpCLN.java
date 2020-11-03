package org.little.smtpcln;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class SmtpCLN {
       private static final Logger  logger = LoggerFactory.getLogger(SmtpCLN.class);

       public static void main(String[] args) {
              SmtpClient cln=new SmtpClient();
              cln.start();
              cln.run();
              cln.stop();

       }
}
