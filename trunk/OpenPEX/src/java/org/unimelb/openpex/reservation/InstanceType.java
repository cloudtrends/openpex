/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.unimelb.openpex.reservation;

/**
 *
 * @author srikumar
 */
public enum InstanceType {

    SMALL(1,768,"SMALL"),
    MEDIUM(2,1536,"MEDIUM"),
    LARGE(3,2034,"LARGE"),
    XLARGE(4,3072,"XLARGE");


    InstanceType(int numCPU, int memory, String name){
        this.numCPU = numCPU;
        this.memoryInMB = memory;
        this.name = name;
    }

    private final int numCPU;
    private final int memoryInMB;
    private final String name;

    public int getNumCPU(){
        return numCPU;
    }

    public int getMemoryInMB(){
        return memoryInMB;
    }

}
