package org.little.mail.imap.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.AppendableCharSequence;

import org.little.mail.imap.command.param.AtomParameter;
import org.little.mail.imap.command.param.ChunkParameter;
import org.little.mail.imap.command.param.CloseListParameter;
import org.little.mail.imap.command.param.LiteralParameter;
import org.little.mail.imap.command.param.NilParameter;
import org.little.mail.imap.command.param.NumberParameter;
import org.little.mail.imap.command.param.OpenListParameter;
import org.little.mail.imap.command.param.QuotedStringParameter;
import org.little.util.Logger;
import org.little.util.LoggerFactory;



public class ImapParameterDecoder {
       private static final Logger logger = LoggerFactory.getLogger(ImapParameterDecoder.class);

        private static final byte QUOTE             = '"';
        private static final byte OPEN_BRACKET      = '{';
        private static final byte CLOSE_BRACKET     = '}';
        private static final byte OPEN_PARENTHESES  = '(';
        private static final byte CLOSE_PARENTHESES = ')';
        public  static final byte CR                = 13;
        public  static final byte LF                = 10;
        public  static final byte SP                = 32;

        private static final int MAX_ATOM_LENGTH = 8192;


        public enum State {
                EMPTY, READ_QUOTED_STRING, READ_LITERAL_LENGTH, READ_PARAM_LIST, READ_ATOM, END_PARAM_LIST, NEXT, Ended, READ_LITERAL
        }

        private final AppendableCharSequence seq = new AppendableCharSequence(128);

        private int           size = 0;
        private State         currentState = State.NEXT;
        private LiteralLength literalLength;
        private boolean       statusParameter;

        public ImapParameterDecoder(boolean b) {
                statusParameter = b;
        }

        public State getState() {
                return currentState;
        }

