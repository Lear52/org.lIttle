package org.little.imap.command.cmd.fetch.ex;

    /** See https://tools.ietf.org/html/rfc3501#page-55 : partial */

class Partial {

        int start;
        int size;

        public int computeLength(final int contentSize) {
            if ( size > 0) {
                return Math.min(size, contentSize - start); // Only up to max available bytes
            } else {
                // First len bytes
                return contentSize;
            }
        }

        public int computeStart(final int contentSize) {
            return Math.min(start, contentSize);
        }

        public boolean is(int content) {
            return (start<=content&& content<(start+size));
        }

        public static Partial as(int start, int size) {
            Partial p = new Partial();
            p.start = start;
            p.size = size;
            return p;
        }
        public String toString() {
        	return "start:"+start+" size:"+size;
     	
        	
        }
    }
