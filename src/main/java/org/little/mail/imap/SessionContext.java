package org.little.mail.imap;

import io.netty.util.AttributeKey;
import io.netty.channel.ChannelHandlerContext;

public class SessionContext {

        public static final AttributeKey<SessionContext> ATTRIBUTE_KEY = AttributeKey.valueOf("sessionContext");

        public IMAPTransaction                           imapTransaction;
        public ChannelHandlerContext                     ctx;

        public void                                      resetMailTransaction() {this.imapTransaction = null;}


}
