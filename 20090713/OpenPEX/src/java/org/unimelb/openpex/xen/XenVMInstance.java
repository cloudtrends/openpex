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

package org.unimelb.openpex.xen;

import java.util.logging.Logger;

import org.unimelb.openpex.ClusterNode;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.VMInstance;

import com.xensource.xenapi.VM;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.unimelb.openpex.PexOperationFailedException;

@Entity
public class XenVMInstance extends VMInstance {

    static final Logger logger = Logger.getLogger(XenVMInstance.class.getName());
    @Transient
    private XenDispatcher dispatcher = null;
    @Transient
    private VM.Record record = null;

    public XenVMInstance() throws PexException {
        dispatcher = XenDispatcher.getInstance();
    }

    @Override
    public String create(String template, String name, String mac) throws PexOperationFailedException {
        String vmID = "";
        vmID = dispatcher.vmCreatefromTemplate(this, template, name);
        if (!(mac == null || mac.equals("")))
            dispatcher.addInterfaceToVm(this, mac);    
        return vmID;

    }

    @Override
    public void startInstance(ClusterNode node) throws PexOperationFailedException {

        XenClusterNode xnode = (XenClusterNode) node;
        dispatcher.vmStart(this, node.getIpAddress());

    }

    @Override
    public void stopInstance() throws PexOperationFailedException {
        dispatcher.vmStop(this);
    }
    
    @Override
    public void rebootInstance() throws PexOperationFailedException {
        dispatcher.vmReboot(this);
    }

    @Override
    public void migrateTo(ClusterNode node) throws PexOperationFailedException {
        XenClusterNode xnode = (XenClusterNode) node;
        dispatcher.vmMigrate(this, xnode.getVhost());
    }

    @Override
    public void deleteInstance() throws PexOperationFailedException {
        dispatcher.vmDelete(this);
    }

    public void queryInstance() {
    }

    public VM.Record getRecord() {
        return record;
    }

    public void setRecord(VM.Record record) {
        this.record = record;
    }

}
