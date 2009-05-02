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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unimelb.openpex.xen;

import com.xensource.xenapi.Host;
import com.xensource.xenapi.HostMetrics;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.unimelb.openpex.ClusterNode;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.PexNodeException;
import org.unimelb.openpex.PexOperationFailedException;

/**
 *
 * @author srikumar
 */
@Entity
public class XenClusterNode extends ClusterNode {

    @Transient
    private Host vhost;
    @Transient
    private Host.Record hostRecord;
    @Transient
    private HostMetrics metrics;
    @Transient
    private XenDispatcher dispatcher = null;
    
    public XenClusterNode(){}
    
    public XenClusterNode(String name, String ipAddress, Calendar epoch) throws PexException {
        super(name,ipAddress, epoch);
        dispatcher = XenDispatcher.getInstance();
        this.vhost = dispatcher.getHost(ipAddress);
        if (vhost == null) {
            logger.severe("Host" + name + " is not a pex node");
            throw new PexNodeException("Host not a pex node");
        }
        this.hostRecord = dispatcher.getHostRecord(vhost);
        if (hostRecord == null) {
            logger.severe("Host Record could not be retrieved");
            throw new PexNodeException("Host Record could not be retrieved");
        }
        logger.info(this.getName() + " Received instance of host");


        /*
         * For the time being, set the number of VMs allowed on a single machine to 
         * be equal to the number of cores. 
         */
        this.setAllowed_vms(this.hostRecord.hostCPUs.size());
        logger.info(" Allowed VMs for node " + this.getName() + " is " + this.getAllowed_vms());
//		reservationSet = new TreeSet<ReservationRecord>(new ReservationSlotComparator());
    }

    public XenClusterNode(String name, String ipAddress) throws PexException {
        this(name, ipAddress, null);
    }

    public Host getVhost() {
        return vhost;
    }

    public void setVhost(Host vhost) {
        this.vhost = vhost;
    }

    public Host.Record getHostRecord() {
        return hostRecord;
    }

    public void setHostRecord(Host.Record hostRecord) {
        this.hostRecord = hostRecord;
    }

    public HostMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(HostMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public void refresh() throws PexOperationFailedException{
        dispatcher.getHostMetrics(this.getVhost());
        logger.info("Got information for node " + this.getHostRecord().address);
    }
    
    
    
}
