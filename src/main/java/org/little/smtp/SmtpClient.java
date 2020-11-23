package org.little.smtp;

import java.net.InetSocketAddress;

import org.little.smtp.element.SmtpRequest;
import org.little.smtp.element.SmtpRequestBuilder;
import org.little.smtp.handler.SmtpClnCommandHandler;
import org.little.smtp.handler.SmtpClnInitializer;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util._Base64;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;


public class SmtpClient{

        private static final Logger logger = LoggerFactory.getLogger(SmtpClient.class);
        private int                   remote_port;
        private String                remote_host;
        private EventLoopGroup        localGroup;
        private SmtpClnInitializer    start_handler;
        private SmtpClnCommandHandler commnad_handler;
        private Channel               client_channel;
        private ChannelFuture         f_client_channel;
        private Channel               server_channel; 

        public SmtpClient() {
               server_channel=null;
               localGroup    =null; 
               remote_port   =commonSMTP.get().getClientPort();         
               remote_host   =commonSMTP.get().getClientHost();
        }
        public SmtpClient(Channel  _server_channel) {
               server_channel=_server_channel;
               localGroup    =server_channel.eventLoop(); 
               remote_port   =commonSMTP.get().getClientPort();         
               remote_host   =commonSMTP.get().getClientHost();
        }

        public Channel getChannel() {return client_channel;}
        public ChannelFuture getChannelFuture() {return f_client_channel;}
                
        private int start_test() {
            start_handler  =new SmtpClnInitializer();
            commnad_handler=start_handler.getCommandHandler(); 
            commnad_handler.setChannel(server_channel);
            init(commnad_handler);
            connect();

            logger.trace("connect client session");

            return 0;
     }
        public int start() {
               start_handler=new SmtpClnInitializer();
               commnad_handler=start_handler.getCommandHandler(); 
               commnad_handler.setChannel(server_channel);

               connect();

               logger.trace("connect client session");

               return 0;
        }
        
        public void stop() {
               try {
                    if(localGroup!=null)localGroup.shutdownGracefully().sync();
               } catch (InterruptedException e) {
                    logger.error("close SmtpClient", e);
               }
               finally {
                   localGroup   = null; 
               }

        }
        private void connect() {
                if(localGroup==null)localGroup          = new NioEventLoopGroup();

                try {
                     Bootstrap boot_strap = new Bootstrap();
                     boot_strap.group(localGroup);
              
                     boot_strap.channel(NioSocketChannel.class);
                     boot_strap.handler(start_handler);
                     boot_strap.option (ChannelOption.TCP_NODELAY, true);
                     boot_strap.option (ChannelOption.AUTO_READ, true);
                     boot_strap.option (ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);
              
                     f_client_channel = boot_strap.connect(new InetSocketAddress(remote_host, remote_port));
                     client_channel=f_client_channel.channel();
                     logger.trace("channel id:"+client_channel.id().asShortText());
                } 
                catch (Exception e) {
                        logger.error("run SmtpClient", e);
                } 
        	
        	
        	
        }
        public void run() {
                try {
                     f_client_channel=f_client_channel.sync();
                     // Wait until the connection is closed.
                     f_client_channel=f_client_channel.channel().closeFuture();
                     f_client_channel.sync();
                } 
                catch (Exception e) {
                        logger.error("run SmtpClient", e);
                } 
                finally {
                       stop();
                }
        }
        
