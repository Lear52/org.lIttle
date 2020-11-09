package org.little.smtp.util;

import java.util.ArrayList;
import java.util.List;

import org.little.smtp.commonSMTP;
import org.little.smtp.store.MailStore;
import org.little.smtp.tool.Path;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;

/**
 * models an ongoing mail transaction
 */
public class SmtpMailTransaction {

        private static final Logger logger = LoggerFactory.getLogger(SmtpMailTransaction.class);

        private Path              reversePath; // MAIL command (1 time) - i.e. FROM
        private List<Path>        forwardPath = new ArrayList<>(); //RCPT commands (n times, should be limited to 100?) - i.e. TO

        private MailStore  mailDB;

        public SmtpMailTransaction() {
               logger.info("Begin new MailTransaction");
               mailDB = commonSMTP.get().getMailDB();
        }

        // from SMTP commands
        public void addTo(CharSequence argument) {
               if(argument == null) throw new IllegalArgumentException();

               forwardPath.add(Path.parse(argument));
        }

        public void setFrom(CharSequence argument) {
               if(argument         == null)throw new IllegalArgumentException();
               if(this.reversePath != null) throw new IllegalStateException();

               this.reversePath = Path.parse(argument);
        }

        // from SMTP data
        public void addDataLine(ByteBuf lineWithoutCRLF) {
               if(lineWithoutCRLF == null) throw new IllegalArgumentException();

               mailDB.addDataLine(lineWithoutCRLF);
        }

        public boolean mailFinished() {
               logger.info("End MailTransaction");
               return mailDB.finish();
        }
}