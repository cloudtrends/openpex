/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unimelb.openpex.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;
import org.unimelb.openpex.reservation.InstanceType;
import org.unimelb.openpex.reservation.ReservationProposal;
import org.unimelb.openpex.reservation.ReservationReply;
import org.unimelb.openpex.reservation.ReservationReply.ReservationReplyType;
import org.unimelb.openpex.rest.JsonHTTPDateValueProcessor;

/**
 *
 * @author brobergj
 */
public class RESTfulClientTest {

    static final String resEndpoint = "http://tyrellcorp.csse.unimelb.edu.au:8080/OpenPEX/reservations/";
    static final String instancesEndpoint = "http://tyrellcorp.csse.unimelb.edu.au:8080/OpenPEX/instances/";
    String pexUser = "test";
    String pexPass = "test";
    ReservationReplyType reply_type = null;

    public RESTfulClientTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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

    //@Test
    public void testListReservationsCall() throws MalformedURLException, IOException {
        System.out.print(listReservationsCall());
    }

    //@Test
    public void testListInstancesCall() throws MalformedURLException, IOException {
        System.out.print(listInstancesCall());
    }

    @Test
    public void testCreateReservationsCall() throws MalformedURLException, IOException {
        ReservationProposal re = new ReservationProposal();
        ReservationReply reply = new ReservationReply();

        Calendar startTime_ = Calendar.getInstance();
        startTime_.setTimeInMillis(System.currentTimeMillis());
        re.setTemplate("PEX Debian Etch 4.0 Template");
        re.setType(InstanceType.XLARGE);
        re.setStartTime(startTime_.getTime());
        re.setDuration(3600000);
        re.setNumInstancesFixed(1);
        re.setNumInstancesOption(0);

        String params = createReservationJSON(re);
        System.out.println("Sending POST to /reservations with following body:");
        System.out.println(params);

        String resResponse = createReservationCall(params);

        System.out.println("Response to POST to /reservations was:");
        System.out.println(resResponse);

        // Check reply was ACCEPT
        reply = createReservationReply(resResponse);

        if (reply.getReply() == ReservationReplyType.ACCEPT) {
            System.out.println("Sending PUT to /reservations/resid:");
            reply.setReply(ReservationReplyType.CONFIRM_REQUEST);
            String updateResResponse = updateReservationCall(reply);

            System.out.println("Response to PUT to /reservations/resid was:");
            System.out.println(updateResResponse);


        } else if (reply.getReply() == ReservationReplyType.COUNTER) {
            System.out.println("Sending PUT to /reservations/resid:");
            reply.setReply(ReservationReplyType.ACCEPT);
            String updateResResponse = updateReservationCall(reply);

            System.out.println("Response to first PUT to /reservations/resid was:");
            System.out.println(updateResResponse);

            reply = createReservationReply(updateResResponse);
            reply.setReply(ReservationReplyType.CONFIRM_REQUEST);
            updateResResponse = updateReservationCall(reply);
            System.out.println("Response to second PUT to /reservations/resid was:");
            System.out.println(updateResResponse);


        } else {
            System.out.println("Reservation not accepted");
        }

        String activateResResponse = activateReservationCall(reply.getProposal().getId());
        System.out.println("Response to PUT to /reservations/resid/activate was:");
        System.out.println(activateResResponse);


//        System.out.println("Sending another PUT to /reservations/resid:");
//        String updateResResponse2 = updateReservationCall(updateResResponse);
//        System.out.println("Response to another PUT to /reservations/resid was:");
//        System.out.println(updateResResponse2);

    }

    public String createReservationCall(String params) throws MalformedURLException, IOException {
        URL url = new URL(resEndpoint);
        DataOutputStream out;
        String response;

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        conn.addRequestProperty("OpenPEX-User", pexUser);
        conn.addRequestProperty("OpenPEX-Pass", pexPass);

        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        conn.setRequestProperty("Content-Type", "application/json");
        // Send POST output.
        out = new DataOutputStream(conn.getOutputStream());

        out.writeBytes(params);
        out.flush();
        out.close();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String line = reader.readLine();
        StringBuffer content = new StringBuffer();
        while (line != null) {
            content.append(line + "\n");
            line = reader.readLine();
        }
        response = content.toString();
        reader.close();
        conn.disconnect();

        return response;
    }

