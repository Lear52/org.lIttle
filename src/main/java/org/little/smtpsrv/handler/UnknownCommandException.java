package org.little.smtpsrv.handler;

import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class UnknownCommandException extends Exception {
       private static Logger logger = LoggerFactory.getLogger(SmtpReplyEncoder.class);

	private static final long serialVersionUID = 1L;

	public UnknownCommandException(String line) {
	       logger.error("UnknownCommandException:"+line);
        }

}
