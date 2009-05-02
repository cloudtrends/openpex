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

/*
 * SessionBean1.java
 *
 * Created on Mar 17, 2009, 4:03:45 PM
 * Copyright brobergj
 */
package org.unimelb.openpex.portal;

import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.FacesException;
import org.unimelb.openpex.VMInstance;
import org.unimelb.openpex.VmUser;
import org.unimelb.openpex.reservation.ReservationEntity;
import org.unimelb.openpex.reservation.ReservationReply;
import org.unimelb.openpex.storage.PexStorage;

/**
 * <p>Session scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available across
 *  multiple HTTP requests for an individual user.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
public class SessionBean1 extends AbstractSessionBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">
    private int __placeholder;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    // </editor-fold>
    static Logger logger = Logger.getLogger(PexStorage.class.getName());
    
    private VmUser user;
    private List<ReservationEntity> reservations;
    private List<VMInstance> vmInstances;
    private PexStorage store;
    private ReservationReply reply;

    public ReservationReply getReply() {
        return reply;
    }

    public void setReply(ReservationReply reply) {
        this.reply = reply;
    }





    public VmUser getUser() {
        return user;
    }

    public void setUser(VmUser user) {
        this.user = user;
    }

    public List<ReservationEntity> getReservations() {
        reservations = this.getStore().getReservationsbyUserid(user.getUserid());
        return reservations;
    }

    public void setReservations(List<ReservationEntity> reservations) {
        this.reservations = reservations;
    }

    public List<VMInstance> getVmInstances() {
        vmInstances = this.getStore().getVMInstancesbyUserid(user.getUserid());
        return vmInstances;
    }

    public void setVmInstances(List<VMInstance> vmInstances) {
        this.vmInstances = vmInstances;
    }



    public PexStorage getStore() {
        return store;
    }

    public void setStore(PexStorage store) {
        this.store = store;
    }

    

    

    /**
     * <p>Construct a new session data bean instance.</p>
     */
    public SessionBean1() {
    }

    /**
     * <p>This method is called when this bean is initially added to
     * session scope.  Typically, this occurs as a result of evaluating
     * a value binding or method binding expression, which utilizes the
     * managed bean facility to instantiate this bean and store it into
     * session scope.</p>
     * 
     * <p>You may customize this method to initialize and cache data values
     * or resources that are required for the lifetime of a particular
     * user session.</p>
     */
    public void init() {
        // Perform initializations inherited from our superclass
        super.init();
        // Perform application initialization that must complete
        // *before* managed components are initialized
        // TODO - add your own initialiation code here
        
        // <editor-fold defaultstate="collapsed" desc="Managed Component Initialization">
        // Initialize automatically managed components
        // *Note* - this logic should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("SessionBean1 Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }
        
        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized
        // TODO - add your own initialization code here
        logger.entering(PexStorage.class.getName(), "init sessioBean1");
        this.setStore(PexStorage.getInstance());
    }

    /**
     * <p>This method is called when the session containing it is about to be
     * passivated.  Typically, this occurs in a distributed servlet container
     * when the session is about to be transferred to a different
     * container instance, after which the <code>activate()</code> method
     * will be called to indicate that the transfer is complete.</p>
     * 
     * <p>You may customize this method to release references to session data
     * or resources that can not be serialized with the session itself.</p>
     */
    public void passivate() {
    }

    /**
     * <p>This method is called when the session containing it was
     * reactivated.</p>
     * 
     * <p>You may customize this method to reacquire references to session
     * data or resources that could not be serialized with the
     * session itself.</p>
     */
    public void activate() {
    }

    /**
     * <p>This method is called when this bean is removed from
     * session scope.  Typically, this occurs as a result of
     * the session timing out or being terminated by the application.</p>
     * 
     * <p>You may customize this method to clean up resources allocated
     * during the execution of the <code>init()</code> method, or
     * at any later time during the lifetime of the application.</p>
     */
    public void destroy() {
        this.getStore().closeEntityManager();
    }
}
