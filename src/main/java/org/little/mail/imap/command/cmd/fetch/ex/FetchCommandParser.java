package org.little.mail.imap.command.cmd.fetch.ex;
//import org.little.imap.command.cmd.fetch.*;

class FetchCommandParser {

    static final char CHR_SPACE = ' ';
    static final char CHR_CR = '\r';
//    private boolean isCHAR(char chr) {        return chr >= 0x01 && chr <= 0x7f;}
    protected boolean isListWildcard(char chr) {        return chr == '*' || chr == '%';}
//    private boolean isQuotedSpecial(char chr) { return chr == '"' || chr == '\\';    }
    public static boolean isCrOrLf(final char chr) {    return '\r' == chr || '\n' == chr;}
    protected void consumeChar(ImapRequestLineReader request, char expected) {
//        char consumed = request.consume();
//        if (consumed != expected) {
//            throw new ProtocolException("Expected:'" + expected + "' found:'" + consumed + '\'');
//        }
    }

/*

       public FetchRequest fetchRequest(ImapRequestLineReader request) throws ProtocolException {
              FetchRequest fetch = new FetchRequest();

              // Parenthesis optional if single 'atom'
              char next = nextNonSpaceChar(request);
              boolean parenthesis = '(' == next;
              if (parenthesis) {
                  consumeChar(request, '(');
                  next = nextNonSpaceChar(request);
                  while (next != ')') {
                        addNextElement(request, fetch);
                        next = nextNonSpaceChar(request);
                  }
                  consumeChar(request, ')');
              } 
              else {
                // Single item
                addNextElement(request, fetch);
            }

            return fetch;
        }
       */
/*
        public void addNextElement(ImapRequestLineReader command, FetchRequest fetch) throws ProtocolException {
               char next = nextCharInLine(command);
               StringBuilder element = new StringBuilder();
            
               while (next != ' ' && next != '[' && next != ')' && !isCrOrLf(next)) {
                   element.append(next);
                   command.consume();
                   next = command.nextChar();
               }
               
               String name = element.toString();
            
            // Simple elements with no '[]' parameters.
            if (next == ' ' || next == ')' || isCrOrLf(next)) {
                if ("FAST".equalsIgnoreCase(name)) {
                    fetch.flags = true;
                    fetch.internalDate = true;
                    fetch.size = true;
                } else if ("FULL".equalsIgnoreCase(name)) {
                    fetch.flags = true;
                    fetch.internalDate = true;
                    fetch.size = true;
                    fetch.envelope = true;
                    fetch.body = true;
                } else if ("ALL".equalsIgnoreCase(name)) {
                    fetch.flags = true;
                    fetch.internalDate = true;
                    fetch.size = true;
                    fetch.envelope = true;
                } else if ("FLAGS".equalsIgnoreCase(name)) {
                    fetch.flags = true;
                } else if ("RFC822.SIZE".equalsIgnoreCase(name)) {
                    fetch.size = true;
                } else if ("ENVELOPE".equalsIgnoreCase(name)) {
                    fetch.envelope = true;
                } else if ("INTERNALDATE".equalsIgnoreCase(name)) {
                    fetch.internalDate = true;
                } else if ("BODY".equalsIgnoreCase(name)) {
                    fetch.body = true;
                } else if ("BODYSTRUCTURE".equalsIgnoreCase(name)) {
                    fetch.bodyStructure = true;
                } else if ("UID".equalsIgnoreCase(name)) {
                    fetch.uid = true;
                } else if ("RFC822".equalsIgnoreCase(name)) {
                    fetch.add(new BodyFetchElement("RFC822", ""), false);
                } else if ("RFC822.HEADER".equalsIgnoreCase(name)) {
                    fetch.add(new BodyFetchElement("RFC822.HEADER", "HEADER"), true);
                } else if ("RFC822.TEXT".equalsIgnoreCase(name)) {
                    fetch.add(new BodyFetchElement("RFC822.TEXT", "TEXT"), false);
                } else {
                    throw new ProtocolException("Invalid fetch attribute: " + name);
                }
            } else {
                consumeChar(command, '[');

                StringBuilder sectionIdentifier = new StringBuilder();
                next = nextCharInLine(command);
                while (next != ']') {
                    sectionIdentifier.append(next);
                    command.consume();
                    next = nextCharInLine(command);
                }
                consumeChar(command, ']');

                String parameter = sectionIdentifier.toString();

                Partial partial = null;
                next = command.nextChar(); // Can be end of line if single option
                if ('<' == next) { // Partial eg <2000> or <0.1000>
                    partial = parsePartial(command);
                }

                if ("BODY".equalsIgnoreCase(name)) {
                    fetch.add(new BodyFetchElement("BODY[" + parameter + ']', parameter, partial), false);
                } else if ("BODY.PEEK".equalsIgnoreCase(name)) {
                    fetch.add(new BodyFetchElement("BODY[" + parameter + ']', parameter, partial), true);
                } else {
                    throw new ProtocolException("Invalid fetch attribute: " + name + "[]");
                }
            }
        }
*/
        public Partial parsePartial(ImapRequestLineReader command) throws ProtocolException {
            consumeChar(command, '<');
            int size = (int) consumeLong(command); // Assume <start>
            int start = 0;
            if (command.nextChar() == '.') {
                consumeChar(command, '.');
                start = size; // Assume <start.size> , so switch fields
                size = (int) consumeLong(command);
            }
            consumeChar(command, '>');
            return Partial.as(start, size);
        }

        private int consumeLong(ImapRequestLineReader command) {
			// TODO Auto-generated method stub
			return 0;
		}
		public char nextCharInLine(ImapRequestLineReader request) throws ProtocolException {
            char next = request.nextChar();
            if (isCrOrLf(next)) {
                request.dumpLine();
                throw new ProtocolException("Unexpected end of line (CR or LF).");
            }
            return next;
        }

        public char nextNonSpaceChar(ImapRequestLineReader request) throws ProtocolException {
            char next = request.nextChar();
            while (next == ' ') {
                request.consume();
                next = request.nextChar();
            }
            return next;
        }

    }
