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

import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import org.unimelb.openpex.storage.PexStorage;
import org.unimelb.openpex.storage.PexStorageFailedException;
import org.unimelb.openpex.xen.XenClusterNode;
import org.unimelb.openpex.xen.XenDispatcher;

public class NodeMonitor {

    static Logger logger = Logger.getLogger(NodeMonitor.class.getName());
    private XenDispatcher dispatcher = null;
    private static NodeMonitor monitor = null;
    private PexStorage store = null;

    private NodeMonitor() throws PexException {
        dispatcher = XenDispatcher.getInstance();
        store = PexStorage.getInstance();
    }

    public static NodeMonitor getInstance() throws PexException {
        if (monitor == null) {
            monitor = new NodeMonitor();
        }
        return monitor;
    }

    public void doMonitor() {
        List<ClusterNode> nodes;
        try {
            nodes = store.getNodes();
        } catch (PexStorageFailedException ex) {
            logger.severe("Getting nodes from storage failed");
            return;
        }
        for (ClusterNode node : nodes) {
            try {
                node.refresh();
//              logger.debug(node.getMetrics().toString());
                store.saveNode(node);
            } catch (PexStorageFailedException ex) {
                logger.severe("A storage operation failed "+node.getName());
            } catch (PexOperationFailedException e) {
                logger.warning("Unable to retieve information for node " + node.getName());
                node.setAvailable(false);
            }

        }
    }

    public void addClusterNode(String name, String ipAddress, Calendar epoch) throws PexException {
    
        ClusterNode newNode = new XenClusterNode(name, ipAddress, epoch);

//		HostMetrics metrics = dispatcher.getHostMetrics(newHost);
        store.saveNode(newNode);
        logger.info("Successfully added node " + ipAddress);
    }

    public List<ClusterNode> getAvailableNodes() {
        return store.getAvailableNodes();
    }

    public List<ClusterNode> getNodes() throws PexStorageFailedException {
        return store.getNodes();
    }

}