        public ImapCommandParameter next(ChannelHandlerContext ctx, ByteBuf in) {
               //logger.trace("next currentState:"+currentState);

               if(in.readableBytes() == 0) {
                  logger.trace("in.readableBytes() == 0 currentState:"+currentState);
                  return null;
               }

               byte firstByte = in.getByte(in.readerIndex());

               if(currentState == State.EMPTY && firstByte == ' ') {
                  in.skipBytes(1);
                  return next(ctx, in);
               }
               //logger.trace("byte " + new Character((char) firstByte));

               switch (currentState) {
               case NEXT: {
                       switch (firstByte) {
                       case SP:
                               currentState = State.EMPTY;
                               in.skipBytes(1);
                               return next(ctx, in);
                       case CR:
                               if(statusParameter) {
                                  logger.error("byte " + new Character((char) firstByte));
                                  throw new CorruptedFrameException("byte " + new Character((char) firstByte));
                               }
                               in.skipBytes(1);
                               return next(ctx, in);
                       case LF:
                               if(statusParameter) {
                                  logger.error("byte " + new Character((char) firstByte));
                                  throw new CorruptedFrameException("byte " + new Character((char) firstByte));
                               }
                               in.skipBytes(1);
                               currentState = State.Ended;
                               return null;
                       case ']':
                               if(statusParameter) {
                                  in.skipBytes(1);
                                  currentState = State.Ended;
                                  return null;
                               }
                       case CLOSE_PARENTHESES:
                               currentState = State.EMPTY;
                               break;
                       default:
                               logger.error("byte " + new Character((char) firstByte));
                               throw new CorruptedFrameException("byte " + new Character((char) firstByte));

                       }
               }

               case EMPTY:
                       switch (firstByte) {
                       case QUOTE:
                               currentState = State.READ_QUOTED_STRING;
                               break;
                       case OPEN_BRACKET:
                               currentState = State.READ_LITERAL_LENGTH;
                               break;
                       case OPEN_PARENTHESES:
                               currentState = State.READ_PARAM_LIST;
                               break;
                       case CLOSE_PARENTHESES:
                               currentState = State.END_PARAM_LIST;
                               break;
                       default:
                               currentState = State.READ_ATOM;
                       }
               default:
               }

               switch (currentState) {
               case EMPTY:
                       return null;
               case READ_ATOM: {
                       ImapCommandParameter atom = decodeAtom(in);
                       if (atom != null) {
                          currentState = State.NEXT;
                       }
                       return atom;
               }
               case READ_QUOTED_STRING: {
                       ImapCommandParameter ret = decodeQuotedString(in);
                       if(ret != null) {
                          currentState = State.NEXT;
                       }
                       return ret;
               }
               case READ_PARAM_LIST: {
                       currentState = State.EMPTY;
                       in.skipBytes(1);
                       return new OpenListParameter();
               }
               case END_PARAM_LIST: {
                       currentState = State.NEXT;
                       in.skipBytes(1);
                       return new CloseListParameter();
               }
               case READ_LITERAL_LENGTH: {
                       //logger.trace("in.readableBytes():"+in.readableBytes());

                       literalLength = decodeLiteralLength(in);

                       //logger.trace("in.readableBytes():"+in.readableBytes());

                       if (literalLength != null) {
                           in.skipBytes(1);  /**/
                           //in.skipBytes(3); 
                           //currentState = State.READ_LITERAL;
                           //logger.trace("0literalLength:"+literalLength+" currentState:"+currentState);
                           logger.trace("in.readableBytes():"+in.readableBytes());
                           currentState = State.NEXT;
                           return new AtomParameter("{"+literalLength.remainingLength()+"}");

                       } else {
                           logger.trace("literalLength:null   -> return null currentState:"+currentState);
                           return null;
                       }
               }
               case READ_LITERAL: {
                       //logger.trace("READ_LITERAL");
                       //logger.trace("111  in.readableBytes():"+in.readableBytes());
                       ImapCommandParameter literalCommand = decodeLiteral(ctx, in);
                       //logger.trace("2222 in.readableBytes():"+in.readableBytes());

                       if (literalLength == null) {
                           currentState = State.NEXT;
                       }
                       //logger.trace("in.readableBytes():"+in.readableBytes());
                       //logger.trace("READ_LITERAL -> "+currentState);

                       return literalCommand;
               }
               default:
               }
               return null;
        }

        private ImapCommandParameter decodeLiteral(ChannelHandlerContext ctx, ByteBuf in) {
                logger.trace("decodeLiteral");

                int toRead = Math.min(literalLength.remainingLength(), in.readableBytes());
                if (toRead == 0) {
                    logger.trace("decodeLiteral:null");
                    return null;
                }

                logger.trace("in.readableBytes():"+in.readableBytes()+" literalLength.remainingLength()"+literalLength.remainingLength());

                ByteBuf read = ByteBufUtil.readBytes(ctx.alloc(), in, toRead);

                ImapCommandParameter ret = null;
                if (literalLength.first()) {
                    logger.trace("new LiteralParameter");
                    logger.trace("1   in.readableBytes():"+in.readableBytes());
                    ret = new LiteralParameter(read, literalLength.length, literalLength.plus);
                    logger.trace("2   in.readableBytes():"+in.readableBytes());
                    literalLength.read(toRead);
                    logger.trace("3   in.readableBytes():"+in.readableBytes());
                } else {
                    literalLength.read(toRead);
                    logger.trace("new ChunkParameter");
                    ret = new ChunkParameter(read, literalLength.remainingLength() == 0);
                }

                if (literalLength.remainingLength() == 0) {
                    logger.trace("literalLength set  null");
                    literalLength = null;
                }

                logger.trace("decodeLiteral:"+ret);
                return ret;
        }

