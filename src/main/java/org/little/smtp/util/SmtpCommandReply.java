package org.little.smtp.util;

import java.util.ArrayList;
import java.util.List;

public class SmtpCommandReply {

	private final SmtpReplyStatus replyCode;
	private final List<CharSequence> text;

	public SmtpCommandReply(SmtpReplyStatus statusCode, CharSequence text) {
		replyCode = statusCode;
		this.text = new ArrayList<CharSequence>();
		this.text.add(text);
	}

	public SmtpCommandReply(SmtpReplyStatus statusCode, List<String> lines) {
		replyCode = statusCode;
		this.text = new ArrayList<CharSequence>();
		this.text.addAll(lines);
	}

	public SmtpReplyStatus getReplyCode() {
		return replyCode;
	}

	public List<CharSequence> getLines() {
		return this.text;
	}
	public String toString(){
	       String str="";
	       for(int i=0;i<text.size();i++)str+=text.get(i).toString();
               return getReplyCode()+" "+str;
       }
}
