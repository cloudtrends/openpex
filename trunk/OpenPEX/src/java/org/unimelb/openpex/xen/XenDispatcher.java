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
 * Title        :  XenDispatcher.java
 * Package      :  org.unimelb.pex
 * Project      :  ProvisioningXen
 * Description	:  Testing Xen API
 * Created on   :  May 1, 2008
 * Author	    :  Srikumar Venugopal (srikumar@cs.mu.oz.au)
 * 
 */
package org.unimelb.openpex.xen;

import com.xensource.xenapi.Types.XenAPIException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.PexOperationFailedException;
import org.unimelb.openpex.VMListener.VMStatus;

import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.HostMetrics;
import com.xensource.xenapi.Network;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.Task;
import com.xensource.xenapi.Types;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.Types.BadServerResponse;
import com.xensource.xenapi.Types.OperationNotAllowed;
import com.xensource.xenapi.Types.OtherOperationInProgress;
import com.xensource.xenapi.Types.SessionAuthenticationFailed;
import com.xensource.xenapi.Types.TaskStatusType;
import com.xensource.xenapi.Types.VmBadPowerState;
import com.xensource.xenapi.Types.VmIsTemplate;
import com.xensource.xenapi.Types.VmPowerState;
import com.xensource.xenapi.VIF;
import com.xensource.xenapi.VM.Record;
import com.xensource.xenapi.VMGuestMetrics;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XenDispatcher {

    static Logger logger = Logger.getLogger(XenDispatcher.class.getName());
    public static final int TASK_THREADS = 10;
    public static final int TASK_POLLTIME = 5000;
    public static final int IP_WAIT_TIME = 60000;
    private final ExecutorService xenTaskService;
    private static XenDispatcher dispatcher = null;
    private Matcher ipMatch = null;
    //private OperationsThread operations = null;
    /*
     * TODO : Change this to properties so that this code can be removed from the SVN.
     */
    private Connection xenConnection = null;

    private XenDispatcher() throws PexException {

        xenTaskService = Executors.newFixedThreadPool(TASK_THREADS);
//		operations = new OperationsThread();
//		new Thread(operations).start();
        try {
            InputStream properties = XenDispatcher.class.getResourceAsStream("pex.properties");
            Properties pexProperties = new Properties();
            pexProperties.load(properties);
            String username = pexProperties.getProperty("xen.username");
            String password = pexProperties.getProperty("xen.password");
            String hostname = pexProperties.getProperty("xen.url");
            

            logger.fine("Obtained properties");
            xenConnection = new Connection(hostname, username, password);
            logger.info("Opened connection with the xen manager");

            Session xenSession = Session.loginWithPassword(xenConnection, username, password, "1.2");
            logger.info("Opened session with xen manager.. Xen Dispatcher setup complete");
        } catch (MalformedURLException e) {
            logger.severe("URL not valid");
            throw new PexOperationFailedException("Connecting to Xen Manager failed " + e.getMessage());
        } catch (BadServerResponse e) {
            logger.severe("Server did not like msg " + e.getMessage());
            throw new PexOperationFailedException("Connecting to Xen Manager failed" + e.getMessage());
        } catch (SessionAuthenticationFailed e) {
            logger.severe("Cannot authenticate " + e.getMessage());
            throw new PexOperationFailedException("Connecting to Xen Manager failed", e);
        } catch (XmlRpcException e) {
            logger.severe("XML RPC call failed " + e.getMessage());
            throw new PexOperationFailedException("Connecting to Xen Manager failed", e);
        } catch (FileNotFoundException e) {
            logger.severe("Pex Properties file not found");
            throw new PexOperationFailedException("Pex Properties file not found", e);
        } catch (IOException e) {
            logger.severe("Pex Properties file could not be read");
            throw new PexOperationFailedException("Pex Properties file could not be read", e);
        }
    }

    public static XenDispatcher getInstance() throws PexException {
        if (dispatcher == null) {
            dispatcher = new XenDispatcher();
        }
        return dispatcher;
    }

    public Host getHost(String address) throws PexOperationFailedException {
        Set<Host> hosts = null;
        Host vhost = null;
        try {

            hosts = Host.getAll(xenConnection);
            for (Host host : hosts) {
                String ip = host.getAddress(xenConnection);
//				String hostn = vhost.getHostname(xenConnection);
                if (ip.contains(address)) {
                    logger.info("got host " + address);
                    vhost = host;
                    break;
                }
            }

        } catch (XenAPIException e) {
            logger.severe("got bad response " + e.getMessage());
            throw new PexOperationFailedException("GetHostOperation failed ", e);
        } catch (XmlRpcException e) {
            logger.severe("got exception " + e.getMessage());
            throw new PexOperationFailedException("GetHostOperation failed ", e);
        }
        return vhost;
    }

    public Host.Record getHostRecord(Host vhost) throws PexOperationFailedException {
        Host.Record record = null;
        try {
            record = vhost.getRecord(xenConnection);
        } catch (XenAPIException e) {
            logger.severe("Server returned bad response " + e);
            throw new PexOperationFailedException("GetHostRecord failed ", e);
        } catch (XmlRpcException e) {
            logger.severe("Wrong RPC Call/ Parameters " + e);
            throw new PexOperationFailedException("GetHostRecord failed ", e);
        }
        return record;
    }

    public HostMetrics getHostMetrics(Host vhost) throws PexOperationFailedException {
        HostMetrics metrics = null;
        try {
                metrics = vhost.getMetrics(xenConnection);

        } catch (XenAPIException e) {
            logger.severe("Server returned bad response " + e);
            throw new PexOperationFailedException("GetHostMetrics failed ", e);
        } catch (XmlRpcException e) {
            logger.severe("Wrong RPC Call/ Parameters " + e);
            throw new PexOperationFailedException("GetHostMetrics failed ", e);
        }
        return metrics;
    }

    public VM.Record getTemplateVM(String template) throws PexOperationFailedException {
        VM.Record record = null;
        List<VM.Record> records = getTemplateVMs();
        for (VM.Record temp : records) {
            if (temp.nameLabel.equals(template)) {
                record = temp;
                break;
            }
        }
        return record;
    }

    public List<VM.Record> getTemplateVMs() throws PexOperationFailedException {
        Map<VM, Record> vmRecordMap = null;
        List<VM.Record> records = new ArrayList<VM.Record>();

        try {
            vmRecordMap = VM.getAllRecords(xenConnection);
            Set<VM> keyset = vmRecordMap.keySet();
            for (VM vm : keyset) {
                VM.Record record = vmRecordMap.get(vm);
                if (record.isATemplate) {
                    records.add(record);
                }
            }

        } catch (XenAPIException e) {
            logger.severe("Server returned bad response " + e);
            throw new PexOperationFailedException("GetHostMetrics failed ", e);
        } catch (XmlRpcException e) {
            logger.severe("Server returned bad response " + e);
            throw new PexOperationFailedException("GetTemplateVMs failed ", e);
        }
        return records;
    }

    public List<String> getTemplateStrings() throws PexOperationFailedException {
        List<VM.Record> templates = getTemplateVMs();
        List<String> templateNames = new ArrayList<String>();
        for (VM.Record template : templates) {
            templateNames.add(template.nameLabel);
        }
        return templateNames;
    }

    public List<VM.Record> getAllVMRecords() throws PexOperationFailedException {
        Map<VM, Record> vmRecordMap = null;
        List<VM.Record> records = new ArrayList<VM.Record>();

        try {
            vmRecordMap = VM.getAllRecords(xenConnection);
            Set<VM> keyset = vmRecordMap.keySet();
            for (VM vm : keyset) {
                VM.Record record = vmRecordMap.get(vm);
                records.add(record);
            }

        } catch (XenAPIException e) {
            logger.severe("Server returned bad response " + e);
            throw new PexOperationFailedException("GetHostMetrics failed ", e);
        } catch (XmlRpcException e) {
            logger.severe("Server returned bad response " + e);
            throw new PexOperationFailedException("GetAllVMs failed ", e);
        }
        return records;
    }

    public String vmCreatefromRecord(final XenVMInstance xvm, String template, String mac, String newName) throws PexException {

        String uuid = "";
        try {
            final VM.Record record = getTemplateVM(template);
            logger.info("got vm template instance for " + template);

            if (record == null) {
                throw new PexException("Vm Record is null");
            }

//            Set<VIF> vifSet = record.VIFs;
//
//            if (vifSet.isEmpty()) {
//                logger.info("Oh noes!! There are no network interfaces associated with this template");
//                return null;
//            }
//
//            logger.info("Currently there are " + vifSet.size() + " interfaces associated with VM " + template);
            
            Set<VIF> vifSet = VIF.getAll(xenConnection);
            
            VIF vif = vifSet.iterator().next();
            VIF.Record vifRec = vif.getRecord(xenConnection);
            logger.info("Got vif " + vifRec.toString());
//            vif.setMAC(xenConnection, mac);

            VIF.Record vifRec2 = new VIF.Record();
            vifRec2.MAC = mac;
            vifRec2.MTU = vifRec.MTU;
            vifRec2.VM = VM.getByUuid(xenConnection, record.uuid);
            logger.info("Got vm ref");
            vifRec2.allowedOperations = vifRec.allowedOperations;
            vifRec2.currentOperations = vifRec.currentOperations;
            vifRec2.qosAlgorithmParams = vifRec.qosAlgorithmParams;
            vifRec2.qosSupportedAlgorithms = vifRec.qosSupportedAlgorithms;
            VIF vif2 = VIF.create(xenConnection, vifRec2);
            logger.info("Created new VIF with MAC "+mac);
            
            record.VIFs.add(vif2);
            record.nameLabel = newName;
            
            Future<String> handle2 = xenTaskService.submit(new Callable<String>() {

                public String call() throws Exception {
                    
                    final VM newVM = VM.create(xenConnection, record);
                    
                    logger.info("Got new vm clone with uuid " + newVM.getUuid(xenConnection));
                    Task provisionTask = newVM.provisionAsync(xenConnection);
                    TaskStatusType status = TaskStatusType.UNRECOGNIZED;
                    do {
                        logger.info("Provisioning..");
                        Thread.sleep(TASK_POLLTIME);
                        status = provisionTask.getStatus(xenConnection);
                        logger.info("Provision task state is " + status.toString());
                    } while (status != TaskStatusType.SUCCESS && status != TaskStatusType.FAILURE && status != TaskStatusType.CANCELLED);
                    logger.info("task status is " + status.toString());
                    VM.Record vmr = null;
                    if (status == TaskStatusType.SUCCESS) {
                        vmr = newVM.getRecord(xenConnection);
                        xvm.setRecord(vmr);
                        xvm.updateStatus(translateStatus(xvm.getRecord().powerState));
                        return vmr.uuid;
                    } else {

                        logger.severe("Failure in creating vm " + xvm.getName());
                        Set<String> errors = provisionTask.getErrorInfo(xenConnection);
                        for (String error : errors) {
                            logger.severe(error);
                        }
                        return null;
                    }
                }
            });

             uuid = handle2.get();
//            final Task createTask = VM.createAsync(xenConnection, record);
//            Future<Void> handle = xenTaskService.submit(new Callable<Void>() {
//
//                public Void call() throws Exception {
//                    TaskStatusType status = TaskStatusType.UNRECOGNIZED;
//                    do {
//                        logger.info("Creating..");
//                        Thread.sleep(TASK_POLLTIME);
//                        status = createTask.getStatus(xenConnection);
//                        logger.info("task state is " + status.toString());
//                    } while (status != TaskStatusType.SUCCESS &&
//                            status != TaskStatusType.FAILURE && status != TaskStatusType.CANCELLED);
//                    logger.info("task status is " + status.toString());
//                    return null;
//                }
//            });
        } catch (InterruptedException ex) {
            logger.severe("Future got interrupted " + ex);
        } catch (ExecutionException ex) {
            logger.severe("Something happened " + ex.getCause());
            throw new PexOperationFailedException("CreateVM failed ", ex.getCause());
        } catch (XenAPIException e) {
            logger.severe("An API call failed " + e);
            throw new PexOperationFailedException("CreateVM failed " + e);
        } catch (XmlRpcException e) {
            logger.severe("Xen RPC call failed " + e.getMessage());
            throw new PexOperationFailedException("CreateVM failed " + e);
        }

        return uuid;
    }

    public String vmCreatefromTemplate(final XenVMInstance xvm, String template, final String newName)
            throws PexOperationFailedException {


//    	final VM.Record record = vmInst.getRecord();
//
//        
        final VM.Record record = getTemplateVM(template);

        if (record == null) {
            throw new PexOperationFailedException("Template " + template + " not found");
        }

        /* TODO: Put in code to verify the cloning */
        /*Then provision the template */
        Future<String> handle2 = xenTaskService.submit(new Callable<String>() {

            public String call() throws Exception {
                VM vm = VM.getByUuid(xenConnection, record.uuid);
                logger.info("got vm template instance for " + record.uuid);
                final VM newVM = vm.createClone(xenConnection, newName);
                logger.info("Got new vm clone with uuid " + newVM.getUuid(xenConnection));
                Task provisionTask = newVM.provisionAsync(xenConnection);
                TaskStatusType status = TaskStatusType.UNRECOGNIZED;
                do {
                    logger.info("Provisioning..");
                    Thread.sleep(TASK_POLLTIME);
                    status = provisionTask.getStatus(xenConnection);
                    logger.info("Provision task state is " + status.toString());
                } while (status != TaskStatusType.SUCCESS && status != TaskStatusType.FAILURE && status != TaskStatusType.CANCELLED);
                logger.info("task status is " + status.toString());
                VM.Record vmr = null;
                if (status == TaskStatusType.SUCCESS) {
                    vmr = newVM.getRecord(xenConnection);
                    xvm.setRecord(vmr);
                    xvm.updateStatus(translateStatus(xvm.getRecord().powerState));
                    return vmr.uuid;
                } else {

                    logger.severe("Failure in creating vm " + xvm.getName());
                    Set<String> errors = provisionTask.getErrorInfo(xenConnection);
                    for (String error : errors) {
                        logger.severe(error);
                    }
                    return null;
                }
            }
        });

        String uuid = "";
        try {
            uuid = handle2.get();
        } catch (InterruptedException e) {
            logger.severe("Interrupted during creation ");
        } catch (ExecutionException e) {
            logger.severe("VM Creation failed " + e.getMessage());
            throw new PexOperationFailedException("VM Creation failed", e.getCause());
        }
        return uuid;


    }

    public void vmStart(final XenVMInstance vmInstance, String ipAddress) throws PexOperationFailedException {
        try {
            final VM vm = VM.getByUuid(xenConnection, vmInstance.getRecord().uuid);
            if (vm == null) {
                throw new PexOperationFailedException("There is no VM instance present");
            }
            
            final Boolean opSuccess;
            VM.Record vmr = vm.getRecord(xenConnection);
            if ((vmr.powerState == VmPowerState.HALTED) &&
                    !(vmr.isATemplate)) {
                Host host = getHost(ipAddress);
                String hostN = host.getHostname(xenConnection);
                logger.info("Starting vm "+vmr.nameLabel+" on node "+hostN);
                final Task startTask = vm.startOnAsync(xenConnection, host, false, false);
                Future<Void> handle = xenTaskService.submit(new Callable<Void>() {

                    public Void call() throws Exception {
                        TaskStatusType status = TaskStatusType.UNRECOGNIZED;
                        do {
                            logger.info("Booting..");
                            Thread.sleep(TASK_POLLTIME);
                            status = startTask.getStatus(xenConnection);
                            logger.info("task state is " + status.toString());
                        } while (status != TaskStatusType.SUCCESS && status != TaskStatusType.FAILURE && status != TaskStatusType.CANCELLED);
                        logger.info("start task status is " + status.toString());
                        
                        
                        if (status == TaskStatusType.SUCCESS) {
                           logger.info("VM in booting.. " + vmInstance.getName());
                            Thread.sleep(IP_WAIT_TIME);
                            logger.info("Trying to get IP address.. " + vmInstance.getName());
                            vmInstance.setRecord(vm.getRecord(xenConnection));
                            vmInstance.setIpAddress(dispatcher.getIpAddress(vmInstance.getRecord().uuid));
                            logger.info("Got IP address "+vmInstance.getIpAddress());
                            vmInstance.updateStatus(translateStatus(vmInstance.getRecord().powerState));
                        } else {
                            Set<String> errorSet = startTask.getErrorInfo(xenConnection);
                            logger.severe("oh noes 111 Epic failure in VM creation " + vmInstance.getVmID());
                            for (String error : errorSet) {
                                logger.severe(error);
                            }
                            vmInstance.updateStatus(VMStatus.HALTED);
                        }
                        return null;
                    }
                });
                
            } else {
                throw new PexOperationFailedException("VM is not stopped or is a template");
            }
        } catch (XenAPIException e) {
            logger.severe("An API call failed " + e.getMessage());
            throw new PexOperationFailedException("vmStartfailed ", e);
        } catch (XmlRpcException e) {
            logger.severe("Xen RPC call failed " + e.getMessage());
            throw new PexOperationFailedException("vmStartfailed ", e);
        }
    }

    public void vmMigrate(final XenVMInstance xvm, Host host) throws PexOperationFailedException {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("live", "true");

        try {
            final VM vm = VM.getByUuid(xenConnection, xvm.getVmID());
            if (vm == null) {
                throw new PexOperationFailedException("VM could not be found");
            }
            VM.Record record = vm.getRecord(xenConnection);
            Host currHost = record.residentOn;
            String currIP = currHost.getAddress(xenConnection);
            String nextIP = host.getAddress(xenConnection);
            if (currIP.equals(nextIP)) {
                throw new PexOperationFailedException("Attempting to migrate a vm to the same host");
            }
            if (record.powerState != VmPowerState.HALTED) {
                logger.info("Migrating VM from " + currIP + " to " + nextIP);
                final Task migrateTask = vm.poolMigrateAsync(xenConnection, host, options);
                xvm.updateStatus(VMStatus.UNDER_MIGRATION);
                Future<Void> handle = xenTaskService.submit(new Callable<Void>() {

                    public Void call() throws Exception {
                        TaskStatusType status = TaskStatusType.UNRECOGNIZED;
                        do {

                            logger.info("Migrating..");
                            Thread.sleep(TASK_POLLTIME);
                            status = migrateTask.getStatus(xenConnection);
                            logger.info("task state is " + status.toString());
                        } while (status != TaskStatusType.SUCCESS && status != TaskStatusType.FAILURE && status != TaskStatusType.CANCELLED);
                        logger.info("task status is " + status.toString());
                        if (status == TaskStatusType.SUCCESS) {
                            logger.info("Migration task successful ");
                        }else{
                            logger.severe("Failure in migrating vm "+xvm.getName());
                            Set<String> errors = migrateTask.getErrorInfo(xenConnection);
                            for(String error: errors)
                                logger.severe(error);
                        }
                        xvm.setRecord(vm.getRecord(xenConnection));
                        xvm.updateStatus(translateStatus(xvm.getRecord().powerState));

                        //TODO: Need some way of changing the clusternode instance in the xvm to the right instance.
                        return null;
                    }
                });
            }
        } catch (XmlRpcException e) {
            logger.severe("Xen RPC call failed " + e.getMessage());
            throw new PexOperationFailedException("vmMigrate failed ", e);
        } catch (XenAPIException e) {
            logger.severe("An API call failed " + e);
            throw new PexOperationFailedException("vmMigrate failed ", e);
        }
    }

    public void vmStop(final XenVMInstance xvm) throws PexOperationFailedException {

        VM.Record vmr = null;
        try {
            final VM vmInstance = VM.getByUuid(xenConnection, xvm.getVmID());
            if (vmInstance == null) {
                throw new PexOperationFailedException("VM cannot be found");
            }
            vmr = vmInstance.getRecord(xenConnection);
            if ((vmr.powerState != VmPowerState.HALTED)) {
                logger.info("MUHAHAHAHA Going to shutdown " + vmr.uuid + " " + vmr.nameDescription);
                final Task stopTask = vmInstance.cleanShutdownAsync(xenConnection);
                Future<Void> handle = xenTaskService.submit(new Callable<Void>() {

                    public Void call() throws Exception {
                        TaskStatusType status = TaskStatusType.UNRECOGNIZED;
                        do {
                            logger.info("Shutting Down");
                            Thread.sleep(TASK_POLLTIME);
                            status = stopTask.getStatus(xenConnection);
                            logger.info("task state is " + status.toString());
                        } while (status != TaskStatusType.SUCCESS && 
                                status != TaskStatusType.FAILURE && 
                                status != TaskStatusType.CANCELLED);
                        logger.info("task status is " + status.toString());
                        if (status == TaskStatusType.SUCCESS) {
                            logger.info("Stopping task successful ");
                        }else{
                            logger.severe("Failure in stoppinf vm "+xvm.getName());
                            Set<String> errors = stopTask.getErrorInfo(xenConnection);
                            for(String error: errors)
                                logger.severe(error);
                        }
                        xvm.setRecord(vmInstance.getRecord(xenConnection));
                        xvm.updateStatus(translateStatus(xvm.getRecord().powerState));
                        return null;
                    }
                });
            } else {
                throw new PexOperationFailedException("VM is already halted");
            }
        } catch (XenAPIException e) {
            logger.severe("An API call failed " + e);
            throw new PexOperationFailedException("vmStop failed ", e);
        } catch (XmlRpcException e) {
            logger.severe("Xen RPC call failed " + e.getMessage());
            throw new PexOperationFailedException("vmStop failed ", e);
        }
    }
    
    public void vmDelete(final XenVMInstance xvm) throws PexOperationFailedException{
        try {
            final VM vm = VM.getByUuid(xenConnection, xvm.getRecord().uuid);
            if (vm == null) {
                throw new PexOperationFailedException("VM cannot be found");
            }
            VM.Record vmr = vm.getRecord(xenConnection);
            
            if(vmr.isATemplate || vmr.nameLabel.contains("Base"))
                throw new PexOperationFailedException("Cannot delete this vm");
            
            if (vmr.powerState == VmPowerState.HALTED) {
                logger.info("MUHAHAHAHA Going to delete " + vmr.uuid + " " + vmr.nameLabel);
                final Task deleteTask = vm.destroyAsync(xenConnection);
                Future<Void> handle = xenTaskService.submit(new Callable<Void>() {

                    public Void call() throws Exception {
                        TaskStatusType status = TaskStatusType.UNRECOGNIZED;
                        do {
                            logger.info("Deleting Vm..");
                            Thread.sleep(TASK_POLLTIME);
                            status = deleteTask.getStatus(xenConnection);
                            logger.info("delete task state is " + status.toString());
                        } while (status != TaskStatusType.SUCCESS &&
                                status != TaskStatusType.FAILURE &&
                                status != TaskStatusType.CANCELLED);
                        logger.info("delete task status is " + status.toString());
                        if (status == TaskStatusType.SUCCESS) {
                            logger.info("Delete  task successful ");
                            xvm.setRecord(vm.getRecord(xenConnection));
                            xvm.updateStatus(translateStatus(xvm.getRecord().powerState));
                        } else {
                            logger.severe("Failure in deleting vm " + xvm.getName());
                            Set<String> errors = deleteTask.getErrorInfo(xenConnection);
                            for (String error : errors) {
                                logger.severe(error);
                            }
                        }

                        return null;
                    }
                });
            }else {
                throw new PexOperationFailedException("VM is not halted.. call stopVm() before this operation");
            }
        } catch (XenAPIException e) {
            logger.severe("An API call failed " + e);
            throw new PexOperationFailedException("vmDelete failed ", e); 
        } catch (XmlRpcException ex) {
            logger.severe("Xen RPC call failed " + ex.getMessage());
            throw new PexOperationFailedException("vmStop failed ", ex);
        }
    }
    
    public void vmReboot(final XenVMInstance xvm) throws PexOperationFailedException {
        try {
            final VM vm = VM.getByUuid(xenConnection, xvm.getRecord().uuid);

            if (vm == null) {
                throw new PexOperationFailedException("VM cannot be found");
            }

            VM.Record vmr = vm.getRecord(xenConnection);
            if (vmr.isATemplate) {
                throw new PexOperationFailedException("is a template ... Cannot reboot this vm");
            }
            if (vmr.powerState == VmPowerState.RUNNING) {
                logger.info("Going to reboot vm " + vmr.nameLabel);
                final Task rebootTask = vm.cleanRebootAsync(xenConnection);
                Future<Void> handle = xenTaskService.submit(new Callable<Void>() {

                    public Void call() throws Exception {
                        TaskStatusType status = TaskStatusType.UNRECOGNIZED;
                        do {
                            logger.info("Rebooting Vm..");
                            Thread.sleep(TASK_POLLTIME);
                            status = rebootTask.getStatus(xenConnection);
                            logger.info("delete task state is " + status.toString());
                        } while (status != TaskStatusType.SUCCESS &&
                                status != TaskStatusType.FAILURE &&
                                status != TaskStatusType.CANCELLED);
                        logger.info("reboot task status is " + status.toString());
                        if (status == TaskStatusType.SUCCESS) {
                            logger.info("Reboot task successful ");
                            xvm.setRecord(vm.getRecord(xenConnection));
                            xvm.updateStatus(translateStatus(xvm.getRecord().powerState));
                        } else {
                            logger.severe("Failure in rebooting vm " + xvm.getName());
                            Set<String> errors = rebootTask.getErrorInfo(xenConnection);
                            for (String error : errors) {
                                logger.severe(error);
                            }
                        }

                        return null;
                    }
                });
            }else {
                throw new PexOperationFailedException("VM is not running.. cant reboot");
            }

        } catch (VmBadPowerState ex) {
            throw new PexOperationFailedException("Reboot failed");
        } catch (OtherOperationInProgress ex) {
            throw new PexOperationFailedException("Reboot failed");
        } catch (OperationNotAllowed ex) {
            throw new PexOperationFailedException("Reboot failed");
        } catch (VmIsTemplate ex) {
            throw new PexOperationFailedException("Reboot failed");
        } catch (XenAPIException e) {
            logger.severe("An API call failed " + e);
            throw new PexOperationFailedException("vm Reboot failed ", e);
        } catch (XmlRpcException ex) {
            logger.severe("Xen RPC call failed " + ex.getMessage());
            throw new PexOperationFailedException("vm Reboot failed ", ex);
        }
    }

    public String getIpAddress(String uuid) throws PexOperationFailedException {
        String address = "";
        try {
            VM vm = VM.getByUuid(xenConnection, uuid);
            VMGuestMetrics metrics = vm.getGuestMetrics(xenConnection);
            Map<String, String> networkMap = metrics.getNetworks(xenConnection);
            logger.info("networks size" + networkMap.size());

            logger.info("Got networks " + networkMap.toString());
            String ipAdd = networkMap.toString();
            
            /*
             *The output is assumed to be of type :  {0/ip=128.250.33.183}
             *Code below parses the IP address out.
             */
            if(ipMatch == null){
                Pattern ipPattern = Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3}");
                ipMatch = ipPattern.matcher(ipAdd);
            } else {
                ipMatch.reset(ipAdd);
            }
            if(ipMatch.find())
                address = ipAdd.substring(ipMatch.start(), ipMatch.end());
            
            logger.info("got ip address "+address);
//            
//            /*
//             *First thing check the code below... 
//             */
//            Collection<String> ipAddresses = networkMap.keySet();
//            ipAdd = ((String[]) ipAddresses.toArray())[0];
////            ipAdd.
//            logger.info("Setting vm ip address " + ipAdd);
        } catch (Exception e) {
            logger.severe("Major barfage "+e);
            throw new PexOperationFailedException("Getting IP address failed",e);
        }
        return address;
    }
    
    public VM.Record getRecordForVM(String uuid){
        VM.Record vmr = null;
        try {
            VM vm = VM.getByUuid(xenConnection, uuid);
            vmr = vm.getRecord(xenConnection);
        } catch (XenAPIException e) {
            logger.severe("An API call failed " + e);
        } catch (XmlRpcException ex) {
            Logger.getLogger(XenDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vmr;
    }

    public void getInfo() {
        Set<VM> vmSet = null;
        VM.Record vmr = null;

        try {
            vmSet = VM.getAll(xenConnection);
            for (VM vmInst : vmSet) {
                vmr = vmInst.getRecord(xenConnection);
                logger.info(vmr.uuid + " " + vmr.domid + " " + vmr.powerState.toString());
                if (vmr.powerState == Types.VmPowerState.RUNNING) {
                    logger.info(vmr.toString());
                }
            }

        } catch (XenAPIException e) {
            logger.severe("An API call failed " + e);
        } catch (XmlRpcException e) {
            logger.severe("Xen RPC call failed " + e.getMessage());
        }

    }
    
    public void addInterfaceToVm(XenVMInstance xvm, String mac) throws PexOperationFailedException {
        try {

            final VM vm = VM.getByUuid(xenConnection, xvm.getRecord().uuid);
            VM.Record vmr = vm.getRecord(xenConnection);
//            if(vmr.powerState != VmPowerState.HALTED)
//                throw new PexOperationFailedException("VM must be halted before adding interface..");

            Set<VIF> vifSet = vm.getVIFs(xenConnection);
            if (vifSet.isEmpty()) {
                logger.info("Oh noes!! There are no network interfaces associated with this VM");
                return;
            }
            logger.info("Currently there are " + vifSet.size() + " interfaces associated with VM " + xvm.getName());

            VIF vif = vifSet.iterator().next();
            VIF.Record vifRec = vif.getRecord(xenConnection);
            logger.info("Got vif " + vifRec.toString());
//            vif.setMAC(xenConnection, mac);

            VIF.Record vifRec2 = new VIF.Record();
            vifRec2.MAC = mac;
            vifRec2.MTU = vifRec.MTU;
            vifRec2.VM = vifRec.VM;
            vifRec2.device = vifRec.device;
            vifRec2.allowedOperations = vifRec.allowedOperations;
            vifRec2.currentOperations = vifRec.currentOperations;
            vifRec2.network = vifRec.network;
            Network net = vifRec2.network;
            Set<VIF> netVifSet = net.getVIFs(xenConnection);
            netVifSet.remove(vif);
            
            vifRec2.qosAlgorithmParams = vifRec.qosAlgorithmParams;
            vifRec2.qosSupportedAlgorithms = vifRec.qosSupportedAlgorithms;
            vifRec2.runtimeProperties = vifRec.runtimeProperties;
            logger.info("Now destroying old VIF..");
            vif.destroy(xenConnection);
            logger.info("Now creating new VIF..");
            VIF vif2 = VIF.create(xenConnection, vifRec2);
            logger.info("The mac of new VIF is.." + vifRec2.MAC);
//            logger.info("Now unplugging old VIF..");
//            vif.unplug(xenConnection);
//            logger.info("Now plugging in new VIF..");
//            vif2.plug(xenConnection);
            
            
//            VIF.Record vifRec2 = vif.getRecord(xenConnection);
//            logger.info("The mac of new VIF is.."+vifRec2.MAC);
            
        
        } catch (XenAPIException e) {
            logger.severe("An API call failed " + e);
            throw new PexOperationFailedException("adding interface failed ", e);
        } catch (XmlRpcException ex) {
            Logger.getLogger(XenDispatcher.class.getName()).log(Level.SEVERE, null, ex);
            throw new PexOperationFailedException("adding interface failed ", ex);
        }
        
    }

    public void shutdown() throws InterruptedException {
        
        logger.info("Closing connection..");
        xenConnection.dispose();
        xenTaskService.shutdown();
        logger.info("Sent shutdown message to execution service");
        xenTaskService.awaitTermination(500, TimeUnit.MILLISECONDS);
        logger.info("exec service has been stopped ");
        // operations.interrupt();
        logger.info("Operations interrupted");
    }

    public VMStatus translateStatus(VmPowerState xenState) {
        VMStatus pexStatus = VMStatus.UNRECOGNIZED;
        switch (xenState) {
            case UNRECOGNIZED:
            case UNKNOWN:
                pexStatus = VMStatus.UNRECOGNIZED;
                break;
            case HALTED:
                pexStatus = VMStatus.HALTED;
                break;
            case RUNNING:
                pexStatus = VMStatus.RUNNING;
                break;
            case PAUSED:
                pexStatus = VMStatus.PAUSED;
                break;
            case SUSPENDED:
                pexStatus = VMStatus.SUSPENDED;
                break;
            default:
                pexStatus = VMStatus.UNRECOGNIZED;
        }
        return pexStatus;
    }
    
    private class OpSuccess{
        private boolean success;

        public OpSuccess() {
            success = false;
        }
        
        
        
        
    }
}

