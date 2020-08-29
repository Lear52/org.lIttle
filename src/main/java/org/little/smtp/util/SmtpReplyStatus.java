package org.little.smtp.util;

public class SmtpReplyStatus {

	public static final SmtpReplyStatus R220 = new SmtpReplyStatus((short) 220);
	public static final SmtpReplyStatus R221 = new SmtpReplyStatus((short) 221);
	public static final SmtpReplyStatus R250 = new SmtpReplyStatus((short) 250); //Ok
	
	public static final SmtpReplyStatus R235 = new SmtpReplyStatus((short) 235); //Authentication Succeeded

        public static final SmtpReplyStatus R334 = new SmtpReplyStatus((short) 334);

	public static final SmtpReplyStatus R354 = new SmtpReplyStatus((short) 354);

	public static final SmtpReplyStatus R450 = new SmtpReplyStatus((short) 450);
	public static final SmtpReplyStatus R454 = new SmtpReplyStatus((short) 454);

	public static final SmtpReplyStatus R500 = new SmtpReplyStatus((short) 500); // syntax error
	public static final SmtpReplyStatus R501 = new SmtpReplyStatus((short) 501);
	public static final SmtpReplyStatus R502 = new SmtpReplyStatus((short) 502);

	public static final SmtpReplyStatus R503 = new SmtpReplyStatus((short) 503); //already authenticated
	public static final SmtpReplyStatus R535 = new SmtpReplyStatus((short) 535); //5.7.8  Authentication credentials invalid

	public static final SmtpReplyStatus R530 = new SmtpReplyStatus((short) 530); //5.7.0  Authentication required

	private short status;

	public SmtpReplyStatus(short status) {
		this.setStatus(status);
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
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
