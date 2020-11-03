package org.little.smtpcln.rr;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SmtpUtils {

       public static List<CharSequence> toUnmodifiableList(CharSequence... sequences) {
           if (sequences == null || sequences.length == 0) {
               return Collections.emptyList();
           }
           return Collections.unmodifiableList(Arrays.asList(sequences));
       }
      
       private SmtpUtils() { }
}
