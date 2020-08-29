package org.little.imap.command.cmd.fetch;

    /** See https://tools.ietf.org/html/rfc3501#page-55 : partial */

public class Partial {

        int start;
        int size;

        int computeLength(final int contentSize) {
            if ( size > 0) {
                return Math.min(size, contentSize - start); // Only up to max available bytes
            } else {
                // First len bytes
                return contentSize;
            }
        }

        int computeStart(final int contentSize) {
            return Math.min(start, contentSize);
        }

        public static Partial as(int start, int size) {
            Partial p = new Partial();
            p.start = start;
            p.size = size;
            return p;
        }
    }
