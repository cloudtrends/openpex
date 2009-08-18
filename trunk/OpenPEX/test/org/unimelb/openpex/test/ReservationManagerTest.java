/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unimelb.openpex.test;

import java.util.Calendar;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.VmUser;
import org.unimelb.openpex.reservation.InstanceType;
import org.unimelb.openpex.reservation.ReservationProposal;
import org.unimelb.openpex.reservation.ReservationReply;
import org.unimelb.openpex.reservation.ReservationReply.ReservationReplyType;
import org.unimelb.openpex.reservation.SimpleReservationManager;
import org.unimelb.openpex.storage.PexStorage;
import static org.junit.Assert.*;

/**
 *
 * @author dipu
 */
public class ReservationManagerTest {

    protected static Logger logger = Logger.getLogger(ReservationManagerTest.class.getName());
    static String reservationId = null;
    static short userid = 0;
    static ReservationReply reply=null;
    SimpleReservationManager srmanager = null;

    public ReservationManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws PexException {
        srmanager = new SimpleReservationManager();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testInitiateReservation() throws Exception {

        userid = SaveUser();
        reservationId = srmanager.initiateReservation(userid);
        assertNotNull(reservationId);
        System.out.println("reservationId: " + reservationId);


    }

    @Test
    public void testrequestReservation() throws Exception {
        saveNodes();
        ReservationProposal rp = getReservationProposal();
        reply =srmanager.requestReservation(reservationId, rp);
        assertEquals(ReservationReplyType.ACCEPT,reply.getReply());
    }

    /*@Test
    public void testReplyToCounter() throws Exception{
        ReservationReply newReply = srmanager.replyToCounter(reservationId, reply);
    }*/
    @Test
    public void testconfirmReservation() throws Exception{
        logger.info("Starting Confirm reservation......");
        saveNodes();
        reservationId = srmanager.initiateReservation(userid);
        ReservationProposal rp = getReservationProposal();
        reply =srmanager.requestReservation(reservationId, rp);
        boolean expResult = true;
        ReservationProposal resProposal = getReservationProposal();
        boolean result = srmanager.confirmReservation(reservationId, resProposal);
        assertEquals(expResult,result);
        logger.info("End Confirm reservation......");
    }

    @Test
    public void testDeleteReservation() throws Exception {

        System.out.println("testDeleteReservation:" + reservationId);
        boolean isdeleted = srmanager.deleteReservation(reservationId);
        boolean expresult = true;
        assertEquals(expresult, isdeleted);

    }
    

    private short SaveUser() throws Exception {
        System.out.println("In testSaveUser method");
        VmUser vmUser1 = new VmUser();
        vmUser1.setUsername("dipu");
        vmUser1.setEmail("dipu_iub@yahoo.com");
        vmUser1.setFullname("Afzal Hossain");
        vmUser1.setPassword("dipu123");
        vmUser1.setBalance(500);
        short projid = 3;
        vmUser1.setProjid(projid);

        PexStorage storage = PexStorage.getInstance();
        storage.saveUser(vmUser1);
        System.out.println("userId:" + vmUser1.getUserid());
        return vmUser1.getUserid();

    }

    private void saveNodes() throws Exception {
        Calendar reservationEpoch = Calendar.getInstance();
        reservationEpoch.add(Calendar.MINUTE, 5);
        PexStorage store = PexStorage.getInstance();

        //We must use Argumnent constructor otherwise timeSlotMap becomes null
        TestClusterNode node1 = new TestClusterNode("node1", "127.0.0.1", reservationEpoch);

//
//        node1.setName("node1");
//        node1.setIpAddress("127.0.0.1");
//        node1.setReservationEpoch(reservationEpoch);
        node1.setAvailable(true);

        TestClusterNode node2 = new TestClusterNode("node2", "127.0.0.1", reservationEpoch);
//        node2.setName("node2");
//        node2.setIpAddress("127.0.0.1");
//        node2.setReservationEpoch(reservationEpoch);
        node2.setAvailable(true);
        store.saveNode(node1);
        store.saveNode(node2);
    }

    private ReservationProposal getReservationProposal() {
        ReservationProposal rp = new ReservationProposal(reservationId);
        rp.setNumInstancesFixed(2);
        rp.setNumInstancesOption(2);
        rp.setTemplate("template");
        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.MINUTE, 0);
        rp.setStartTime(startTime.getTime());
        rp.setType(InstanceType.SMALL);
        rp.setUserid(userid);
        rp.setDuration(1200000);
        return rp;
    }
}
