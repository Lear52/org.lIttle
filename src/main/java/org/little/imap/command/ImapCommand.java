package org.little.imap.command;

import java.util.ArrayList;
import java.util.List;

import org.little.imap.IMAPTransaction;
import org.little.imap.SessionContext;
import org.little.imap.response.EmptyResponse;
import org.little.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class ImapCommand implements ImapConstants{
        private static final Logger logger = LoggerFactory.getLogger(ImapCommand.class);

        private String                     tag;
        private String                     command;
        private List<ImapCommandParameter> parameters;

        public static String print(List<ImapCommandParameter> parameters) {
               StringBuilder ret=new StringBuilder();
               for(int i=0;i<parameters.size();i++)ret.append(parameters.get(i).toString()).append(" ");         
               return ret.toString();
        }

        public ImapCommand(String tag, String command) {
                this.tag = tag;
                this.command = command;
                this.parameters = null;
        }
        public ImapCommand(String tag, String command, List<ImapCommandParameter> parameters) {
                this.tag = tag;
                this.command = command;
                this.parameters = parameters;
        }

        public String                     getTag()        { return tag;       }
        public List<ImapCommandParameter> getParameters() { return parameters;}
        public String                     getCommand()    { return command;   }

        public String toString() {
                StringBuilder sb = new StringBuilder();

                if (tag     != null) sb.append(tag + " ");
                if (command != null) sb.append(command);
                for (ImapCommandParameter param : parameters) sb.append(" " + param);

                return sb.toString();
        }

        public boolean partial() {
                if (parameters.size() == 0) return false;
                return getLastParameter().isPartial();
        }

        public ImapCommandParameter getLastParameter() {
                if (parameters.size() == 0) return null;
                return parameters.get(parameters.size() - 1);
        }

        public void write(ByteBuf buf){
                ByteBufUtil.writeAscii(buf, tag);
                buf.writeByte(' ');
                ByteBufUtil.writeAscii(buf, command);
                if (parameters != null && !parameters.isEmpty()) {
                        ImapCommandParameter.write(buf, parameters);
                }
        }
        public boolean checkAuthState(SessionContext  sessionContext){
              if(sessionContext==null)return false;
              IMAPTransaction txSession = sessionContext.imapTransaction;
              if(txSession==null)return false;
              if(txSession.getUserName()==null)return false;
              return true;
        }

        public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
               ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
               ImapResponse ret=null;
               ret=new EmptyResponse(getTag(),ImapConstants.BAD+" ["+getCommand()+"] "+ImapConstants.UNCOMPLETED+" ???????");   responase.add(ret);
               logger.trace("response:"+ret);
               return responase;
        }  
        public void appendBuf(ByteBuf in){


       }
}
