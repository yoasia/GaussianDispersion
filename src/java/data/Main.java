/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



/**
 *
 * @author Joanna
 */
public class Main {
    public static final String RESOURCE_PATH = "/home/joanna/Dokumenty/Magisterka/Server/src/resources/";
    private JCudaGauss gaussObject;
    private static Data dataObject;
    
    public static JSONObject main(DIMENSION dim) throws IOException {
        
        readInput();
        
        dataObject.calculate(dim);
        
        return dataObject.getResult();
    
    }
    
        
    public static void readInput(){
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
            int stability_class_num = (int) jsonObject.get("stability_class_num");
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
}
