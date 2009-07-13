//    “Copyright 2008, 2009 Srikumar Venugopal & James Broberg”
//
//    This file is part of OpenPEX.
//
//    OpenPEX is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 2 of the License, or
//    (at your option) any later version.
//
//    OpenPEX is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with OpenPEX.  If not, see <http://www.gnu.org/licenses/>.

package org.unimelb.openpex.test;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Test {
	private static XmlRpcClientConfigImpl config;
	private static XmlRpcClient client;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		URL url = null;
		try {
			url = new URL("http://XXXXXXXXX");
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL?");
			System.exit(-1);
		}
		config = new XmlRpcClientConfigImpl();
		config.setServerURL(url);
		client = new XmlRpcClient();
		client.setConfig(config);
		String username = "root";
		String password = "XXXXX";
		Object[] params = new Object[] { username, password };
		HashMap<String, String> result = null;
		try {
			result = (HashMap) client.execute("session.login_with_password",params);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			//System.out.println(e.getMessage());
			System.out.println("Could not open session");
			System.exit(-1);
		}
		
		String status = result.get("Status");
		if (status.compareTo("Success") == 0) {
			String uuid = result.get("Value");
			params = new Object[] { uuid };
			try {
				result = (HashMap) client.execute("VM.get_all", params);
			} catch (XmlRpcException e) {
				System.out.println("Could not get VMs' UUIDs");
				System.exit(-1);
			}
			Object res = result.get("Value");
			Object[] arr = null;
			if (res.getClass() == Object[].class) {
				arr = (Object[]) res;
			
				for (int i = 0; i < arr.length; i++) {
					System.out.println("VM UUID: " + (String) arr[i]);
					System.out.println("");
				}
			}
			
			Object[] new_params=new Object[]{ uuid, arr[0] };
			try{
				result = (HashMap) client.execute("VM.get_record", new_params);
			}catch(XmlRpcException e){
				System.out.println(e.getMessage());
			}
			
			System.out.println((String)result.get("name_label"));
			
			try {
				result = (HashMap) client.execute("session.logout", params);
			} catch (XmlRpcException e) {
				System.out.println("Could not logout");
				System.exit(-1);
			}
			
			status = result.get("Status");
			if (status.compareTo("Success") == 0) {
				System.out.println("Logged out successfully");
			}

		}
		
	}
}
