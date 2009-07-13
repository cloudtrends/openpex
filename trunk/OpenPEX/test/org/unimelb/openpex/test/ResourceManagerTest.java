/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.unimelb.openpex.test;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.unimelb.openpex.Bootstrap;
import org.unimelb.openpex.NodeMonitor;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.ResourceManager;
import org.unimelb.openpex.VMInstance;
import org.unimelb.openpex.VMListener;
import org.unimelb.openpex.reservation.ReservationProposal;
import org.unimelb.openpex.reservation.ReservationReply;
import org.unimelb.openpex.storage.PexStorage;
import static org.junit.Assert.*;

/**
 *
 * @author srikumar
 */
public class ResourceManagerTest {

    
    static Logger logger = Logger.getLogger(NodeMonitor.class.getName());
    public ResourceManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
                /*
         * First, start the resource manager by connecting to the Xen machine
         */
        try {
            ResourceManager manager = ResourceManager.getInstance();
            System.out.println("Obtained the resource manager");
            NodeMonitor monitor = NodeMonitor.getInstance();
            System.out.println("Obtained the nodemonitor");
            Calendar reservationEpoch = Calendar.getInstance();

            /*
             * Reservation epoch is 5 minutes into the future
             */
            reservationEpoch.add(Calendar.MINUTE, 5);
            System.out.println("the reservation epoch is " + reservationEpoch.getTime().toString());

            PexStorage store = PexStorage.getInstance();

            /*
             * Test Nodes
             */
            store.saveNode(new TestClusterNode("node1", "127.0.0.1", reservationEpoch));
            store.saveNode(new TestClusterNode("node2", "127.0.0.1", reservationEpoch));
            store.saveNode(new TestClusterNode("node3", "127.0.0.1", reservationEpoch));
            store.saveNode(new TestClusterNode("node4", "127.0.0.1", reservationEpoch));

            logger.exiting(Bootstrap.class.getName(), "Bootstrap complete.. over to you");

        } catch (PexException e) {
            logger.severe("Error in Pex Initialisation");
            throw new PexException("Nodes not found", e);
        } catch (Exception e) {
            logger.severe("Unknown failure");
            throw new PexException("Unknown oh noes1111", e);
        }
        PexStorage store = PexStorage.getInstance();
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }


    /**
     * Test of createVM method, of class ResourceManager.
     */
    @Test
    public void testCreateVM() throws Exception {
        System.out.println("createVM");
        String reservationID = "";
        String name = "";
        ResourceManager instance = null;
        String expResult = "";
        String result = instance.createVM(reservationID, name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createVMInstance method, of class ResourceManager.
     */
    @Test
    public void testCreateVMInstance() throws Exception {
        System.out.println("createVMInstance");
        String reservationID = "";
        String name = "";
        boolean autoStart = false;
        ResourceManager instance = null;
        String expResult = "";
        String result = instance.createVMInstance(reservationID, name, autoStart);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of startVM method, of class ResourceManager.
     */
    @Test
    public void testStartVM() throws Exception {
        System.out.println("startVM");
        String reservationID = "";
        String vmID = "";
        VMListener listener = null;
        ResourceManager instance = null;
        instance.startVM(reservationID, vmID, listener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of startVMInstance method, of class ResourceManager.
     */
    @Test
    public void testStartVMInstance() throws Exception {
        System.out.println("startVMInstance");
        String reservationID = "";
        String vmID = "";
        ResourceManager instance = null;
        instance.startVMInstance(reservationID, vmID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of stopVM method, of class ResourceManager.
     */
    @Test
    public void testStopVM() throws Exception {
        System.out.println("stopVM");
        String reservationID = "";
        String vmID = "";
        ResourceManager instance = null;
        instance.stopVM(reservationID, vmID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTemplateStrings method, of class ResourceManager.
     */
    @Test
    public void testGetTemplateStrings() throws Exception {
        System.out.println("getTemplateStrings");
        ResourceManager instance = null;
        List<String> expResult = null;
        List<String> result = instance.getTemplateStrings();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPexTemplateStrings method, of class ResourceManager.
     */
    @Test
    public void testGetPexTemplateStrings() throws Exception {
        System.out.println("getPexTemplateStrings");
        ResourceManager instance = null;
        List<String> expResult = null;
        List<String> result = instance.getPexTemplateStrings();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of statusChanged method, of class ResourceManager.
     */
    @Test
    public void testStatusChanged() {
        System.out.println("statusChanged");
        VMInstance instance_2 = null;
        ResourceManager instance = null;
        instance.statusChanged(instance_2);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMacList method, of class ResourceManager.
     */
    @Test
    public void testGetMacList() {
        System.out.println("getMacList");
        ResourceManager instance = null;
        ConcurrentLinkedQueue<String> expResult = null;
        ConcurrentLinkedQueue<String> result = instance.getMacList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMacList method, of class ResourceManager.
     */
    @Test
    public void testSetMacList() {
        System.out.println("setMacList");
        List<String> macList = null;
        ResourceManager instance = null;
        /*instance.setMacList(macList);*/
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUniqueIdentifier method, of class ResourceManager.
     */
    @Test
    public void testGetUniqueIdentifier() {
        System.out.println("getUniqueIdentifier");
        ResourceManager instance = null;
        long expResult = 0L;
        long result = instance.getUniqueIdentifier();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of initiateReservation method, of class ResourceManager.
     */
    @Test
    public void testInitiateReservation() throws Exception {
        System.out.println("initiateReservation");
        short userid = 0;
        ResourceManager instance = null;
        String expResult = "";
        String result = instance.initiateReservation(userid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of requestReservation method, of class ResourceManager.
     */
    @Test
    public void testRequestReservation() throws Exception {
        System.out.println("requestReservation");
        String reservationID = "";
        ReservationProposal request = null;
        ResourceManager instance = null;
        ReservationReply expResult = null;
        ReservationReply result = instance.requestReservation(reservationID, request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of replyToCounter method, of class ResourceManager.
     */
    @Test
    public void testReplyToCounter() throws Exception {
        System.out.println("replyToCounter");
        String reservationID = "";
        ReservationReply reply = null;
        ResourceManager instance = null;
        ReservationReply expResult = null;
        ReservationReply result = instance.replyToCounter(reservationID, reply);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of confirmReservation method, of class ResourceManager.
     */
    @Test
    public void testConfirmReservation() throws Exception {
        System.out.println("confirmReservation");
        String reservationID = "";
        ReservationProposal proposal = null;
        ResourceManager instance = null;
        boolean expResult = false;
        boolean result = instance.confirmReservation(reservationID, proposal);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteReservation method, of class ResourceManager.
     */
    @Test
    public void testDeleteReservation() throws Exception {
        System.out.println("deleteReservation");
        String reservationID = "";
        ResourceManager instance = null;
        boolean expResult = false;
        boolean result = instance.deleteReservation(reservationID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}