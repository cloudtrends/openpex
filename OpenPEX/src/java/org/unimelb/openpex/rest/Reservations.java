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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.unimelb.openpex.VmUser;
import org.unimelb.openpex.reservation.ReservationEntity;
import org.unimelb.openpex.storage.PexStorage;

/**
 *
 * @author brobergj
 */
public class Reservations extends HttpServlet {

    PexStorage store = PexStorage.getInstance();

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
        //String user = request.getHeader("OpenPEX-User");
        //String pass = request.getHeader("OpenPEX-Pass");

        String path = request.getPathInfo();
        short user = Short.parseShort(path.substring(1));

        PrintWriter out = response.getWriter();

        VmUser vmuser = store.getUserById(user);
        List<ReservationEntity> reservations = store.getReservationsbyUserid(user);

        for (Iterator it = reservations.iterator(); it.hasNext();) {
            ReservationEntity re = (ReservationEntity) it.next();
            HashMap mapRe = new HashMap();
            mapRe.put("reservation_id", re.getRequestId());
            mapRe.put("status", re.getStatus());
            mapRe.put("templates", re.getTemplate());
            mapRe.put("instance_type", re.getType());
            mapRe.put("instances", re.getNumInstancesFixed());
            mapRe.put("start_time", re.getStartTime().toString());
            mapRe.put("end_time", re.getEndTime().toString());

            jsonResponse.put(mapRe);
        }


        try {
            out.print(jsonResponse.toString(3));
        } catch (JSONException ex) {
            Logger.getLogger(Reservations.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
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

        JSONArray jsonResponse = new JSONArray();
        response.setContentType("application/json");

        //String auth = request.getHeader("Authorization");
        //String user = request.getHeader("OpenPEX-User");
        //String pass = request.getHeader("OpenPEX-Pass");

        String path = request.getPathInfo();
        short user = Short.parseShort(path.substring(1));

        PrintWriter out = response.getWriter();

        VmUser vmuser = store.getUserById(user);
        List<ReservationEntity> reservations = store.getReservationsbyUserid(user);

        for (Iterator it = reservations.iterator(); it.hasNext();) {
            ReservationEntity re = (ReservationEntity) it.next();
            HashMap mapRe = new HashMap();
            mapRe.put("reservation_id", re.getRequestId());
            mapRe.put("status", re.getStatus());
            mapRe.put("templates", re.getTemplate());
            mapRe.put("instance_type", re.getType());
            mapRe.put("instances", re.getNumInstancesFixed());
            mapRe.put("start_time", re.getStartTime().toString());
            mapRe.put("end_time", re.getEndTime().toString());

            jsonResponse.put(mapRe);
        }


        try {
            out.print(jsonResponse.toString(3));
        } catch (JSONException ex) {
            Logger.getLogger(Reservations.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
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
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}