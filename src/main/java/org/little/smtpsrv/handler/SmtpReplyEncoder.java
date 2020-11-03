package org.little.smtpsrv.handler;
 
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.little.smtpsrv.util.SmtpCommandReply;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 4.2 SMTP Replies
 * @author thomas
 *
 */
public class SmtpReplyEncoder extends MessageToByteEncoder<SmtpCommandReply> {
        private static Logger logger = LoggerFactory.getLogger(SmtpReplyEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, SmtpCommandReply msg, ByteBuf out) throws Exception {

		List<CharSequence> lines = msg.getLines();

                logger.trace("encode:"+lines.size());

		for(int i = 0, n = lines.size(); i < n; i++) {
			//FIXME: split on max line size!
			String rt;
			CharSequence line = lines.get(i);
			if(i == n - 1) {
				rt = String.format("%03d %s\r\n", msg.getReplyCode().getStatus(), line);
			} else {
				rt = String.format("%03d-%s\r\n", msg.getReplyCode().getStatus(), line);
			}
			byte[] bytes = rt.getBytes(StandardCharsets.US_ASCII);
			out.writeBytes(bytes);
		}
	}

}
