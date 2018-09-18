package constants;

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
    public static final int MAX_NUMBER_OF_POINTS = 150000; //meters
    public static final int MAX_AREA_DIMENSION = 10000; //meters
    public static final int DEFAULT_AREA_HEIGHT = 10000; //meters
    
    
    //Do not change!
    public static final String RESOURCE_PATH = MY_PATH+"src/resources/";
    public static final String RESULT_PATH = MY_PATH+"results/";
    public static final double NO_OUTPUT_HEIGHT = -419;
    
}