package org.little.imap.command.cmd.fetch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FetchRequest {
       public boolean flags;
       public boolean uid;
       public boolean internalDate;
       public boolean size;
       public boolean envelope;
       public boolean body;
       public boolean bodyStructure;

        private boolean setSeen = false;

        private Set<BodyFetchElement> bodyElements = new HashSet<>();

        public Collection<BodyFetchElement> getBodyElements() {
            return bodyElements;
        }

        public boolean isSetSeen() {
            return setSeen;
        }

        public void add(BodyFetchElement element, boolean peek) {
            if (!peek) {
                setSeen = true;
            }
            bodyElements.add(element);
        }
    }
