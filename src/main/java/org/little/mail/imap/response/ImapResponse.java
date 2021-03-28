package org.little.mail.imap.response;

import java.util.List;

import org.little.mail.imap.command.ImapCommandParameter;

import io.netty.buffer.ByteBuf;


public interface ImapResponse {

        public boolean tagged();

        public List<ImapCommandParameter> getParameters();

        public void write(ByteBuf buf);
}
