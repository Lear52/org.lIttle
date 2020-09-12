package org.little.imap.command.cmd.search;
import javax.mail.Message;
import javax.mail.search.SearchTerm;
      
/**
 * Search term that matches all messages
 */
public class AllSearchTerm extends SearchTerm {
    private static final long serialVersionUID = 135627179677024837L;

    @Override
    public boolean match(Message msg) {
        return true;
    }
}

