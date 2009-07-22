/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unimelb.openpex.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.unimelb.openpex.VMInstance;
import org.unimelb.openpex.VmUser;
import org.unimelb.openpex.storage.PexStorage;

/**
 *
 * @author brobergj
 */
public class Instances extends HttpServlet {

    PexStorage store = PexStorage.getInstance();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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

        List<VMInstance> instances = store.getVMInstancesbyUserid(vmuser.getUserid());

        for (Iterator it = instances.iterator(); it.hasNext();) {
            VMInstance vm = (VMInstance) it.next();
            HashMap mapVm = new HashMap();
            mapVm.put("vm_id", vm.getVmID());
            mapVm.put("reservation_id", vm.getReservation().getRequestId());
            mapVm.put("start_time", vm.getStart_time().toString());
            mapVm.put("end_time", vm.getEnd_time().toString());
            mapVm.put("name", vm.getName());
            mapVm.put("ip_address", vm.getIpAddress());
            mapVm.put("status", vm.getStatus());
            mapVm.put("userid", vm.getUserID());
            mapVm.put("vm_pass", vm.getVmPassword());
            mapVm.put("node_name", vm.getClusterNode().getName());

            jsonResponse.put(mapVm);
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
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Instances</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Instances at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
             */
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
        processRequest(request, response);
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
