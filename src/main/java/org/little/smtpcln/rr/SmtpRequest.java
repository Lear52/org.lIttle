package org.little.smtpcln.rr;

import io.netty.util.internal.ObjectUtil;

import java.util.Collections;
import java.util.List;

public class SmtpRequest implements SmtpElement {

       private final SmtpCommand        command;
       private final List<CharSequence> parameters;

       private int                      id;
      
       /**
        * Creates a new instance with the given command and no parameters.
        */
       public SmtpRequest(SmtpCommand command) {
           this.command = ObjectUtil.checkNotNull(command, "command");
           this.id=0;
           parameters = Collections.emptyList();
       }
      
       /**
        * Creates a new instance with the given command and parameters.
        */
       public SmtpRequest(SmtpCommand command, CharSequence... parameters) {
           this.command = ObjectUtil.checkNotNull(command, "command");
           this.id=0;
           this.parameters = SmtpUtils.toUnmodifiableList(parameters);
       }
      
       /**
        * Creates a new instance with the given command and parameters.
        */
       public SmtpRequest(CharSequence command, CharSequence... parameters) {
           this(SmtpCommand.valueOf(command), parameters);
       }
      
       public SmtpRequest(SmtpCommand command, List<CharSequence> parameters) {
           this.command = ObjectUtil.checkNotNull(command, "command");
           this.id=0;
           this.parameters = parameters != null ?  Collections.unmodifiableList(parameters) : Collections.<CharSequence>emptyList();
       }
      
       public int  getID() { return id;}
       public void setID(int _id) { id=_id; }

       public SmtpCommand command() {
           return command;
       }
      
       public List<CharSequence> parameters() {
           return parameters;
       }
      
       public int hashCode() {
           return command.hashCode() * 31 + parameters.hashCode();
       }
      
       public boolean equals(Object o) {
           if (!(o instanceof SmtpRequest)) {
               return false;
           }
      
           if (o == this) {
               return true;
           }
      
           SmtpRequest other = (SmtpRequest) o;
      
           return command().equals(other.command()) && parameters().equals(other.parameters());
       }
      
       @Override
       public String toString() {
           return "DefaultSmtpRequest{" +  "command=" + command + ", parameters=" + parameters + '}';
       }
}     
