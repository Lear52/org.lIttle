package org.little.syslog.impl;

import org.productivity.java.syslog4j.server.SyslogServerEventIF;

public interface printEvent {

       public void print(SyslogServerEventIF event);

}