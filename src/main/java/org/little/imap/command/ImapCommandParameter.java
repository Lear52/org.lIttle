package org.little.imap.command;

import java.util.List;

import io.netty.buffer.ByteBuf;

import org.little.imap.command.param.ChunkParameter;
import org.little.imap.command.param.CloseListParameter;
import org.little.imap.command.param.OpenListParameter;



public interface ImapCommandParameter {

        public boolean isPartial();

        public static void write(ByteBuf buf, List<ImapCommandParameter> parameters) {
                if (parameters == null) return;

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
