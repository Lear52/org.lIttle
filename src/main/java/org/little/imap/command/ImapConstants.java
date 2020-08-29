package org.little.imap.command;

public interface ImapConstants {
       // Basic response types
       String OK = "OK";
       String NO = "NO";
       String BAD = "BAD";
       String BYE = "BYE";
       String UNTAGGED = "*";
      
       String SP = " ";

       String VERSION = "IMAP4rev1";  //if this is an IMAP4rev1 server

       String CAPABIL_IMAP4           ="IMAP4";
       String CAPABIL_LITERAL_PLUS    ="LITERAL+";//whether this Protocol supports non-synchronizing literals.
       String CAPABIL_SASL_IR2        ="SASL-IR";
       String CAPABIL_LOGIN_REFERRALS ="LOGIN-REFERRALS";
       String CAPABIL_X_UNAUTHENTICATE="X-UNAUTHENTICATE"; //"Netscape/iPlanet/SunONE Messaging Server extension"
       String CAPABIL_CONDSTORE       ="CONDSTORE";
       String CAPABIL_QRESYNC         ="QRESYNC";
       String CAPABIL_ENABLE          ="ENABLE";  // see "RFC 5161"
       String CAPABIL_UNSELECT        ="UNSELECT";// see "RFC 3691"    supported UNSELECT 
       String CAPABIL_IMAP4SUNVERSION ="IMAP4SUNVERSION";// supported STATUS
       String CAPABIL_UIDPLUS         ="UIDPLUS";//see "RFC4315, section 2"  UID EXPUNGE not supported
       String CAPABIL_MOVE            ="MOVE";//see "RFC 6851" see "RFC 4315, section 3"    suported MOVE
       String CAPABIL_SORT            ="SORT*";//"RFC 5256" suported "RFC 5256"
       String CAPABIL_NAMESPACE       ="NAMESPACE"; // see "RFC2342"
       String CAPABIL_QUOTA           ="QUOTA";  //see "RFC2087" superted command GETQUOTAROOT
       String CAPABIL_ACL             ="ACL";//suported command SETACL "RFC2086"
       String CAPABIL_IDLE            ="IDLE";//see "RFC2177" //Note that while this method is blocked waiting for a response,no other threads may issue any commands to the server that would use this same connection.
       String CAPABIL_ID              ="ID"; //see "RFC 2971"
       







       String CAPABILITIES = CAPABIL_LITERAL_PLUS + SP + CAPABIL_SORT + SP + CAPABIL_UIDPLUS;  //"CHILDREN IDLE QUOTA SORT ACL NAMESPACE RIGHTS=texk[nl]"
      
       String USER_NAMESPACE           = "#mail";
      
       char   HIERARCHY_DELIMITER_CHAR = '.';
       char   NAMESPACE_PREFIX_CHAR    = '#';
       String HIERARCHY_DELIMITER      = String.valueOf(HIERARCHY_DELIMITER_CHAR);
       String NAMESPACE_PREFIX         = String.valueOf(NAMESPACE_PREFIX_CHAR);
      
       String INBOX_NAME               = "INBOX";
       String STORAGE                  = "STORAGE";
       String MESSAGES                 = "MESSAGES";
       String COMPLETED                = "completed";
       String UNCOMPLETED              = "uncompleted";
}
