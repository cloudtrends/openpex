/*
 * copyright Srikumar Venugopal and James Broberg 2009
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
    private boolean isDaemon = true;
    
    private CommandQueue(){
        eventThread = new Timer("EventQueue", isDaemon);
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
