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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unimelb.openpex.reservation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.unimelb.openpex.Constants.ReservationStatus;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.command.PexCommand;

/**
 *
 * @author srikumar
 */
public class LateBindingReservationManager extends SimpleReservationManager {

    static Logger logger = Logger.getLogger(LateBindingReservationManager.class.getName());
    
    public LateBindingReservationManager() throws PexException{
        super();       
    }
    
    public synchronized ReservationReply requestReservation(String reservationID, ReservationProposal request)
            throws PexReservationFailedException {

        logger.info("Reesarwayshun reeceevd with ID " + reservationID);

        ReservationEntity record = store.getReservation(reservationID);

        if (record == null) {
            logger.severe("Epic fail! this reeserwayshun does not exist ");
            throw new PexReservationFailedException("Record not found");
        }

        if (record.getStatus() != ReservationStatus.INITIATED &&
                record.getStatus() != ReservationStatus.COUNTERED) {
            logger.severe(" Oh noes! Reesirwayshun " + reservationID + " in bad state " + record.getStatus() + " no cheezburger for yooz");
            throw new PexReservationFailedException("Reservation in incorrect state");
        }

        
//        List<ClusterNode> nodes = nodeMonitor.getAvailableNodes();
//        if (nodes.isEmpty()) {
//            logger.severe("Oh noes ! I dont has nodez");
//            throw new PexReservationFailedException("No available nodes");
//        }
//
//        ReservationRecord newRec = new ReservationRecord(request, ReservationStatus.SUBMITTED);
////        ReservationEntity newRecord = newRec.convertToEntity();
//        record = newRec.convertToEntity();
//        record.setRequestId(reservationID);
//        ReservationReply reply = null;
//
//        List<ClusterNode> feasibleList = new ArrayList<ClusterNode>();
//        for (ClusterNode node : nodes) {
//            if (node.isFeasible(record)) {
//                feasibleList.add(node);
//                logger.info("Reservation " + record.getRequestId() + " is feasible for node " + node.getName());
//            }
//        }
//        if (feasibleList.size() > 0) {
//            Random rand = new Random();
//            ClusterNode node = feasibleList.get(rand.nextInt(feasibleList.size()));
//            logger.info("Chose node " + node.getName() + "for reservation " + record.getRequestId());
//            logger.info("Reservation " + record.getRequestId() + " is changed to status ACCEPTED");
//            record.setStatus(ReservationStatus.ACCEPTED);
//            Set<ClusterNode> serviceNodes = new HashSet<ClusterNode>();
//            serviceNodes.add(node);
//            record.setNodes(serviceNodes);
//            logger.info("Storing new status ACCEPTED for reservation " + record.getRequestId());
//            reserveMap.remove(record.getRequestId());
//            reserveMap.put(record.getRequestId(), record);
//            record.setStatus(ReservationStatus.ACCEPTED);
//            try {
//                store.saveReservation(record);
//            } catch (PexStorageFailedException ex) {
//                logger.severe("Error in saving record to store");
//                throw new PexReservationFailedException("Store failure", ex);
//            }
//            logger.info("Sending reply ACCEPT for reservation " + record.getRequestId());
//            reply = new ReservationReply();
//            reply.setProposal((ReservationProposal) record.convertToProposal());
//            reply.setReply(ReservationReplyType.ACCEPT);
////    		ReservationRecord tempRecord = reserveMap.get(record.getId());
////    		logger.info("Msg from tmpRecord status is "+tempRecord.getStatus());
//        } else {
//            Calendar[] alt = null, chosen = null;
//            long diff = 0, minDiff = Long.MAX_VALUE;
//            ClusterNode selected = null;
//            for (ClusterNode candidate : nodes) {
//                alt = candidate.getAlternateSlot(record);
//                diff = alt[0].getTimeInMillis() - record.getStartTime().getTime();
//                if (diff < 0) {
//                    logger.info("Strange new slot " + alt[0].getTime() + " " + alt[1].getTime() + " from " + candidate.getName());
//                }
//                if (diff < minDiff) {
//                    selected = candidate;
//                    chosen = alt;
//                    minDiff = diff;
//                }
//            }
//            Set<ClusterNode> serviceNodes = new HashSet<ClusterNode>();
//            serviceNodes.add(selected);
//            record.setNodes(serviceNodes);
//            record.setStatus(ReservationStatus.COUNTERED);
//            record.setStartTime(chosen[0].getTime());
//            record.setEndTime(chosen[1].getTime());
//            reserveMap.remove(record.getRequestId());
//            reserveMap.put(record.getRequestId(), record);
//            try {
//                store.saveReservation(record);
//            } catch (PexStorageFailedException ex) {
//                logger.severe("Error in saving record to store");
//                throw new PexReservationFailedException("Store failure", ex);
//            }
//            reply = new ReservationReply();
//            reply.setProposal((ReservationProposal) record.convertToProposal());
//            reply.setReply(ReservationReplyType.COUNTER);
//        }

        return null;
    }
//
//    public synchronized ReservationReply replyToCounter(String reservationID, ReservationReply reply)
//            throws PexReservationFailedException {
//
//        logger.info("Reesarwayshun reply reeceevd with ID " + reservationID);
//
//        ReservationEntity record = store.getReservation(reservationID);
//
//        if (record == null) {
//            logger.severe("Epic fail! this reeserwayshun does not exist ");
//            throw new PexReservationFailedException("Record not found");
//        }
//
//        if (record.getStatus() != ReservationStatus.COUNTERED) {
//            logger.severe(" Oh noes! Reesirwayshun " + reservationID + " in bad state " + record.getStatus() + " no cheezburger for yooz");
//            throw new PexReservationFailedException("Reservation in incorrect state");
//        }
//
//        ReservationReply newReply = new ReservationReply();
//        switch (reply.getReply()) {
//            case ACCEPT:
//                newReply.setProposal(reply.getProposal());
//                newReply.setReply(ReservationReply.ReservationReplyType.ACCEPT);
//                record.setStatus(ReservationStatus.ACCEPTED);
//                break;
//            case REJECT:
//                newReply.setReply(ReservationReply.ReservationReplyType.REJECT);
//                record.setStatus(ReservationStatus.REJECTED);
//                break;
//            case COUNTER:
//                newReply.setReply(ReservationReplyType.REJECT);
//                record.setStatus(ReservationStatus.REJECTED);
//                break;
//        }
//
//        try {
//            store.saveReservation(record);
//        } catch (PexStorageFailedException ex) {
//            logger.severe("Error in saving record to store");
//            throw new PexReservationFailedException("Store failure", ex);
//        }
//
//        return newReply;
//    }
//
//    public synchronized boolean confirmReservation(String reservationID, ReservationProposal proposal)
//            throws PexReservationFailedException {
//
//        boolean result = false;
//        ReservationEntity record = store.getReservation(reservationID);
//        if (record == null) {
//            throw new PexReservationFailedException("Record cannot be found!!");
//        }
//
//        logger.info("Status of the reservation " + reservationID + " is " + record.getStatus());
//        if (record.getStatus() == ReservationStatus.ACCEPTED) {
//            /*
//             *TODO Have to put some method of recording the reservation
//             */
//            ReservationEntity altRecord = reserveMap.get(reservationID);
//            Set<ClusterNode> confNodes = altRecord.getNodes();
//            for (ClusterNode node : confNodes) {
//                try {
//                    node.commitReservation(record);
//                    store.saveNode(node);
//                } catch (PexStorageFailedException ex) {
//                    logger.severe("Failed to save node into storage");
//                }
//            }
//            record.setNodes(confNodes);
//            record.setStatus(ReservationStatus.CONFIRMED);
//            try {
//                store.saveReservation(record);
//            } catch (PexStorageFailedException ex) {
//                logger.severe("Error in saving record to store");
//                throw new PexReservationFailedException("Store failure", ex);
//            }
//            reserveMap.put(reservationID, record);
//            result = true;
//        } else {
//            throw new PexReservationFailedException("Wrong state for reservation");
//        }
//        return result;
//    }
//
//    public boolean deleteReservation(String reservationID) throws PexOperationFailedException {
//        ReservationEntity record = store.getReservation(reservationID);
//        if (record == null) {
//            logger.severe("Oh noes! Reeserwayshun duz nawt exist!");
//            throw new PexOperationFailedException("Reservation not found, cannot start vm");
//        }
//        if (record.getStatus() == ReservationStatus.ACTIVATED) {
//            logger.severe("Oh noes!! Reservation already activated");
//            throw new PexOperationFailedException("Reservation already activated cannot be deleted");
//        }
//
//        try {
//            store.deleteReservation(reservationID);
//        } catch (PexStorageFailedException e) {
//            logger.severe("Failed to remove reservation");
//            throw new PexOperationFailedException("Delete failed", e);
//        }
//
//        return true;
//
//    }

    class Monitor extends PexCommand {

        @Override
        public void execute() throws Exception {
            Calendar now = Calendar.getInstance();
            Date current = now.getTime();
            List<ReservationEntity> resList = store.getReservationsCrossingTime(current);
            for (ReservationEntity res : resList) {
                
            }
        }
    }
}
