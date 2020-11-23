package org.little.smtp.element;

public class SmtpResponseStatus {

        public static final SmtpResponseStatus R220 = new SmtpResponseStatus((short) 220);  // SMTP Service ready
        public static final SmtpResponseStatus R221 = new SmtpResponseStatus((short) 221);  // Service closing
        public static final SmtpResponseStatus R250 = new SmtpResponseStatus((short) 250);  // Ok   Requested action taken and completed
        
        public static final SmtpResponseStatus R235 = new SmtpResponseStatus((short) 235); //Authentication Succeeded

        public static final SmtpResponseStatus R334 = new SmtpResponseStatus((short) 334);

        public static final SmtpResponseStatus R354 = new SmtpResponseStatus((short) 354); // Start message input and end with .

        public static final SmtpResponseStatus R421 = new SmtpResponseStatus((short) 421); //  The service is not available and the connection will be closed
        public static final SmtpResponseStatus R450 = new SmtpResponseStatus((short) 450); // The requested command failed because the user’s mailbox was unavailable (for example because it was locked) try again later. 
        public static final SmtpResponseStatus R451 = new SmtpResponseStatus((short) 451); //The command has been aborted due to a server error.
        public static final SmtpResponseStatus R452 = new SmtpResponseStatus((short) 452); // The command has been aborted because the server has insufficient system storage.
        public static final SmtpResponseStatus R454 = new SmtpResponseStatus((short) 454);
        public static final SmtpResponseStatus R455 = new SmtpResponseStatus((short) 455); // The server cannot deal with the command at this time. 

        public static final SmtpResponseStatus R500 = new SmtpResponseStatus((short) 500); // syntax error
        public static final SmtpResponseStatus R501 = new SmtpResponseStatus((short) 501); //A syntax error was encountered in command arguments.
        public static final SmtpResponseStatus R502 = new SmtpResponseStatus((short) 502); //This command is not implemented. 
        public static final SmtpResponseStatus R503 = new SmtpResponseStatus((short) 503); //already authenticated The server has encountered a bad sequence of commands. 
        public static final SmtpResponseStatus R504 = new SmtpResponseStatus((short) 504); // A command parameter is not implemented. 

        public static final SmtpResponseStatus R521 = new SmtpResponseStatus((short) 521); // This host never accepts mail; a response by a dummy server. 

        public static final SmtpResponseStatus R530 = new SmtpResponseStatus((short) 530); //5.7.0  Authentication required

        public static final SmtpResponseStatus R535 = new SmtpResponseStatus((short) 535); //5.7.8  Authentication credentials invalid

        public static final SmtpResponseStatus R541 = new SmtpResponseStatus((short) 541); //The message could not be delivered for policy reasons — typically a spam filter 


        public static final SmtpResponseStatus R550 = new SmtpResponseStatus((short) 550); // The requested command failed because the user’s mailbox was unavailable
        public static final SmtpResponseStatus R551 = new SmtpResponseStatus((short) 551); // The recipient is not local to the server. 
        public static final SmtpResponseStatus R552 = new SmtpResponseStatus((short) 552); // The action was aborted due to exceeded storage allocation. 
        public static final SmtpResponseStatus R553 = new SmtpResponseStatus((short) 553); // The command was aborted because the mailbox name is invalid.

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
