package org.little.syslog.impl;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Except;

/**
 * Simple implementation of rfc5424 syslog message format (c.f.
 * http://tools.ietf.org/html/rfc5424#section-6).
 *
 */
public class Rfc5424Event implements SyslogServerEventIF {
       private static Logger logger = LoggerFactory.getLogger(Rfc5424Event.class);

       private static final long serialVersionUID = 1L;
       private static final char SP = ' ';
       private static final String CHARSET = "UTF-8";
       private static final String NIL = "-";
       private static final byte[] UTF_8_BOM = { (byte) 0xef, (byte) 0xbb, (byte) 0xbf };

       private byte[] raw;

       private String prioVersion;
       private int    facility;
       private int    level;
       private int    version;

       private String timestamp;
       private String host;
       private String appName;
       private String procId;
       private String msgId;
       private String structuredData;
       private String message;

       public Rfc5424Event(byte[] data, int offset, int length) {
              make(data,offset,length);
       }
       private void make(byte[] data, int offset, int length) {
              raw = new byte[length - offset];
              System.arraycopy(data, offset, raw, 0, length);
              int startPos = 0;
              int endPos = -1;

              endPos      = searchChar(raw, startPos, SP);
              prioVersion = getString(raw, startPos, endPos);

              startPos  = endPos + 1;
              endPos    = searchChar(raw, startPos, ' ');
              timestamp = getString(raw, startPos, endPos);

              startPos  = endPos + 1;
              endPos    = searchChar(raw, startPos, ' ');
              host      = getString(raw, startPos, endPos);

              startPos  = endPos + 1;
              endPos    = searchChar(raw, startPos, ' ');
              appName   = getString(raw, startPos, endPos);

              startPos  = endPos + 1;
              endPos    = searchChar(raw, startPos, ' ');
              procId    = getString(raw, startPos, endPos);

              startPos  = endPos + 1;
              endPos    = searchChar(raw, startPos, ' ');
              msgId     = getString(raw, startPos, endPos);

              startPos  = endPos + 1;
              if(raw[startPos] == '[') {
                 endPos = searchChar(raw, startPos, ']') + 1;
              } else {
                 endPos = searchChar(raw, startPos, ' ');
                 if(endPos == -1)endPos = raw.length;
              }
              structuredData = getString(raw, startPos, endPos);

              startPos  = endPos + 1;
              if(startPos < raw.length) {
                 if(startPos + 3 < raw.length && raw[startPos] == UTF_8_BOM[0] && raw[startPos + 1] == UTF_8_BOM[1] && raw[startPos + 2] == UTF_8_BOM[2]) {
                    startPos += 3;
                 }
                 message = getString(raw, startPos, raw.length);
              } 
              else {
                 message = null;
              }

              // parse priority and version
              endPos = prioVersion.indexOf(">");
              final String priorityStr = prioVersion.substring(1, endPos);
              int priority = 0;
              try {
                  priority = Integer.parseInt(priorityStr);
              } catch (NumberFormatException nfe) {
                  logger.error("ex:"+new Except("Can't parse priority ("+priorityStr+") for :"+new String(getRaw()),nfe));
              }

              level = priority & 7;
              facility = (priority - level) >> 3;

              startPos = endPos + 1;
              int ver = 0;
              if (startPos < prioVersion.length()) {
                     try {
                         ver = Integer.parseInt(prioVersion.substring(startPos));
                     } catch (NumberFormatException nfe) {
                         logger.error("ex:"+new Except("Can't parse version ( in "+prioVersion+" start:"+startPos+" -> "+prioVersion.substring(startPos)+ ") for :"+new String(getRaw()),nfe));
                         ver = -1;
                     }
              }
              version = ver;
       }

       private String getString(byte[] data, int startPos, int endPos) {
              try {
                   return new String(data, startPos, endPos - startPos, CHARSET);
              } catch (UnsupportedEncodingException e) {
                     logger.error("ex:"+new Except("Unsupported encoding for :"+new String(getRaw()),e));
              }
              return "";
       }

       /**
        * Try to find a character in given byte array, starting from startPos.
        *
        * @param data
        * @param startPos
        * @param c
        * @return position of the character or -1 if not found
        */
       private int searchChar(byte[] data, int startPos, char c) {
              for (int i = startPos; i < data.length; i++) {
                     if (data[i] == c) {
                            return i;
                     }
              }
              return -1;
       }

       public String getPrioVersion   () {return prioVersion;}
       @Override
       public int    getFacility      () {return facility;   }
       @Override
       public int    getLevel         () {return level;      }
       public int    getVersion       () {return version;    }
       public String getTimestamp     () {return timestamp;  }
       @Override
       public String getHost          () {return host;       }
       public String getAppName       () {return appName;    }
       public String getProcId        () {return procId;     }
       public String getMsgId         () {return msgId;      }
       public String getStructuredData() {return structuredData;}
       @Override
       public String getMessage       () {return message;}
       @Override
       public String getCharSet       () {return CHARSET;}
       @Override
       public byte[] getRaw           () {return raw;}
       @Override
       public Date   getDate          () {
              if (NIL.equals(timestamp)) {
                     return null;
              }

              String fixTz          = timestamp.replace("Z", "+00:00");
              int    tzSeparatorPos = fixTz.lastIndexOf(":");
              if(tzSeparatorPos==-1)return null;

              fixTz = fixTz.substring(0, tzSeparatorPos) + fixTz.substring(tzSeparatorPos + 1);
              //logger.trace("data:"+fixTz);

              int    tzSeparatorPos1 = fixTz.lastIndexOf(".");
              int    tzSeparatorPos2 = fixTz.lastIndexOf("+");
              if(tzSeparatorPos2<0)tzSeparatorPos2 = fixTz.lastIndexOf("-");
              int len=tzSeparatorPos2-tzSeparatorPos1;
              if(len>3)len=3;
              fixTz = fixTz.substring(0, tzSeparatorPos1+1)+fixTz.substring(tzSeparatorPos1 + 1,tzSeparatorPos1 + 1+len)+ fixTz.substring(tzSeparatorPos2);

              //logger.trace("data:"+fixTz);
              try {
                  return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ").parse(fixTz);
              } catch (ParseException e) {
                  logger.error("ex:"+new Except("Unable to parse date ("+fixTz+") for :"+new String(getRaw()),e));
              }
              return null;
       }

       @Override
       public void setCharSet(String charSet){}
       @Override
       public void setFacility(int facility) {}
       @Override
       public void setDate(Date date)        {}
       @Override
       public void setLevel(int level)       {}
       @Override
       public void setHost(String host)      {}
       @Override
       public void setMessage(String message){}

       @Override
       public String toString() {
              Date   d=getDate();
              String dd="";
              if(d!=null)dd=""+d;
              //logger.trace("------------------------------------------------------------------------------------------------");
              String ret="[prioVersion=" + getPrioVersion() + ", facility=" + getFacility() + ", level=" + getLevel()
                   + ", version=" + getVersion() + ", timestamp=" + dd + ", host=" + getHost() + ", appName=" + getAppName()
                   + ", procId=" + getProcId() + ", msgId=" + getMsgId() + ", structuredData=" + getStructuredData() + ", message="
                   + getMessage() + "]";
              //logger.trace("------------------------------------------------------------------------------------------------");
              return ret;
       }
       public String print() {
              return "["+ getMessage() + "]";

       }

       @Override
       public boolean isHostStrippedFromMessage() {return false;}

}
