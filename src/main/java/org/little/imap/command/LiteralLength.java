package org.little.imap.command;



public class LiteralLength {

        boolean     plus;
        int         length;
        private int r;

        public LiteralLength(int parseInt, boolean b) {
                this.length = parseInt;
                r = parseInt;
                this.plus = b;
        }
        public boolean first() {return (r - length) == 0;}

        public int     remainingLength() {return r;}

        public void    read(int toRead){r = r - toRead;}

        public String toString(){return "LiteralLength plus:"+plus+" length:"+length+" r:"+r;}

}
