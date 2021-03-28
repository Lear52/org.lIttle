package org.little.mail.imap.command;

import java.util.List;

import org.little.mail.imap.command.param.ChunkParameter;
import org.little.mail.imap.command.param.CloseListParameter;
import org.little.mail.imap.command.param.OpenListParameter;

import io.netty.buffer.ByteBuf;



public interface ImapCommandParameter {

        public boolean isPartial();

        public static void write(ByteBuf buf, List<ImapCommandParameter> parameters) {

                if(parameters == null) return;

                ImapCommandParameter last = null;

                for(ImapCommandParameter p : parameters) {
                    if (last == null || (!(last instanceof OpenListParameter) && !(p instanceof CloseListParameter) && !(p instanceof ChunkParameter))) {
                        buf.writeByte(' ');
                    }
                    last = p;
                    p.write(buf);
                }
        }

        public void write(ByteBuf buf);

        public String toString();
}
