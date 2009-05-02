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


import java.util.logging.Logger;
import org.unimelb.openpex.storage.PexStorage;
import org.unimelb.openpex.storage.PexStorageFailedException;

/**
 *
 * @author srikumar
 */
public class VMMonitor implements VMListener {

    private static VMMonitor monitor = null;
    static Logger logger = Logger.getLogger(VMMonitor.class.getName());
    private PexStorage store = null;
    
    private VMMonitor(){
        store = PexStorage.getInstance();
    }
    
    public static VMMonitor getInstance(){
        if (monitor == null)
            monitor = new VMMonitor();
        return monitor;
    }
    
    public void statusChanged(VMInstance instance) {
        try {
            if(instance.getStatus() == VMStatus.DELETED)
                instance.removeListener(this);
            store.saveVM(instance);
        } catch (PexStorageFailedException ex) {
            logger.severe("Saving vm failed "+ex);
        }
    }

}
