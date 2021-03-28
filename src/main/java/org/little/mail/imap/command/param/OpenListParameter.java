package org.little.mail.imap.command.param;

import org.little.mail.imap.command.ImapCommandParameter;

import io.netty.buffer.ByteBuf;

public class OpenListParameter implements ImapCommandParameter {

        @Override
        public boolean isPartial() { return false; }

        @Override
        public String toString() { return "("; }

        @Override
        public int hashCode() {  return 31; }

        @Override
        public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null) return false;
                if (getClass() != obj.getClass()) return false;
                return true;
        }

        @Override
        public void write(ByteBuf buf) {
                buf.writeByte('(');
        }

}
