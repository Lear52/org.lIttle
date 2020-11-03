package org.little.smtpsrv.command;

import java.util.List;

import org.little.smtpsrv.SessionContext;


public abstract class AbstractSmtpCommand implements SmtpCommand {


	@Override
	public CharSequence getHelloKeyword(SessionContext ctx) {
		return null;
	}

	@Override
	public List<CharSequence> getHelloParams(SessionContext ctx) {
		return null;
	}

	@Override
	public List<CharSequence> getMailParams(SessionContext ctx) {
		return null;
	}

	@Override
	public List<CharSequence> getRecipentParams(SessionContext ctx) {
		return null;
	}

	@Override
	public int getAdditionalDataLen() {
		return 0;
	}

	@Override
	public int getAdditionalCommandLen() {
		return 0;
	}

}
