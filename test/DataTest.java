/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import calculation.CSVUtils;
import calculation.DIMENSION;
import calculation.Data;
import static calculation.Data.RESULT_PATH;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author joanna
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

//    /**
//     * Test of loadDemo method, of class Data.
//     */
//    @Test
//    public void testLoadDemo() {
//        System.out.println("loadDemo");
//        Data instance = new Data();
//        instance.loadDemo();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getResult method, of class Data.
//     */
//    @Test
//    public void testGetResult() {
//        System.out.println("getResult");
//        Data instance = new Data();
//        JSONObject expResult = null;
//        JSONObject result = instance.getResult();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getWind_speed_horizontal method, of class Data.
//     */
//    @Test
//    public void testGetWind_speed_horizontal() {
//        System.out.println("getWind_speed_horizontal");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getWind_speed_horizontal();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setWind_speed_horizontal method, of class Data.
//     */
//    @Test
//    public void testSetWind_speed_horizontal() {
//        System.out.println("setWind_speed_horizontal");
//        double wind_speed_horizontal = 0.0;
//        Data instance = new Data();
//        instance.setWind_speed_horizontal(wind_speed_horizontal);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getWind_direction method, of class Data.
//     */
//    @Test
//    public void testGetWind_direction() {
//        System.out.println("getWind_direction");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getWind_direction();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setWind_direction method, of class Data.
//     */
//    @Test
//    public void testSetWind_direction() {
//        System.out.println("setWind_direction");
//        double wind_direction = 0.0;
//        Data instance = new Data();
//        instance.setWind_direction(wind_direction);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getStd_deviation_y method, of class Data.
//     */
//    @Test
//    public void testGetStd_deviation_y() {
//        System.out.println("getStd_deviation_y");
//        Data instance = new Data();
//        double[] expResult = null;
//        double[] result = instance.getStd_deviation_y();
//        junit.framework.Assert.assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setStd_deviation_y method, of class Data.
//     */
//    @Test
//    public void testSetStd_deviation_y() {
//        System.out.println("setStd_deviation_y");
//        double[] std_deviation_y = null;
//        Data instance = new Data();
//        instance.setStd_deviation_y(std_deviation_y);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getStd_deviation_z method, of class Data.
//     */
//    @Test
//    public void testGetStd_deviation_z() {
//        System.out.println("getStd_deviation_z");
//        Data instance = new Data();
//        double[] expResult = null;
//        double[] result = instance.getStd_deviation_z();
//         junit.framework.Assert.assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setStd_deviation_z method, of class Data.
//     */
//    @Test
//    public void testSetStd_deviation_z() {
//        System.out.println("setStd_deviation_z");
//        double[] std_deviation_z = null;
//        Data instance = new Data();
//        instance.setStd_deviation_z(std_deviation_z);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getA method, of class Data.
//     */
//    @Test
//    public void testGetA() {
//        System.out.println("getA");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getA();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setA method, of class Data.
//     */
//    @Test
//    public void testSetA() {
//        System.out.println("setA");
//        double a = 0.0;
//        Data instance = new Data();
//        instance.setA(a);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getB method, of class Data.
//     */
//    @Test
//    public void testGetB() {
//        System.out.println("getB");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getB();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setB method, of class Data.
//     */
//    @Test
//    public void testSetB() {
//        System.out.println("setB");
//        double b = 0.0;
//        Data instance = new Data();
//        instance.setB(b);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRelease_height method, of class Data.
//     */
//    @Test
//    public void testGetRelease_height() {
//        System.out.println("getRelease_height");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getRelease_height();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRelease_height method, of class Data.
//     */
//    @Test
//    public void testSetRelease_height() {
//        System.out.println("setRelease_height");
//        double release_height = 0.0;
//        Data instance = new Data();
//        instance.setRelease_height(release_height);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getP method, of class Data.
//     */
//    @Test
//    public void testGetP() {
//        System.out.println("getP");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getP();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setP method, of class Data.
//     */
//    @Test
//    public void testSetP() {
//        System.out.println("setP");
//        double p = 0.0;
//        Data instance = new Data();
//        instance.setP(p);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getQ method, of class Data.
//     */
//    @Test
//    public void testGetQ() {
//        System.out.println("getQ");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getQ();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setQ method, of class Data.
//     */
//    @Test
//    public void testSetQ() {
//        System.out.println("setQ");
//        double q = 0.0;
//        Data instance = new Data();
//        instance.setQ(q);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSource_strength method, of class Data.
//     */
//    @Test
//    public void testGetSource_strength() {
//        System.out.println("getSource_strength");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getSource_strength();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSource_strength method, of class Data.
//     */
//    @Test
//    public void testSetSource_strength() {
//        System.out.println("setSource_strength");
//        double source_strength = 0.0;
//        Data instance = new Data();
//        instance.setSource_strength(source_strength);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRefflection_co method, of class Data.
//     */
//    @Test
//    public void testGetRefflection_co() {
//        System.out.println("getRefflection_co");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getRefflection_co();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRefflection_co method, of class Data.
//     */
//    @Test
//    public void testSetRefflection_co() {
//        System.out.println("setRefflection_co");
//        double refflection_co = 0.0;
//        Data instance = new Data();
//        instance.setRefflection_co(refflection_co);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getStability_class_num method, of class Data.
//     */
//    @Test
//    public void testGetStability_class_num() {
//        System.out.println("getStability_class_num");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getStability_class_num();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setStability_class_num method, of class Data.
//     */
//    @Test
//    public void testSetStability_class_num() {
//        System.out.println("setStability_class_num");
//        int stability_class_num = 0;
//        Data instance = new Data();
//        instance.setStability_class_num(stability_class_num);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getZ0 method, of class Data.
//     */
//    @Test
//    public void testGetZ0() {
//        System.out.println("getZ0");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getZ0();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setZ0 method, of class Data.
//     */
//    @Test
//    public void testSetZ0() {
//        System.out.println("setZ0");
//        double z0 = 0.0;
//        Data instance = new Data();
//        instance.setZ0(z0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGrid method, of class Data.
//     */
//    @Test
//    public void testGetGrid() {
//        System.out.println("getGrid");
//        Data instance = new Data();
//        double expResult = 0.0;
//        double result = instance.getGrid();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setGrid method, of class Data.
//     */
//    @Test
//    public void testSetGrid() {
//        System.out.println("setGrid");
//        double grid = 0.0;
//        Data instance = new Data();
//        instance.setGrid(grid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
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
        Data instance;
        
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

            instance = new Data(wind_speed_horizontal, 
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
                lat);


            instance.calculate(dim);
            JSONObject resultObj = instance.getResult();
            JSONArray result = (JSONArray) resultObj.get("result");
            CSVUtils.writeLine(writer, Arrays.asList("x", "y", "z", "concentration"));
            
            for (int j = 0; j < result.size(); j++) {
                JSONObject line = (JSONObject) result.get(i);
                CSVUtils.writeLine(writer, Arrays.asList(line.get("x"), line.get("y"), line.get("z"), line.get("value")));
                
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
        
        
        double[] result = Data.calculateTranslation(angle, area_dimension);
        double[] result1 = Data.calculateTranslation(angle1, area_dimension);
        double[] result2 = Data.calculateTranslation(angle2, area_dimension);
        double[] result3 = Data.calculateTranslation(angle3, area_dimension);
        double[] result4 = Data.calculateTranslation(angle4, area_dimension);
        double[] result5 = Data.calculateTranslation(angle5, area_dimension);
        double[] result6 = Data.calculateTranslation(angle6, area_dimension);
        double[] result7 = Data.calculateTranslation(angle7, area_dimension);
        double[] result8 = Data.calculateTranslation(angle8, area_dimension);
        double[] result9 = Data.calculateTranslation(angle9, area_dimension);
        double[] result10 = Data.calculateTranslation(angle10, area_dimension);
        double[] result11 = Data.calculateTranslation(angle11, area_dimension);
        double[] result12 = Data.calculateTranslation(angle12, area_dimension);
        double[] result13 = Data.calculateTranslation(angle13, area_dimension);
        double[] result14 = Data.calculateTranslation(angle14, area_dimension);
        double[] result15 = Data.calculateTranslation(angle15, area_dimension);

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
//
//    /**
//     * Test of getColor method, of class Data.
//     */
//    @Test
//    public void testGetColor() {
//        System.out.println("getColor");
//        int[] color1 = null;
//        int[] color2 = null;
//        double procent = 0.0;
//        int alpha = 0;
//        Data instance = new Data();
//        int[] expResult = null;
//        int[] result = instance.getColor(color1, color2, procent, alpha);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateNewCoords method, of class Data.
//     */
//    @Test
//    public void testCalculateNewCoords() {
//        System.out.println("calculateNewCoords");
//        double lon_start = 0.0;
//        double lat_start = 0.0;
//        double x_distance = 0.0;
//        double y_distance = 0.0;
//        Data instance = new Data();
//        double[] expResult = null;
//        double[] result = instance.calculateNewCoords(lon_start, lat_start, x_distance, y_distance);
//         junit.framework.Assert.assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
