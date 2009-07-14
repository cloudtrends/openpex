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

/*
 * Title        :  TestAPI.java
 * Package      :  org.unimelb.pex
 * Project      :  ProvisioningXen
 * Description	:  Testing Xen API
 * Created on   :  May 1, 2008
 * Author	    :  Srikumar Venugopal (srikumar@cs.mu.oz.au)
 * 
 */
package org.unimelb.openpex.test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;

import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.Types;
import com.xensource.xenapi.VIF;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.Types.BadServerResponse;
import com.xensource.xenapi.Types.BootloaderFailed;
import com.xensource.xenapi.Types.OperationNotAllowed;
import com.xensource.xenapi.Types.OtherOperationInProgress;
import com.xensource.xenapi.Types.UnknownBootloader;
import com.xensource.xenapi.Types.VmBadPowerState;
import com.xensource.xenapi.Types.VmIsTemplate;
import com.xensource.xenapi.Types.VmPowerState;
import com.xensource.xenapi.Types.XenAPIException;
import com.xensource.xenapi.VM.Record;

public class TestAPI {
	
	String username = "root";
	String password = "XXXXXX";
	String hostname = "http://XXXXXXXXX";
	
	Connection xenConnection = null;
	
	public TestAPI(){
		try{
			xenConnection = new Connection(hostname, username,password);
			Session xenSession = Session.loginWithPassword(xenConnection, username, password, "1.2");
		}catch(MalformedURLException e){
			System.out.println("URL not valid");
		} catch (XenAPIException e) {
			System.out.println("Server did not like msg "+e.getMessage());
		} catch (XmlRpcException e) {
			System.out.println("XML RPC call failed "+e.getMessage());
		}
	}
	
	public static void main(String[] args){
		TestAPI tapi=new TestAPI();
		VM vmInstance = null;
		
		tapi.vmStop(11);
//		List<VM.Record> records = tapi.getTemplateVMs();
//		for(VM.Record record: records){
//			if(record.nameLabel.contains("CentOS")){
//				System.out.println(record.toString());
//			}
//		}
//		Host selectedHost = tapi.getHost("128.250.33.135");
//		vmInstance = tapi.vmStart(selectedHost);
//		tapi.printMACs(vmInstance);
		
//		System.out.println(tapi.getTemplateVMs());
//		Host selectedHost = tapi.getHost("128.250.33.135");
//		Host migrateHost = tapi.getHost("128.250.33.132");
//		
//		if(selectedHost != null)
//			vmInstance = tapi.vmStart(selectedHost);
//		if(vmInstance != null)
//			tapi.vmMigrate(vmInstance, migrateHost);
//		tapi.getInfo();
	}
	
