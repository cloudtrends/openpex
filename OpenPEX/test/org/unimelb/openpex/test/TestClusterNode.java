/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.unimelb.openpex.test;

import java.util.Calendar;
import org.unimelb.openpex.ClusterNode;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.PexOperationFailedException;


/**
 *
 * @author srikumar
 */
public class TestClusterNode extends ClusterNode{


    public TestClusterNode(){}
    
    public TestClusterNode(String name, String ipAddress, Calendar epoch) throws PexException{
        super(name, ipAddress, epoch);
        this.setAllowed_vms(4);
    }

    @Override
    public void refresh() throws PexOperationFailedException {
        
    }

}
