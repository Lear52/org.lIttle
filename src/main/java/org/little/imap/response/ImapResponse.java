package org.little.imap.response;

import java.util.List;

import org.little.imap.command.ImapCommandParameter;

import io.netty.buffer.ByteBuf;


public interface ImapResponse {

        public boolean tagged();

        public List<ImapCommandParameter> getParameters();

        public void write(ByteBuf buf);
}
