package org.little.imap.command.cmd.search;
import java.util.List;

import javax.mail.Message;

import org.little.imap.command.cmd.fetch.IdRange;
      

/**
 * Term for searching by message number ids.
 */
public class MessageNumberSearchTerm extends AbstractIdSearchTerm {
    private static final long serialVersionUID = -2792493451441320161L;

    /**
     * @param idRanges the MSNs to search for.
     */
    public MessageNumberSearchTerm(List<IdRange> idRanges) {
        super(idRanges);
    }

    @Override
    public boolean match(Message msg) {
        return match(msg.getMessageNumber());
    }
}

