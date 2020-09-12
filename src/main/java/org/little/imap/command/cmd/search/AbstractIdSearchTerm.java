package org.little.imap.command.cmd.search;
import java.util.List;

import javax.mail.Message;
import javax.mail.search.SearchTerm;

import org.little.imap.command.cmd.fetch.IdRange;
      
/**
 * Search term that matches all messages
 */

/**
 * Supports general searching by id sequences such as MSN or UID.
 *
 * Note:
 * Not very efficient due to underlying JavaMail based impl.
 * The term compares each mail if matching.
 *
 * @see MessageNumberSearchTerm
 * @see UidSearchTerm
 */
public class AbstractIdSearchTerm extends SearchTerm {
    private static final long serialVersionUID = -5935470270189992292L;
    private final List<IdRange> idRanges;

    public AbstractIdSearchTerm(final List<IdRange> idRanges) {
        this.idRanges = idRanges;
    }

    @Override
    public  boolean match(Message msg){return false;};

    /**
     * Matches id against sequence numbers.
     *
     * @param id the identifier
     * @return true, if matching
     */
    public boolean match(final long id) {
        for (IdRange idRange : idRanges) {
            if (idRange.includes(id)) {
                return true;
            }
        }
        return false;
    }
}

