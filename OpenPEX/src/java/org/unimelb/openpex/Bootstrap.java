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

package org.unimelb.openpex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import org.unimelb.openpex.xen.XenDispatcher;

public class Bootstrap {
	
	static Logger logger = Logger.getLogger(Bootstrap.class.getName());
	
	public static void bootstrap() throws PexException{
                System.out.println("We are bootstrapping .. hoo .. haa ");
		
//		ConsoleHandler console = new ConsoleHandler();
//		logger.addHandler(console);
//		logger.setLevel(Level.ALL);
		
//		URL logConfig = Bootstrap.class.getResource("/"+Constants.PEX_LOGGER_CONFIG);
//		PropertyConfigurator.configure(logConfig);
		
		/*
		 * First, start the resource manager by connecting to the Xen machine
		 */
		try {
			XenDispatcher dispatcher = XenDispatcher.getInstance();
                        System.out.println("Obtained the dispatcher");
			ResourceManager manager = ResourceManager.getInstance();
                        System.out.println("Obtained the resource manager");
			NodeMonitor monitor = NodeMonitor.getInstance();
                        System.out.println("Obtained the nodemonitor");
			Calendar reservationEpoch = Calendar.getInstance();
                        
			/*
			 * Reservation epoch is 5 minutes into the future
			 */
			reservationEpoch.add(Calendar.MINUTE, 5);
                        System.out.println("the reservation epoch is "+reservationEpoch.getTime().toString());
			/*
			 * Now, read the list of nodes from a nodes file;
			 */
			InputStream fin = Bootstrap.class.getResourceAsStream("pex.nodes");
                        System.out.println("Obtained the pex nodes file");
		
			BufferedReader fread = new BufferedReader(new InputStreamReader(fin));
			String node = "", name="", ip="";
			while((node = fread.readLine())!=null &&
					!node.equals("")){
                    
				String[] resource = node.split(" ");
                                logger.info("Parsing "+node);
				name = resource[0];
				ip = resource[1];
				try{
					monitor.addClusterNode(name, ip, reservationEpoch);
					logger.info("Successfully added "+name+" "+ip);
				}catch(PexException e){
					logger.warning("Error while adding node "+name);
					if(!( e instanceof PexNodeException)) {
						throw e;
					}
				}
				
			}
                        
                        /*Read the list of MAC addresses that can be granted to VMs */
                        InputStream fin2 = Bootstrap.class.getResourceAsStream("pex.macs");
                        System.out.println("Obtained the MAC addresses for PEX VMs");
                        BufferedReader fread2 = new BufferedReader(new InputStreamReader(fin2));
			String mac = "";
                        List<String> macList = new ArrayList<String>();
			while((mac = fread2.readLine())!=null){
                            if(mac.equals(""))
                                continue;
                            macList.add(mac);
                        }
                        
                        manager.setMacList(macList);
                        
//                        PexStorage store = new PexStorage();
//                        store.initEntityManager();
            
			logger.exiting(Bootstrap.class.getName(), "Bootstrap complete.. over to you");
                                
		} catch (PexException e) {
			logger.severe("Error in Pex Initialisation");
			throw new PexException("Nodes not found",e);
		} catch (FileNotFoundException e) {
			logger.severe("The list of nodes was not found");
			throw new PexException("Nodes not found",e);
		} catch (IOException e) {
			logger.severe("Could not read nodes file");
			throw new PexException("Nodes file unreadable",e);
		} catch (Exception e) {
                        logger.severe("Unknown failure");
			throw new PexException("Unknown oh noes1111", e);
                }
		
	}

}
