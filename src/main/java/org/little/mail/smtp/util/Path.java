package org.little.mail.smtp.util;

import java.util.ArrayList;
import java.util.List;

import org.little.mail.smtp.commonSMTP;

public class Path {

        //private CharSequence mailbox;
        private List<Domain> routes;

        public Path() {
                routes = new ArrayList<Domain>();
        }

        public String toString() {
               return routes.toString();
        }

        public static Path parse(CharSequence argument) {

               //FIXME: not sure about 256 for path, see rfc5321 - "4.5.3.1.3"
               if(argument == null || argument.length() == 0 || argument.length() > 256) {
                  return null;
               }

               // check bounds
               if(argument.charAt(0) != '<' && argument.charAt(argument.length() - 1) != '>') {
                  return null;
               }

               int mode = 0;
               if(argument.length() > 1) {
                  if(argument.charAt(1) == '@')mode = 2;
                  else                         mode = 1;
               }
               if(mode == 0) {
                  return null;
               }

               Path path = new Path();
               StringBuilder mailbox = new StringBuilder();
               StringBuilder domain  = new StringBuilder();

               // mode 2 = in-adl;
               // mode 1 = mailbox
               char lc = 0;
               for(int i = mode, n = argument.length() - 1; i < n; i++) {
                   char cc = argument.charAt(i);

                   switch(mode) {
                   case 2: // in-adl
                           if(cc == ':') { // end of a-d-l
                              mode = 1;
                              Domain d = Domain.parse(domain);
                              if(d == null) return null;
                              path.addRoute(d);
                           } 
                           else 
                           if(cc == ',') {
                              Domain d = Domain.parse(domain);
                              if(d == null) return null;
                              path.addRoute(d);
                              domain.setLength(0);
                           } 
                           else 
                           if(cc == '@') {
                              if(lc != ',')return null;
                           } 
                           else {
                              domain.append(cc);
                           }
                           break;
                   case 1: //mailbox
                           mailbox.append(cc);
                   }

                   lc = cc;
               }

               if(commonSMTP.get().isProxy()==false){
                  if(commonSMTP.get().verifyUser(argument.toString())==false){
                     return null;
                  }
               }
               //path.setMailbox(mailbox.toString());
               return path;
        }
        private void addRoute(Domain domain) {
                routes.add(domain);
        }
}
