/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import static calculation.GaussianModel.calculateGrid;
import static calculation.GaussianModel.preparePtxFile;
import constants.Configuration;
import constants.DIMENSION;
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
import java.util.concurrent.TimeUnit;

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
        
        int i = 2;

        for (int j = 0; j < 40; j++) {
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
                Configuration.TIME_TEST[i]
            );

            //CUDA
            //Start time
            startTimeC = System.nanoTime();

            instance.calculate(dim, Configuration.TIME_TEST[i]);
            JSONObject resultObj = instance.getSortedResult();

            //Stop time
            stopTimeC = System.nanoTime();
            cudaTime = (stopTimeC - startTimeC)/ 1000000;

            //Classic way  
            //Start time
            startTime = System.nanoTime();

            instance.calculateWithoutCUDA(dim, Configuration.TIME_TEST[i]);
            resultObj = instance.getSortedResult();
            JSONArray result = (JSONArray) resultObj.get("result");

            //Stop time
            stopTime = System.nanoTime();
            classicTime = (stopTime - startTime)/ 1000000;

            CSVUtils.writeLine(writer, Arrays.asList(Double.toString(cudaTime), Double.toString(classicTime), Integer.toString(Configuration.TIME_TEST[i]), Double.toString(wind_speed_horizontal), 
                    Double.toString(wind_direction), Double.toString(release_height), Double.toString(source_strength), String.valueOf(stability_class_num),Integer.toString(MAX_AREA_DIMENSION) ));

            System.out.println("Time CUDA [ms]: "+cudaTime + " ;Time no CUDA [ms]: "+ classicTime + 
                    " ;number of points: "+Configuration.TIME_TEST[i]+
                    ";wind speed (u): "+wind_speed_horizontal + 
                    ";wind direction: "+ wind_direction + 
                    ";height (H): " + release_height + 
                    ";source strength (Q): " + source_strength + 
                    ";stability class:" + String.valueOf(stability_class_num) );

        }
        
        writer.flush();
        writer.close();
    }

    /**
     * Test of calculateWithoutCUDA method, of class Data.
     */
    @Test
    public void testCalculateWithoutCUDA(){
        
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
 
        double grid1 = calculateGrid(10.0, 2.0, Configuration.MAX_NUMBER_OF_POINTS);
        double grid2 = calculateGrid(10000.0, DEFAULT_AREA_HEIGHT, Configuration.MAX_NUMBER_OF_POINTS);
        double grid3 = calculateGrid(300.0, 30.0, Configuration.MAX_NUMBER_OF_POINTS);

        System.out.println(grid1);        
        junit.framework.Assert.assertTrue(grid1 > 0);
        
        System.out.println(grid2);        
        junit.framework.Assert.assertTrue(grid2 > 0);
        
        System.out.println(grid3);        
        junit.framework.Assert.assertTrue(grid3 > 0);
        
        System.out.println("Test Passed!");
        
    }
    
    @Test
    public void testTime(){
        
    }
    
}
