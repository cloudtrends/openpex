package org.unimelb.openpex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.unimelb.openpex.VMListener.VMStatus;
import org.unimelb.openpex.reservation.ReservationEntity;


@Entity
@Table(name = "PEX_VM")
@NamedQueries({
    @NamedQuery(name="VMInstance.findByVmId", query="SELECT v FROM VMInstance v WHERE v.vmID = :vmID"),
    @NamedQuery(name="VMInstance.findByStatus", query="SELECT v from VMInstance v WHERE v.status = :status"),
    @NamedQuery(name="VMInstance.findByUserid", query="SELECT v from VMInstance v WHERE v.userID = :userID")
})
public abstract class VMInstance implements Serializable {
    
    static Logger logger = Logger.getLogger(VMInstance.class.getName());
    @Transient
    final ArrayList<VMListener> listeners = new ArrayList<VMListener>();
    
    @Id
    @Column(name="vm_id", nullable=false)
    private String vmID;
    @Column(name="name", nullable=false, length=20)
    private String name;
    private short userID;
    @Column(name="status", nullable=false)
    VMStatus status = VMStatus.UNRECOGNIZED;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="start_time", nullable=false)
    private Date start_time;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="end_time", nullable=false)
    private Date end_time;
    @ManyToOne
    @JoinColumn(name = "NODE_NAME", nullable = true)
    private ClusterNode clusterNode;
    @ManyToOne
    @JoinColumn(name="RESERVATION_ID", nullable=false)
    private ReservationEntity reservation;
    @Column(name="IP_ADDRESS", nullable=true, unique=true)
    private String ipAddress;
    @Column(name="VM_PASS", nullable=true)
    private String vmPassword;
    
    public VMInstance(){}
    
    public ClusterNode getClusterNode() {
        return clusterNode;
    }

    public void setClusterNode(ClusterNode clusterNode) {
        this.clusterNode = clusterNode;
    }

    public ReservationEntity getReservation() {
        return reservation;
    }

    public void setReservation(ReservationEntity reservation) {
        this.reservation = reservation;
    }
    
    
    public abstract String create(String template, String name, String mac) throws PexOperationFailedException;

    /**
     * Starts the VM Instance
     * Set the node before invoking this call or else an exception will be thrown
     * @throws PexException 
     */
    public abstract void startInstance() throws PexOperationFailedException;

    /**
     * Stops the VM Instance
     */
    public abstract void stopInstance() throws PexOperationFailedException;

    /**
     * Migrates the VMInstance to the destination node
     * @param detination
     * @throws org.unimelb.pex.PexException
     */
    public abstract void migrateTo(ClusterNode destination) throws PexOperationFailedException;

    /**
     * Queries the instance of the VM to determine its status
     */
    public abstract void queryInstance();
    
    /**
     * Deletes the instance from the VM list completely
     * @throws PexOperationFailedException - if the VM was not in HALTED state
     */
    public abstract void deleteInstance() throws PexOperationFailedException;
    
    /**
     * Reboots the instance
     * @throws PexOperationFailedException - if the VM was not in RUNNING state
     */
    public abstract void rebootInstance() throws PexOperationFailedException;

    /**
     * registers a listener for status updates on this VM
     * @param listener
     */
    public final void registerListener(VMListener listener) {
        synchronized (listeners) {
            if (listeners == null) {
                return;
            }
            listeners.add(listener);
        }

    }

    public final void removeListener(VMListener listener) {
        synchronized (listeners) {
            if (listeners != null) {
                listeners.remove(listener);
            }
        }
    }

    public void updateStatus(VMStatus status) {
        this.setStatus(status);
        logger.info("Status for VM " + this.vmID + " changed to " + status);
        notifyListenersBlocking(this);
    }

        /**
     * Invokes the status Changed method on all listeners.
     * @param status
     */
    private void notifyListenersBlocking(VMInstance instance) {
        /*
         * Copied from Krishna's method of JobListeners in the Broker.
         */

        ArrayList<VMListener> listenerCopy = (ArrayList<VMListener>) listeners.clone();
        for (VMListener listener : listenerCopy) {
            try {
                listener.statusChanged(instance);
            } catch (Exception e) {
                logger.severe("An exception occured in the event handler " + e);
                continue;
            }

        }


    }
    /**
     * Invokes the status Changed method on all listeners.
     * @param status
     */
    private void notifyListeners(final VMInstance instance) {
        /*
         * Copied from Krishna's method of JobListeners in the Broker.
         */
        new Thread("VM Listener-notification-thread " + this.getVmID()) {

            public void run() {
                ArrayList<VMListener> listenerCopy = (ArrayList<VMListener>) listeners.clone();
                for (VMListener listener : listenerCopy) {
                    try {
                        listener.statusChanged(instance);
                    } catch (Exception e) {
                        logger.severe("An exception occured in the event handler " + e);
                        continue;
                    }

                }
            }
        }.start();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VMStatus getStatus() {
        return status;
    }

    protected void setStatus(VMStatus status) {
        this.status = status;
    }

    public String getVmID() {
        return vmID;
    }

    public void setVmID(String id) {
        this.vmID = id;
    }

    public short getUserID() {
        return userID;
    }

    public void setUserID(short userID) {
        this.userID = userID;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getVmPassword() {
        return vmPassword;
    }

    public void setVmPassword(String vmPassword) {
        this.vmPassword = vmPassword;
    }    
    
}
