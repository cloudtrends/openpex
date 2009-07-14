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

public interface VMListener {

    /**
     * Corresponds to the states in Types.VmPowerState of XenAPI plus extends them to add its own.
     * 
     */
    public enum VMStatus{

        /**
         * The value does not belong to this enumeration
         * @see Types.VmPowerState
         */
        UNRECOGNIZED,
        /**
         * @see Types.VmPowerState
         * VM is offline and not using any resources
         */
        HALTED,
        /**
         * @see Types.VmPowerState
         * All resources have been allocated but the VM itself is paused and its vCPUs are not running
         */
        PAUSED,
        /**
         * @see Types.VmPowerState
         * Running
         */
        RUNNING,
        /**
         * @see Types.VmPowerState
         * VM state has been saved to disk and it is nolonger running. 
         * Note that disks remain in-use while the VM is suspended.
         */
        SUSPENDED,
        DELETED,
        NOT_PROVISIONED,
        UNDER_MIGRATION
    };

    public void statusChanged(VMInstance instance);
}
