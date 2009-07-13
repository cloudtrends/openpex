/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.unimelb.openpex.command;

import org.unimelb.openpex.ClusterNode;

/**
 *
 * @author srikumar
 */
public class VMStartCommand extends PexCommand{

    String vmID = null;
    ClusterNode node = null;
    
    public VMStartCommand(String vmID, ClusterNode node){
        this.vmID = vmID;
        this.node = node;
    }
    
    public void execute() throws Exception{
        
    }

}
