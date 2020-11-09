package org.little.smtp.util;

public class SmtpResponseStatus {

        public static final SmtpResponseStatus R220 = new SmtpResponseStatus((short) 220);
        public static final SmtpResponseStatus R221 = new SmtpResponseStatus((short) 221);
        public static final SmtpResponseStatus R250 = new SmtpResponseStatus((short) 250); //Ok
        
        public static final SmtpResponseStatus R235 = new SmtpResponseStatus((short) 235); //Authentication Succeeded

        public static final SmtpResponseStatus R334 = new SmtpResponseStatus((short) 334);

        public static final SmtpResponseStatus R354 = new SmtpResponseStatus((short) 354);

        public static final SmtpResponseStatus R450 = new SmtpResponseStatus((short) 450);
        public static final SmtpResponseStatus R454 = new SmtpResponseStatus((short) 454);

        public static final SmtpResponseStatus R500 = new SmtpResponseStatus((short) 500); // syntax error
        public static final SmtpResponseStatus R501 = new SmtpResponseStatus((short) 501);
        public static final SmtpResponseStatus R502 = new SmtpResponseStatus((short) 502);

        public static final SmtpResponseStatus R503 = new SmtpResponseStatus((short) 503); //already authenticated
        public static final SmtpResponseStatus R535 = new SmtpResponseStatus((short) 535); //5.7.8  Authentication credentials invalid

        public static final SmtpResponseStatus R530 = new SmtpResponseStatus((short) 530); //5.7.0  Authentication required

        private short status;

        public SmtpResponseStatus(short status) {
                this.setStatus(status);
        }

        public short getStatus() {
                return status;
        }

        public void setStatus(short status) {
               if (status < 100 || status > 599) {
                  throw new IllegalArgumentException("code must be 100 <= code <= 599");
               }
               this.status = status;
        }

        public String toString() {
               return "ReplyStatus:" + status;
        }

}
   /*
   235 2.7.0  Authentication Succeeded
   432 4.7.12 A password transition is needed
   454 4.7.0  Temporary authentication failure
   534 5.7.9  Authentication mechanism is too weak
   535 5.7.8  Authentication credentials invalid
   530 5.7.0  Authentication required
   538 5.7.11 Encryption required for requested authentication mechanism
   550 5.1.1  Mailbox "nosuchuser" does not exist
   551-5.7.1  Forwarding to remote hosts disabled


   */
