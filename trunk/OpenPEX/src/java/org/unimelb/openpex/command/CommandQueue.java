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

package org.unimelb.openpex.command;

import java.util.Timer;
import java.util.logging.Logger;

/**
 *
 * @author srikumar
 */
public class CommandQueue {

    static Logger logger = Logger.getLogger(CommandQueue.class.getName());
    private static CommandQueue eventQueue = null;
    private Timer eventThread = null;
    
    private CommandQueue(){
        eventThread = new Timer("EventQueue");
    }
    
    public static CommandQueue getInstance(){
        if(eventQueue == null)
            eventQueue = new CommandQueue();
        return eventQueue;
    }
    
    public void addCommand(PexCommand command, long delay){
        eventThread.schedule(command, delay); 
    }
    
    public void addRepeatingCommand(PexCommand command, long delay, long interval){
        eventThread.scheduleAtFixedRate(command, delay, interval);
    }
    
    public void stop(){
        eventThread.cancel();
    }
}
