package org.little.mailx;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.lang3.StringUtils;

public class sendfile {

       private static String  to1      = "av@chado";
       //private static String  to2      = "iap";
       private static String  from     = "av@chado";
       private static String  host     = "127.0.0.1";
       private static int     port     = 2525;
       private static boolean is_auth   = true;
       private static String  filename = " ";
       private static boolean debug   = true;
       private static String  username = "av";
       private static String  password = "123";
       private static String  subject = "CN=92svc-CA-test, OU=PKI, OU=Tatarstan, DC=region, DC=cbr, DC=ru";
       private static String  msgText1 = ""
+"CERTIFICATE\n"
+"DER\n"
+"Thu Feb 07 12:56:45 MSK 2019\n"
+"Sat May 05 02:59:00 MSK 2035\n"
+"85486455983605724770238770388276084954\n"
+"CN=92svc-CA-test, OU=PKI, OU=Tatarstan, DC=region, DC=cbr, DC=ru\n"
+"CN=ROOTsvc-CA-test, OU=GUBZI, OU=PKI, DC=region, DC=cbr, DC=ru\n"
;

       public static final String      OPTION_HOST   = "host";
       public static final String      OPTION_PORT   = "port";
       public static final String      OPTION_USER   = "user";
       public static final String      OPTION_PSWD   = "pswd";
       public static final String      OPTION_FILE   = "file";
       public static final String      OPTION_TO     = "to";
       public static final String      OPTION_FROM   = "from";
       public static final String      OPTION_AUTH   = "auth";
       public static final String      OPTION_DEBUG  = "debug";
       public static final String      OPTION_HELP   = "help";




        public static void print() {
               String str="";
               str+=OPTION_HOST+":"+host+"\r\n";
               str+=OPTION_PORT+":"+port +"\r\n";
               str+=OPTION_USER+":"+username +"\r\n"; 
               str+=OPTION_PSWD+":"+password +"\r\n"; 
               str+=OPTION_FILE+":"+filename +"\r\n"; 
               str+=OPTION_TO  +":"+to1 +"\r\n"; 
               str+=OPTION_FROM+":"+from +"\r\n"; 
               str+=OPTION_AUTH+":"+is_auth +"\r\n"; 
               str+=OPTION_DEBUG+":"+debug +"\r\n";

               System.out.println(str+"start ...");
        }
        public static void run() {
               System.setProperty("java.net.preferIPv4Stack","true");
               
               SmtpMailXClient cln=new SmtpMailXClient();

               cln.setHost(host);
               cln.setPort(port);

               cln.setUserName(username);
               cln.setPassword(password);
              
               cln.setAuth(is_auth);
               cln.setTo(to1);
               cln.setFrom(from);
               cln.setSubject(subject);
               cln.setMsgText(msgText1);
               cln.setDebug(debug);
               cln.sent(filename);
       
        }
        public static void printHelp(final Options options,final String errorMessage) {
            if (!StringUtils.isBlank(errorMessage)) {
                System.err.println(errorMessage);
            }
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("org.little.mailx.sendfile", options);
        }
        
        public static void main(String[] args) {
               Options           options;
               CommandLineParser parser;
               CommandLine       cmd;

               options = new Options();
               parser  = new DefaultParser();

               options.addOption(null, OPTION_HOST  , true, "connect to host (localhost).");
               options.addOption(null, OPTION_PORT  , true, "connect on port (2525).");
               options.addOption(null, OPTION_USER  , true, "connect use username (username).");
               options.addOption(null, OPTION_PSWD  , true, "connect use passwd  (*******).");
               options.addOption(null, OPTION_FILE  , true, "file name (sertificate.ser).");
               options.addOption(null, OPTION_TO    , true, "to address (aaaaa@ccccccc).");
               options.addOption(null, OPTION_FROM  , true, "from address (aaaaa@ccccccc).");
               options.addOption(null, OPTION_AUTH  , true, "use auth (true/false).");

               options.addOption(null, OPTION_DEBUG , true,"Display debug log (true/false).");
               options.addOption(null, OPTION_HELP  , false,"Display command line help.");

               try{
                   cmd = parser.parse(options, args);
               } catch (final ParseException e) {
                   printHelp(options,"Could not parse command line: " + Arrays.asList(args));
                   return ;
               }
       
               if(cmd.hasOption(OPTION_HELP)) {
                   printHelp(options, null);
                   return ;
               }
               if(cmd.hasOption(OPTION_FILE)) {
                  filename = cmd.getOptionValue(OPTION_FILE);
               } 
               if(cmd.hasOption(OPTION_HOST)) {
                  host = cmd.getOptionValue(OPTION_HOST);
               } 
               if(cmd.hasOption(OPTION_PORT)) {
                  String _port = cmd.getOptionValue(OPTION_PORT);
                  if(_port!=null)
                  try{port=Integer.parseInt(_port, 10);}catch(Exception e){port=2525;}
               } 
       
               if(cmd.hasOption(OPTION_AUTH)) {
                  String _auth = cmd.getOptionValue(OPTION_AUTH);
                  if(_auth!=null)
                  try{is_auth=Boolean.parseBoolean(_auth);}catch(Exception e){is_auth=true;}
               } 
               if(cmd.hasOption(OPTION_DEBUG)) {
                  String _debug = cmd.getOptionValue(OPTION_DEBUG);
                  if(_debug!=null)
                  try{debug=Boolean.parseBoolean(_debug);}catch(Exception e){debug=true;}
               } 
               if(cmd.hasOption(OPTION_USER)) {
            	  username = cmd.getOptionValue(OPTION_USER);
               } 
               if(cmd.hasOption(OPTION_PSWD)) {
                  password = cmd.getOptionValue(OPTION_PSWD);
               } 
               if(cmd.hasOption(OPTION_FROM)) {
            	  from = cmd.getOptionValue(OPTION_FROM);
               } 
               if(cmd.hasOption(OPTION_TO)) {
                  to1 = cmd.getOptionValue(OPTION_TO);
               } 

               print();

               run();      

               System.out.println("ok!");
       
        }
}
