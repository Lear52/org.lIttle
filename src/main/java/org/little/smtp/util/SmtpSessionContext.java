package org.little.smtp.util;

import io.netty.util.AttributeKey;

/**
 * models the current mail session, i.e. connection
 * @author thomas
 *
 */
public class SmtpSessionContext {

	public static final AttributeKey<SmtpSessionContext> ATTRIBUTE_KEY = AttributeKey.valueOf("sessionContext");

	public SmtpMailTransaction mailTransaction;

	public SmtpAuthTransaction authTransaction;

	public CharSequence    lastCmd;

	public boolean         tlsActive;

	public void            resetMailTransaction() {this.mailTransaction = null;}

}
