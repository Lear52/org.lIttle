package org.little.imap.command.cmd;

//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.internet.MimeUtility;

import org.little.imap.IMAPTransaction;
import org.little.imap.SessionContext;
import org.little.imap.commonIMAP;
import org.little.imap.command.ImapCommand;
import org.little.imap.command.ImapCommandParameter;
import org.little.imap.command.ImapConstants;
import org.little.imap.command.cmd.fetch.BodyFetchElement;
import org.little.imap.command.cmd.fetch.FetchRequest;
import org.little.imap.command.cmd.fetch.ImapDate;
import org.little.imap.command.cmd.fetch.Partial;
import org.little.imap.response.EmptyResponse;
import org.little.imap.response.ImapResponse;
import org.little.store.lFolder;
import org.little.store.lMessage;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util._Base64;
import org.little.util.stringParser;



/**
 * Handles processing for the FETCH imap command.
 * <p/>
 * https://tools.ietf.org/html/rfc3501#section-6.4.5
 *
 */
public class FetchCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(FetchCommand.class);

       public static final String NAME = "FETCH";
       public static final String ARGS = "<message-set> <fetch-profile>";

       public FetchCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------
              
              ImapResponse ret=null;
              String          id    = getParameters().get(0).toString();
              int             num;
              try{ num=Integer.parseInt(id);}catch(Exception e){num=-1;}
              IMAPTransaction txSession     = sessionContext.imapTransaction;
              lMessage msg=null;
              if(txSession.getMsg()!=null)  msg=lFolder.getMsg4Id(txSession.getMsg(),num);
              if(msg==null){}


              FetchRequest    fetch=new FetchRequest();
              ArrayList<String> fetch_param=new ArrayList<String>();
              for(int i=0;i<getParameters().size();i++){
                  String param  = getParameters().get(i).toString();
                  // -- разделить imap parametr на fetch parametn
                  logger.trace("IMAP fetch cmd param old name:"+param);
                  stringParser parser=new stringParser(param,"<>[]");
                  String new_param;
                  while((new_param=parser.get())!=null){
                         fetch_param.add(new_param);
                         logger.trace("IMAP fetch cmd new param name:"+new_param);
                  }
              }
              //for(int i=0;i<getParameters().size();i++){
              for(int i=0;i<fetch_param.size();i++){
                  //String name  = getParameters().get(i).toString();
                  String name  = fetch_param.get(i);

                  logger.trace("IMAP fetch cmd param name:"+name);
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
                         i++;
                         String  parameter = fetch_param.get(i);
                         Partial partial   = null;
                         i++;
                         partial=parsePartial(fetch_param.get(i));
                         fetch.add(new BodyFetchElement("BODY[" + parameter + ']', parameter, partial), false);
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
                  }
              }

              StringBuilder buf=new StringBuilder();

              if (fetch.flags ) {
                  buf.append("FLAGS "); 
              }
             
              // INTERNALDATE response
              if (fetch.internalDate) {
                  buf.append("INTERNALDATE \""+date2prn(msg.getCreateDate())+"\""+" ");   
              }
             
              // RFC822.SIZE response
              if (fetch.size) {
                  buf.append("RFC822.SIZE "+msg.getSize()+" ");   
              }
             
              // ENVELOPE response
              if (fetch.envelope) {
                  buf.append("ENVELOPE "+printEnvelope(msg));   
              }
             
              // BODY response
              if (fetch.body) {
                  buf.append("BODY"+printBody(msg,fetch));   
              }
             
              // BODYSTRUCTURE response
              if (fetch.bodyStructure) {
                  buf.append("BODYSTRUCTURE "+printBodyStructure(msg));   
              }
             
              //--------------------------------------------------------------------------------------------------------------------------------------
              
              ret=new EmptyResponse(id+" "+NAME+" ("+buf.toString()+")");   responase.add(ret);
              //ret=new EmptyResponse(id+" FLAGS ");   responase.add(ret);
              ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);

              logger.trace("IMAP:response:"+ret);

              return responase;
       }
       private static final String SP = " ";
       private static final String NIL = "NIL";
       private static final String Q = "\"";
       private static final String LB = "(";
       private static final String RB = ")";
       //private static final String MULTIPART = "MULTIPART";
      // private static final String MESSAGE = "MESSAGE";
       private  static String date2prn(Date d){
    	       if(d==null)d=new Date();
    	       return ImapDate.toIMAPDateTime(d);
       }

       private static  Partial parsePartial(String command){
            stringParser parser=new stringParser(command,".");
            int start=0;
            int size=0;
            String _start=parser.get();
            String _size=parser.get();
            if(_size==null){
               _size=_start;
            }
            else{
               try{start=Integer.parseInt(_start,10);}catch(Exception e){start=0;}
            }
            try{size=Integer.parseInt(_size,10);}catch(Exception e){size=0;}
            return Partial.as(start, size);
        }

       private static String escapeHeader(String str){
           return MimeUtility.unfold(str).replace("\\", "\\\\").replace("\"", "\\\"");
       }
       private static String canonicalAddr(String addr){
                       if(addr.indexOf("@")==-1)addr+=("@"+commonIMAP.get().getDefaultDomain());
                       if(addr.indexOf("@")==(addr.length()-1))addr+=(commonIMAP.get().getDefaultDomain());
               return addr;
       }
       private static StringBuilder printAddr(StringBuilder buf,String addr){
                      if(addr==null){buf.append(SP+NIL + SP);}
                      else{
                           addr=canonicalAddr(addr);
                           buf.append(LB);
                           buf.append(Q+addr+Q);
                           buf.append(SP+NIL + SP);
                           String user;
                           try{user=addr.substring(0,addr.indexOf("@"));}catch(Exception e){user=" ";}
                           buf.append(Q+user+Q);
                           buf.append(SP);
                           String domain;
                           try{domain=addr.substring(addr.indexOf("@")+1);}catch(Exception e){domain=" ";}
                           buf.append(Q+domain+Q);
                           buf.append(RB);
                      }
               return buf;
       }
       private static String printBodyStructure(lMessage msg){
               StringBuilder buf=new StringBuilder();
               buf.append(LB);
               //---------------------------------------------------------------------------------------------
               buf.append(LB);
               int len_txt=1;
               if(msg.getBodyTxt()!=null)len_txt=msg.getBodyTxt().length();
               buf.append("\"TEXT\" \"PLAIN\" (\"CHARSET\" \"us-ascii\") NIL NIL \"7bit\" "+len_txt+" 2");
               buf.append(RB);

               buf.append(LB);

               if(msg.getBodyBin76()!=null)len_txt=msg.getBodyBin76().length();
               buf.append("\"APPLICATION\" \"OCTET-STREAM\" (\"NAME\" \""+msg.getFilename()+"\") NIL NIL \"base64\" "+len_txt+" NIL (\"attachment\" (\"FILENAME\" \""+msg.getFilename()+"\"))");

               buf.append(RB);

               buf.append(" \"MIXED\" (\"BOUNDARY\" \"----=_Part_0_1637070917.1594301407736\") NIL NIL");
               //---------------------------------------------------------------------------------------------
               buf.append(RB);
               return buf.toString();
       }
       private static String printBody(lMessage msg,FetchRequest fetch){
               StringBuilder buf=new StringBuilder();
               //String        primaryType;
               //String        secondaryType;
               //buf.append(LB);
/*
 * TODO *********
 * */
               int part=1;
               Collection<BodyFetchElement> arg=fetch.getBodyElements();
               Iterator<BodyFetchElement> elm=arg.iterator();   
               while(elm.hasNext()){
            	   BodyFetchElement e=elm.next();
            	   String p=e.getParameters();
            	   try {part=Integer.parseInt(p,10);}catch(Exception ex) {part=1;}
               }

               int len_txt=1;
               if(part==1){
                  if(msg.getBodyTxt()!=null)len_txt=msg.getBodyTxt().length();
                  buf.append("[1]<0> {"+len_txt+"} ");
                  buf.append(msg.getBodyTxt());
                  buf.append(SP);
               }
               if(part==2){
                  if(msg.getBodyBin76()!=null)len_txt=msg.getBodyBin76().length();
                  buf.append("[2]<0> {"+len_txt+"}");
                  buf.append("\r\n");
                  buf.append(msg.getBodyBin76());
                  
             }
             return buf.toString();
       }

       private static String printEnvelope(lMessage msg){
               StringBuilder buf=new StringBuilder();

               buf.append(LB);
               {
                 buf.append(Q + date2prn(msg.getSentDate()) + Q + SP);
                
                 //2. Subject ---------------
                 if (msg.getSubject() != null && (msg.getSubject().length() != 0)) {
                     buf.append(Q + escapeHeader(msg.getSubject()) + Q + SP);
                 } else {
                     buf.append(NIL + SP);
                 }
                 //3. From ---------------
                 String [] to=msg.getTO();
                 {
                  buf.append(LB);
                  printAddr(buf,msg.getFrom());
                  buf.append(RB);
                 }
                 //4. Sender ---------------
                 buf.append(SP);
                 {
                  buf.append(LB);
                  for(int i=0;i<to.length;i++){if(i>1)buf.append(SP); printAddr(buf,to[i]); }
                  buf.append(RB);
                 }
                 buf.append(SP);
                 {
                  buf.append(LB);
                  for(int i=0;i<to.length;i++){if(i>1)buf.append(SP); printAddr(buf,to[i]); }
                  buf.append(RB);
                 }
                 //
                 buf.append(SP);
                 buf.append(NIL);
                 buf.append(SP);
                 buf.append(NIL);
                 buf.append(SP);
                 buf.append(NIL);
                 buf.append(SP);
                 // ID MSG
                 if (msg.getId() != null && msg.getId().length() > 0) {
                     buf.append(Q + escapeHeader(msg.getId()) + Q);
                 } else {
                     buf.append(NIL);
                 }

               }
               buf.append(RB);


               return buf.toString();
       }
}

