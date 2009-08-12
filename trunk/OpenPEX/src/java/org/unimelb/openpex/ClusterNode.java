package org.unimelb.openpex;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.persistence.Temporal;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.unimelb.openpex.reservation.PexReservationFailedException;
import org.unimelb.openpex.reservation.ReservationEntity;

@Entity
@Table(name = "PEX_NODES")
public abstract class ClusterNode implements Serializable {

    /*length of the slot in ms. */
    static final long SLOT_INTERVAL = 5 * 60 * 1000;
    protected static Logger logger = Logger.getLogger(ClusterNode.class.getName());
    @Id
    @Column(name = "NODE_NAME", nullable = false)
    private String name;
    @Column(name = "IP_ADDRESS", nullable = false)
    private String ipAddress;
    @OneToMany(mappedBy = "clusterNode")
    private Set<VMInstance> vmSet;
    @Column(name = "AVAIL", nullable = false)
    private boolean available = false;
    @Column(name = "EPOCH", nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Calendar reservationEpoch;
    @ManyToMany(mappedBy = "nodes", fetch = FetchType.LAZY)
    private List<ReservationEntity> reservations;
    
    /*
     *Setting the max size of the timeslotmap in the mysql table to be 16 MB in bytes.
     */
    @Lob
    @Column(name="TREESLOTMAP", length=16777215)
    private TreeMap<Calendar, BitSet> timeSlotMap = null;
    private int allowed_vms = 0;

    public ClusterNode() {
    }

    public ClusterNode(String name, String ipAddress, Calendar epoch) throws PexException {
        this.name = name;
        /*
         * Create a bit map for mapping availability of vms on a single machine
         * 1 is when a slot is occupied and 0 when it is free. 
         * The slots are separated by SLOT_INTERVAL variable
         */
        timeSlotMap = new TreeMap<Calendar, BitSet>();
        if (epoch == null) {
            this.reservationEpoch = Calendar.getInstance();
        } else {
            this.reservationEpoch = epoch;
        }
        this.setIpAddress(ipAddress);
        logger.info(this.name + " The epoch for reservations is " + reservationEpoch.getTime().toString());
        timeSlotMap.put(reservationEpoch, new BitSet(allowed_vms));
        setAvailable(true);
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isFeasible(ReservationEntity record) {
        boolean result = true;

        logger.info(this.name + " Checking if reservation " + record.getRequestId() + " is feasible ");
//		Calendar[] boundaries = getBoundaries(record);
        Calendar newStart = Calendar.getInstance();
        newStart.setTime(record.getStartTime());
        Calendar newEnd = Calendar.getInstance();
        newEnd.setTime(record.getEndTime());

        logger.info(this.name + " Searching existing reservations from " + newStart.getTime().toString() + " to " + newEnd.getTime().toString());


        SortedMap<Calendar, BitSet> slots = timeSlotMap.subMap(newStart, newEnd);
        BitSet res = new BitSet(allowed_vms);
        /*
         * Check if enough CPUS are available across the duration. Or-ing
         * preserves the results of previous interval.
         */
        for (Calendar slot : slots.keySet()) {
            res.or(timeSlotMap.get(slot));
            logger.info(this.name + " No of vms upto slot " + slot.getTime() + " is " + res.cardinality());
            if ((res.cardinality() + record.getNumInstancesFixed()) > allowed_vms) {
                logger.info(this.name + " Found a block at interval " + slot.getTime().toString());
                result = false;
                logger.info(this.name + " Reservation " + record.getRequestId() + " is infeasible ");
                break;
            }
        }

        return result;
    }

    public synchronized Calendar[] getAlternateSlot(ReservationEntity record) {

        logger.info(this.name + " Finding alternate slot for reservation " + record.getRequestId());

        Calendar newStart = Calendar.getInstance();
        newStart.setTime(record.getStartTime());
        Calendar newEnd = Calendar.getInstance();
        newEnd.setTime(record.getEndTime());
        long duration = newEnd.getTimeInMillis() - newStart.getTimeInMillis();

        logger.info(this.name + " Searching for slots from " + newStart.getTime().toString() + " to " + newEnd.getTime().toString());

        BitSet res = new BitSet(allowed_vms);
        Calendar slot = null;
        boolean found = false;
        SortedMap<Calendar, BitSet> slots = timeSlotMap.subMap(newStart, newEnd);

        /*
         * Find an alternate slot. For this, traverse the slot list, until we come to a slot interval
         * that "blocks" the reservation, i.e., has reservations with more CPUS than the required
         * If blocked, then move to the next slot interval and continue traversing.
         * If not blocked until end time or the slotMap runs out, return the new interval to the user.
         */
        while (!found && !slots.isEmpty()) {
            boolean blocked = false;
            Iterator<Calendar> it = slots.keySet().iterator();
            while ((it.hasNext()) && (!blocked)) {
                slot = it.next();
                BitSet r = timeSlotMap.get(slot);
                res.or(r);
                if (res.cardinality() + record.getNumInstancesFixed() > allowed_vms) {
                    logger.info(this.name + " Found a block at interval " + slot.getTime().toString());
                    blocked = true;
                }

            }
            if (blocked) {
                newStart = (Calendar) slot.clone();
                newStart.add(Calendar.SECOND, (int) (SLOT_INTERVAL / 1000));
                newEnd = (Calendar) slot.clone();
                newEnd.add(Calendar.SECOND, (int) ((duration + SLOT_INTERVAL) / 1000));
                logger.info(this.name + " Searching again for slots from " + newStart.getTime().toString() + " to " + newEnd.getTime().toString());
                slots = timeSlotMap.subMap(newStart, newEnd);
                res.clear();
            } else {
                found = true;
            }
        }
        
        Calendar[] result = new Calendar[]{newStart,newEnd};
        return result;
    }

    public synchronized boolean commitReservation(ReservationEntity record) throws PexReservationFailedException {
        boolean result = false;

        logger.info(this.name + " Committing reservation " + record.getRequestId());

//		Calendar boundaries[] = getBoundaries(record);

        Calendar newStart = Calendar.getInstance();
        newStart.setTime(record.getStartTime());
        Calendar newEnd = Calendar.getInstance();
        newEnd.setTime(record.getEndTime());

        logger.info(this.name + " Committing reservation from " + newStart.getTime().toString() + " to " + newEnd.getTime().toString() + " for " + record.getNumInstancesFixed() + " cpus ");

        /*
         * To cover the case if the reservation is far into the future, then create
         * slots in the slotmap and mark them as empty.
         */
        Calendar preSlot = timeSlotMap.lastKey();
        while (preSlot.before(newStart)) {
            BitSet map = new BitSet(allowed_vms);
            logger.info(this.name + " Adding slot into map for " + preSlot.getTime() + " " + map.cardinality());
            timeSlotMap.put((Calendar) preSlot.clone(), map);
            preSlot.add(Calendar.SECOND, (int) (SLOT_INTERVAL / 1000));
        }

        /*
         * Now find the slots in the map that intersect with the reservatuion
         */
        SortedMap<Calendar, BitSet> slots = timeSlotMap.subMap(newStart, newEnd);

        for (Calendar slot : slots.keySet()) {
            BitSet res = timeSlotMap.get(slot);
            logger.info(this.name + " Found slot at " + slot.getTime().toString());
            if (res.cardinality() + record.getNumInstancesFixed()*record.getType().getNumCPU() > allowed_vms) {
                logger.severe(this.name + " Oh noes! Commit failed as the slot is already blocked");
                throw new PexReservationFailedException("Failed commit");
            }
            int index = res.nextClearBit(0);
            /*
             * 22/9 Currently, we consider that each reservation has atleast one CPU.
             * Set a bit for each CPU (core) that is requested by the reservation.  
             */
            res.set(index, index + record.getNumInstancesFixed()*record.getType().getNumCPU());
            logger.info(this.name + " Number of cores set at " + slot.getTime().toString() + " is " + res.cardinality());
        }

        /*
         * If there aren't enough slot intervals in the map then add new ones until the 
         * end time of the reservation. Set as many bits as the number of CPUs requested
         * by the reservation.
         */
        Calendar newSlot = (Calendar) slots.lastKey().clone();

        newSlot.add(Calendar.SECOND, (int) (SLOT_INTERVAL / 1000));
        while (newSlot.before(newEnd)) {
            BitSet map = new BitSet(allowed_vms);
            map.set(0, record.getNumInstancesFixed(), true);
            logger.info(this.name + " Adding slot into map for " + newSlot.getTime() + " " + map.cardinality());
            timeSlotMap.put((Calendar) newSlot.clone(), map);
            newSlot.add(Calendar.SECOND, (int) (SLOT_INTERVAL / 1000));
        }
        reservations.add(record);
        result = true;

        return result;
    }

//    private Calendar[] getBoundaries(ReservationRecord record) {
//        /*
//         * Find the slot interval boundaries for the requested reservation. 
//         */
//        Calendar newStart = (Calendar) record.getStartTime().clone();
//        Calendar newEnd = (Calendar) record.getStartTime().clone();
//        newEnd.add(Calendar.MILLISECOND, (int) record.getDuration());
//        logger.info(this.name + " Getting boundaries for reservation from " + newStart.getTime().toString() + " to " + newEnd.getTime().toString() + " for " + record.getCPUs() + " cpus ");
//        /*
//         * Check if the reservation start time & end times lie on slot interval boundaries
//         */
//        long diff = (newStart.getTimeInMillis() - reservationEpoch.getTimeInMillis()) % SLOT_INTERVAL;
//        if (diff != 0) {
//            newStart.add(Calendar.MILLISECOND, (int) (-1 * SLOT_INTERVAL));
//        }
//        diff = (newEnd.getTimeInMillis() - reservationEpoch.getTimeInMillis()) % SLOT_INTERVAL;
//        if (diff != 0) {
//            newEnd.add(Calendar.MILLISECOND, (int) SLOT_INTERVAL);
//        }
//        Calendar[] boundaries = new Calendar[]{newStart, newEnd};
//        return boundaries;
//    }
    public int getAllowed_vms() {
        return allowed_vms;
    }

    public void setAllowed_vms(int allowed_vms) {
        this.allowed_vms = allowed_vms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getReservationEpoch() {
        return reservationEpoch;
    }

    protected void setReservationEpoch(Calendar reservationEpoch) {
        this.reservationEpoch = reservationEpoch;
    }

    public Set<VMInstance> getVmSet() {
        return vmSet;
    }

    public void setVmSet(Set<VMInstance> vmSet) {
        this.vmSet = vmSet;
    }

    public List<ReservationEntity> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationEntity> reservations) {
        this.reservations = reservations;
    }

    public abstract void refresh() throws PexOperationFailedException;
    /*	public boolean isFeasible(ReservationRecord record){
    
     * To check if a reservation is feasible, we have to get all already agreed reservations (slots) that have
     * (end_time of slot > start_time of reservation) && (start_time of slot < end_time of reservation)
     * Then, we have to check if any overlapping slots during this interval take up more CPUs than 
     * what is asked for the reservation
    
    boolean result = true;
    int required = record.getNodes();
    Calendar startTime = record.getStartTime();
    Calendar endTime = (Calendar) startTime.clone();
    int duration = (int) Math.floor(record.getDuration()/1000);
    endTime.add(Calendar.SECOND, duration);
    
     * Finding the slots that satisfy the first condition.
     * Create a dummy record for searching..
    
    ReservationRecord dummy = new ReservationRecord();
    dummy.setStartTime(startTime);
    dummy.setDuration(0.0);
    SortedSet<ReservationRecord> reservedSlots = reservationSet.tailSet(dummy);
    
    if(reservedSlots.isEmpty())
    result = false;
    
    Calendar horizon = startTime;
    int cpusOccupied = 0;
    Set<com.xensource.xenapi.HostCpu> cpus = this.getHostRecord().hostCPUs;
    int cpusAvailable = cpus.size();
    Iterator<ReservationRecord> resIt = reservedSlots.iterator();
    
    do{
    ReservationRecord slot = resIt.next();
    
    Calendar slotEnd = (Calendar) slot.getStartTime().clone();
    slotEnd.add(Calendar.SECOND, (int) Math.floor(slot.getDuration()/1000));
    
    if((slot.getStartTime().after(endTime))
    ||(slot.getStartTime().equals(endTime))){
    if(slotEnd.after(horizon))
    break;
    continue;
    }
    
    if(!(horizon.after(slot.getStartTime()))){
    horizon = slotEnd;
    cpusOccupied = slot.getCPUs();
    if((cpusOccupied+record.getCPUs())>cpusAvailable){
    result = false;
    break;
    }
    continue;
    }
    
    //			if(slotEnd.after(horizon)){
    //				horizon = slotEnd;
    //				cpusOccupied += slot.getCPUs();
    //			}
    
    horizon = slotEnd;
    cpusOccupied += slot.getCPUs();
    if((cpusOccupied+record.getCPUs())>cpusAvailable){
    result = false;
    break;
    }
    
    
    }while(resIt.hasNext());	
    
    return result;
    }*///	/*
//	 * This is for ordering the reservations according to the start time of the reservations
//	 * 
//	 */
//	class ReservationSlotComparator implements Comparator<ReservationRecord>,Serializable{
//
//		public int compare(ReservationRecord arg0, ReservationRecord arg1) {
//			Calendar endTime1 = (Calendar) arg0.getStartTime().clone();
//			endTime1.add(Calendar.SECOND,(int) Math.floor(arg0.getDuration()/1000));
//			Calendar endTime2 = (Calendar) arg1.getStartTime().clone();
//			endTime1.add(Calendar.SECOND,(int) Math.floor(arg1.getDuration()/1000));
//			if(endTime1.before(endTime2))
//				return -1;
//			else if(endTime1.after(endTime2))
//				return 1;
//			else return 0;
//		}
//		
//	}
}
