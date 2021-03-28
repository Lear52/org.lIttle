package org.little.mail.imap.command.cmd.fetch;

public class FetchElement {

    private int    type;
	private String arg1;
    private String arg2;
    
    public FetchElement(int _type) {type=_type;arg1=null;arg2=null;}
    public FetchElement(int _type,String _arg1) {type=_type;arg1=_arg1;arg2=null;}

    public int getType() {
		return type;
	}
	public String getArg1() {
		return arg1;
	}
	public String getArg2() {
		return arg2;
	}

}
