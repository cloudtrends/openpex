package org.unimelb.openpex;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.unimelb.openpex.Constants.ReservationStatus;
import org.unimelb.openpex.VMListener.VMStatus;
import org.unimelb.openpex.command.CommandQueue;
import org.unimelb.openpex.command.PexCommand;
import org.unimelb.openpex.command.VMShutdownCommand;
import org.unimelb.openpex.reservation.PexReservationFailedException;
import org.unimelb.openpex.reservation.ReservationEntity;
import org.unimelb.openpex.reservation.ReservationManager;
import org.unimelb.openpex.reservation.ReservationProposal;
import org.unimelb.openpex.reservation.ReservationReply;
import org.unimelb.openpex.reservation.SimpleReservationManager;
import org.unimelb.openpex.storage.PexStorage;
import org.unimelb.openpex.storage.PexStorageFailedException;
import org.unimelb.openpex.xen.XenDispatcher;
import org.unimelb.openpex.xen.XenVMInstance;


public class ResourceManager extends ReservationManager {

    static Logger logger = Logger.getLogger(ResourceManager.class.getName());
    private static ResourceManager manager = null;
    private NodeMonitor nodeMonitor = null;
    private VMMonitor vmMonitor = null;
    private PexStorage store = null;
    private XenDispatcher dispatcher = null;
    private ConcurrentLinkedQueue<String> macList = null;
    private long identifier = 0;
    private ReservationManager reservationMgr = null;

    private ResourceManager() throws PexException {
        dispatcher = XenDispatcher.getInstance();
        nodeMonitor = NodeMonitor.getInstance();
        vmMonitor = VMMonitor.getInstance();
        store = PexStorage.getInstance();
//        users = new ArrayList<VmUser>();
//        reserveMap = new Hashtable<String, ReservationEntity>();
        Random rand = new Random();
        identifier = rand.nextLong();
        reservationMgr = new SimpleReservationManager();

    }

    public static ResourceManager getInstance() throws PexException {
        logger.info("Getting instance");

        if (manager == null) {
            manager = new ResourceManager();
        }
        return manager;
    }

    /**
     * @deprecated
     * @param reservationID
     * @param name
     * @return
     * @throws org.unimelb.pex.PexException
     */
    public String createVM(String reservationID, String name)
            throws PexException {
        /*
         * TODO First, Check if reservation is valid
         * TODO Then, check if all the VMs for that reservation have started
         * Create the VM on it
         * return to user
         */
        ReservationEntity record = store.getReservation(reservationID);
        if (record == null) {
            logger.severe("Oh noes! Reeserwayshun duz nawt exist!");
            throw new PexOperationFailedException("Reservation not found, cannot start vm");
        }

        if (record.getStatus() != ReservationStatus.CONFIRMED) {
            logger.severe("Oh noes! Reeserwayshun ees neither Confirmed nor Activated");
            throw new PexOperationFailedException("Reservation is not confirmed or activated");
        }

        Calendar startTime = Calendar.getInstance();
        startTime.setTime(record.getStartTime());

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(record.getEndTime());

        Calendar now = Calendar.getInstance();
        /*
         * Srikumar- 15/6/9 - commented below code to allow for VMs to be created
         * before the start time of the reservation
         */
//        if (now.before(startTime)) {
//            logger.severe("Oh noes! Reeserwayshun duz nawt start until " + record.getStartTime().getTime());
//            throw new PexOperationFailedException("Starting before reservation start time");
//        }

        if (now.after(endTime) || record.getStatus() == ReservationStatus.EXPIRED) {
            logger.severe("Oh noes! Reeservayshun oreddy pushing up the daisies");
            throw new PexOperationFailedException("Reservation expired");
        }

        String template = record.getTemplate();
        if (template.equals("")) {
            logger.severe("Oh noes! Template string empty");
            throw new PexOperationFailedException("template string empty");
        }
        VMInstance vmInst = new XenVMInstance();
        vmInst.setStatus(VMStatus.NOT_PROVISIONED);
        String mac = this.getMacList().poll();
        if (mac == null)
            logger.info("Run out of macs... VM will autogenerate macs");
        String id = vmInst.create(template, name, mac);
        vmInst.setVmID(id);
        vmInst.setName(name);
        vmInst.setReservation(record);
        vmInst.setUserID(record.getUserid());
        vmInst.setStart_time(Calendar.getInstance().getTime());
        vmInst.setEnd_time(endTime.getTime());
        vmInst.registerListener(vmMonitor);
        store.saveVM(vmInst);
        record.setStatus(ReservationStatus.ACTIVATED);
        store.saveReservation(record);
        
        return id;
    }

