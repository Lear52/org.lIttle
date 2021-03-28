package org.little.mail.imap.command.param;

import org.little.mail.imap.command.ImapCommandParameter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class HumanReadableParameter implements ImapCommandParameter {

        private final String message;

        public HumanReadableParameter(String message) {        this.message = message; }

        @Override
        public boolean isPartial() { return false; }

        @Override
        public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((message == null) ? 0 : message.hashCode());
                return result;
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null) return false;
                if (getClass() != obj.getClass()) return false;
                HumanReadableParameter other = (HumanReadableParameter) obj;
                if (message == null) {
                        if (other.message != null)return false;
                } 
                else 
                if (!message.equals(other.message)) return false;
                return true;
        }

        @Override
        public String toString() {
                return "HumanReadableParameter [message=" + message + "]";
        }

        @Override
        public void write(ByteBuf buf) {
                ByteBufUtil.writeAscii(buf, message);
        }

}