    public String updateReservationCall(ReservationReply reply) throws MalformedURLException, IOException {


        this.reply_type = reply.getReply();

        if (reply.getReply() == ReservationReplyType.ACCEPT) {
            System.out.println("Confirm accept offer");
        //reply.setReply(ReservationReplyType.CONFIRM_ACCEPT);
        } else if (reply.getReply() == ReservationReplyType.COUNTER) {
            System.out.println("Accepting counter offer");
        //reply.setReply(ReservationReplyType.ACCEPT);
        }



        URL url = new URL(resEndpoint + reply.getProposal().getId());
        DataOutputStream out;
        String response;

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");

        conn.addRequestProperty("OpenPEX-User", pexUser);
        conn.addRequestProperty("OpenPEX-Pass", pexPass);

        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        conn.setRequestProperty("Content-Type", "application/json");
        // Send POST output.
        out = new DataOutputStream(conn.getOutputStream());

        out.writeBytes(createReservationReplyJSON(reply));
        out.flush();
        out.close();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String line = reader.readLine();
        StringBuffer content = new StringBuffer();
        while (line != null) {
            content.append(line + "\n");
            line = reader.readLine();
        }
        response = content.toString();
        reader.close();
        conn.disconnect();

        return response;

    }

    public String activateReservationCall(String resId) throws MalformedURLException, IOException {

        URL url = new URL(resEndpoint + resId + "/" + "activate");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.addRequestProperty("OpenPEX-User", pexUser);
        conn.addRequestProperty("OpenPEX-Pass", pexPass);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        InputStream is = conn.getInputStream();
        String response = conn.getResponseMessage();

        is.close();
        conn.disconnect();

        return response;


    }

    public String createReservationJSON(ReservationProposal re) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {

            public boolean apply(Object source, String name, Object value) {
                if ("id".equals(name) || "userid".equals(name)) {
                    return true;
                }
                return false;
            }
        });
        jsonConfig.registerJsonValueProcessor(ReservationProposal.class, "startTime", new JsonHTTPDateValueProcessor());

        JSONObject jsonResponse = (JSONObject) JSONSerializer.toJSON(re, jsonConfig);
        return jsonResponse.toString(3);
    }

    public String createReservationReplyJSON(ReservationReply reply) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(ReservationProposal.class, "startTime", new JsonHTTPDateValueProcessor());
        JSONObject jsonResponse = (JSONObject) JSONSerializer.toJSON(reply, jsonConfig);
        return jsonResponse.toString(3);
    }

    public ReservationReply createReservationReply(String params) {
        JSONObject jsonRequest = (JSONObject) JSONSerializer.toJSON(params);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.setIgnoreJPATransient(true);
        jsonConfig.setRootClass(ReservationReply.class);
        jsonConfig.registerJsonValueProcessor(ReservationProposal.class, "startTime", new JsonHTTPDateValueProcessor());

        ReservationReply reply = (ReservationReply) JSONSerializer.toJava(jsonRequest, jsonConfig);
        return reply;
    }

    public String listReservationsCall() throws MalformedURLException, IOException {
        URL url = new URL(resEndpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("OpenPEX-User", pexUser);
        conn.addRequestProperty("OpenPEX-Pass", pexPass);
        conn.setRequestMethod("GET");

        conn.setDoInput(true);
        conn.setUseCaches(false);
        String response;

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String line = reader.readLine();
        StringBuffer content = new StringBuffer();
        while (line != null) {
            content.append(line + "\n");
            line = reader.readLine();
        }
        response = content.toString();
        reader.close();
        conn.disconnect();

        return response;
    }

    public String listInstancesCall() throws MalformedURLException, IOException {
        URL url = new URL(instancesEndpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("OpenPEX-User", pexUser);
        conn.addRequestProperty("OpenPEX-Pass", pexPass);
        conn.setRequestMethod("GET");

        conn.setDoInput(true);
        conn.setUseCaches(false);
        String response;

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String line = reader.readLine();
        StringBuffer content = new StringBuffer();
        while (line != null) {
            content.append(line + "\n");
            line = reader.readLine();
        }
        response = content.toString();
        reader.close();
        conn.disconnect();

        return response;
    }
}