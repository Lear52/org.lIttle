package org.little.imap.command.param;

import org.little.imap.command.ImapCommandParameter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class NumberParameter implements ImapCommandParameter {

        private final int     value;
        private final String  s_value;

        public NumberParameter(int value,String s_value) {  this.value = value; this.s_value = s_value;}

        @Override
        public boolean isPartial() {  return false; }

        public int getValue() { return value; }

        @Override
        public int hashCode() {
                final int prime  = 31;
                int       result = 1;
                result           = prime * result + value;
                return result;
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null) return false;
                if (getClass() != obj.getClass()) return false;
                NumberParameter other = (NumberParameter) obj;
                if (value != other.value) return false;
                return true;
        }

        @Override
        public void write(ByteBuf buf) {
                ByteBufUtil.writeAscii(buf, Integer.toString(value));
        }
        @Override
        public String toString() {
                return s_value;
        }

}
