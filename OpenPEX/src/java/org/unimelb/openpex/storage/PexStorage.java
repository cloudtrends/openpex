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
package org.unimelb.openpex.storage;

import org.unimelb.openpex.reservation.ReservationEntity;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.unimelb.openpex.ClusterNode;
import org.unimelb.openpex.Constants.ReservationStatus;
import org.unimelb.openpex.VMInstance;
import org.unimelb.openpex.VmUser;

/**
 *
 * @author srikumar
 */
public class PexStorage {

    private static PexStorage storage = null;
    private EntityManagerFactory emf;
    private EntityManager em;
    private String DEFAULT_PERSISTENCE_UNIT_NAME = "PexJava";
    static Logger logger = Logger.getLogger(PexStorage.class.getName());

    public static PexStorage getInstance() {
        if (storage == null) {
            storage = new PexStorage();
        }
        return storage;
    }

    private PexStorage() {
//        if(unitName == null || unitName.equals(""))
//            emf = Persistence.createEntityManagerFactory(DEFAULT_PERSISTENCE_UNIT_NAME);
//        else
        logger.info("Creating an instance of the persistent store..");
        emf = Persistence.createEntityManagerFactory(DEFAULT_PERSISTENCE_UNIT_NAME);
        em = emf.createEntityManager();
        logger.info("Persistent store instance created");
    }

    public void closeEntityManager() {
        em.close();
        emf.close();
    }

    public void saveReservation(ReservationEntity res) throws PexStorageFailedException {
        logger.entering(PexStorage.class.getName(), "create()");
        try {
            em.getTransaction().begin();
            ReservationEntity re = em.find(ReservationEntity.class, res.getRequestId());

            if (re == null) {
                logger.info("Saving new reservation " + res.getRequestId());
                em.persist(res);
            } else {
                logger.info("updating reservation " + res.getRequestId());
//                re = res;
                /*
                 * srikumar - This is a disgusting hack just because I havent udenrstood JPA yet.
                 */
                re.setStartTime(res.getStartTime());
                re.setEndTime(res.getEndTime());
                re.setNumInstancesFixed(res.getNumInstancesFixed());
                re.setNumInstancesOption(res.getNumInstancesOption());
                re.setStatus(res.getStatus());
                re.setTemplate(res.getTemplate());
                re.setType(res.getType());
                re.setNodes(res.getNodes());
                em.persist(re);
            }

            em.getTransaction().commit();
            logger.info("Saved reservation " + res.getRequestId());
        } catch (Exception e) {
            logger.severe("Commit of reservation " + res + " failed");
            throw new PexStorageFailedException("Commit failed", e);
        }
    }

    public ReservationEntity getReservation(String reservationID) {
        logger.entering(PexStorage.class.getName(), "getReservation");
        ReservationEntity res = em.find(ReservationEntity.class, reservationID);
        return res;
//        ReservationEntity res = (ReservationEntity) em.createQuery(
//                "select r from ReservationEntity r where r.requestId = :requestId").setParameter("requestId", requestID).getSingleResult();
//        logger.info("Query returned: " + res.getRequestId() + " " + res.getTemplate() + " " +res.getStatus());
    }

    public List<ReservationEntity> getReservationsbyUserid(short userid) {
        logger.entering(PexStorage.class.getName(), "getReservationbyUserid");
        Query resQuery = em.createNamedQuery("ReservationEntity.findByUserid").setParameter("userid", userid);
        List<ReservationEntity> resList = resQuery.getResultList();
        return resList;
    }

    public List<VMInstance> getVMInstancesbyUserid(short userid) {
        logger.entering(PexStorage.class.getName(), "getVMInstancesbyUserid");
        Query resQuery = em.createNamedQuery("VMInstance.findByUserid").setParameter("userID", userid);
        List<VMInstance> resList = resQuery.getResultList();
        return resList;
    }

    public List<ReservationEntity> getReservationsCrossingInterval(Date startTime, Date endTime, ReservationStatus status){
        Query resQuery = em.createQuery("select r from ReservationEntity r where" +
                " (r.endTime >= :sTime) AND (r.startTime <= :eTime) AND (r.status = :status)");
        resQuery.setParameter("sTime", startTime, TemporalType.TIMESTAMP);
        resQuery.setParameter("eTime", endTime, TemporalType.TIMESTAMP);
        resQuery.setParameter("status", status);
        List<ReservationEntity> resList = resQuery.getResultList();
        return resList;
    }


