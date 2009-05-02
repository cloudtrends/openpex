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


package org.unimelb.openpex.reservation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.unimelb.openpex.*;
import org.unimelb.openpex.Constants.ReservationStatus;
import org.unimelb.openpex.command.PexCommand;
import org.unimelb.openpex.storage.PexStorage;
import org.unimelb.openpex.storage.PexStorageFailedException;
import org.unimelb.openpex.util.RandomGUID;

/**
 *
 * @author srikumar
 */
public abstract class ReservationManager {

    static Logger logger = Logger.getLogger(ReservationManager.class.getName());
    protected PexStorage store = null;
    protected long identifier;

    public ReservationManager() {
        store = PexStorage.getInstance();
        Random rand = new Random();
        identifier = rand.nextLong();
    }

    /**
     * Returns the unique identifier of the reservation manager. It should be the same as that of the resoruce manager
     * that implements this interfaces
     * @return identifier
     */
    public abstract long getUniqueIdentifier();

    /**
     * Initiates the reservation session.
     * @return identifier for the session
     */
    public String initiateReservation(short userid)
            throws PexReservationFailedException {

        VmUser user = store.getUserById(userid);
        RandomGUID random = new RandomGUID();
        String reservationID = random.toString();
        logger.info("Generating a new reservationID " + reservationID);
        ReservationEntity record = new ReservationEntity(reservationID);
        record.setStatus(ReservationStatus.INITIATED);
        record.setUserid(userid);

        try {
            store.saveReservation(record);
        } catch (PexStorageFailedException ex) {
            logger.severe("Error in saving record to store");
            throw new PexReservationFailedException("Store failure", ex);
        }
        return reservationID;
    }

    /**
     * Submits a reservation request to the Reservation Manager. The reply is composed of one of three
     * possible values - ACCEPT, REJECT or COUNTER. In case of COUNTER, the reply is accompanied by a 
     * counter proposal
     * @param reservationID - the identifier for the reservation session.
     * @param request - the reservation requirement
     * @return reply to the request
     * @throws PexReservationFailedException 
     */
    public abstract ReservationReply requestReservation(String reservationID, ReservationProposal request)
            throws PexReservationFailedException;

    /**
     * IS a reply to a counter proposal. The reply that is returned can be one of three again: ACCEPT, REJECT
     * or COUNTER. 
     * @param reservationID - the identifier for the reservation session.
     * @param reply - reply from the other party to the counter.
     * @return reply to the counter .. 
     * @throws PexReservationFailedException - in case the reservation was not countered before.
     */
    public abstract ReservationReply replyToCounter(String reservationID, ReservationReply reply)
            throws PexReservationFailedException;

    /**
     * Confirms the agreed-upon proposal. Throws an exception if the proposal is not in the Accepted state.
     * 
     * @param reservationID
     * @param proposal
     * @return true - if the proposal is confirmed. false - if not.
     * @throws org.unimelb.pex.PexReservationFailedException - 
     * in case the proposal is in an incorrect state or cannot be found
     */
    public abstract boolean confirmReservation(String reservationID, ReservationProposal proposal)
            throws PexReservationFailedException;

    /**
     * Delete an agreed-upon proposal. Throws an exception if the proposal is not in the Accepted state.
     * 
     * @param reservationID
     * @return true - if the proposal is deleted. false - if not.
     * @throws org.unimelb.pex.PexReservationFailedException - 
     * in case the proposal is in an incorrect state or cannot be found
     */
    public abstract boolean deleteReservation(String reservationID)
            throws PexOperationFailedException;

}
