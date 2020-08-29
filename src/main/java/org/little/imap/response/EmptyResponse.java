package org.little.imap.response;

import java.util.List;

import org.little.imap.command.ImapCommandParameter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public  class EmptyResponse implements ImapResponse {
        public  final String                     tag;
        private final String                     response;

        public EmptyResponse(String reponse) {
                this.tag = null;
                this.response = reponse;
        }
        public EmptyResponse(String tag, String response) {
                this.tag = tag;
                this.response = response;
        }

        @Override
        public boolean tagged() {
                return tag != null;
        }

        @Override
        public List<ImapCommandParameter> getParameters() {
                return null;
        }


        @Override
        public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((response == null) ? 0 : response.hashCode());
                result = prime * result + ((tag == null) ? 0 : tag.hashCode());
                return result;
        }

        @Override
        public boolean equals(Object obj) {
                if(this == obj)                      return true;
                if(obj == null)                      return false;
                if(getClass() != obj.getClass())     return false;

                EmptyResponse other = (EmptyResponse) obj;

                if(response == null) {
                   if (other.response != null)       return false;
                } 
                else 
                if(!response.equals(other.response)) return false;
                if(tag == null) {
                   if (other.tag != null)            return false;
                } 
                else 
                if (!tag.equals(other.tag))          return false;

                return true;
        }

        @Override
        public String toString() {
                if(tag != null) return "EmptyReponse [tag=" + tag + ", response=" + response +  "]";
                else            return "EmptyReponse [tag=*, response=" + response +  "]";
        }

        @Override
        public void write(ByteBuf buf) { // tag+" "+respanse

                if(tag != null) {
                   ByteBufUtil.writeAscii(buf, tag);
                } 
                else {
                   buf.writeByte('*');
                }
                buf.writeByte(' ');
                ByteBufUtil.writeAscii(buf, response);

        }

}
