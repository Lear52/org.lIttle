package org.little.smtp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.little.smtp.tool.SequenceUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;

public class SmtpRequest  {

       protected SmtpCommand              command;
       protected List<CharSequence>       parameters;
       private   ArrayList<SmtpResponse>  response;
      
       public SmtpRequest() {
              this.command    = null;
              this.parameters = Collections.emptyList();
              this.response   = new ArrayList<SmtpResponse>(); 
       }
       /**
        * Creates a new instance with the given command and no parameters.
        */
       public SmtpRequest(SmtpCommand command) {
              this.command    = ObjectUtil.checkNotNull(command, "command");
              this.parameters = new ArrayList<CharSequence>();//Collections.emptyList();
              this.response   = new ArrayList<SmtpResponse>(); 
       }
      
       /**
        * Creates a new instance with the given command and parameters.
        */
       public SmtpRequest(SmtpCommand command, CharSequence... parameters) {
              this.command    = ObjectUtil.checkNotNull(command, "command");
              this.parameters = SequenceUtils.toUnmodifiableList(parameters);
              this.response   = new ArrayList<SmtpResponse>(); 
       }
      
      
       public SmtpRequest(SmtpCommand command, List<CharSequence> parameters) {
              this.command    = ObjectUtil.checkNotNull(command, "command");
              this.parameters = parameters != null ?  Collections.unmodifiableList(parameters) : new ArrayList<CharSequence>();
              this.response   = new ArrayList<SmtpResponse>(); 
       }

       public  void add(CharSequence p){
               parameters.add(p);
       };

       public  void add(SmtpResponse res){response.add(res);};
      

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

       public CharSequence getCommandVerb(){
              return command.name().toString();
       };

       public void  setParam(List<CharSequence> _parameters){
              this.parameters = _parameters != null ?  Collections.unmodifiableList(_parameters) : Collections.<CharSequence>emptyList();
       };

       public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel){
              return null;
       }

       public SmtpResponse   filterCommand(){
              return null;
       }

      
       @Override
       public String toString() {
           return "SmtpRequest{" +  "command=" + command + ", parameters=" + parameters + '}';
       }
}     