    /**
     *
     * @param reservationID
     * @param name
     * @param autoStart
     * @return
     * @throws org.unimelb.pex.PexException
     */
    public String createVMInstance(final String reservationID, String name, boolean autoStart)
            throws PexException {
        /*
         * TODO First, Check if reservation is valid
         * TODO Then, check if all the VMs for that reservation have started
         * Create the VM on it
         * return to user
         */
        ReservationEntity record = store.getReservation(reservationID);
        if (record == null) {
            logger.severe("Oh noes! Reeserwayshun duz nawt exist!");
            throw new PexOperationFailedException("Reservation not found, cannot start vm");
        }

        if (record.getStatus() != ReservationStatus.CONFIRMED) {
            logger.severe("Oh noes! Reeserwayshun ees neither Confirmed nor Activated");
            throw new PexOperationFailedException("Reservation is not confirmed or activated");
        }

        Calendar startTime = Calendar.getInstance();
        startTime.setTime(record.getStartTime());

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(record.getEndTime());

        Calendar now = Calendar.getInstance();
        /*
         * Srikumar- 15/6/9 - commented below code to allow for VMs to be created
         * before the start time of the reservation
         */
//        if (now.before(startTime)) {
//            logger.severe("Oh noes! Reeserwayshun duz nawt start until " + record.getStartTime().getTime());
//            throw new PexOperationFailedException("Starting before reservation start time");
//        }

        if (now.after(endTime) || record.getStatus() == ReservationStatus.EXPIRED) {
            logger.severe("Oh noes! Reeservayshun oreddy pushing up the daisies");
            throw new PexOperationFailedException("Reservation expired");
        }

        String template = record.getTemplate();
        if (template.equals("")) {
            logger.severe("Oh noes! Template string empty");
            throw new PexOperationFailedException("template string empty");
        }
        VMInstance vmInst = new XenVMInstance();
        vmInst.setStatus(VMStatus.NOT_PROVISIONED);
        String mac = this.getMacList().poll();
        if (mac == null)
            logger.info("Run out of macs... VM will autogenerate macs");
        String id = vmInst.create(template, name, mac);
        vmInst.setVmID(id);
        vmInst.setName(name);
        vmInst.setReservation(record);
        vmInst.setUserID(record.getUserid());
        vmInst.setStart_time(Calendar.getInstance().getTime());
        vmInst.setEnd_time(endTime.getTime());
        vmInst.registerListener(vmMonitor);
        store.saveVM(vmInst);

        final String vmID = id;
        if(autoStart){
            long delay=0;
            if (now.before(startTime))
                delay = startTime.getTimeInMillis() - now.getTimeInMillis();
                CommandQueue.getInstance().addCommand(new PexCommand(){

                @Override
                public void execute() throws Exception {
                    startVMInstance(reservationID, vmID);
                }

                }, delay);
        }

        record.setStatus(ReservationStatus.ACTIVATED);
        store.saveReservation(record);

        return id;
    }

