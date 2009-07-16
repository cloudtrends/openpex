/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unimelb.openpex.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
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
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JSONObject jsonResponse = new JSONObject();

        //response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/json");



        //String auth = request.getHeader("Authorization");
        //String user = request.getHeader("OpenPEX-User");
        //String pass = request.getHeader("OpenPEX-Pass");
        String path = request.getPathInfo();
        short user = Short.parseShort(path.substring(1));

        PrintWriter out = response.getWriter();

        VmUser vmuser = store.getUserById(user);
        List<ReservationEntity> reservations = store.getReservationsbyUserid(user);

        

        try {
            /* TODO output your page here */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Reservations</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Reservations at " + request.getContextPath () + "</h1>");
            out.println(user);
            out.println("</body>");
            out.println("</html>");
             
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
        processRequest(request, response);
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
