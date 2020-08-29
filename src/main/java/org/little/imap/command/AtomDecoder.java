package org.little.imap.command;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;

public class AtomDecoder implements ByteProcessor {

        public  static final byte CR = 13;
        public  static final byte LF = 10;
        private static final int  MAX_ATOM_LENGTH = 128;
        public  static final byte SP = 32;

        private final AppendableCharSequence seq = new AppendableCharSequence(128);

        private int size = 0;

        public String parse(ByteBuf buffer) {
                reset();
                int pos = buffer.forEachByte((ByteProcessor) this);
                if (pos == -1) return null;
                buffer.readerIndex(pos);
                return new String(seq.toString());
                
        }

        public void reset() {
                seq.reset();
                size = 0;
        }

        @Override
        public boolean process(byte value) throws Exception {
                char nextByte = (char) value;
                if (nextByte == CR)  return false;
                else 
                if (nextByte == LF)  return false;
                else 
                if (nextByte == ']') return false;
                else 
                if (nextByte == SP)  return false;
                else {
                        if (size >= MAX_ATOM_LENGTH) {
                           throw new TooLongFrameException("ATOM is larger than " + MAX_ATOM_LENGTH + " bytes.");
                        }
                        size++;
                        seq.append(nextByte);
                        return true;
                }

        }

}
