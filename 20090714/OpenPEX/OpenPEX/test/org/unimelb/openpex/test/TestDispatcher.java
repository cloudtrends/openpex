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

import java.util.List;
import java.util.logging.Logger;

import org.unimelb.openpex.ClusterNode;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.PexOperationFailedException;
import org.unimelb.openpex.xen.XenDispatcher;
import org.unimelb.openpex.xen.XenVMInstance;

import com.xensource.xenapi.Host;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.Types.VmPowerState;
import org.unimelb.openpex.xen.XenClusterNode;

public class TestDispatcher {
	static Logger logger = Logger.getLogger(TestDispatcher.class.getName());
	static XenDispatcher dispatcher = null;
	
	public static void main(String[] args){
	
		try {
			dispatcher = XenDispatcher.getInstance();
		} catch (PexException e1) {
			System.err.println("Fail "+e1);
			System.exit(-1);
		}
		XenVMInstance xvm = null;
		ClusterNode node = null;
		Host newHost = null;
		List<VM.Record> records =null;
		try {
			node = new XenClusterNode("node1","128.250.33.135");
			records = dispatcher.getAllVMRecords();
		} catch (PexOperationFailedException e1) {
			System.err.println("Fail "+e1);
			System.exit(-1);
		} catch (PexException e) {
			System.err.println("Fail "+e);
			System.exit(-1);
		}
		
		
		for(VM.Record record: records){
			if(record.nameLabel.contains("CentOS") && !(record.isATemplate) && 
					(record.powerState == VmPowerState.HALTED)){
				logger.info(record.toString());
				try {
					xvm = new XenVMInstance();
					xvm.setRecord(record);
					xvm.setClusterNode(node);
                    xvm.startInstance();
				} catch (PexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(true){
					logger.info("The Vm is in Power State "+xvm.getRecord().powerState.toString());
					if((xvm.getRecord().powerState==VmPowerState.RUNNING) || 
							(xvm.getRecord().powerState==VmPowerState.UNKNOWN)){
								System.out.println("The Vm is in Power State "+xvm.getRecord().powerState.toString());
								break;
							}
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						logger.severe("Sleep disturbed "+ e);
					}
				}
				logger.info("Now trying to shut down");
				try{
					xvm.stopInstance();
				}catch (PexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(true){
					logger.info("The Vm is in Power State "+xvm.getRecord().powerState.toString());
					if((xvm.getRecord().powerState==VmPowerState.HALTED) || 
							(xvm.getRecord().powerState==VmPowerState.UNKNOWN)){
								System.out.println("The Vm is in Power State "+xvm.getRecord().powerState.toString());
								break;
							}
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						logger.warning("Sleep disturbed "+ e);
					}
				}
					
			}
		}
		try {
			dispatcher.shutdown();
		} catch (InterruptedException e) {
		
		}
	}
}
