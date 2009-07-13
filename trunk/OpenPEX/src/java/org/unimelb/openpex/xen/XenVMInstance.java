package org.unimelb.openpex.xen;

import java.util.logging.Logger;
import com.xensource.xenapi.VM;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.unimelb.openpex.ClusterNode;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.PexOperationFailedException;
import org.unimelb.openpex.VMInstance;


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
    public void startInstance() throws PexOperationFailedException {

        if(this.getClusterNode()==null)
            throw new PexOperationFailedException("Cluster node not set for starting instance");
        XenClusterNode xnode = (XenClusterNode) this.getClusterNode();
        dispatcher.vmStart(this, xnode.getIpAddress());

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
