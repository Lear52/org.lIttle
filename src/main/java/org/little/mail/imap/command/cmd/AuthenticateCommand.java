package org.little.mail.imap.command.cmd;

import java.util.ArrayList;
import java.util.List;

import org.little.mail.imap.SessionContext;
import org.little.mail.imap.command.ImapCommand;
import org.little.mail.imap.command.ImapCommandParameter;
import org.little.mail.imap.command.ImapConstants;
import org.little.mail.imap.response.EmptyResponse;
import org.little.mail.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

/**
 * Handles processing for the AUTHENTICATE imap command.
 *
 */
public class AuthenticateCommand extends ImapCommand {
       private static final Logger logger = LoggerFactory.getLogger(AuthenticateCommand.class);

       public static final String NAME = "AUTHENTICATE";
       public static final String ARGS = "<auth_type> *(CRLF base64)";
       
       public AuthenticateCommand(String _tag, String _command, List<ImapCommandParameter> _parameters) { super(_tag,_command,_parameters);}


       @Override
       public ArrayList<ImapResponse> doProcess(SessionContext  sessionContext) throws Exception {
              ArrayList<ImapResponse> responase =new ArrayList<ImapResponse>();
              logger.trace("IMAP:doProcess:"+NAME+" "+ImapCommand.print(getParameters()));
              String auth_type=null;
              //--------------------------------------------------------------------------------------------------------------------------------------
              auth_type = getParameters().get(0).toString();

              if("KERBEROS_V4".equalsIgnoreCase(auth_type)){}
              if("LOGIN".equalsIgnoreCase(auth_type)){}

              //--------------------------------------------------------------------------------------------------------------------------------------
              ImapResponse ret=null;
              //ret=new EmptyResponse(getTag(),ImapConstants.OK+" "+NAME+" "+ImapConstants.COMPLETED);   responase.add(ret);
              ret=new EmptyResponse(getTag(),ImapConstants.NO+" "+NAME+" "+ImapConstants.UNCOMPLETED);   responase.add(ret);
              logger.trace("IMAP:response:"+ret);

              return responase;
       }
}

/*
6.2.1.  AUTHENTICATE Command

   Arguments:  authentication mechanism name

   Responses:  continuation data can be requested

   Result:     OK - authenticate completed, now in authenticated state
               NO - authenticate failure: unsupported authentication
                    mechanism, credentials rejected
              BAD - command unknown or arguments invalid,
                    authentication exchange cancelled

      The AUTHENTICATE command indicates an authentication mechanism,
      such as described in [IMAP-AUTH], to the server.  If the server
      supports the requested authentication mechanism, it performs an
      authentication protocol exchange to authenticate and identify the
      client.  It MAY also negotiate an OPTIONAL protection mechanism
      for subsequent protocol interactions.  If the requested
      authentication mechanism is not supported, the server SHOULD
      reject the AUTHENTICATE command by sending a tagged NO response.

      The authentication protocol exchange consists of a series of
      server challenges and client answers that are specific to the
      authentication mechanism.  A server challenge consists of a
      command continuation request response with the "+" token followed
      by a BASE64 encoded string.  The client answer consists of a line
      consisting of a BASE64 encoded string.  If the client wishes to
      cancel an authentication exchange, it issues a line with a single
      "*".  If the server receives such an answer, it MUST reject the
      AUTHENTICATE command by sending a tagged BAD response.

      A protection mechanism provides integrity and privacy protection
      to the connection.  If a protection mechanism is negotiated, it is
      applied to all subsequent data sent over the connection.  The
      protection mechanism takes effect immediately following the CRLF
      that concludes the authentication exchange for the client, and the
      CRLF of the tagged OK response for the server.  Once the
      protection mechanism is in effect, the stream of command and
      response octets is processed into buffers of ciphertext.  Each
      buffer is transferred over the connection as a stream of octets
      prepended with a four octet field in network byte order that
      represents the length of the following data.  The maximum
      ciphertext buffer length is defined by the protection mechanism.

      Authentication mechanisms are OPTIONAL.  Protection mechanisms are
      also OPTIONAL; an authentication mechanism MAY be implemented
      without any protection mechanism.  If an AUTHENTICATE command
      fails with a NO response, the client MAY try another
      authentication mechanism by issuing another AUTHENTICATE command,
      or MAY attempt to authenticate by using the LOGIN command.  In
      other words, the client MAY request authentication types in
      decreasing order of preference, with the LOGIN command as a last
      resort.

   Example:    S: * OK KerberosV4 IMAP4rev1 Server
               C: A001 AUTHENTICATE KERBEROS_V4
               S: + AmFYig==
               C: BAcAQU5EUkVXLkNNVS5FRFUAOCAsho84kLN3/IJmrMG+25a4DT
                  +nZImJjnTNHJUtxAA+o0KPKfHEcAFs9a3CL5Oebe/ydHJUwYFd
                  WwuQ1MWiy6IesKvjL5rL9WjXUb9MwT9bpObYLGOKi1Qh
               S: + or//EoAADZI=
               C: DiAF5A4gA+oOIALuBkAAmw==
               S: A001 OK Kerberos V4 authentication successful

      Note: the line breaks in the first client answer are for editorial
      clarity and are not in real authenticators.
*/
