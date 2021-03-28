package org.little.mail.imap.command.param;

import java.nio.charset.Charset;

import org.little.mail.imap.command.ImapCommandParameter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ChunkParameter implements ImapCommandParameter {

        private ByteBuf buffer;
        private boolean last;

        public ChunkParameter(ByteBuf read, boolean last) {
                this.buffer = read;
                this.last = last;
        }

        @Override
        public String toString() {
                ByteBuf buf = Unpooled.buffer();
                write(buf);
                return buf.toString(Charset.defaultCharset());
        }

        @Override
        public boolean isPartial() {
                return last;
        }

        @Override
        public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((buffer == null) ? 0 : buffer.hashCode());
                result = prime * result + (last ? 1231 : 1237);
                return result;
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null) return false;
                if (getClass() != obj.getClass()) return false;
                ChunkParameter other = (ChunkParameter) obj;
                if (buffer == null) {
                        if (other.buffer != null) return false;
                } 
                else 
                if (!buffer.equals(other.buffer))return false;

                if (last != other.last) return false;
                return true;
        }

        @Override
        public void write(ByteBuf buf) {
                buf.writeBytes(buffer.slice());
        }

}