    /**
     * @deprecated
     * @param reservationID
     * @param vmID
     * @param listener
     * @throws org.unimelb.pex.PexOperationFailedException
     * @throws org.unimelb.pex.storage.PexStorageFailedException
     */
    public void startVM(String reservationID, String vmID, VMListener listener)
            throws PexOperationFailedException, PexStorageFailedException {
        /*
         *First, first check if reservation ID is valid 
         *then check if the VM exists, if so get the instance
         *After done, return IP to the user.
         */
        ReservationEntity record = store.getReservation(reservationID);
        if (record == null) {
            logger.severe("Oh noes! Reeserwayshun duz nawt exist!");
            throw new PexOperationFailedException("Reservation not found, cannot start vm");
        }

        if (record.getStatus() != ReservationStatus.CONFIRMED && record.getStatus() != ReservationStatus.ACTIVATED) {
            logger.severe("Oh noes! Reeserwayshun ees neither Confirmed nor Activated");
            throw new PexOperationFailedException("Reservation is not confirmed or activated");
        }

        if (record.getStatus() == ReservationStatus.ACTIVATED &&
                record.getActivatedInstances() >= (record.getNumInstancesFixed()+record.getNumInstancesOption())) {
            logger.severe("Oh noes! Already started enough VMs for reservation " + reservationID);
            throw new PexOperationFailedException("Reservation already activated with enough instances");
        }

        Calendar startTime = Calendar.getInstance();
        startTime.setTime(record.getStartTime());
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(record.getEndTime());

        Calendar now = Calendar.getInstance();
        if (now.before(record.getStartTime())) {
            logger.severe("Oh noes! Reeserwayshun duz nawt start until " + record.getStartTime().getTime());
            throw new PexOperationFailedException("Starting before reservation start time");
        }

        if (now.after(endTime) || record.getStatus() == ReservationStatus.EXPIRED) {
            logger.severe("Oh noes! Reeservayshun oreddy pushing up the daisies");
            throw new PexOperationFailedException("Reservation expired");
        }

        VMInstance vm = store.getVm(vmID);

        if (vm == null) {
            throw new PexOperationFailedException(
                    "Did not find VM Instance given by the reference " + vmID);
        }
        
        Set<ClusterNode> nodeSet = record.getNodes();
        
        if(nodeSet.size()<=0)
            throw new PexOperationFailedException("Oh noes!! no node associated with reservation "+ reservationID);
        
        try {
            ClusterNode node = nodeSet.iterator().next();
            vm.setClusterNode(node);
            vm.startInstance();
            vm.setStart_time(Calendar.getInstance().getTime());
            vm.setEnd_time(record.getEndTime());
            store.saveVM(vm);

            record.incrementInstances();
            store.saveReservation(record);
            long delay = endTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
            CommandQueue.getInstance().addCommand(new VMShutdownCommand(vmID), delay);

        } catch (PexOperationFailedException ex) {
            logger.severe("starting vm failed " + ex);
            throw new PexOperationFailedException("VM start failed ", ex);
        }

    }

