/*
 * Copyright (c) 2014 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the Apache license 2.0
 * This file has been modified by the copyright holder.
 * Original file can be found at http://james.apache.org
 */
package org.little.imap.command.cmd.search;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import javax.mail.search.AndTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;

import org.little.imap.command.cmd.fetch.ex.ImapRequestLineReader;
import org.little.imap.command.cmd.fetch.ex.ProtocolException;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

/**
 * Handles processing for the SEARCH imap command.
 *
 * @author Darrell DeBoer <darrell@apache.org>
 */
class SearchCommandParser  {
      private final Logger log = LoggerFactory.getLogger(SearchCommandParser.class);
      private static final String CHARSET_TOKEN = "CHARSET";
      static  char CHR_SPACE = ' ';
      static  char CHR_CR = '\r';
      public static final Pattern SEQUENCE = Pattern.compile("\\d+|\\d+\\:\\d+");
          
      @SuppressWarnings("unused")
	private boolean isCHAR(final char chr) {return chr >= 0x01 && chr <= 0x7f;}
      protected boolean isListWildcard(char chr) {        return chr == '*' || chr == '%';}
      public static boolean isCrOrLf(final char chr) {    return '\r' == chr || '\n' == chr;}
      protected void consumeChar(ImapRequestLineReader request1, char expected){}
     
      /**
       * Parses the request argument into a valid search term. Not yet fully implemented - see SearchKey enum.
       * <p>
       * Other searches will return everything for now.
       */
      public SearchTerm searchTerm(ImapRequestLineReader request1)             throws ProtocolException, CharacterCodingException {
          
              
          SearchTerm resultTerm = null;
          SearchTermBuilder b = null;
          SearchKey key = null;
          
          boolean orKey = false;
          boolean negated = false;
          // Dummy implementation
          // Consume to the end of the line.
          char next = request1.nextChar();
          StringBuilder sb = new StringBuilder();
          boolean quoted = false;
          Charset charset = null;
     
          while (next != '\n') {
              if (next != '\"' && (quoted || (next != CHR_SPACE && next != CHR_CR))) {
                  sb.append(next);
              }
              request1.consume();
              next = request1.nextChar();
              if (next == '\"') {
                  quoted = !quoted;
                  if (quoted) {
                      continue;
                  }
              }
              if (!quoted && (next == CHR_SPACE || next == '\n') && sb.length() > 0) {
                  log.debug("Search request is "+ sb);
                  // Examples:
                  // HEADER Message-ID <747621499.0.1264172476711.JavaMail.tbuchert@dev16.int.consol.de> ALL
                  // FLAG SEEN ALL
                  if (null == b) {
                      try {
                          String keyValue = sb.toString();
                          // Parentheses?
                          if (keyValue.charAt(0) == '('
                                  && keyValue.charAt(keyValue.length() - 1) == ')') {
                              keyValue = keyValue.substring(1, keyValue.length() - 1);
                          }
     
                          // Message set?
                          if (SEQUENCE.matcher(keyValue).matches()) {
                              b = SearchTermBuilder.create(SearchKey.SEQUENCE_SET);
     
                              // Try to get additional number sequences.
                              // Sequence can be a whitespace separated list of either a number or number range
                              // Example: '2 5:9 9'
                              next = request1.nextChar();
                              while (next == CHR_SPACE || Character.isDigit(next) || next == ':') {
                                  request1.consume();
                                  sb.append(next);
                                  next = request1.nextChar();
                              }
                              b.addParameter(sb.toString());
                          }
                          else if (CHARSET_TOKEN.equals(keyValue)) { // Charset handling
                              request1.nextChar(); // Skip spaces
                              String c = this.astring(request1);
                              log.debug("Searching with given CHARSET "+ c);
                              charset = Charset.forName(c);
                          } else {
                              // Term?
                              key = SearchKey.valueOf(keyValue);
                              if (SearchKey.NOT == key) {
                                  negated = true;
                              } else {
                                  b = SearchTermBuilder.create(key);
                              }
     
                              if (null!=b && b.expectsParameter() && key.isCharsetAware() && null != charset && next == CHR_SPACE) {
                                  next = request1.nextChar();
                                  if (next == '{') {
                                      String textOfCharset = new String(consumeLiteralAsBytes(request1), charset);
                                      b.addParameter(textOfCharset);
                                      log.debug("Searching for text  of charset "+ textOfCharset+" "+ charset);
                                  }
                              }
                          }
                      } catch (IllegalArgumentException ex) {
                          // Ignore for now instead of breaking. See issue#35 .
                          log.warn("Ignoring not yet implemented search command '{}'"+ sb+" "+ ex);
                          negated = false;
                      }
                  } else if (b.expectsParameter()) {
                      if (b.isCharsetAware() && null != charset) {
                          request1.consume(); // \n
                          next = request1.nextChar();
                          final int capacity = Integer.parseInt(sb.substring(1, sb.length() - 1));
                          ByteBuffer bb = ByteBuffer.allocate(capacity);
                          while (next != CHR_CR) {
                              request1.consume(); // \n
                              sb.append(next);
                              next = request1.nextChar();
                          }
                          final String decoded = charset.decode(bb).toString();
                          log.info("Decoded  into "+ bb+" "+ decoded);
                          b = b.addParameter(decoded);
                      } else {
                          b = b.addParameter(sb.toString());
                      }
                  }
                  if (b != null && !b.expectsParameter()) {
                      SearchTerm searchTerm = b.build();
                      if (negated) {
                          searchTerm = new NotTerm(searchTerm);
                          negated = false;
                      }
                      b = null;
                      if (SearchKey.OR == key) {
                          resultTerm = resultTerm == null ? searchTerm : new OrTerm(resultTerm, searchTerm);
                          orKey = true;
                      } 
                      else {
                         if (orKey) {
                             if (SearchKey.ALL == key) {
                                 resultTerm = resultTerm == null ? searchTerm : new AndTerm(resultTerm, searchTerm);
                             } else {
                                 resultTerm = resultTerm == null ? searchTerm : new OrTerm(resultTerm, searchTerm);
                             }
                         } else {
                             resultTerm = resultTerm == null ? searchTerm : new AndTerm(resultTerm, searchTerm);
                         }
                      }                
                  }
                  sb.setLength(0);
                  next = request1.nextChar();
              }
          }
          return resultTerm;
      }
      private byte[] consumeLiteralAsBytes(ImapRequestLineReader request1) {
                return null;
      }
      private String astring(ImapRequestLineReader request1) {
                return null;
     }
}
