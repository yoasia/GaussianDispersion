package constants;

import jdk.nashorn.internal.objects.Global;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author joanna
 */
public class Configuration {

    private Configuration() { }
    
    //Can be changed!
    public static final String MY_PATH = "/home/yoa/Studia/Magisterka/Server/"; //path of the project directory
    public static final boolean SAVE_RESULT_IMAGE = false;  //images will be saved to folder "result" in project directory
    public static final int NUMBER_OF_DATA_RANGES = 6;    
    public static final double MIN_CONCENTRATION = 0.01; //[micro grams] Minimal consentration that will be returned  
    public static final int MAX_NUMBER_OF_POINTS = 400000;
    
    public static final int DEFAULT_AREA_HEIGHT = 10000; //[meters]
    
    //Change with caution
    public static int CUDA_BLOCK_SIZE_X = 1024;
    public static final int MAX_NUMBER_RETURNED_POINTS = 15000;
    public static final int MAX_AREA_DIMENSION = 10000; //[meters]
    
    //Do not change!
    public static final String RESOURCE_PATH = MY_PATH+"src/resources/";
    public static final String RESULT_PATH = MY_PATH+"results/";
    public static final double NO_OUTPUT_HEIGHT = -419;
    
    //array with max good, standard, alert, warning, emergency and significant harm concentration
    public static final String[] RANGES_NAMES =  new String[]{"Standard", "Alert", "Warning", "Emergency", "Significant Harm"};
    public static final double[] CO = new double[]{0, 2, 6, 9, 15, 21, 24, Global.Infinity}; //mg/m3
    public static final double[] SO2 = new double[]{0, 2, 9, 19, 38, 50, 63, Global.Infinity}; //mg/m3

}
