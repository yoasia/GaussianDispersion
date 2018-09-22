/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import constants.DIMENSION;
import static calculation.GaussianModel.calculateGrid;
import static calculation.GaussianModel.preparePtxFile;
import static constants.Configuration.MAX_AREA_DIMENSION;
import static constants.Configuration.NO_OUTPUT_HEIGHT;
import static constants.Configuration.RESULT_PATH;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import jcuda.driver.JCudaDriver;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static constants.Configuration.DEFAULT_AREA_HEIGHT;

/**
 *
 * @author yoa
 */
public class DataTest {
    
    public DataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
         
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of preparePtxFile method, of class Data.
     */
    @Test
    public void testPreparePtxFile() throws IOException {
        System.out.println("setGrid");

        // Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);

        // Create the PTX file by calling the NVCC
        String absolutePath = GaussianModel.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String ptxFileName =  preparePtxFile(absolutePath+"cuda/kernels/JCudaGaussKernel.cu");
        System.out.println("Test Passed!");
        
    }

    /**
     * Test of calculate method, of class Data.
     */
    @Test
    public void testCalculate() throws Exception {
        boolean noResult = true;
        double wind_speed_horizontal;
        double wind_direction;
        double release_height;
        double source_strength;
        double refflection_co = 1;
        int stability_class_num;
        double lon = 20;
        double lat = 52;
        double z0 = 0;
        double a = 0;
        double b = 0;
        double p = 0;
        double q = 0;
        FileWriter writer;
        String csvFileName;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("DD-MM_HH-mm");
        DIMENSION dim = DIMENSION.THREE;
        GaussianModel instance;
        
        System.out.println("calculateGauss");

        for (int i = 0; i < 100; i++) {
            noResult = true;
            System.out.println("TEST "+ i);
            csvFileName = RESULT_PATH+sdf.format(cal.getTime()) +i+"_3d.csv";
            writer = new FileWriter(csvFileName);

            wind_speed_horizontal = Math.random() * 49.5 + 0.5;
            wind_direction = Math.random() * 360 ;
            release_height = Math.random() * 300 + 0;
            source_strength = Math.random() * 1000 ; 
            stability_class_num = (int) Math.random() * 5 + 1 ;
            
            
            System.out.print("wind speed (u): "+wind_speed_horizontal + 
                    "\nwind direction: "+ wind_direction + 
                    "\nheight (H): " + release_height + 
                    "\nsource strength (Q): " + source_strength + 
                    "\nstability class:" + String.valueOf(stability_class_num) );

            instance = new GaussianModel(
                wind_speed_horizontal, 
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
                lon, 
                lat,
                NO_OUTPUT_HEIGHT,
                MAX_AREA_DIMENSION
            );

            instance.calculate(dim);
            JSONObject resultObj = instance.getSortedResult();
            JSONArray result = (JSONArray) resultObj.get("result");
            CSVUtils.writeLine(writer, Arrays.asList("lat", "lon", "z", "concentration"));
            
            for (int j = 0; j < result.size(); j++) {
                JSONObject line = (JSONObject) result.get(i);
                CSVUtils.writeLine(writer, Arrays.asList(line.get("lat"), line.get("lon"), line.get("z"), line.get("value")));
                
                if(Double.parseDouble((String)line.get("value")) > 0.0){
                    noResult = false;
                }
            }
            
            writer.flush();
            writer.close();
            if(noResult){
                System.err.println("Test failed.");
            }
            else{
                System.out.println("Test Passed!");
            }
            
            System.err.println(RESULT_PATH+sdf.format(cal.getTime()) +i+"_3d.csv");
            System.err.println("");
            
            junit.framework.Assert.assertEquals(noResult, false);

        }
    }

