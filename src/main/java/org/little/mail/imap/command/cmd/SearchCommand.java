package org.little.mail.imap.command.cmd;

import java.util.ArrayList;
import java.util.List;

import org.little.lmsg.lMessage;
import org.little.mail.imap.IMAPTransaction;
import org.little.mail.imap.SessionContext;
import org.little.mail.imap.command.ImapCommand;
import org.little.mail.imap.command.ImapCommandParameter;
import org.little.mail.imap.command.ImapConstants;
import org.little.mail.imap.response.EmptyResponse;
import org.little.mail.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


/**
 * Handles processing for the SEARCH imap command.
 *
 */
public class SearchCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(SearchCommand.class);

       public static final String NAME = "SEARCH";
       public static final String ARGS = "<search term>";

       public boolean isUID ;


       public SearchCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);isUID = false;}
       public SearchCommand(String _tag, String _command, List<ImapCommandParameter> _parameters,boolean _isUID ) { super(_tag,_command,_parameters); isUID=_isUID;}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));

              for(int i=0;i<getParameters().size();i++) {
                  String arg1   = getParameters().get(i).toString();
                  if(arg1.equalsIgnoreCase("ALL"         )){}
                  if(arg1.equalsIgnoreCase("ANSWERED"    )){}
                  if(arg1.equalsIgnoreCase("BCC"         )){ i++;}
                  if(arg1.equalsIgnoreCase("CC"          )){ i++;}
                  if(arg1.equalsIgnoreCase("DELETED"     )){}
                  if(arg1.equalsIgnoreCase("DRAFT"       )){}
                  if(arg1.equalsIgnoreCase("FLAGGED"     )){}
                  if(arg1.equalsIgnoreCase("FROM"        )){ i++;}
                  if(arg1.equalsIgnoreCase("HEADER"      )){i+=2;}
                  if(arg1.equalsIgnoreCase("KEYWORD"     )){ i++;}
                  if(arg1.equalsIgnoreCase("NEW"         )){}
                  if(arg1.equalsIgnoreCase("NOT"         )){}
                  if(arg1.equalsIgnoreCase("OLD"         )){}
                  if(arg1.equalsIgnoreCase("RECENT"      )){}
                  if(arg1.equalsIgnoreCase("SEEN"        )){}
                  if(arg1.equalsIgnoreCase("SUBJECT"     )){ i++;}
                  if(arg1.equalsIgnoreCase("TO"          )){ i++;}
                  if(arg1.equalsIgnoreCase("TEXT"        )){ i++;}
                  if(arg1.equalsIgnoreCase("UID"         )){ i++;}
                  if(arg1.equalsIgnoreCase("UNANSWERED"  )){}
                  if(arg1.equalsIgnoreCase("UNDELETED"   )){}
                  if(arg1.equalsIgnoreCase("UNDRAFT"     )){}
                  if(arg1.equalsIgnoreCase("UNFLAGGED"   )){}
                  if(arg1.equalsIgnoreCase("UNKEYWORD"   )){ i++;}
                  if(arg1.equalsIgnoreCase("UNSEEN"      )){}
                  if(arg1.equalsIgnoreCase("SEQUENCE_SET")){ i++;}
                  if(arg1.equalsIgnoreCase("OR"          )){}
                  if(arg1.equalsIgnoreCase("SINCE"       )){ i++;}
                  if(arg1.equalsIgnoreCase("ON"          )){ i++;}
                  if(arg1.equalsIgnoreCase("BEFORE"      )){ i++;}
                  if(arg1.equalsIgnoreCase("SENTSINCE"   )){ i++;}
                  if(arg1.equalsIgnoreCase("SENTON"      )){ i++;}
                  if(arg1.equalsIgnoreCase("SENTBEFORE"  )){ i++;}
                  if(arg1.equalsIgnoreCase("LARGER"      )){ i++;}
                  if(arg1.equalsIgnoreCase("SMALLER"     )){ i++;}            	  
            	  
              }
              
              
              
              
              IMAPTransaction txSession     = sessionContext.imapTransaction;

              String num_msg;
              //ArrayList<lMessage> list=txSession.getMsg();
              ArrayList<lMessage> list=txSession.getFolder().getMsg();
              
              
              
              if(isUID)num_msg=getUIDMsg(list,getParameters());
              else     num_msg=getNumMsg(list,getParameters());

              ImapResponse ret=null;
              
              ret=new EmptyResponse(NAME+" "+num_msg+" ");                                               responase.add(ret);

              if(isUID==false) {
            	  ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED); responase.add(ret);
            	  logger.trace("IMAP:response:"+ret); 
              }

              return responase;
       }
       private String getNumMsg(ArrayList<lMessage> list,List<ImapCommandParameter> _parameters){
               StringBuilder buf=new StringBuilder();
               
               for(int i=0;i<list.size();i++){
                   if(i>0)buf.append(" ");
                   buf.append(list.get(i).getNum());
               }

               return buf.toString();
       }
       private String getUIDMsg(ArrayList<lMessage> list,List<ImapCommandParameter> _parameters){
           StringBuilder buf=new StringBuilder();
           for(int i=0;i<list.size();i++){
               if(i>0)buf.append(" ");
               buf.append(list.get(i).getUID());
           }

           return buf.toString();
       }

}

