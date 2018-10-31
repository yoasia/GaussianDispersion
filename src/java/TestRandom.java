/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import calculation.CSVUtils;
import calculation.GaussianModel;
import constants.Configuration;
import static constants.Configuration.MAX_AREA_DIMENSION;
import static constants.Configuration.MAX_NUMBER_OF_POINTS;
import static constants.Configuration.NO_OUTPUT_HEIGHT;
import static constants.Configuration.RESULT_PATH;
import constants.DIMENSION;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author yoa
 */
@WebServlet(urlPatterns = {"/test"})
public class TestRandom extends HttpServlet {

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
        
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String ifCuda = request.getParameter("cuda");
        boolean cuda = (ifCuda == null ? false : Boolean.parseBoolean(ifCuda));

        try (PrintWriter out = response.getWriter()) {
            double wind_speed_horizontal;
            double wind_direction;
            double release_height;
            double source_strength;
            int stability_class_num;
            double lon = 20;
            double lat = 52;
            int ticks;
            long startTime, startTimeC;
            long stopTime, stopTimeC;
            long cudaTime = 0, classicTime = 0;

            String csvFileName;
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("DD-MM_HH-mm");
            DIMENSION dim = DIMENSION.THREE;
            GaussianModel instance;
            csvFileName = RESULT_PATH+"timeTest_"+sdf.format(cal.getTime())+".csv";
            FileWriter writer = new FileWriter(csvFileName);

            System.out.println("Time test. Result saved in "+ csvFileName);
            CSVUtils.writeLine(writer, Arrays.asList("time CUDA [ms]", "time no CUDA [ms]", "number of points", "wind speed", "wind direction", "height", "Q", "stablity class", "area dimension"));


            wind_speed_horizontal = Math.random() * 49.5 + 0.5;
            wind_direction = Math.random() * 360 ;
            release_height = Math.random() * 300 + 0;
            source_strength = Math.random() * 1000 ; 
            stability_class_num = (int) (Math.random() * 5 + 1) ;
            instance = new GaussianModel(
                wind_speed_horizontal, 
                wind_direction, 
                release_height,
                source_strength,
                stability_class_num,              
                lon, 
                lat,
                MAX_AREA_DIMENSION,                
                NO_OUTPUT_HEIGHT ,
                MAX_NUMBER_OF_POINTS
            );

            //CUDA
            if(cuda)
                instance.calculate(dim, MAX_NUMBER_OF_POINTS);
            else{
             //Classic way  
                instance.calculateWithoutCUDA(dim, MAX_NUMBER_OF_POINTS);
            }

            JSONObject json = instance.getRangedResult("CO");

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
