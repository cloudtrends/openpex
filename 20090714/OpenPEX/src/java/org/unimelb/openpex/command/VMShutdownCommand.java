/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.unimelb.openpex.command;

import org.unimelb.openpex.PexOperationFailedException;
import org.unimelb.openpex.VMInstance;
import org.unimelb.openpex.storage.PexStorage;

/**
 *
 * @author srikumar
 */
public class VMShutdownCommand extends PexCommand{

    String vmID = "";
    public VMShutdownCommand(String vmID){
        this.vmID = vmID;
    }
    
    public void execute() throws Exception {

        PexStorage store = PexStorage.getInstance();
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
        

}
