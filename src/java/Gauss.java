/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import data.DIMENSION;
import data.Data;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author joanna
 */
@WebServlet(urlPatterns = {"/gauss"})
public class Gauss extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Data data;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String s_wind_speed_horizontal = request.getParameter("wind_speed");
        String s_wind_direction = request.getParameter("wind_angle");
        String s_release_height = request.getParameter("h");
        String s_source_strength = request.getParameter("src_str");
        String s_refflection_co = request.getParameter("reffl");
        String s_stability_class_num = request.getParameter("stb_class");
        String s_z0 = request.getParameter("z0");
        String s_a = request.getParameter("a");
        String s_b = request.getParameter("b");
        String s_p = request.getParameter("p");
        String s_q = request.getParameter("q");
        String s_area_dim = request.getParameter("dim");
        String s_grid = request.getParameter("grid");
        String s_dimension = request.getParameter("dimension");

        //Check if user set every parametr
        if(s_wind_speed_horizontal == null
        || s_wind_direction == null
        || s_release_height == null
        || s_source_strength == null
        || s_refflection_co == null
        || s_stability_class_num == null
        || s_a == null
        || s_z0 == null
        || s_b == null
        || s_p == null
        || s_q == null
        ){
            try (PrintWriter out = response.getWriter()) {
                JSONObject json = new JSONObject();

                json.put("message","Some parametr(s) not found");
                json.put("wind_speed_horizontal",s_wind_speed_horizontal);
                json.put("wind_angle",s_wind_direction);
                json.put("release_height",s_release_height);
                json.put("source_strength",s_source_strength);
                json.put("refflection_co",s_refflection_co);
                json.put("stability_class_num",s_stability_class_num);
                json.put("z0",s_z0);
                json.put("a",s_a);
                json.put("b",s_b);
                json.put("p",s_p);
                json.put("q",s_q);
                json.put("dim",s_area_dim);
                json.put("grid",s_grid);
                json.put("dimension",s_dimension);
                 
                 // finally output the json string       
                out.print(json);
                out.flush();
                out.close ();
                return;
            }
        }


        double wind_speed_horizontal = Double.parseDouble(s_wind_speed_horizontal);
        double wind_direction = Double.parseDouble(s_wind_direction);
        double release_height = Double.parseDouble(s_release_height);
        double source_strength = Double.parseDouble(s_source_strength);
        double refflection_co = Double.parseDouble(s_refflection_co);
        int stability_class_num = Integer.parseInt(s_stability_class_num);
        double z0 = Double.parseDouble(s_z0);
        double a = Double.parseDouble(s_a);
        double b = Double.parseDouble(s_b);
        double p = Double.parseDouble(s_p);
        double q = Double.parseDouble(s_q);
        double area_dimension = 0.0;
        double grid = 0.0;
        
        if(s_area_dim != null)
            area_dimension = Double.parseDouble(s_area_dim);
        if(s_grid != null)
            grid = Double.parseDouble(s_grid);

        if(s_area_dim != null && s_grid != null){
            data = new Data(wind_speed_horizontal, 
            wind_direction, 
            release_height,
            source_strength,
            refflection_co,
            stability_class_num,
            z0,
            a,
            b,
            p,
            q,
            area_dimension,
            grid);
        }
        else if(s_area_dim!=null){
            data = new Data(wind_speed_horizontal, 
            wind_direction, 
            release_height,
            source_strength,
            refflection_co,
            stability_class_num,
            z0,
            a,
            b,
            p,
            q,
            area_dimension);
        }
        else{

            data = new Data(wind_speed_horizontal, 
                wind_direction, 
                release_height,
                source_strength,
                refflection_co,
                stability_class_num,
                z0,
                a,
                b,
                p,
                q);
        }
        
        if(s_dimension == "2d")
            data.calculate(DIMENSION.TWO);
        else
            data.calculate(DIMENSION.THREE);

        
        
        try (PrintWriter out = response.getWriter()) {
            JSONObject json = data.getResult();
             
             // finally output the json string       
            out.print(json);
            out.flush();
            out.close ();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
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
     *
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
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
