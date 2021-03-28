package org.little.mail.imap.command.cmd.search;
import java.util.List;

import javax.mail.Message;

import org.little.mail.imap.command.cmd.fetch.IdRange;
      


/**
 * Term for searching uids.
 */
public class UidSearchTerm extends AbstractIdSearchTerm {
    private static final long serialVersionUID = 1135219503729412087L;

    /**
     * @param idRanges the UIDs to search for.
     */
    public UidSearchTerm(List<IdRange> idRanges) {
        super(idRanges);
    }

    @Override
    public boolean match(Message msg) {
            /*
        if (msg instanceof StoredMessage.UidAwareMimeMessage) {
            long uid = ((StoredMessage.UidAwareMimeMessage) msg).getUid();
            return match(uid);
        } else {
            final Logger log = LoggerFactory.getLogger(UidSearchTerm.class);
            log.warn("No uid support for message {}, failing to match.", msg);
            return false;
        }
         */
            return true;
    }
}