    /**
     * Test of calculateTranslation method, of class Data.
     */
    @Test
    public void testCalculateTranslation() {
        System.out.println("calculateTranslation");
        double area_dimension = 2000.0;
        double max = area_dimension/2;
        
        double angle = 0.0;
        double angle1 = 30.0;
        double angle2 = 45.0;
        double angle3 = 60.0;
        double angle4 = 90.0;
        double angle5 = 120.0;
        double angle6 = 135.0;
        double angle7 = 150.0;
        double angle8= 180.0;
        double angle9 = 210.0;
        double angle10 = 225.0;
        double angle11 = 240.0;
        double angle12 = 270.0;
        double angle13 = 300.0;
        double angle14 = 315.0;
        double angle15 = 330.0;
        
        double expResult[] = new double[2];
        double expResult1[] = new double[2];
        double expResult2[] = new double[2];
        double expResult3[] = new double[2];
        double expResult4[] = new double[2];
        double expResult5[] = new double[2];
        double expResult6[] = new double[2];
        double expResult7[] = new double[2];
        double expResult8[] = new double[2];
        double expResult9[] = new double[2];
        double expResult10[] = new double[2];
        double expResult11[] = new double[2];
        double expResult12[] = new double[2];
        double expResult13[] = new double[2];
        double expResult14[] = new double[2];
        double expResult15[] = new double[2];
        
        //0
        expResult[0] = -max;
        expResult[1] = 0.0;
        
        //30
        expResult1[0] = -max;
        expResult1[1] = -max * Math.tan(30 * Math.PI/180);
        
        //45
        expResult2[0] = -max;
        expResult2[1] = -max;
        
        //60
        expResult3[0] = -max * Math.tan(30 * Math.PI/180);
        expResult3[1] = -max;
        
        //90
        expResult4[0] = 0.0;
        expResult4[1] = -max;
        
        //120
        expResult5[0] = max * Math.tan(30 * Math.PI/180);
        expResult5[1] = -max;
        
        //135
        expResult6[0] = max;
        expResult6[1] = -max;
        
        //150
        expResult7[0] = max;
        expResult7[1] = -max * Math.tan(30 * Math.PI/180);
        
        //180
        expResult8[0] = max;
        expResult8[1] = 0.0;
        
        //210
        expResult9[0] = max;
        expResult9[1] = max * Math.tan(30 * Math.PI/180);
        
        //225
        expResult10[0] = max;
        expResult10[1] = max;
        
        //240
        expResult11[0] = max * Math.tan(30 * Math.PI/180);
        expResult11[1] = max;
        
        //270
        expResult12[0] = 0.0;
        expResult12[1] = max;
        
        //300
        expResult13[0] = -max * Math.tan(30 * Math.PI/180);
        expResult13[1] = max;
        
        //315
        expResult14[0] = -max;
        expResult14[1] = max;
        
        //330
        expResult15[0] = -max;
        expResult15[1] = max * Math.tan(30 * Math.PI/180);
        
        
        double[] result = GaussianModel.calculateTranslation(angle, area_dimension);
        double[] result1 = GaussianModel.calculateTranslation(angle1, area_dimension);
        double[] result2 = GaussianModel.calculateTranslation(angle2, area_dimension);
        double[] result3 = GaussianModel.calculateTranslation(angle3, area_dimension);
        double[] result4 = GaussianModel.calculateTranslation(angle4, area_dimension);
        double[] result5 = GaussianModel.calculateTranslation(angle5, area_dimension);
        double[] result6 = GaussianModel.calculateTranslation(angle6, area_dimension);
        double[] result7 = GaussianModel.calculateTranslation(angle7, area_dimension);
        double[] result8 = GaussianModel.calculateTranslation(angle8, area_dimension);
        double[] result9 = GaussianModel.calculateTranslation(angle9, area_dimension);
        double[] result10 = GaussianModel.calculateTranslation(angle10, area_dimension);
        double[] result11 = GaussianModel.calculateTranslation(angle11, area_dimension);
        double[] result12 = GaussianModel.calculateTranslation(angle12, area_dimension);
        double[] result13 = GaussianModel.calculateTranslation(angle13, area_dimension);
        double[] result14 = GaussianModel.calculateTranslation(angle14, area_dimension);
        double[] result15 = GaussianModel.calculateTranslation(angle15, area_dimension);

        System.out.println(angle);        
         junit.framework.Assert.assertEquals(round(expResult[0], 6), round( result[0], 6));
         junit.framework.Assert.assertEquals(round(expResult[1], 6), round( result[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle1);
         junit.framework.Assert.assertEquals(round(expResult1[0], 6), round( result1[0], 6));
         junit.framework.Assert.assertEquals(round(expResult1[1], 6), round( result1[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle2);        
         junit.framework.Assert.assertEquals(round(expResult2[0], 6), round( result2[0], 6));
         junit.framework.Assert.assertEquals(round(expResult2[1], 6), round( result2[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle3);  
         junit.framework.Assert.assertEquals(round(expResult3[0], 6), round( result3[0], 6));
         junit.framework.Assert.assertEquals(round(expResult3[1], 6), round( result4[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle4);  
         junit.framework.Assert.assertEquals(round(expResult4[0], 6), round( result4[0], 6));
         junit.framework.Assert.assertEquals(round(expResult4[1], 6), round( result4[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle5);  
         junit.framework.Assert.assertEquals(round(expResult5[0], 6), round( result5[0], 6));
         junit.framework.Assert.assertEquals(round(expResult5[1], 6), round( result5[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle6);  
         junit.framework.Assert.assertEquals(round(expResult6[0], 6), round( result6[0], 6));
         junit.framework.Assert.assertEquals(round(expResult6[1], 6), round( result6[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle7);  
         junit.framework.Assert.assertEquals(round(expResult7[0], 6), round( result7[0], 6));
         junit.framework.Assert.assertEquals(round(expResult7[1], 6), round( result7[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle8); 
         junit.framework.Assert.assertEquals(round(expResult8[0], 6), round( result8[0], 6));
         junit.framework.Assert.assertEquals(round(expResult8[1], 6), round( result8[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle9); 
         junit.framework.Assert.assertEquals(round(expResult9[0], 6), round( result9[0], 6));
         junit.framework.Assert.assertEquals(round(expResult9[1], 6), round( result9[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle10); 
         junit.framework.Assert.assertEquals(round(expResult10[0], 6), round( result10[0], 6));
         junit.framework.Assert.assertEquals(round(expResult10[1], 6), round( result10[1],  6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle11); 
         junit.framework.Assert.assertEquals(round(expResult11[0], 6), round( result11[0],  6));
         junit.framework.Assert.assertEquals(round(expResult11[1], 6), round( result11[1],  6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle12); 
         junit.framework.Assert.assertEquals(round(expResult12[0], 6), round( result12[0], 6));
         junit.framework.Assert.assertEquals(round(expResult12[1], 6), round( result12[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle13); 
         junit.framework.Assert.assertEquals(round(expResult13[0], 6), round( result13[0], 6));
         junit.framework.Assert.assertEquals(round(expResult13[1], 6), round( result13[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle14); 
         junit.framework.Assert.assertEquals(round(expResult14[0], 6), round( result14[0], 6));
         junit.framework.Assert.assertEquals(round(expResult14[1], 6), round( result14[1], 6));
        // TODO review the generated test code and remove the default call to fail.
        
        System.out.println(angle15); 
         junit.framework.Assert.assertEquals(round(expResult15[0], 6), round( result15[0], 6));
         junit.framework.Assert.assertEquals(round(expResult15[1], 6), round( result15[1], 6));
        // TODO review the generated test code and remove the default call to fail.
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    /**
     * Test of calculateGrid method, of class Data.
     */
    @Test
    public void testCalculateGrid() throws IOException {
        System.out.println("calculateGrid");
 
        double grid1 = calculateGrid(10.0, 2.0);
        double grid2 = calculateGrid(10000.0, DEFAULT_AREA_HEIGHT);
        double grid3 = calculateGrid(300.0, 30.0);

        System.out.println(grid1);        
        junit.framework.Assert.assertTrue(grid1 >= 1.0);
        
        System.out.println(grid2);        
        junit.framework.Assert.assertTrue(grid2 >= 1.0);
        
        System.out.println(grid3);        
        junit.framework.Assert.assertTrue(grid3 >= 1.0);
        
        System.out.println("Test Passed!");
        
    }
    
}
