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
import org.unimelb.openpex.storage.PexStorageFailedException;

/**
 *
 * @author srikumar
 */
public class LateBindingReservationManager extends SimpleReservationManager {

    public static final int TOTAL_CPUS = 20;
    public static final float CLAIM_RISK = (float) 0.80;
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
        
        /* Step1. Get all reservations that are bound or not, but starting at the requested start time*/
        /* Step2a. If the total resource consumption of the bound researvations + requested is higher 
         * than available, then generate counter offer */
        /* Step 2b. Else if the total resource consumption of bound + unbound * risk factor + requested 
         * is higher than available then generate counter offer */
        /* Step 2c. Else generate a price for the existing offer and compare it against the offered price
         * 
         * 
         */
         
         record = request.convertToEntity();
         Date startTime = record.getStartTime();
         Date endTime = record.getEndTime();
         
         List<ReservationEntity> boundList = 
                 store.getReservationsCrossingInterval(startTime, endTime, ReservationStatus.CONFIRMED);

         boundList.addAll(store.getReservationsCrossingInterval(startTime, endTime, ReservationStatus.ACTIVATED));

         ReservationReply reply;
         int boundLoad = 0, unboundLoad =0;

         for(ReservationEntity res:boundList){
             boundLoad =+ res.getNumInstancesFixed()*res.getType().getNumCPU();
             unboundLoad =+ res.getNumInstancesOption()*res.getType().getNumCPU();
         }

         if (boundLoad + unboundLoad > TOTAL_CPUS){
            //Reject the offer
             reply = new ReservationReply();
             reply.setProposal(request);
             reply.setReply(ReservationReply.ReservationReplyType.REJECT);
         }

         
         int futureBoundLoad = boundLoad + record.getNumInstancesFixed()*record.getType().getNumCPU();
         float futurePossibleLoad = futureBoundLoad +
                 (unboundLoad + record.getNumInstancesOption()*record.getType().getNumCPU())*CLAIM_RISK;
         
         if (futureBoundLoad > TOTAL_CPUS){
             /*Convert to mixed fixed + option offer */

             int diff = futureBoundLoad - TOTAL_CPUS;

             //getCounterOffer();
             //reply = new ReservationReply();
             //reply.setProposal((ReservationProposal) record.convertToProposal());
             //reply.setReply(ReservationReplyType.COUNTER);
             //return;
         }

         if(futurePossibleLoad > TOTAL_CPUS)
         {
             /**/
             //getCounterOffer();
             //reply = new ReservationReply();
             //reply.setProposal((ReservationProposal) record.convertToProposal());
             //reply.setReply(ReservationReplyType.COUNTER);
             //return;
         } else {
             float price = computePrice(record);
             record.setStatus(ReservationStatus.ACCEPTED);
             try {
                 store.saveReservation(record);
             } catch (PexStorageFailedException ex) {
                 logger.severe("Error in saving record to store");
                 throw new  PexReservationFailedException("Store failure", ex);  
             }
             logger.info("Sending reply ACCEPT for reservation " + record.getRequestId());
             reply = new ReservationReply();
             reply.setProposal((ReservationProposal) record.convertToProposal());
             reply.setReply(ReservationReply.ReservationReplyType.ACCEPT);
         }
         return null;
    }

    private float computePrice(ReservationEntity record) {
        /* 1 unit = 1 cpu, 768 MB RAM, 10 G HDD
         */
        
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private ReservationProposal getCounterOffer(ReservationProposal origProposal){
        ReservationProposal newProposal = null;
        
        /*Step1. Determine the availability profile for the next time horizon
         *Step2. Find a gap where a new slot can be given.
         *Step3. Return the slot, pricing and other info as a counter proposal
         */


        
        return newProposal;
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
