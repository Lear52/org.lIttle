package org.little.imap;

import io.netty.util.AttributeKey;

public class SessionContext {

        public static final AttributeKey<SessionContext> ATTRIBUTE_KEY = AttributeKey.valueOf("sessionContext");

        public IMAPTransaction imapTransaction;

        public void            resetMailTransaction() {this.imapTransaction = null;}

}