/*
6.4.5.  FETCH Command

   Arguments:  message set
               message data item names

   Responses:  untagged responses: FETCH

   Result:     OK - fetch completed
               NO - fetch error: can't fetch that data
               BAD - command unknown or arguments invalid

      The FETCH command retrieves data associated with a message in the
      mailbox.  The data items to be fetched can be either a single atom
      or a parenthesized list.

      The currently defined data items that can be fetched are:

      ALL            Macro equivalent to: (FLAGS INTERNALDATE
                     RFC822.SIZE ENVELOPE)

      BODY           Non-extensible form of BODYSTRUCTURE.

      BODY[<section>]<<partial>>
                     The text of a particular body section.  The section
                     specification is a set of zero or more part
                     specifiers delimited by periods.  A part specifier
                     is either a part number or one of the following:
                     HEADER, HEADER.FIELDS, HEADER.FIELDS.NOT, MIME, and
                     TEXT.  An empty section specification refers to the
                     entire message, including the header.

                     Every message has at least one part number.
                     Non-[MIME-IMB] messages, and non-multipart
                     [MIME-IMB] messages with no encapsulated message,
                     only have a part 1.

                     Multipart messages are assigned consecutive part
                     numbers, as they occur in the message.  If a
                     particular part is of type message or multipart,
                     its parts MUST be indicated by a period followed by
                     the part number within that nested multipart part.

                     A part of type MESSAGE/RFC822 also has nested part
                     numbers, referring to parts of the MESSAGE part's
                     body.

                     The HEADER, HEADER.FIELDS, HEADER.FIELDS.NOT, and
                     TEXT part specifiers can be the sole part specifier
                     or can be prefixed by one or more numeric part
                     specifiers, provided that the numeric part
                     specifier refers to a part of type MESSAGE/RFC822.
                     The MIME part specifier MUST be prefixed by one or
                     more numeric part specifiers.

                     The HEADER, HEADER.FIELDS, and HEADER.FIELDS.NOT
                     part specifiers refer to the [RFC-822] header of
                     the message or of an encapsulated [MIME-IMT]
                     MESSAGE/RFC822 message.  HEADER.FIELDS and
                     HEADER.FIELDS.NOT are followed by a list of
                     field-name (as defined in [RFC-822]) names, and
                     return a subset of the header.  The subset returned
                     by HEADER.FIELDS contains only those header fields
                     with a field-name that matches one of the names in
                     the list; similarly, the subset returned by
                     HEADER.FIELDS.NOT contains only the header fields
                     with a non-matching field-name.  The field-matching
                     is case-insensitive but otherwise exact.  In all
                     cases, the delimiting blank line between the header
                     and the body is always included.

                     The MIME part specifier refers to the [MIME-IMB]
                     header for this part.

                     The TEXT part specifier refers to the text body of
                     the message, omitting the [RFC-822] header.


                       Here is an example of a complex message
                       with some of its part specifiers:

                        HEADER     ([RFC-822] header of the message)
                        TEXT       MULTIPART/MIXED
                        1          TEXT/PLAIN
                        2          APPLICATION/OCTET-STREAM
                        3          MESSAGE/RFC822
                        3.HEADER   ([RFC-822] header of the message)
                        3.TEXT     ([RFC-822] text body of the message)
                        3.1        TEXT/PLAIN
                        3.2        APPLICATION/OCTET-STREAM
                        4          MULTIPART/MIXED
                        4.1        IMAGE/GIF
                        4.1.MIME   ([MIME-IMB] header for the IMAGE/GIF)
                        4.2        MESSAGE/RFC822
                        4.2.HEADER ([RFC-822] header of the message)
                        4.2.TEXT   ([RFC-822] text body of the message)
                        4.2.1      TEXT/PLAIN
                        4.2.2      MULTIPART/ALTERNATIVE
                        4.2.2.1    TEXT/PLAIN
                        4.2.2.2    TEXT/RICHTEXT


                     It is possible to fetch a substring of the
                     designated text.  This is done by appending an open
                     angle bracket ("<"), the octet position of the
                     first desired octet, a period, the maximum number
                     of octets desired, and a close angle bracket (">")
                     to the part specifier.  If the starting octet is
                     beyond the end of the text, an empty string is
                     returned.

                     Any partial fetch that attempts to read beyond the
                     end of the text is truncated as appropriate.  A
                     partial fetch that starts at octet 0 is returned as
                     a partial fetch, even if this truncation happened.

                          Note: this means that BODY[]<0.2048> of a
                          1500-octet message will return BODY[]<0>
                          with a literal of size 1500, not BODY[].

                          Note: a substring fetch of a
                          HEADER.FIELDS or HEADER.FIELDS.NOT part
                          specifier is calculated after subsetting
                          the header.


                     The \Seen flag is implicitly set; if this causes
                     the flags to change they SHOULD be included as part
                     of the FETCH responses.

      BODY.PEEK[<section>]<<partial>>
                     An alternate form of BODY[<section>] that does not
                     implicitly set the \Seen flag.

      BODYSTRUCTURE  The [MIME-IMB] body structure of the message.  This
                     is computed by the server by parsing the [MIME-IMB]
                     header fields in the [RFC-822] header and
                     [MIME-IMB] headers.

      ENVELOPE       The envelope structure of the message.  This is
                     computed by the server by parsing the [RFC-822]
                     header into the component parts, defaulting various
                     fields as necessary.

      FAST           Macro equivalent to: (FLAGS INTERNALDATE
                     RFC822.SIZE)

      FLAGS          The flags that are set for this message.

      FULL           Macro equivalent to: (FLAGS INTERNALDATE
                     RFC822.SIZE ENVELOPE BODY)

      INTERNALDATE   The internal date of the message.

      RFC822         Functionally equivalent to BODY[], differing in the
                     syntax of the resulting untagged FETCH data (RFC822
                     is returned).

      RFC822.HEADER  Functionally equivalent to BODY.PEEK[HEADER],
                     differing in the syntax of the resulting untagged
                     FETCH data (RFC822.HEADER is returned).

      RFC822.SIZE    The [RFC-822] size of the message.

      RFC822.TEXT    Functionally equivalent to BODY[TEXT], differing in
                     the syntax of the resulting untagged FETCH data
                     (RFC822.TEXT is returned).

      UID            The unique identifier for the message.

   Example:    C: A654 FETCH 2:4 (FLAGS BODY[HEADER.FIELDS (DATE FROM)])
               S: * 2 FETCH ....
               S: * 3 FETCH ....
               S: * 4 FETCH ....
               S: A654 OK FETCH completed


7.4.2.  FETCH Response

   Contents:   message data

      The FETCH response returns data about a message to the client.
      The data are pairs of data item names and their values in
      parentheses.  This response occurs as the result of a FETCH or
      STORE command, as well as by unilateral server decision (e.g. flag
      updates).

      The current data items are:

      BODY           A form of BODYSTRUCTURE without extension data.

      BODY[<section>]<<origin_octet>>
                     A string expressing the body contents of the
                     specified section.  The string SHOULD be
                     interpreted by the client according to the content
                     transfer encoding, body type, and subtype.

                     If the origin octet is specified, this string is a
                     substring of the entire body contents, starting at
                     that origin octet.  This means that BODY[]<0> MAY
                     be truncated, but BODY[] is NEVER truncated.

                     8-bit textual data is permitted if a [CHARSET]
                     identifier is part of the body parameter
                     parenthesized list for this section.  Note that
                     headers (part specifiers HEADER or MIME, or the
                     header portion of a MESSAGE/RFC822 part), MUST be
                     7-bit; 8-bit characters are not permitted in
                     headers.  Note also that the blank line at the end
                     of the header is always included in header data.

                     Non-textual data such as binary data MUST be
                     transfer encoded into a textual form such as BASE64
                     prior to being sent to the client.  To derive the
                     original binary data, the client MUST decode the
                     transfer encoded string.

      BODYSTRUCTURE  A parenthesized list that describes the [MIME-IMB]
                     body structure of a message.  This is computed by
                     the server by parsing the [MIME-IMB] header fields,
                     defaulting various fields as necessary.

                     For example, a simple text message of 48 lines and
                     2279 octets can have a body structure of: ("TEXT"
                     "PLAIN" ("CHARSET" "US-ASCII") NIL NIL "7BIT" 2279
                     48)

                     Multiple parts are indicated by parenthesis
                     nesting.  Instead of a body type as the first
                     element of the parenthesized list there is a nested
                     body.  The second element of the parenthesized list
                     is the multipart subtype (mixed, digest, parallel,
                     alternative, etc.).

                     For example, a two part message consisting of a
                     text and a BASE645-encoded text attachment can have
                     a body structure of: (("TEXT" "PLAIN" ("CHARSET"
                     "US-ASCII") NIL NIL "7BIT" 1152 23)("TEXT" "PLAIN"
                     ("CHARSET" "US-ASCII" "NAME" "cc.diff")
                     "<960723163407.20117h@cac.washington.edu>"
                     "Compiler diff" "BASE64" 4554 73) "MIXED"))

                     Extension data follows the multipart subtype.
                     Extension data is never returned with the BODY
                     fetch, but can be returned with a BODYSTRUCTURE
                     fetch.  Extension data, if present, MUST be in the
                     defined order.

                     The extension data of a multipart body part are in
                     the following order:

                     body parameter parenthesized list
                        A parenthesized list of attribute/value pairs
                        [e.g. ("foo" "bar" "baz" "rag") where "bar" is
                        the value of "foo" and "rag" is the value of
                        "baz"] as defined in [MIME-IMB].

                     body disposition
                        A parenthesized list, consisting of a
                        disposition type string followed by a
                        parenthesized list of disposition
                        attribute/value pairs.  The disposition type and
                        attribute names will be defined in a future
                        standards-track revision to [DISPOSITION].

                     body language
                        A string or parenthesized list giving the body
                        language value as defined in [LANGUAGE-TAGS].

                     Any following extension data are not yet defined in
                     this version of the protocol.  Such extension data
                     can consist of zero or more NILs, strings, numbers,
                     or potentially nested parenthesized lists of such
                     data.  Client implementations that do a
                     BODYSTRUCTURE fetch MUST be prepared to accept such
                     extension data.  Server implementations MUST NOT
                     send such extension data until it has been defined
                     by a revision of this protocol.

                     The basic fields of a non-multipart body part are
                     in the following order:

                     body type
                        A string giving the content media type name as
                        defined in [MIME-IMB].

                     body subtype
                        A string giving the content subtype name as
                        defined in [MIME-IMB].

                     body parameter parenthesized list
                        A parenthesized list of attribute/value pairs
                        [e.g. ("foo" "bar" "baz" "rag") where "bar" is
                        the value of "foo" and "rag" is the value of
                        "baz"] as defined in [MIME-IMB].

                     body id
                        A string giving the content id as defined in
                        [MIME-IMB].

                     body description
                        A string giving the content description as
                        defined in [MIME-IMB].

                     body encoding
                        A string giving the content transfer encoding as
                        defined in [MIME-IMB].

                     body size
                        A number giving the size of the body in octets.
                        Note that this size is the size in its transfer
                        encoding and not the resulting size after any
                        decoding.

                     A body type of type MESSAGE and subtype RFC822
                     contains, immediately after the basic fields, the
                     envelope structure, body structure, and size in
                     text lines of the encapsulated message.

                     A body type of type TEXT contains, immediately
                     after the basic fields, the size of the body in
                     text lines.  Note that this size is the size in its
                     content transfer encoding and not the resulting
                     size after any decoding.

                     Extension data follows the basic fields and the
                     type-specific fields listed above.  Extension data
                     is never returned with the BODY fetch, but can be
                     returned with a BODYSTRUCTURE fetch.  Extension
                     data, if present, MUST be in the defined order.

                     The extension data of a non-multipart body part are
                     in the following order:

                     body MD5
                        A string giving the body MD5 value as defined in
                        [MD5].

                     body disposition
                        A parenthesized list with the same content and
                        function as the body disposition for a multipart
                        body part.

                     body language
                        A string or parenthesized list giving the body
                        language value as defined in [LANGUAGE-TAGS].

                     Any following extension data are not yet defined in
                     this version of the protocol, and would be as
                     described above under multipart extension data.

      ENVELOPE       A parenthesized list that describes the envelope
                     structure of a message.  This is computed by the
                     server by parsing the [RFC-822] header into the
                     component parts, defaulting various fields as
                     necessary.

                     The fields of the envelope structure are in the
                     following order: date, subject, from, sender,
                     reply-to, to, cc, bcc, in-reply-to, and message-id.
                     The date, subject, in-reply-to, and message-id
                     fields are strings.  The from, sender, reply-to,
                     to, cc, and bcc fields are parenthesized lists of
                     address structures.

                     An address structure is a parenthesized list that
                     describes an electronic mail address.  The fields
                     of an address structure are in the following order:
                     personal name, [SMTP] at-domain-list (source
                     route), mailbox name, and host name.

                     [RFC-822] group syntax is indicated by a special
                     form of address structure in which the host name
                     field is NIL.  If the mailbox name field is also
                     NIL, this is an end of group marker (semi-colon in
                     RFC 822 syntax).  If the mailbox name field is
                     non-NIL, this is a start of group marker, and the
                     mailbox name field holds the group name phrase.

                     Any field of an envelope or address structure that
                     is not applicable is presented as NIL.  Note that
                     the server MUST default the reply-to and sender
                     fields from the from field; a client is not
                     expected to know to do this.

      FLAGS          A parenthesized list of flags that are set for this
                     message.

      INTERNALDATE   A string representing the internal date of the
                     message.

      RFC822         Equivalent to BODY[].

      RFC822.HEADER  Equivalent to BODY.PEEK[HEADER].

      RFC822.SIZE    A number expressing the [RFC-822] size of the
                     message.

      RFC822.TEXT    Equivalent to BODY[TEXT].

      UID            A number expressing the unique identifier of the
                     message.


   Example:    S: * 23 FETCH (FLAGS (\Seen) RFC822.SIZE 44827)

*/
