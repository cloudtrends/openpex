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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unimelb.openpex.ClusterNode;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.PexOperationFailedException;
import org.unimelb.openpex.xen.XenDispatcher;
import org.unimelb.openpex.xen.XenVMInstance;

import com.xensource.xenapi.Host;
import com.xensource.xenapi.VM;
import java.util.Calendar;
import org.unimelb.openpex.ResourceManager;
import org.unimelb.openpex.VMListener.VMStatus;
import org.unimelb.openpex.reservation.InstanceType;
import org.unimelb.openpex.reservation.ReservationEntity;
import org.unimelb.openpex.storage.PexStorage;
import org.unimelb.openpex.xen.XenClusterNode;

public class TestDispatcher2 {

    static Logger logger = Logger.getLogger(TestDispatcher2.class.getName());
    static XenDispatcher dispatcher = null;
    static PexStorage store = null;
    
    public static void main(String[] args){
        try {
            ResourceManager rm = ResourceManager.getInstance();
            dispatcher = XenDispatcher.getInstance();
            store = PexStorage.getInstance();
            testStart();
            Thread.sleep(70000);
//            testDelete();
//            Thread.sleep(60000);
//            String uuid = printVMIds("William 1");
//            testMigrate(uuid, "128.250.33.132");
//            String uuid2 = printVMIds("William 2");
//            testMigrate(uuid2, "128.250.33.135");
            dispatcher.shutdown();
        } catch (InterruptedException ex) {
            Logger.getLogger(TestDispatcher2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PexException e2) {
            System.err.println("Fail " + e2);
            System.exit(-1);
        }
    }
    
    private static void testStart() {
        
        XenVMInstance xvm = null;
        Host newHost = null;
        ClusterNode node = null;
        List<VM.Record> records = null;

        try {
            newHost = dispatcher.getHost("128.250.33.132");
            node = new XenClusterNode("pris", "128.250.33.132");
            records = dispatcher.getTemplateVMs();
        } catch (PexOperationFailedException e2) {
            System.err.println("Fail " + e2);
            System.exit(-1);
        } catch (PexException e) {
            System.err.println("Fail " + e);
            System.exit(-1);
        }

        for (VM.Record record : records) {
            //System.out.println(record.toString());
            if (record.nameLabel.contains("PEX Debian Etch") && record.isATemplate) {
                logger.info(record.uuid);
//				Map<String, String> newMap = new HashMap<String,String>();
//				newMap.put("mac", "ee:0f:95:02:23:4e");
                //record.HVMShadowMultiplier = null;

                try {
                    xvm = new XenVMInstance();
                    xvm.setRecord(record);
                    ReservationEntity one = new ReservationEntity("lolz");
                    one.setType(InstanceType.SMALL);
                    one.setStartTime(Calendar.getInstance().getTime());
                    one.setEndTime(Calendar.getInstance().getTime());
                    one.setUserid((short)1);
                    store.saveReservation(one);
                    
                    String uuid = dispatcher.vmCreatefromTemplate(xvm, record.nameLabel, "Pex Clone 3");
                    Thread.sleep(10000);
                    xvm.setVmID(uuid);
                    xvm.setName("Pex Clone 3");
                    xvm.setStart_time(Calendar.getInstance().getTime());
                    xvm.setEnd_time(Calendar.getInstance().getTime());
                    xvm.setReservation(one);
                    xvm.setClusterNode(node);
                    dispatcher.addInterfaceToVm(xvm, "42:b9:be:a4:dc:22");
                    Thread.sleep(10000);
                    
                    try {
                        xvm.setClusterNode(node);
                        xvm.startInstance();
                    } catch (PexException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Thread.sleep(90000);
                    
//                    try{
//                        dispatcher.addInterfaceToVm(xvm, "ee:0f:95:02:d3:4e");
//                        xvm.rebootInstance();
//                        Thread.sleep(120000);
//                    }catch(PexException e){
//                        System.exit(-1);
//                    }
                    
                    String ipAdd = dispatcher.getIpAddress(uuid);
                    System.out.println("Ip Adrress is "+ipAdd);
                 
                } catch (PexException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }

        }
    }
    
    private static String printVMIds(String VMname){
        logger.info("Searching through Veeyumz..");
        String uuid = "";
        try {
            List<VM.Record> vms = dispatcher.getAllVMRecords();
            for(VM.Record record: vms){
                if (!record.isATemplate && record.nameLabel.contains(VMname)){
                    System.out.println(" UUID "+record.uuid);
                    uuid = record.uuid;
                    break;
                }
            }
        } catch (PexOperationFailedException ex) {
            Logger.getLogger(TestDispatcher2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uuid;
        
    }
    
    private static void testMigrate(String uuid, String host2) throws PexException{
        ReservationEntity one = new ReservationEntity("lol2");
        one.setType(InstanceType.SMALL);
        one.setStartTime(Calendar.getInstance().getTime());
        one.setEndTime(Calendar.getInstance().getTime());
        one.setUserid((short) 1);
        store.saveReservation(one);
        
        logger.info("Constructing VMinstance ..");
        XenVMInstance xvm = new XenVMInstance();
        xvm.setVmID(uuid);
        xvm.setRecord(dispatcher.getRecordForVM(uuid));
        xvm.setName("Pex Clone 3");
        xvm.setStart_time(Calendar.getInstance().getTime());
        xvm.setEnd_time(Calendar.getInstance().getTime());
        xvm.setReservation(one);
        store.saveVM(xvm);
        
        XenClusterNode node = new XenClusterNode("host2", host2);
        xvm.migrateTo(node);
    }
 
    private static void testDelete() throws PexException {
        ReservationEntity one = new ReservationEntity("lol2");
        one.setType(InstanceType.SMALL);
        one.setStartTime(Calendar.getInstance().getTime());
        one.setEndTime(Calendar.getInstance().getTime());
        one.setUserid((short) 1);
        store.saveReservation(one);

        logger.info("Searching through Veeyumz..");
        List<VM.Record> vms = dispatcher.getAllVMRecords();
        String uuid = "";
        for (VM.Record vm : vms) {
            if (!vm.isATemplate && vm.nameLabel.contains("Pex Clone 3")) {
                uuid = vm.uuid;
                XenVMInstance xvm = new XenVMInstance();
                xvm.setVmID(uuid);
                xvm.setRecord(dispatcher.getRecordForVM(uuid));
                xvm.setName("Pex Clone 3");
                xvm.setStart_time(Calendar.getInstance().getTime());
                xvm.setEnd_time(Calendar.getInstance().getTime());
                xvm.setReservation(one);
                store.saveVM(xvm);
                xvm.updateStatus(dispatcher.translateStatus(xvm.getRecord().powerState));
                
                if (xvm.getStatus() != VMStatus.HALTED) {
                    try {
                        xvm.stopInstance();
                        Thread.sleep(30000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TestDispatcher2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                xvm.deleteInstance();
            }
        }
    }
}