	private void printMACs(VM vmInstance) {
		try {
			Set<VIF> vifs = vmInstance.getVIFs(xenConnection);
			for(VIF vif:vifs){
				String mac = vif.getMAC(xenConnection);
				System.out.println(mac);
			}
		} catch (XenAPIException ex) {
            Logger.getLogger(TestAPI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Host getHost(String address){
		Set<Host> hosts = null;
		Host vhost = null;
		try { 
			
			hosts = Host.getAll(xenConnection);
			for(Iterator<Host> it = hosts.iterator(); 
				it.hasNext();){
				vhost = it.next();
				String ip = vhost.getAddress(xenConnection);
//				String hostn = vhost.getHostname(xenConnection);
				if(ip.contains(address)){
					System.out.println("got host "+address);
					break;
				}
			}
			return vhost;
		} catch (BadServerResponse e) {
			System.err.println("got bad response "+e.getMessage());
		} catch (XmlRpcException e) {
			System.err.println("got exception "+e.getMessage());
		} finally{
			return vhost;
		}
	}
	
	public List<VM.Record> getTemplateVMs(){
		Map<VM, Record> vmRecordMap = null;
		List<VM.Record> records = new ArrayList<VM.Record>();
		
		try{
			vmRecordMap = VM.getAllRecords(xenConnection);
			Set<VM> keyset = vmRecordMap.keySet();
			for(VM vm: keyset){
				VM.Record record = vmRecordMap.get(vm);
				if(record.isATemplate){
					records.add(record);
				}
			}
			
		} catch (XenAPIException ex) {
            Logger.getLogger(TestAPI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (XmlRpcException e) {
			System.err.println("Xen RPC call failed "+e.getMessage());
		} 
		return records;
	}
	
	public VM vmStart(Host host){
		VM vmInst = null;
		Set<VM> vmSet = null;
		VM.Record vmr = null;
		VmPowerState vpower = VmPowerState.UNRECOGNIZED;
		try{
			vmSet = VM.getAll(xenConnection);
			Iterator<VM> vmit = vmSet.iterator();
			while(vmit.hasNext()){
				vmInst = vmit.next();
				vmr = vmInst.getRecord(xenConnection);
				if((vmr.powerState == VmPowerState.HALTED) && 
						!(vmr.isATemplate) && (vmr.nameLabel.contains("CentOS"))){
					System.out.println(vmr);
					vmInst.setMemoryStaticMax(xenConnection, (long)512*1024*1024);
					vmInst.setMemoryDynamicMax(xenConnection, (long)512*1024*1024);
					vmInst.setMemoryDynamicMin(xenConnection, (long)256*1024*1024);
					//vmInst.setVCPUsAtStartup(xenConnection, (long)2);
					vmInst.startOnAsync(xenConnection, host, false, false);
					//vmInst.startAsync(xenConnection, false, false);
					do{
						System.out.println("Booting..");
						Thread.sleep(10000);
						vpower = vmInst.getPowerState(xenConnection);
						System.out.println("Vm state is "+vpower.toString());
					}while(vpower != VmPowerState.RUNNING && vpower != VmPowerState.HALTED && 
						vpower != VmPowerState.PAUSED);
					vmr=vmInst.getRecord(xenConnection);
					System.out.println("Booted "+vmr);
					break;
				}
			}
			return vmInst;
			
		}catch(Types.BadServerResponse e){
			System.err.println("Server did not like "+e.getMessage());
		} catch (XmlRpcException e) {
			System.err.println("Xen RPC call failed "+e.getMessage());
		} catch (VmBadPowerState e) {
			System.err.println("vm not in the power state "+e.getMessage());
		} catch (VmIsTemplate e) {
			System.err.println("vm is a template "+e.getMessage());
		} catch (OtherOperationInProgress e) {
			System.err.println("other operation in progress "+e.getMessage());
		} catch (OperationNotAllowed e) {
			System.err.println("operation not allowed "+e.getMessage());
		} catch (BootloaderFailed e) {
			System.err.println("boot loader failed "+e.getMessage());
		} catch (UnknownBootloader e) {
			System.err.println("unknown bootloader "+e.getMessage());
		} catch (InterruptedException e) {
			System.err.println("Sleep interrupted "+e.getMessage());
		} finally{
			return vmInst;
		}
	}
	
	public void vmMigrate(VM vmInst, Host host){
		HashMap<String, String> options = new HashMap<String,String>();
		options.put("live", "true");
		
		try{
			vmInst.poolMigrate(xenConnection, host, options);
		} catch(XmlRpcException e) {
			System.err.println("Xen RPC call failed "+e.getMessage());
		} catch (XenAPIException e){
			System.err.println("Server did not like "+e.getMessage());
        }
	}
	
	public void vmStop(int id){
		VM vmInst = null;
		Set<VM> vmSet = null;
		VM.Record vmr = null;
		
		try{
			vmSet = VM.getAll(xenConnection);
			Iterator<VM> vmit = vmSet.iterator();
			while(vmit.hasNext()){
				vmInst = vmit.next();
				vmr = vmInst.getRecord(xenConnection);
				if((vmr.powerState == Types.VmPowerState.RUNNING) && (vmr.domid == id)){
					System.out.println("MUHAHAHAHA Going to shutdown "+vmr.uuid+" "+vmr.nameDescription);
					vmInst.cleanShutdown(xenConnection);
				}
			}
			
		}catch(XenAPIException e){
			System.err.println("Something went wrong "+e.getMessage());
		} catch (XmlRpcException e) {
			System.err.println("Xen RPC call failed "+e.getMessage());
		} 
	}
	
	public void getInfo(){		
		VM vmInst = null;
		Set<VM> vmSet = null;
		VM.Record vmr = null;
		
		try{
			vmSet = VM.getAll(xenConnection);
			Iterator<VM> vmit = vmSet.iterator();
			while(vmit.hasNext()){
				vmInst = vmit.next();
				vmr = vmInst.getRecord(xenConnection);
				System.out.println(vmr.uuid+" "+vmr.domid+" "+vmr.powerState.toString());
				if(vmr.powerState == Types.VmPowerState.RUNNING){
					System.out.println(vmr.toString());
				}
			}
			
		}catch(XenAPIException e){
			System.err.println("Something went wrong "+e.getMessage());
		} catch (XmlRpcException e) {
			System.err.println("Xen RPC call failed "+e.getMessage());
		}
		
			
	}
}
