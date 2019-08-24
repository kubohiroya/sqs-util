/*

 NetworkUtil.java

 Copyright 2007 KUBO Hiroya (hiroya@cuc.ac.jp).

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Created on 2007/01/11

 */
package net.sqs2.net;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class NetworkUtil {

	/*
	public static boolean isMyAddress(InetAddress address) throws SocketException {
		return InetAddress.getLocalHost().contains(address);
	}

	public static boolean isMyAddress(InetAddress[] addresses) throws SocketException {
		List<InetAddress> list = getInetAddressList();
		for (InetAddress address : addresses) {
			if (list.contains(address)) {
				return true;
			}
		}
		return false;
	}

	public static LinkedList<InetAddress> getInetAddressList() throws SocketException {
		LinkedList<InetAddress> inetAddressList = new LinkedList<InetAddress>();
		inetAddressList.addAll(Inet4.getAddressList());
		inetAddressList.addAll(Inet6.getAddressList());
		return inetAddressList;
	}*/
	
	public static class Inet4{
		
		public static InetAddress inet4Localhost = null;

		private static String LOOPBACK_HOSTNAME = "localhost";
		
		static {
			try {
				inet4Localhost = InetAddress.getByName(LOOPBACK_HOSTNAME);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public static InetAddress getLocalhost() {
			return inet4Localhost;
		}

		public static LinkedList<InetAddress> getAddressList() throws SocketException, UnknownHostException {
			LinkedList<InetAddress> inetAddressList = new LinkedList<InetAddress>();
			/*
			for (Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces(); enu.hasMoreElements();) {
				NetworkInterface networkInterface = enu.nextElement();
				try {
					// JDK1.6 API 
					if(! networkInterface.isUp() || networkInterface.isLoopback()){ continue; }
				} catch (NoSuchMethodError ignore) {
				}
				for (Enumeration<InetAddress> e = networkInterface.getInetAddresses(); e.hasMoreElements();) {
					InetAddress address = e.nextElement();
					if (! address.isLoopbackAddress() && address instanceof Inet4Address) {
						inet4AddressList.add((Inet4Address) address);
					}
				}
			}*/

			InetAddress in = InetAddress.getLocalHost(); 
			 inetAddressList.add(in);
			return inetAddressList;
		}


		/*
		public static InetAddress getAddress() throws SocketException {
			if (inetAddress == null) {
				inetAddress = createAddress();
			}
			return inetAddress;
		}

		public static InetAddress createAddress() throws SocketException {
			LinkedList<InetAddress> list = getAddressList();
			if (list.isEmpty()) {
				throw new SocketException("no avaliable address");
			}
			return list.getFirst();
		}

		public static String getHostAddress() throws SocketException {
			return getAddress().getHostAddress();
		}
	}*/

	public static class Inet6{
		private static Inet6Address inet6Address = null;
		private static List<Inet6Address> inet6AddressList = null;

		public static boolean isMyAddress(Inet6Address address) throws SocketException {
			return getAddressList().contains(address);
		}

		public static Inet6Address getAddress() throws SocketException {
			if (inet6Address == null) {
				inet6Address = createAddress();
			}
			return inet6Address;
		}

		public static List<Inet6Address> getAddressList() throws SocketException {
			if (inet6AddressList == null) {
				inet6AddressList = createAddressList();
			}
			return inet6AddressList;
		}

		public static LinkedList<Inet6Address> createAddressList() throws SocketException {
			LinkedList<Inet6Address> inet6AddressList = new LinkedList<Inet6Address>();
			for (Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces(); enu
					.hasMoreElements();) {
				NetworkInterface net = enu.nextElement();

				if(! net.isUp() || net.isLoopback()){ continue; }

				for (Enumeration<InetAddress> e = net.getInetAddresses(); e.hasMoreElements();) {
					InetAddress address = e.nextElement();
					if (! address.getHostAddress().equals(Inet4.inet4Localhost.getHostAddress()) && address instanceof Inet6Address) {
						inet6AddressList.add((Inet6Address) address);
					}
				}
			}
			return inet6AddressList;
		}

		public static Inet6Address createAddress() throws SocketException {
			LinkedList<Inet6Address> list = createAddressList();
			if (list.isEmpty()) {
				throw new SocketException("no avaliable address");
			}
			return list.getFirst();
		}
		public static String getHostAddress() throws SocketException {
			return getAddress().getHostAddress();
		}
		
	}
	}
}
