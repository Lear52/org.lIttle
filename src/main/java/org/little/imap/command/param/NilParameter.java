package org.little.imap.command.param;

import org.little.imap.command.ImapCommandParameter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class NilParameter implements ImapCommandParameter {

        @Override
        public boolean isPartial() { return false; }

        @Override
        public String toString() { return "NIL"; }

        @Override
        public int hashCode() { return 33; }

        @Override
        public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null) return false;
                if (getClass() != obj.getClass()) return false;
                return true;
        }

        @Override
        public void write(ByteBuf buf) {
                ByteBufUtil.writeAscii(buf, "NIL");
        }

}
