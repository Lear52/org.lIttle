package org.little.smtpsrv.command;

import java.util.ArrayList;
import java.util.List;

import org.little.smtpsrv.SessionContext;
import org.little.smtpsrv.util.SmtpCommandReply;

import io.netty.channel.ChannelHandlerContext;

public interface SmtpCommand {

	CharSequence       getCommandVerb();
	CharSequence       getHelloKeyword(SessionContext ctx);
	List<CharSequence> getHelloParams(SessionContext ctx);
	List<CharSequence> getMailParams(SessionContext ctx);
	List<CharSequence> getRecipentParams(SessionContext ctx);
	int                getAdditionalDataLen();
	int                getAdditionalCommandLen();

	SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, CharSequence argument);
	SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<String> list_cmd);

}
