/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unimelb.openpex.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.unimelb.openpex.reservation.ReservationEntity;

/**
 *
 * @author brobergj
 */
public class RESTfulClient {

    String resEndpoint = "http://tyrellcorp.csse.unimelb.edu.au:8080/OpenPex/reservations/";
    String instancesEndpoint = "http://tyrellcorp.csse.unimelb.edu.au:8080/OpenPex/reservations/";

    public String createReservationCall(String params) throws MalformedURLException, IOException {
        URL url = new URL(resEndpoint);
        InputStream in = url.openStream();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("OpenPEX-User", "");
        conn.addRequestProperty("OpenPEX-Pass", "");
        String type = conn.getContentType();
        
        return null;
    }

    public String createReservation(ReservationEntity re) throws JSONException {
        JSONArray jsonResponse = new JSONArray();
        HashMap mapRe = new HashMap();
        mapRe.put("reservation_id", re.getRequestId());
        mapRe.put("status", re.getStatus());
        mapRe.put("templates", re.getTemplate());
        mapRe.put("instance_type", re.getType());
        mapRe.put("instances", re.getNumInstancesFixed());
        mapRe.put("start_time", re.getStartTime().toString());
        mapRe.put("end_time", re.getEndTime().toString());
        jsonResponse.put(mapRe);
        return jsonResponse.toString(3);
    }
}
