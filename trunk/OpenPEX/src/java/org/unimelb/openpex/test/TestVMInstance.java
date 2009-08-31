/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.unimelb.openpex.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.unimelb.openpex.ClusterNode;
import org.unimelb.openpex.PexOperationFailedException;
import org.unimelb.openpex.VMInstance;


/**
 *
 * @author srikumar
 */
@Entity
public class TestVMInstance extends VMInstance {

    protected static Logger logger = Logger.getLogger(TestVMInstance.class.getName());
    public static final long OPERATION_TIME = 10000;
    
    @Override
    public String create(String template, String name, String mac) throws PexOperationFailedException {
        logger.info("Creating new VM with template "+template+" name "+name);
        try {
            Thread.sleep(OPERATION_TIME);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestVMInstance.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String randomID = "test"+Long.toString(System.currentTimeMillis()).substring(8);
        logger.info("Finished creating VM");
        return randomID;
    }

    @Override
    public void startInstance() throws PexOperationFailedException {
        if(this.getClusterNode()==null)
            throw new PexOperationFailedException("Missing cluster node. .cannot instantiate VM");
        if(this.getVmID().equals(""))
            throw new PexOperationFailedException("VM ID null. .Cannot instantiate VM");
        logger.info("Starting new VM instance with ID "+this.getVmID());
        try{
            Thread.sleep(OPERATION_TIME);
        }catch (InterruptedException ex){}

        logger.info("Finished starting VM instance");
    }

    @Override
    public void stopInstance() throws PexOperationFailedException {
        logger.info("Stopping new VM instance with ID "+this.getVmID());
        try{
            Thread.sleep(OPERATION_TIME);
        }catch (InterruptedException ex){}

        logger.info("Finished stopping VM instance");
    }

    @Override
    public void migrateTo(ClusterNode destination) throws PexOperationFailedException {
        logger.info("Migrating  VM instance with ID "+this.getVmID()+" to node "+destination.getName());
        try{
            Thread.sleep(OPERATION_TIME);
        }catch (InterruptedException ex){}

        logger.info("Finished migrating VM instance");
    }

    @Override
    public void queryInstance() {
       
    }

    @Override
    public void deleteInstance() throws PexOperationFailedException {

    }

    @Override
    public void rebootInstance() throws PexOperationFailedException {
        
    }

}
