package org.little.smtp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmtpResponse {

       private SmtpResponseStatus  replyCode;
       private List<CharSequence>  text;

       public SmtpResponse(SmtpResponseStatus statusCode, CharSequence text) {
              replyCode = statusCode;
              this.text = new ArrayList<CharSequence>();
              this.text.add(text);
       }
       public SmtpResponse(SmtpResponseStatus statusCode, List<String> _text) {
              replyCode = statusCode;

              if(_text == null) {
                 this.text = Collections.emptyList();
              } else {
                 this.text = Collections.unmodifiableList(_text);
              }
       }
       public SmtpResponse(int code, List<CharSequence> _text) {
              this.replyCode   = new SmtpResponseStatus((short)code);

              if (_text == null) {
                  this.text = Collections.emptyList();
              } else {
                  this.text = Collections.unmodifiableList(_text);
              }
       }
       public SmtpResponseStatus getReplyCode() {
              return replyCode;
       }
       public List<CharSequence> getLines() {
              return this.text;
       }
       @Override
       public int hashCode() {
              return replyCode.getStatus() * 31 + text.hashCode();
       }
       @Override
       public boolean equals(Object o) {
              if (!(o instanceof SmtpResponse)) return false;
              if (o == this) return true;
             
              SmtpResponse other = (SmtpResponse) o;
             
              return getReplyCode() == other.getReplyCode() && getLines().equals(other.getLines());
       }
       public String toString(){
           return "SmtpResponse{" + "code=" + replyCode.getStatus() + ", details=" + text + '}';
       }
}
