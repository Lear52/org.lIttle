package org.little.smtp;

public class SmtpCLN {

       public static void main(String[] args) {
              SmtpClient cln=new SmtpClient();
              cln.start();
              cln.run();
              cln.stop();

       }
}