        private void init(final SmtpClnCommandHandler command) {
               logger.trace("begin queue");

               SmtpRequest request;
               request=SmtpRequestBuilder.ehlo("D-SHADRINAV");             command.addQueue(request);
               request=SmtpRequestBuilder.help(null);                      command.addQueue(request);
               request=SmtpRequestBuilder.auth("LOGIN");                   command.addQueue(request);
               String username=_Base64.StrToBase64("av@vip.cbr.ru");
               String password=_Base64.StrToBase64("123");
               request=SmtpRequestBuilder.empty(username);                 command.addQueue(request);
               request=SmtpRequestBuilder.empty(password);                 command.addQueue(request);
               request=SmtpRequestBuilder.vrfy("av@vip.cbr.ru");           command.addQueue(request);
               request=SmtpRequestBuilder.vrfy("iap@vip.cbr.ru");          command.addQueue(request);
              
               request=SmtpRequestBuilder.noop();                          command.addQueue(request);
              
               //MAIL FROM:<av@vip.cbr.ru>                           
               request=SmtpRequestBuilder.mail("av@vip.cbr.ru");           command.addQueue(request);
               //RCPT TO:<av@vip.cbr.ru>
               request=SmtpRequestBuilder.rcpt("av@vip.cbr.ru");           command.addQueue(request);
               request=SmtpRequestBuilder.rcpt("iap@vip.cbr.ru");          command.addQueue(request);
               request=SmtpRequestBuilder.data();                          command.addQueue(request);

               request=SmtpRequestBuilder.lastcontent(body);               command.addQueue(request);
               //SmtpClnContent _request=new SmtpClnLastContent();            _request.set(body);command.addQueue(_request);
               
               request=SmtpRequestBuilder.quit();                          command.addQueue(request);
               logger.trace("end queue");


        }
        private String body= ""
        		+"Date: Tue, 27 Oct 2020 17:55:00 +0300 (MSK)\r\n"
        		+"From: av@vip.cbr.ru\r\n"
        		+"To: av@vip.cbr.ru, iap@vip.cbr.ru\r\n"
        		+"Message-ID: <20961093.1.1603810501001@d-shadrinav.vip.cbr.ru>\r\n"
        		+"Subject: CN=92svc-CA-test, OU=PKI, OU=Tatarstan, DC=region, DC=cbr, DC=ru\r\n"
        		+"MIME-Version: 1.0\r\n"
        		+"Content-Type: multipart/mixed; \r\n"
        		+"	boundary=\"----=_Part_0_18968908.1603810500954\"\r\n"
        		+"\r\n"
        		+"------=_Part_0_18968908.1603810500954\r\n"
        		+"Content-Type: text/plain; charset=us-ascii\r\n"
        		+"Content-Transfer-Encoding: 7bit\r\n"
        		+"\r\n"
        		+"CERTIFICATE\r\n"
        		+"DER\r\n"
        		+"Thu Feb 07 12:56:45 MSK 2019\r\n"
        		+"Sat May 05 02:59:00 MSK 2035\r\n"
        		+"85486455983605724770238770388276084954\r\n"
        		+"CN=92svc-CA-test, OU=PKI, OU=Tatarstan, DC=region, DC=cbr, DC=ru\r\n"
        		+"CN=ROOTsvc-CA-test, OU=GUBZI, OU=PKI, DC=region, DC=cbr, DC=ru\r\n"
        		+"\r\n"
        		+"------=_Part_0_18968908.1603810500954\r\n"
        		+"Content-Type: application/octet-stream; name=92svc-CA-test.cer\r\n"
        		+"Content-Transfer-Encoding: base64\r\n"
        		+"Content-Disposition: attachment; filename=92svc-CA-test.cer\r\n"
        		+"\r\n"
        		+"MIII1zCCCIKgAwIBAgIQQFAXsNJia97B7Xx2XFwA2jAMBggqhQMHAQEDAgUAMHkxEjAQBgoJkiaJ\r\n"
        		+"k/IsZAEZFgJydTETMBEGCgmSJomT8ixkARkWA2NicjEWMBQGCgmSJomT8ixkARkWBnJlZ2lvbjEM\r\n"
        		+"MAoGA1UECxMDUEtJMQ4wDAYDVQQLEwVHVUJaSTEYMBYGA1UEAxMPUk9PVHN2Yy1DQS10ZXN0MB4X\r\n"
        		+"DTE5MDIwNzA5NTY0NVoXDTM1MDUwNDIzNTkwMFowezESMBAGCgmSJomT8ixkARkWAnJ1MRMwEQYK\r\n"
        		+"CZImiZPyLGQBGRYDY2JyMRYwFAYKCZImiZPyLGQBGRYGcmVnaW9uMRIwEAYDVQQLEwlUYXRhcnN0\r\n"
        		+"YW4xDDAKBgNVBAsTA1BLSTEWMBQGA1UEAxMNOTJzdmMtQ0EtdGVzdDBmMB8GCCqFAwcBAQEBMBMG\r\n"
        		+"ByqFAwICIwEGCCqFAwcBAQICA0MABEBRMaqF0iAEez5Otfz1XUC9qWkVKGAjdUOVJnJegOCfqmpN\r\n"
        		+"/wrSXJPojhcT7TfmZx4qvh7OjnmpiYPWh+y8HQNLo4IG2TCCBtUwgbgGCSsGAQQBnFYEDwSBqjCB\r\n"
        		+"p4AU6T1BXNyIQwvWMqVz84qxC7YdeM+hfaR7MHkxEjAQBgoJkiaJk/IsZAEZFgJydTETMBEGCgmS\r\n"
        		+"JomT8ixkARkWA2NicjEWMBQGCgmSJomT8ixkARkWBnJlZ2lvbjEMMAoGA1UECxMDUEtJMQ4wDAYD\r\n"
        		+"VQQLEwVHVUJaSTEYMBYGA1UEAxMPUk9PVHN2Yy1DQS10ZXN0ghBANhC30yJWuQvsFzhRZ8ouMB0G\r\n"
        		+"A1UdDgQWBBTt42bjUFGiGPZ6XFIMJj5/I9Cg3TCBuAYJKwYBBAHQBAQGBIGqMIGngBSi6BTIG8yB\r\n"
        		+"+elKEcNJlXdZ8HkUFqF9pHsweTESMBAGCgmSJomT8ixkARkWAnJ1MRMwEQYKCZImiZPyLGQBGRYD\r\n"
        		+"Y2JyMRYwFAYKCZImiZPyLGQBGRYGcmVnaW9uMQwwCgYDVQQLEwNQS0kxDjAMBgNVBAsTBUdVQlpJ\r\n"
        		+"MRgwFgYDVQQDEw9ST09Uc3ZjLUNBLXRlc3SCEEBQFMBDpXWTXUPmKlp6y4gwKAYJKwYBBAHQBAQD\r\n"
        		+"BBswGQYJKwYBBAHQBAUDBAwyMzgwVEgzNVIxMDEwWAYFKoUDZG8ETwxN0JDQn9CaICLQodGA0LXQ\r\n"
        		+"tNGB0YLQstC+INCa0JfQmCDQodCa0JDQlCAi0KHQuNCz0L3QsNGC0YPRgNCwIiIg0LLQtdGA0YHQ\r\n"
        		+"uNGPIDUwCwYDVR0PBAQDAgGGMHYGA1UdEQRvMG2gSgYDVQQKoEMMQdCe0YLQtNC10LvQtdC90LjQ\r\n"
        		+"tSAtINCd0JEg0KDQtdGB0L/Rg9Cx0LvQuNC60LAg0KLQsNGC0LDRgNGB0YLQsNC9oB8GA1UEDaAY\r\n"
        		+"DBbQodC10YDQuNGPINCY0JDQoSDQojA3MCsGA1UdEAQkMCKADzIwMTkwMjA3MDk1NjQ1WoEPMjAy\r\n"
        		+"MDA1MDQyMzU5MDBaMBAGCSsGAQQB0AQECwQDAgEBMIHVBggrBgEFBQcBAQSByDCBxTBaBggrBgEF\r\n"
        		+"BQcwAoZObGRhcDovL3JlZ2lvbi5jYnIucnUvQ049Uk9PVHN2Yy1DQS10ZXN0LE9VPUdVQlpJLE9V\r\n"
        		+"PVBLSSxEQz1yZWdpb24sREM9Y2JyLERDPXJ1MGcGCCsGAQUFBzAChltsZGFwOi8vcmVnaW9uLmNi\r\n"
        		+"ci5ydS9DTj1ST09Uc3ZjLUNBLTU2MDFJUENUNU4wMS10ZXN0LE9VPUdVQlpJLE9VPVBLSSxEQz1y\r\n"
        		+"ZWdpb24sREM9Y2JyLERDPXJ1MIGyBgNVHSMEgaowgaeAFEH+BGZ+4g6vtPMfo7uEiXcUwYltoX2k\r\n"
        		+"ezB5MRIwEAYKCZImiZPyLGQBGRYCcnUxEzARBgoJkiaJk/IsZAEZFgNjYnIxFjAUBgoJkiaJk/Is\r\n"
        		+"ZAEZFgZyZWdpb24xDDAKBgNVBAsTA1BLSTEOMAwGA1UECxMFR1VCWkkxGDAWBgNVBAMTD1JPT1Rz\r\n"
        		+"dmMtQ0EtdGVzdIIQQFAXsAthaGZpfvSsXFgEijAPBgNVHRMECDAGAQH/AgEAMIIBIgYFKoUDZHAE\r\n"
        		+"ggEXMIIBEwxN0JDQn9CaICLQodGA0LXQtNGB0YLQstC+INCa0JfQmCDQodCa0JDQlCAi0KHQuNCz\r\n"
        		+"0L3QsNGC0YPRgNCwIiIg0LLQtdGA0YHQuNGPIDUMP9CQ0J/QmiAi0KHQuNCz0L3QsNGC0YPRgNCw\r\n"
        		+"LdGB0LXRgNGC0LjRhNC40LrQsNGCIiDQstC10YDRgdC40Y8gNQxU0JLRi9C/0LjRgdC60LAg0LjQ\r\n"
        		+"tyDQl9Cw0LrQu9GO0YfQtdC90LjRjyDihJYxNDkvMy8yLzItMTAyMiDQvtGCIDMwINC40Y7QvdGP\r\n"
        		+"IDIwMTUg0LMuDCvQodCkLzEyOC0yNzQ2INC+0YIgMTcg0L3QvtGP0LHRgNGPIDIwMTUg0LMuMIHE\r\n"
        		+"BgNVHR8EgbwwgbkwVKBSoFCGTmxkYXA6Ly9yZWdpb24uY2JyLnJ1L0NOPVJPT1RzdmMtQ0EtdGVz\r\n"
        		+"dCxPVT1HVUJaSSxPVT1QS0ksREM9cmVnaW9uLERDPWNicixEQz1ydTBhoF+gXYZbbGRhcDovL3Jl\r\n"
        		+"Z2lvbi5jYnIucnUvQ049Uk9PVHN2Yy1DQS01NjAxSVBDVDVOMDEtdGVzdCxPVT1HVUJaSSxPVT1Q\r\n"
        		+"S0ksREM9cmVnaW9uLERDPWNicixEQz1ydTBrBgNVHRIEZDBioD8GA1UECqA4DDbQmtC+0YDQvdC1\r\n"
        		+"0LLQvtC5INCm0KEg0JTQkdCR0KAg0JHQsNC90LrQsCDQoNC+0YHRgdC40LigHwYDVQQNoBgMFtCh\r\n"
        		+"0LXRgNC40Y8g0JjQkNChINCiMDcwDAYIKoUDBwEBAwIFAANBAOX7FXqYWJrcrDh4eP7q7S5yKTa5\r\n"
        		+"mHK3dDT8ZuLgKK6zBXdsZXphfWScQToXvjkn6N6M9F2WNzbtMu/p2jYXF/M=\r\n"
        		+"------=_Part_0_18968908.1603810500954--\r\n"
        		;

        
        
        public static void main(String[] args) {
            SmtpClient cln=new SmtpClient();
            cln.start_test();
            cln.run();
            cln.stop();
        }
}