    /**
     *
     * @param reservationID
     * @param vmID
     * @param listener
     * @throws org.unimelb.pex.PexOperationFailedException
     * @throws org.unimelb.pex.storage.PexStorageFailedException
     */
    public void startVMInstance(String reservationID, String vmID)
            throws PexOperationFailedException, PexStorageFailedException {
        /*
         *First, first check if reservation ID is valid
         *then check if the VM exists, if so get the instance
         *After done, return IP to the user.
         */
        ReservationEntity record = store.getReservation(reservationID);
        if (record == null) {
            logger.severe("Oh noes! Reeserwayshun duz nawt exist!");
            throw new PexOperationFailedException("Reservation not found, cannot start vm");
        }

        if (record.getStatus() != ReservationStatus.CONFIRMED && record.getStatus() != ReservationStatus.ACTIVATED) {
            logger.severe("Oh noes! Reeserwayshun ees neither Confirmed nor Activated");
            throw new PexOperationFailedException("Reservation is not confirmed or activated");
        }

        if (record.getStatus() == ReservationStatus.ACTIVATED &&
                record.getActivatedInstances() >= (record.getNumInstancesFixed()+record.getNumInstancesOption())) {
            logger.severe("Oh noes! Already started enough VMs for reservation " + reservationID);
            throw new PexOperationFailedException("Reservation already activated with enough instances");
        }

        Calendar startTime = Calendar.getInstance();
        startTime.setTime(record.getStartTime());
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(record.getEndTime());

        Calendar now = Calendar.getInstance();
        if (now.before(record.getStartTime())) {
            logger.severe("Oh noes! Reeserwayshun duz nawt start until " + record.getStartTime().getTime());
            throw new PexOperationFailedException("Starting before reservation start time");
        }

        if (now.after(endTime) || record.getStatus() == ReservationStatus.EXPIRED) {
            logger.severe("Oh noes! Reeservayshun oreddy pushing up the daisies");
            throw new PexOperationFailedException("Reservation expired");
        }

        VMInstance vm = store.getVm(vmID);

        if (vm == null) {
            throw new PexOperationFailedException(
                    "Did not find VM Instance given by the reference " + vmID);
        }

        Set<ClusterNode> nodeSet = record.getNodes();

        if(nodeSet.size()<=0)
            throw new PexOperationFailedException("Oh noes!! no node associated with reservation "+ reservationID);

        try {
            ClusterNode node = nodeSet.iterator().next();
            vm.setClusterNode(node);
            vm.startInstance();
            vm.setStart_time(Calendar.getInstance().getTime());
            vm.setEnd_time(record.getEndTime());
            store.saveVM(vm);

            record.incrementInstances();
            store.saveReservation(record);
            long delay = endTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
            CommandQueue.getInstance().addCommand(new VMShutdownCommand(vmID), delay);

        } catch (PexOperationFailedException ex) {
            logger.severe("starting vm failed " + ex);
            throw new PexOperationFailedException("VM start failed ", ex);
        }

    }

    public void stopVM(String reservationID, String vmID) throws PexOperationFailedException, PexStorageFailedException {
        /*
         * First, check if the reservation ID exists and if the VM is linked to the ID
         * then retrieve the VM instance and stop it.
         */
        VMInstance vm = store.getVm(vmID);

        if (vm == null) {
            throw new PexOperationFailedException(
                    "Did not find VM Instance given by the reference " + vmID);
        }
        try {
            vm.stopInstance();
            store.saveVM(vm);
        } catch (PexOperationFailedException ex) {
            logger.severe("VM Stopping failed " + ex);
            throw new PexOperationFailedException("VM stop oepration failed ");
        }
    }

    public List<String> getTemplateStrings() throws PexOperationFailedException {
        return dispatcher.getTemplateStrings();
    }

    public List<String> getPexTemplateStrings() throws PexOperationFailedException {
        List<String> templates = this.getTemplateStrings();
        List<String> filteredTemplates = new ArrayList<String>();

        for (String template : templates) {
            if (template.startsWith("PEX")) {
                filteredTemplates.add(template);
            }
        }

        return filteredTemplates;
    }

    public void statusChanged(VMInstance instance) {
        
    }

    public ConcurrentLinkedQueue<String> getMacList() {
        return macList;
    }

    protected void setMacList(List<String> macList) {
        this.macList = new ConcurrentLinkedQueue<String>(macList);
    }

    public long getUniqueIdentifier() {
        return reservationMgr.getUniqueIdentifier();
    }

    public String initiateReservation(short userid) throws PexReservationFailedException {
        return reservationMgr.initiateReservation(userid);
    }

    public ReservationReply requestReservation(String reservationID, ReservationProposal request) throws PexReservationFailedException {
        return reservationMgr.requestReservation(reservationID, request);
    }

    public ReservationReply replyToCounter(String reservationID, ReservationReply reply) throws PexReservationFailedException {
        return reservationMgr.replyToCounter(reservationID, reply);
    }

    public ReservationReply confirmReservation(String reservationID, ReservationProposal proposal) throws PexReservationFailedException {
        return reservationMgr.confirmReservation(reservationID, proposal);
    }

    public ReservationReply deleteReservation(String reservationID) throws PexOperationFailedException {
        return reservationMgr.deleteReservation(reservationID);
    }
    
}