        private LiteralLength decodeLiteralLength(ByteBuf in) {
                logger.trace("decodeLiteralLength");

                in.skipBytes(1);
                logger.trace("in.readableBytes():"+in.readableBytes());

                int pos = in.forEachByte((value) -> {

                        char nextByte = (char) value;
                        if ((nextByte >= '0' && nextByte <= '9') || nextByte == '+') {
                                if (size >= MAX_ATOM_LENGTH) {
                                    logger.error("ATOM is larger than " + MAX_ATOM_LENGTH + " bytes.");
                                    throw new TooLongFrameException("ATOM is larger than " + MAX_ATOM_LENGTH + " bytes.");
                                }
                                size++;
                                seq.append(nextByte);
                                return true;
                        } else if (nextByte == CLOSE_BRACKET) {
                                return false;
                        } else {
                                logger.error("CorruptedFrameException()");
                                throw new CorruptedFrameException();
                        }
                });


                if (pos == -1) {
                        logger.trace("decodeLiteralLength:null");
                        return null;
                } else {
                        in.readerIndex(pos);
                        String v = new String(seq.toString());
                        logger.trace("decodeLiteralLength v:"+v);
                        LiteralLength ret = null;
                        if (v.charAt(v.length() - 1) == '+') {
                            ret = new LiteralLength(Integer.parseInt(v.substring(0, v.length() - 1)), true);
                        } else {
                            ret = new LiteralLength(Integer.parseInt(v), false);
                        }
                        seq.reset();
                        size = 0;
                        logger.trace("in.readableBytes():"+in.readableBytes());
                        logger.trace("decodeLiteralLength:"+ret);
                        return ret;
                }
        }

        private ImapCommandParameter decodeAtom(ByteBuf in) {
                seq.reset();
                size = 0;
                int pos = in.forEachByte((value) -> {

                        char nextByte = (char) value;
                        if (nextByte == LF || nextByte == CR || nextByte == SP || nextByte == CLOSE_PARENTHESES || (statusParameter && nextByte == ']')) {
                                return false;
                        } else {
                                if (size >= MAX_ATOM_LENGTH) {
                                    logger.error("ATOM is larger than " + MAX_ATOM_LENGTH + " bytes.");
                                    throw new TooLongFrameException("ATOM is larger than " + MAX_ATOM_LENGTH + " bytes.");
                                }
                                size++;
                                seq.append(nextByte);
                                return true;
                        }
                });

                if (pos == -1) {
                        return null;
                } 
                else {
                        in.readerIndex(pos);
                        String               value     = new String(seq.toString());
                        ImapCommandParameter ret       = null;
                        char                 firstChar = value.charAt(0);

                        if (firstChar == 'N' && value.equals("NIL")) {
                            ret = new NilParameter();
                        } else {
                                try {
                                     ret = new NumberParameter(Integer.parseInt(value),value);
                                } catch (Exception e) {
                                     ret = new AtomParameter(value);
                                }
                        }
                        seq.reset();
                        size = 0;
                        return ret;
                }
        }

        private ImapCommandParameter decodeQuotedString(ByteBuf in) {
                logger.trace("decodeQuotedString");
                in.skipBytes(1);

                int pos = in.forEachByte((value) -> {

                        char nextByte = (char) value;
                        if (nextByte == QUOTE) {
                                return false;
                        } else if (nextByte == LF || nextByte == CR) {
                                throw new CorruptedFrameException();
                        } else {
                                if (size >= MAX_ATOM_LENGTH) {
                                    logger.error("ATOM is larger than " + MAX_ATOM_LENGTH + " bytes.");
                                    throw new TooLongFrameException("ATOM is larger than " + MAX_ATOM_LENGTH + " bytes.");
                                }
                                size++;
                                seq.append(nextByte);
                                return true;
                        }
                });

                if (pos == -1) {
                        return null;
                } 
                else {
                        in.readerIndex(pos + 1);
                        ImapCommandParameter ret = new QuotedStringParameter(new String(seq.toString()));
                        seq.reset();
                        size = 0;
                        logger.trace("decodeQuotedString:"+ret);
                        return ret;
                }
        }

        public void reset() {
                seq.reset();
                size = 0;
                currentState = State.NEXT;
        }

}
