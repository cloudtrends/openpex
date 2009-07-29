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
package org.unimelb.openpex.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.ResourceManager;
import org.unimelb.openpex.VmUser;
import org.unimelb.openpex.reservation.ReservationEntity;
import org.unimelb.openpex.reservation.ReservationManager;
import org.unimelb.openpex.reservation.ReservationProposal;
import org.unimelb.openpex.reservation.ReservationReply;
import org.unimelb.openpex.storage.PexStorage;

/**
 *
 * @author brobergj
 */
public class Reservations extends HttpServlet {

    PexStorage store = PexStorage.getInstance();
    ReservationManager rm;

    /** 
     * Processes requests for HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONArray jsonResponse = new JSONArray();
        response.setContentType("application/json");

        //String auth = request.getHeader("Authorization");
        String user = request.getHeader("OpenPEX-User");
        String pass = request.getHeader("OpenPEX-Pass");

        //String path = request.getPathInfo();
        //short user = Short.parseShort(path.substring(1));

        if (user == null || pass == null) {
            response.sendError(response.SC_BAD_REQUEST);
            return;
        } else {
            System.out.println("Creds " + user + " " + pass);
        }

        PrintWriter out = response.getWriter();

        VmUser vmuser = store.getUserByCred(user, pass);

        List<ReservationEntity> reservations = store.getReservationsbyUserid(vmuser.getUserid());

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {

            public boolean apply(Object source, String name, Object value) {
                if ("vmSet".equals(name) || "nodes".equals(name)) {
                    return true;
                }
                return false;
            }
        });

        // Transforms java.util.Date into a JSONObject ideal for JsDate conversion.
        // jsonConfig.registerJsonBeanProcessor( Date.class, new JsDateJsonBeanProcessor() );
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonHTTPDateValueProcessor());




        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.setIgnoreJPATransient(true);
        jsonResponse.addAll(reservations, jsonConfig);
        out.print(jsonResponse.toString(3));
        out.close();

    }

    /**
     * Processes requests for <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonRequest;
        ReservationProposal proposal = null;
        String resID = null;

        response.setContentType("application/json");

        //String auth = request.getHeader("Authorization");
        String user = request.getHeader("OpenPEX-User");
        String pass = request.getHeader("OpenPEX-Pass");

        if (user == null || pass == null) {
            response.sendError(response.SC_BAD_REQUEST);
            return;
        } else {
            System.out.println("Creds " + user + " " + pass);

        }

        PrintWriter out = response.getWriter();


        VmUser vmuser = store.getUserByCred(user, pass);

        try {
            rm = ResourceManager.getInstance();
            resID = rm.initiateReservation(vmuser.getUserid());
        } catch (PexException ex) {
            Logger.getLogger(Reservations.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        StringBuffer content = new StringBuffer();
        while (line != null) {
            content.append(line + "\n");
            line = reader.readLine();
        }

        System.out.println("Received:");
        System.out.println(content.toString());

        jsonRequest = (JSONObject) JSONSerializer.toJSON(content.toString());

        System.out.println("JSON Received:");
        System.out.println(jsonRequest.toString(3));

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.setIgnoreJPATransient(true);

        jsonConfig.setJavaPropertyFilter(new PropertyFilter() {

            public boolean apply(Object source, String name, Object value) {
                if ("id".equals(name) || "userid".equals(name)) {
                    return true;
                }
                return false;
            }
        });

        //Map classMap = new HashMap();
        //classMap.put("startTime", Calendar.class);

        jsonConfig.setRootClass(ReservationProposal.class);
        jsonConfig.registerJsonValueProcessor(ReservationProposal.class, "startTime", new JsonHTTPDateValueProcessor());


        //proposal = (ReservationProposal) JSONObject.toBean(jsonRequest, ReservationProposal.class, classMap);

        proposal = (ReservationProposal) JSONSerializer.toJava(jsonRequest, jsonConfig);
        proposal.setUserid(vmuser.getUserid());

        System.out.println("duration " + proposal.getDuration());
        System.out.println("startTime " + proposal.getStartTime().toString());



        try {
            ReservationReply reply = rm.requestReservation(resID, proposal);
            if (reply.getReply() == ReservationReply.ReservationReplyType.ACCEPT) {
                rm.confirmReservation(resID, proposal);
                jsonResponse = (JSONObject) JSONSerializer.toJSON(reply, jsonConfig);
            } else if (reply.getReply() == ReservationReply.ReservationReplyType.COUNTER) {
                jsonResponse = (JSONObject) JSONSerializer.toJSON(reply, jsonConfig);
            } else {
                response.sendError(response.SC_BAD_REQUEST);
            }
        } catch (PexException e) {
            e.printStackTrace();
        }

        out.print(jsonResponse.toString(3));
        out.close();


    }

    /**
     * Processes requests for HTTP <code>PUT</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject jsonResponse = new JSONObject();
        JSONObject jsonRequest;
        ReservationProposal proposal = null;
        String resID = null;

        response.setContentType("application/json");

        //String auth = request.getHeader("Authorization");
        String user = request.getHeader("OpenPEX-User");
        String pass = request.getHeader("OpenPEX-Pass");

        if (user == null || pass == null) {
            response.sendError(response.SC_BAD_REQUEST);
            return;
        } else {
            System.out.println("Creds " + user + " " + pass);

        }

        String path = request.getPathInfo();
        String reqId = path.substring(1);

        if (reqId.length() != 36) {
            response.sendError(response.SC_BAD_REQUEST);
        }


        PrintWriter out = response.getWriter();

        VmUser vmuser = store.getUserByCred(user, pass);

        try {
            rm = ResourceManager.getInstance();
            resID = rm.initiateReservation(vmuser.getUserid());
        } catch (PexException ex) {
            Logger.getLogger(Reservations.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        StringBuffer content = new StringBuffer();
        while (line != null) {
            content.append(line + "\n");
            line = reader.readLine();
        }

        System.out.println("Received:");
        System.out.println(content.toString());

        jsonRequest = (JSONObject) JSONSerializer.toJSON(content.toString());

        System.out.println("JSON Received:");
        System.out.println(jsonRequest.toString(3));

        String replyType = jsonRequest.getString("reply_type");

        if (replyType.equals("ACCEPT")) {

        } else if (replyType.equals("ACCEPT")) {

        } else {
            response.sendError(response.SC_BAD_REQUEST);
        }


    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processGet(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPost(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPut(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
