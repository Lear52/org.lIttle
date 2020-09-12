package org.little.imap.command.cmd.fetch;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sun.mail.imap.protocol.INTERNALDATE;
import javax.mail.internet.*;


public class ImapDate {

        /**
         * Print an IMAP Date string, that is suitable for the Date
         * SEARCH commands.
         *
         * The IMAP Date string is :
         *       date ::= date_day "-" date_month "-" date_year       
         *
         * Note that this format does not contain the TimeZone
         */
        private static String monthTable[] = { 
         "Jan", "Feb", "Mar", "Apr", "May", "Jun",
         "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
    
    
        public static String toIMAPDate(Date date) {
               StringBuilder s = new StringBuilder();
               Calendar cal = new GregorianCalendar();
               cal.setTime(date);
    
               s.append(cal.get(Calendar.DATE)).append("-");
               s.append(monthTable[cal.get(Calendar.MONTH)]).append('-');
               s.append(cal.get(Calendar.YEAR));
              
               return s.toString();
        }
        public static  String toIMAPDateTime(Date date) {
               StringBuilder s = new StringBuilder();
    
               Calendar cal = new GregorianCalendar();
               cal.setTime(date);
    
               s.append(cal.get(Calendar.DATE)).append("-");
               s.append(monthTable[cal.get(Calendar.MONTH)]).append('-');
               s.append(cal.get(Calendar.YEAR));
               s.append(" ");
               int t;
               t=cal.get(Calendar.HOUR_OF_DAY);
               if(t<10)s.append("0").append(t);else s.append(t);
               
               s.append(':');
               t=cal.get(Calendar.MINUTE);
               if(t<10)s.append("0").append(t);else s.append(t);
               s.append(':');
               t=cal.get(Calendar.SECOND);
               if(t<10)s.append("0").append(t);else s.append(t);
               s.append(' ');
               t=cal.get(Calendar.ZONE_OFFSET);
               t/=3600;
    
               if(t>0)s.append('+');else s.append('-');
               s.append(t);
       
               return s.toString();
        }
        public static  String toInternalDate(Date date) {
               if(null != date) {
                  return INTERNALDATE.format(date);
               }
               return INTERNALDATE.format(new Date());
        }
        public static  String toDateEnvelope(Date date) {
               if(null != date) {
                  return new MailDateFormat().format(date);
               }
               return new MailDateFormat().format(new Date());
        }
    

}
/*
Formats and parses date specification based on the draft-ietf-drums-msg-fmt-08 dated January 26, 2000. This is a followup spec to RFC822.
This class does not take pattern strings. It always formats the date based on the specification below.

3.3 Date and Time Specification

Date and time occur in several header fields of a message. This section specifies the syntax for a full date and time specification. Though folding whitespace is permitted throughout the date-time specification, it is recommended that only a single space be used where FWS is required and no space be used where FWS is optional in the date-time specification; some older implementations may not interpret other occurrences of folding whitespace correctly.

date-time = [ day-of-week "," ] date FWS time [CFWS]

day-of-week = ([FWS] day-name) / obs-day-of-week

day-name = "Mon" / "Tue" / "Wed" / "Thu" / "Fri" / "Sat" / "Sun"

date = day month year

year = 4*DIGIT / obs-year

month = (FWS month-name FWS) / obs-month

month-name = "Jan" / "Feb" / "Mar" / "Apr" /
             "May" / "Jun" / "Jul" / "Aug" /
             "Sep" / "Oct" / "Nov" / "Dec"
 
day = ([FWS] 1*2DIGIT) / obs-day

time = time-of-day FWS zone

time-of-day = hour ":" minute [ ":" second ]

hour = 2DIGIT / obs-hour

minute = 2DIGIT / obs-minute

second = 2DIGIT / obs-second

zone = (( "+" / "-" ) 4DIGIT) / obs-zone

The day is the numeric day of the month. The year is any numeric year in the common era.

The time-of-day specifies the number of hours, minutes, and optionally seconds since midnight of the date indicated.

The date and time-of-day SHOULD express local time.

The zone specifies the offset from Coordinated Universal Time (UTC, formerly referred to as "Greenwich Mean Time") 
that the date and time-of-day represent. The "+" or "-" indicates whether the time-of-day is ahead of or behind Universal Time. 
The first two digits indicate the number of hours difference from Universal Time, and the last two digits indicate the number of minutes difference 
from Universal Time. (Hence, +hhmm means +(hh * 60 + mm) minutes, and -hhmm means -(hh * 60 + mm) minutes). 
The form "+0000" SHOULD be used to indicate a time zone at Universal Time. Though "-0000" also indicates Universal Time, it is used to indicate that the time was generated on a system that may be in a local time zone other than Universal Time.

A date-time specification MUST be semantically valid. That is, the day-of-the week (if included) MUST be the day implied by the date, the numeric day-of-month MUST be between 1 and the number of days allowed for the specified month (in the specified year), the time-of-day MUST be in the range 00:00:00 through 23:59:60 (the number of seconds allowing for a leap second; see [STD-12]), and the zone MUST be within the range -9959 through +9959.
*/