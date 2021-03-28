package org.little.mail.imap.command.cmd.fetch.ex;
//import org.little.imap.command.cmd.fetch.*;


public class ProtocolException extends Exception {
    static final long serialVersionUID = -8903976326699432941L;
    public ProtocolException(String s) {
        super(s);
    }
    public ProtocolException(String s, Throwable cause) {
        super(s,cause);
    }
}
