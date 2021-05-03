package org.little.syslog.impl;

import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.impl.net.udp.UDPNetSyslogServerConfig;

public class UDPSyslogServerConfig extends UDPNetSyslogServerConfig {

	private static final long serialVersionUID = -8613583944552145431L;
	private printEvent log;


       public UDPSyslogServerConfig(printEvent _log){
              log=_log;
       }

       @Override
       public Class<? extends SyslogServerIF> getSyslogServerClass() {
              return UDPSyslogServer.class;
       }
       public printEvent getLog() {return log;};

}