/*
6.4.4.  SEARCH Command

   Arguments:  OPTIONAL [CHARSET] specification
               searching criteria (one or more)

   Responses:  REQUIRED untagged response: SEARCH

   Result:     OK - search completed
               NO - search error: can't search that [CHARSET] or
                    criteria
               BAD - command unknown or arguments invalid

      The SEARCH command searches the mailbox for messages that match
      the given searching criteria.  Searching criteria consist of one
      or more search keys.  The untagged SEARCH response from the server
      contains a listing of message sequence numbers corresponding to
      those messages that match the searching criteria.

      When multiple keys are specified, the result is the intersection
      (AND function) of all the messages that match those keys.  For
      example, the criteria DELETED FROM "SMITH" SINCE 1-Feb-1994 refers
      to all deleted messages from Smith that were placed in the mailbox
      since February 1, 1994.  A search key can also be a parenthesized
      list of one or more search keys (e.g. for use with the OR and NOT
      keys).

      Server implementations MAY exclude [MIME-IMB] body parts with
      terminal content media types other than TEXT and MESSAGE from
      consideration in SEARCH matching.

      The OPTIONAL [CHARSET] specification consists of the word
      "CHARSET" followed by a registered [CHARSET].  It indicates the
      [CHARSET] of the strings that appear in the search criteria.
      [MIME-IMB] content transfer encodings, and [MIME-HDRS] strings in
      [RFC-822]/[MIME-IMB] headers, MUST be decoded before comparing
      text in a [CHARSET] other than US-ASCII.  US-ASCII MUST be
      supported; other [CHARSET]s MAY be supported.  If the server does
      not support the specified [CHARSET], it MUST return a tagged NO
      response (not a BAD).

      In all search keys that use strings, a message matches the key if
      the string is a substring of the field.  The matching is case-
      insensitive.

      The defined search keys are as follows.  Refer to the Formal
      Syntax section for the precise syntactic definitions of the
      arguments.

      <message set>  Messages with message sequence numbers
                     corresponding to the specified message sequence
                     number set

      ALL            All messages in the mailbox; the default initial
                     key for ANDing.

      ANSWERED       Messages with the \Answered flag set.

      BCC <string>   Messages that contain the specified string in the
                     envelope structure's BCC field.

      BEFORE <date>  Messages whose internal date is earlier than the
                     specified date.

      BODY <string>  Messages that contain the specified string in the
                     body of the message.

      CC <string>    Messages that contain the specified string in the
                     envelope structure's CC field.

      DELETED        Messages with the \Deleted flag set.

      DRAFT          Messages with the \Draft flag set.

      FLAGGED        Messages with the \Flagged flag set.

      FROM <string>  Messages that contain the specified string in the
                     envelope structure's FROM field.

      HEADER <field-name> <string>
                     Messages that have a header with the specified
                     field-name (as defined in [RFC-822]) and that
                     contains the specified string in the [RFC-822]
                     field-body.

      KEYWORD <flag> Messages with the specified keyword set.

      LARGER <n>     Messages with an [RFC-822] size larger than the
                     specified number of octets.

      NEW            Messages that have the \Recent flag set but not the
                     \Seen flag.  This is functionally equivalent to
                     "(RECENT UNSEEN)".

      NOT <search-key>
                     Messages that do not match the specified search
                     key.

      OLD            Messages that do not have the \Recent flag set.
                     This is functionally equivalent to "NOT RECENT" (as
                     opposed to "NOT NEW").

      ON <date>      Messages whose internal date is within the
                     specified date.

      OR <search-key1> <search-key2>
                     Messages that match either search key.

      RECENT         Messages that have the \Recent flag set.

      SEEN           Messages that have the \Seen flag set.

      SENTBEFORE <date>
                     Messages whose [RFC-822] Date: header is earlier
                     than the specified date.

      SENTON <date>  Messages whose [RFC-822] Date: header is within the
                     specified date.

      SENTSINCE <date>
                     Messages whose [RFC-822] Date: header is within or
                     later than the specified date.

      SINCE <date>   Messages whose internal date is within or later
                     than the specified date.

      SMALLER <n>    Messages with an [RFC-822] size smaller than the
                     specified number of octets.

      SUBJECT <string>
                     Messages that contain the specified string in the
                     envelope structure's SUBJECT field.

      TEXT <string>  Messages that contain the specified string in the
                     header or body of the message.

      TO <string>    Messages that contain the specified string in the
                     envelope structure's TO field.

      UID <message set>
                     Messages with unique identifiers corresponding to
                     the specified unique identifier set.

      UNANSWERED     Messages that do not have the \Answered flag set.

      UNDELETED      Messages that do not have the \Deleted flag set.

      UNDRAFT        Messages that do not have the \Draft flag set.

      UNFLAGGED      Messages that do not have the \Flagged flag set.

      UNKEYWORD <flag>
                     Messages that do not have the specified keyword
                     set.

      UNSEEN         Messages that do not have the \Seen flag set.

   Example:    C: A282 SEARCH FLAGGED SINCE 1-Feb-1994 NOT FROM "Smith"
               S: * SEARCH 2 84 882
               S: A282 OK SEARCH completed



7.2.5.  SEARCH Response

   Contents:   zero or more numbers

      The SEARCH response occurs as a result of a SEARCH or UID SEARCH
      command.  The number(s) refer to those messages that match the
      search criteria.  For SEARCH, these are message sequence numbers;
      for UID SEARCH, these are unique identifiers.  Each number is
      delimited by a space.

   Example:    S: * SEARCH 2 3 6

*/
