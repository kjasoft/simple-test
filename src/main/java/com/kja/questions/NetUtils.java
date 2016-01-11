package com.kja.questions;

import java.awt.Desktop;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 
 * @author dn210885kja, 08 окт. 2015 г. 9:36:48
 */
public class NetUtils {

	static List<String> getCurIPs(int port) throws SocketException {
		List<String> addresses = new ArrayList<>();
		Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
		while(ifs.hasMoreElements()) {
			NetworkInterface n = ifs.nextElement();
			Enumeration<InetAddress> as = n.getInetAddresses();
			while(as.hasMoreElements()) {
				InetAddress a = as.nextElement();
				String adr = a.getHostAddress();
				if(adr.matches("[\\d\\.]+") && !adr.startsWith("127")) {
					if(NetUtils.isPortActive(adr, port))
						addresses.add(adr);
				}
			}
		}
		return addresses;
	}

	static boolean isPortActive(String adr, int port) {
		try {
		    Socket socket = new Socket();
		    socket.connect(new InetSocketAddress(adr, port), 100);
		    socket.close();
		    return true;
		} catch (Exception ex) {
		    return false;
		}
	}

	public static int getAvailablePort(int i) throws Exception {
		for(int j=i; j<i+100; j++) {
			if(!isPortActive("localhost", j))
				return j;
		}
		throw new Exception("Невозможно найти свободный порт");
	}
	
}
