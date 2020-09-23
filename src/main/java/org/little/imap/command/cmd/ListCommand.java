package org.little.imap.command.cmd;

import java.util.ArrayList;
import java.util.List;

import org.little.imap.IMAPTransaction;
import org.little.imap.SessionContext;
import org.little.imap.command.ImapCommand;
import org.little.imap.command.ImapCommandParameter;
import org.little.imap.command.ImapConstants;
import org.little.imap.response.EmptyResponse;
import org.little.imap.response.ImapResponse;
import org.little.store.lFolder;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.stringCase;
import org.little.util.stringWildCard;

import com.sun.mail.imap.protocol.BASE64MailboxDecoder; // NOSONAR
import com.sun.mail.imap.protocol.BASE64MailboxEncoder; // NOSONAR


/**
 * Handles processeing for the LIST imap command.
 *
 */
public class ListCommand  extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(ListCommand.class);

       public static final String DELIMITER = "/";
       public static final String NAME = "LIST";
       public static final String ARGS = "<reference-name> <mailbox-name-with-wildcards>";


       public ListCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}

       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              //--------------------------------------------------------------------------------------------------------------------------------------
              String arg1 = null;
              String arg2 = null;
              if(getParameters().size()>1) {
            	 arg1 = getParameters().get(0).toString();
                 arg2 = getParameters().get(1).toString();
                 if(arg1.startsWith("\"") && arg1.endsWith("\"")){arg1=arg1.substring(1, arg1.length()-1);}
                 if(arg2.startsWith("\"") && arg2.endsWith("\"")){arg2=arg2.substring(1, arg2.length()-1);}
              }
              logger.trace("find folder for region:"+arg1+" mask:"+arg2);
              //--------------------------------------------------------------------------------------------------------------------------------------
              ImapResponse ret=null;

              if(arg1.length()==0 && arg2.length()==0) {
             	  ret=new EmptyResponse(NAME+" (\\Noselect) \""+DELIMITER+"\" \"\"");responase.add(ret);
                  ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              }
              else
              if(arg2!=null) {
                 IMAPTransaction txSession     = sessionContext.imapTransaction;
                 ArrayList<lFolder >  list_folder=txSession.getStore().getListFolder();
                 if("".equals(arg1) || DELIMITER.equals(arg1)){
                    for(int i=0;i<list_folder.size();i++) {
                        String name_folder=list_folder.get(i).getName();
                        String __name_folder=name_folder.toUpperCase();
                        String __mask=BASE64MailboxDecoder.decode(arg2);

                        logger.trace("name_folder:"+__name_folder+" mask:"+__mask+ " region:"+arg1);

            	        if(stringWildCard.wildcardMatch(__name_folder,__mask, stringCase.INSENSITIVE)) {                   
            	           String _name_folder=BASE64MailboxEncoder.encode(name_folder);
                           //String _name_folder=BASE64MailboxDecoder.decode(name_folder);
                           logger.trace("name_folder:"+name_folder+" -> new name_folder:"+_name_folder);
                 	   ret=new EmptyResponse(NAME+" (\\HasNoChildren) \""+DELIMITER+"\" \""+ _name_folder+"\"");responase.add(ret);
                        }
                    }
                 }
                 ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              }
              else{
                  ret=new EmptyResponse(NAME+" (\\NoSelect) \".\" \"\"");responase.add(ret);
                  ret=new EmptyResponse(getTag(),ImapConstants.BAD+" "+NAME+" "+ImapConstants.BADCOMMAND);   responase.add(ret);
              } 

              logger.trace("response:"+ret);

              return responase;
       }

}