    public List<ReservationEntity> getReservationsCrossingTime(Date position){
        Query resQuery = em.createQuery("select r from ReservationEntity r where (r.endTime >= :sTime)");
        resQuery.setParameter("sTime", position, TemporalType.TIMESTAMP);
        return resQuery.getResultList();
    }

    public boolean deleteReservation(String reservationID) throws PexStorageFailedException {
        try {
            em.getTransaction().begin();
            ReservationEntity re = em.find(ReservationEntity.class, reservationID);
            em.remove(re);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.severe("Deleting reservation " + reservationID + " failed");
            throw new PexStorageFailedException("Deleting reservation " + reservationID + " failed", e);
        }
        return true;
    }

    public List<ClusterNode> getNodes() throws PexStorageFailedException {
        List<ClusterNode> nodes = null;
        try {
            nodes = em.createQuery("SELECT n FROM ClusterNode n").getResultList();
        } catch (Exception e) {
            throw new PexStorageFailedException("Getting nodes failed ...", e);
        }
        return nodes;
    }

    public ClusterNode getNodeByName(String name) {
        return em.find(ClusterNode.class, name);
    }

    public List<ClusterNode> getAvailableNodes() {
        List<ClusterNode> nodes = em.createQuery("SELECT n FROM ClusterNode n where n.available = :avail").setParameter("avail", true).getResultList();
        return nodes;
    }

    public void saveNode(ClusterNode node) throws PexStorageFailedException {
        try {
            em.getTransaction().begin();
            ClusterNode old = em.find(ClusterNode.class, node.getName());

            if (old == null) {
                logger.info("Saving new node " + node.getName());
                em.persist(node);
            } else {
                logger.info("Updating node " + node.getName());
                old.setAllowed_vms(node.getAllowed_vms());
                old.setAvailable(node.isAvailable());
                old.setReservations(node.getReservations());
                old.setVmSet(node.getVmSet());
                em.persist(old);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.severe("Saving node " + node.getName() + " failed ");
            throw new PexStorageFailedException("Saving node failed ", e);
        }
    }

    public VMInstance getVm(String vmID) {
        return em.find(VMInstance.class, vmID);
    }

    public void saveVM(VMInstance vmInst) throws PexStorageFailedException {
        try {
            em.getTransaction().begin();
            VMInstance vm = em.find(VMInstance.class, vmInst.getVmID());

            if (vm == null) {
                logger.info("Saving new vm " + vmInst.getVmID());
                em.persist(vmInst);
            } else {
                logger.info("updating vm " + vm.getVmID());
//                re = res;
                /*
                 * srikumar - This is a disgusting hack just because I havent udenrstood JPA yet.
                 */
                vm.setName(vmInst.getName());
                vm.setClusterNode(vmInst.getClusterNode());
                vm.setReservation(vmInst.getReservation());
                vm.setStart_time(vmInst.getStart_time());
                vm.setEnd_time(vmInst.getEnd_time());
                vm.setUserID(vmInst.getUserID());
                em.persist(vm);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.severe("Saving vm " + vmInst.getVmID() + " failed ");
            throw new PexStorageFailedException("Saving vm failed ", e);
        }
    }

    public VmUser getUserById(short userid) {
        return em.find(VmUser.class, userid);
    }

    public VmUser getUserByCred(String username, String password) {
        VmUser user = null;

        try {
            Query userQuery = em.createQuery("select r from VmUser r where" +
                    " (r.username = :user) AND (r.password = :pass )");
            userQuery.setParameter("user", username);
            userQuery.setParameter("pass", password);
            user = (VmUser) userQuery.getSingleResult();



        } catch (NoResultException nre) {
            logger.info("User " + username + " not found or incorrect pass");
        } catch (Exception e) {
            logger.severe("Login query failed");
        }

        return user;
    }

    public void saveUser(VmUser user) throws PexStorageFailedException {
        try {
            em.getTransaction().begin();
            //VmUser oldUser = em.find(VmUser.class, user.getUserid());

            //if (oldUser == null) {
                logger.info("Saving new user " + user.getUsername());
                em.persist(user);
            //} else {
            //    oldUser.setUsername(user.getUsername());
            //    oldUser.setFullname(user.getFullname());
            //    oldUser.setProjid(user.getProjid());
            //    oldUser.setBalance(user.getBalance());
            //    oldUser.setEmail(user.getEmail());
            //    oldUser.setFullname(user.getFullname());
            //    em.persist(oldUser);
            //}
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.severe("Saving user " + user.getUsername() + " failed ");
            throw new PexStorageFailedException("Saving user failed ", e);
        }
    }
}
