package org.little.imap.command.param;

import org.little.imap.command.ImapCommandParameter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class QuotedStringParameter implements ImapCommandParameter {

        private final String value;

        public QuotedStringParameter(String value) {this.value = value;        }

        @Override
        public boolean isPartial() { return false; }

        @Override
        public String toString() { return "\"" + value + "\"";         }

        @Override
        public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((value == null) ? 0 : value.hashCode());
                return result;
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null) return false;
                if (getClass() != obj.getClass())  return false;
                QuotedStringParameter other = (QuotedStringParameter) obj;
                if (value == null) {
                        if (other.value != null) return false;
                } 
                else 
                if (!value.equals(other.value)) return false;
                return true;
        }

        @Override
        public void write(ByteBuf buf) {
                buf.writeByte('"');
                ByteBufUtil.writeAscii(buf, value);
                buf.writeByte('"');

        }

}
