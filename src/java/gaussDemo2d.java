/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import data.DIMENSION;
import data.Data;
import data.JCudaGauss;
import static data.Main.RESOURCE_PATH;
import static data.Main.readInput;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static java.lang.Math.toIntExact;

/**
 *
 * @author joanna
 */
@WebServlet(urlPatterns = {"/gaussDemo2d"})
public class gaussDemo2d extends HttpServlet {
    public static final String RESOURCE_PATH = "/home/joanna/Dokumenty/Magisterka/Server/src/resources/";
    private JCudaGauss gaussObject;
    private static Data dataObject;
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
        
        loadInput();
        dataObject.calculate(DIMENSION.TWO);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */

            //create Json Object
            JSONObject json = dataObject.getResult();
            out.flush();
            // finally output the json string       
            out.print(json);
            
            out.close (); 
        }
    }
    
    public static void loadInput(){
         JSONParser parser = new JSONParser();

        try {
            String absolutePath = RESOURCE_PATH+"input.json"; //relative path

            Object obj = parser.parse(new FileReader(absolutePath));

            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);

            double wind_speed_horizontal = (Long) jsonObject.get("wind_speed_horizontal");
            double wind_direction = (Long) jsonObject.get("wind_direction");
            double release_height = (Long) jsonObject.get("release_height");
            double source_strength = (Double) jsonObject.get("source_strength");
            double refflection_co = (Long) jsonObject.get("refflection_co");
            int stability_class_num = toIntExact((Long)jsonObject.get("stability_class_num"));
            double z0 = (Double) jsonObject.get("z0");
            double a = (Double) jsonObject.get("a");
            double b = (Double) jsonObject.get("b");
            double p = (Double) jsonObject.get("p");
            double q = (Double) jsonObject.get("q");
            double area_dimension = (Long) jsonObject.get("area_dimension");
            double grid = (Double) jsonObject.get("grid");
            
            dataObject = new Data(wind_speed_horizontal, 
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
            

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
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
