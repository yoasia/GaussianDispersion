/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import constants.DIMENSION;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import static constants.Configuration.MAX_AREA_DIMENSION;
import static constants.Configuration.NO_OUTPUT_HEIGHT;

/**
 *
 * @author joanna
 */
@WebServlet(urlPatterns = {"/gauss"})
public class Servlet extends HttpServlet {

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
        
        calculation.GaussianModel data;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String s_wind_speed_horizontal = request.getParameter("wind_speed");
        String s_wind_direction = request.getParameter("wind_angle");
        String s_release_height = request.getParameter("h");
        String s_source_strength = request.getParameter("src_str");
        String s_stability_class_num = request.getParameter("stb_class");
        String s_dimension = request.getParameter("dimension");
        String s_lon = request.getParameter("lon");
        String s_lat = request.getParameter("lat");
        String s_dimension_h = request.getParameter("dimension_h");
        String s_output_h = request.getParameter("output_h");
        String s_2d = request.getParameter("_2d");
        String s_3d = request.getParameter("_3d");
        String s_geoJSON = request.getParameter("geoJSON");

        String content_type = request.getContentType();
        
        //Check if user set every parametr
        if(s_wind_speed_horizontal == null
        || s_wind_direction == null
        || s_release_height == null
        || s_source_strength == null
        || s_stability_class_num == null
        || s_lon == null
        || s_lat == null
        ){
            try (PrintWriter out = response.getWriter()) {
                JSONObject json = new JSONObject();

                json.put("message","Some parametr(s) not found");
                json.put("wind_speed_horizontal",s_wind_speed_horizontal);
                json.put("wind_angle",s_wind_direction);
                json.put("release_height",s_release_height);
                json.put("source_strength",s_source_strength);
                json.put("stability_class_num",s_stability_class_num);
                json.put("dimension",s_dimension);
                json.put("output_h",s_output_h);
                json.put("s_lon",s_lon);
                json.put("s_lat",s_lat);
                json.put("s_2d",s_2d);
                json.put("s_3d",s_3d);
                json.put("s_dimension_h",s_dimension_h);
                json.put("geoJSON",s_geoJSON);

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
        int stability_class_num = Integer.parseInt(s_stability_class_num);
        double lon = Double.parseDouble(s_lon);
        double lat = Double.parseDouble(s_lat);
        boolean _2d = (s_2d == null ? false : Boolean.parseBoolean(s_2d));
        //boolean _3d = (s_3d == null ? false: Boolean.parseBoolean(s_3d));
        boolean _geoJSON = (s_geoJSON == null ? false : Boolean.parseBoolean(s_geoJSON));

        double _dimension_h = (s_dimension_h == null ? NO_OUTPUT_HEIGHT : Double.parseDouble(s_dimension_h));
        double _output_h = (s_output_h == null ? NO_OUTPUT_HEIGHT : Double.parseDouble(s_output_h));

        double dimension = (s_dimension == null) ? MAX_AREA_DIMENSION: Double.parseDouble(s_dimension);

        data = new calculation.GaussianModel(wind_speed_horizontal, 
            wind_direction, 
            release_height,
            source_strength,
            stability_class_num,
            lon, 
            lat,
            _dimension_h,
            _output_h,
            dimension
            );
        
        if(_2d == true)
            data.calculate(DIMENSION.TWO);
        else
            data.calculate(DIMENSION.THREE);
        
        
        try (PrintWriter out = response.getWriter()) {
            JSONObject json = data.getRangedResult();
             
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
