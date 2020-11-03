package org.little.smtpcln.rr;

import java.util.Collections;
import java.util.List;

public class SmtpResponse{

       private final int                code;
       private final List<CharSequence> details;
      
       /**
        * Creates a new instance with the given smtp code and no details.
        */
       public SmtpResponse(int code) {
              this(code, (List<CharSequence>) null);
       }
      
       /**
        * Creates a new instance with the given smtp code and details.
        */
       public SmtpResponse(int code, CharSequence... details) {
              this(code, SmtpUtils.toUnmodifiableList(details));
       }
      
       public SmtpResponse(int code, List<CharSequence> details) {
              if (code < 100 || code > 599) {
                  throw new IllegalArgumentException("code must be 100 <= code <= 599");
              }
              this.code = code;
              if (details == null) {
                  this.details = Collections.emptyList();
              } else {
                  this.details = Collections.unmodifiableList(details);
              }
       }
      
       
       public int code() {
              return code;
       }
      
       
       public List<CharSequence> details() {
              return details;
       }
      
       @Override
       public int hashCode() {
              return code * 31 + details.hashCode();
       }
      
       @Override
       public boolean equals(Object o) {
              if (!(o instanceof SmtpResponse)) {
                  return false;
              }
             
              if (o == this) {
                  return true;
              }
             
              SmtpResponse other = (SmtpResponse) o;
             
              return code() == other.code() && details().equals(other.details());
       }
      
       @Override
       public String toString() {
           return "SmtpResponse{" + "code=" + code + ", details=" + details + '}';
       }
}     

