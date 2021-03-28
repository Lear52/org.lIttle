package org.little.mail.smtp.element;

import java.util.ArrayList;
import java.util.List;

import org.little.mail.smtp.util.Path;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

/**
 * models an ongoing mail transaction
 */
public class SmtpMailTransaction {

        private static final Logger logger = LoggerFactory.getLogger(SmtpMailTransaction.class);

        private Path              reversePath; // MAIL command (1 time) - i.e. FROM
        private List<Path>        forwardPath = new ArrayList<>(); //RCPT commands (n times, should be limited to 100?) - i.e. TO
        public  SmtpRequest       mail_content;

        public SmtpMailTransaction() {
               logger.info("Begin new MailTransaction");
               mail_content=null;
        }

        public void addTo(CharSequence argument) {
               if(argument == null) throw new IllegalArgumentException();
               forwardPath.add(Path.parse(argument));
        }

        public void setFrom(CharSequence argument) {
               if(argument         == null){
                  logger.error("MailTransaction.setFrom(null)");
                  return ;
                  //throw new IllegalArgumentException();
               }
               Path  _reversePath=Path.parse(argument);
               if(this.reversePath != null){
                  logger.error("MailTransaction.setFrom("+_reversePath+") -> "+this.reversePath);
                  //throw new IllegalStateException();
               }
               else this.reversePath = _reversePath;

               logger.trace("MailTransaction.setFrom("+reversePath+")");
        }
}