/*
6.3..8.  LIST Command

   Arguments:  reference name
               mailbox name with possible wildcards

   Responses:  untagged responses: LIST

   Result:     OK - list completed
               NO - list failure: can't list that reference or name
               BAD - command unknown or arguments invalid

      The LIST command returns a subset of names from the complete set
      of all names available to the client.  Zero or more untagged LIST
      replies are returned, containing the name attributes, hierarchy
      delimiter, and name; see the description of the LIST reply for
      more detail.

      The LIST command SHOULD return its data quickly, without undue
      delay.  For example, it SHOULD NOT go to excess trouble to
      calculate \Marked or \Unmarked status or perform other processing;
      if each name requires 1 second of processing, then a list of 1200
      names would take 20 minutes!

      An empty ("" string) reference name argument indicates that the
      mailbox name is interpreted as by SELECT. The returned mailbox
      names MUST match the supplied mailbox name pattern.  A non-empty
      reference name argument is the name of a mailbox or a level of
      mailbox hierarchy, and indicates a context in which the mailbox
      name is interpreted in an implementation-defined manner.

      An empty ("" string) mailbox name argument is a special request to
      return the hierarchy delimiter and the root name of the name given
      in the reference.  The value returned as the root MAY be null if
      the reference is non-rooted or is null.  In all cases, the
      hierarchy delimiter is returned.  This permits a client to get the
      hierarchy delimiter even when no mailboxes by that name currently
      exist.

      The reference and mailbox name arguments are interpreted, in an
      implementation-dependent fashion, into a canonical form that
      represents an unambiguous left-to-right hierarchy.  The returned
      mailbox names will be in the interpreted form.

      Any part of the reference argument that is included in the
      interpreted form SHOULD prefix the interpreted form.  It SHOULD
      also be in the same form as the reference name argument.  This
      rule permits the client to determine if the returned mailbox name
      is in the context of the reference argument, or if something about
      the mailbox argument overrode the reference argument.  Without
      this rule, the client would have to have knowledge of the server's
      naming semantics including what characters are "breakouts" that
      override a naming context.

      For example, here are some examples of how references and mailbox
      names might be interpreted on a UNIX-based server:

               Reference     Mailbox Name  Interpretation
               ------------  ------------  --------------
               ~smith/Mail/  foo.*         ~smith/Mail/foo.*
               archive/      %             archive/%
               #news.        comp.mail.*   #news.comp.mail.*
               ~smith/Mail/  /usr/doc/foo  /usr/doc/foo
               archive/      ~fred/Mail/*  ~fred/Mail/*

      The first three examples demonstrate interpretations in the
      context of the reference argument.  Note that "~smith/Mail" SHOULD
      NOT be transformed into something like "/u2/users/smith/Mail", or
      it would be impossible for the client to determine that the
      interpretation was in the context of the reference.

      The character "*" is a wildcard, and matches zero or more
      characters at this position.  The character "%" is similar to "*",
      but it does not match a hierarchy delimiter.  If the "%" wildcard
      is the last character of a mailbox name argument, matching levels
      of hierarchy are also returned.  If these levels of hierarchy are
      not also selectable mailboxes, they are returned with the
      \Noselect mailbox name attribute (see the description of the LIST
      response for more details).

      Server implementations are permitted to "hide" otherwise
      accessible mailboxes from the wildcard characters, by preventing
      certain characters or names from matching a wildcard in certain
      situations.  For example, a UNIX-based server might restrict the
      interpretation of "*" so that an initial "/" character does not
      match.

      The special name INBOX is included in the output from LIST, if
      INBOX is supported by this server for this user and if the
      uppercase string "INBOX" matches the interpreted reference and
      mailbox name arguments with wildcards as described above.  The
      criteria for omitting INBOX is whether SELECT INBOX will return
      failure; it is not relevant whether the user's real INBOX resides
      on this or some other server.

   Example:    C: A101 LIST "" ""
               S: * LIST (\Noselect) "/" ""
               S: A101 OK LIST Completed
               C: A102 LIST #news.comp.mail.misc ""
               S: * LIST (\Noselect) "." #news.
               S: A102 OK LIST Completed
               C: A103 LIST /usr/staff/jones ""
               S: * LIST (\Noselect) "/" /
               S: A103 OK LIST Completed
               C: A202 LIST ~/Mail/ %
               S: * LIST (\Noselect) "/" ~/Mail/foo
               S: * LIST () "/" ~/Mail/meetings
               S: A202 OK LIST completed

7.2.2.  LIST Response

   Contents:   name attributes
               hierarchy delimiter
               name

      The LIST response occurs as a result of a LIST command.  It
      returns a single name that matches the LIST specification.  There
      can be multiple LIST responses for a single LIST command.

      Four name attributes are defined:

      \Noinferiors   It is not possible for any child levels of
                     hierarchy to exist under this name; no child levels
                     exist now and none can be created in the future.

      \Noselect      It is not possible to use this name as a selectable
                     mailbox.

      \Marked        The mailbox has been marked "interesting" by the
                     server; the mailbox probably contains messages that
                     have been added since the last time the mailbox was
                     selected.

      \Unmarked      The mailbox does not contain any additional
                     messages since the last time the mailbox was
                     selected.

      If it is not feasible for the server to determine whether the
      mailbox is "interesting" or not, or if the name is a \Noselect
      name, the server SHOULD NOT send either \Marked or \Unmarked.

      The hierarchy delimiter is a character used to delimit levels of
      hierarchy in a mailbox name.  A client can use it to create child
      mailboxes, and to search higher or lower levels of naming
      hierarchy.  All children of a top-level hierarchy node MUST use
      the same separator character.  A NIL hierarchy delimiter means
      that no hierarchy exists; the name is a "flat" name.

      The name represents an unambiguous left-to-right hierarchy, and
      MUST be valid for use as a reference in LIST and LSUB commands.
      Unless \Noselect is indicated, the name MUST also be valid as an
            argument for commands, such as SELECT, that accept mailbox
      names.

   Example:    S: * LIST (\Noselect) "/" ~/Mail/foo
*/
