package org.little.mail.smtp.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SequenceUtils {

       public static List<CharSequence> toUnmodifiableList(CharSequence... sequences) {
           if (sequences == null || sequences.length == 0) {
               return Collections.emptyList();
           }
           return Collections.unmodifiableList(Arrays.asList(sequences));
       }
      
       private SequenceUtils() { }
}
