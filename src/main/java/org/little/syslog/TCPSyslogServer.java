package org.little.syslog;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Set;

import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServer;

/**
 * Syslog4j server for TCP protocol implementation.
 *
 * @author Josef Cacek
 */
public class TCPSyslogServer extends TCPNetSyslogServer {
	protected Set<Socket> sockets;
	
	//@SuppressWarnings("unchecked")
	public TCPSyslogServer() {
		sockets = Collections.synchronizedSet(sockets);
	}
	
	//@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			System.out.println("Creating Syslog server socket");
			this.serverSocket = createServerSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!this.shutdown) {
			try {
				final Socket socket = this.serverSocket.accept();
				System.out.println("Handling Syslog client " + socket.getInetAddress());
				new Thread(new TCPSyslogSocketHandler(this.sockets, this, socket)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
