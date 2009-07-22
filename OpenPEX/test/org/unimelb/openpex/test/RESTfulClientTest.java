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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.unimelb.openpex.reservation.InstanceType;
import org.unimelb.openpex.reservation.ReservationEntity;

/**
 *
 * @author brobergj
 */
public class RESTfulClientTest {

    static final String resEndpoint = "http://tyrellcorp.csse.unimelb.edu.au:8080/OpenPEX/reservations/";
    static final String instancesEndpoint = "http://tyrellcorp.csse.unimelb.edu.au:8080/OpenPEX/instances/";
    String pexUser = "";
    String pexPass = "";

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

    @Test
    public void testListReservationsCall() throws MalformedURLException, IOException {
        System.out.print(listReservationsCall());
    }

    @Test
    public void testCreateReservationsCall() throws MalformedURLException, IOException, JSONException {
        ReservationEntity re = new ReservationEntity();
        re.setTemplate("PEX Windows XP SP2 Template");
        re.setType(InstanceType.SMALL);
        re.setNumInstancesFixed(Short.parseShort("1"));
        re.setStartTime(new Date(System.currentTimeMillis()));
        re.setEndTime(new Date(System.currentTimeMillis() + 3600000));

        String params = createReservation(re);
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

    public String createReservation(ReservationEntity re) throws JSONException {
        JSONArray jsonResponse = new JSONArray();
        HashMap mapRe = new HashMap();
        mapRe.put("templates", re.getTemplate());
        mapRe.put("instance_type", re.getType());
        mapRe.put("instances", re.getNumInstancesFixed());
        mapRe.put("start_time", re.getStartTime().toString());
        mapRe.put("end_time", re.getEndTime().toString());
        jsonResponse.put(mapRe);
        return jsonResponse.toString(3);
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
}