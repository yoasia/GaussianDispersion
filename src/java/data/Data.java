/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Double.NaN;
import static java.lang.Math.toIntExact;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import jcuda.driver.CUmodule;
import jcuda.driver.JCudaDriver;
import static jcuda.driver.JCudaDriver.cuCtxCreate;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuDeviceGet;
import static jcuda.driver.JCudaDriver.cuInit;
import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuMemAlloc;
import static jcuda.driver.JCudaDriver.cuMemFree;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoH;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoD;
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import static jcuda.driver.JCudaDriver.cuModuleLoad;
import jcuda.vec.VecDouble;
import jdk.nashorn.internal.objects.Global;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Joanna
 */
public class Data {
    public static final String MY_PATH = "/media/joanna/Linux/Studia/Magisterka/Server/";
    public static final String RESOURCE_PATH = MY_PATH+"src/resources/";
    public static final String RESULT_PATH = MY_PATH+"results/";
    private static final double DEFAULT_AREA_DIMENSION = 5000;
    private static final double DEFAULT_GRID = 50;
    private static final int R = 6378137;
    
    private double wind_speed_horizontal;
    private double wind_direction;
    private double std_deviation_y[];
    private double std_deviation_z[];
    private double a;
    private double b;
    private double release_height;
    private double K[];
    private double p;
    private double q;
    private double source_strength;
    private double refflection_co;
    private int stability_class_num;
    private double z0;
    private double area_dimension;
    private double grid;
    private double d;
    private double lon;
    private double lat;
    private int N;
    private JSONArray result;
    
    public Data(){
        loadDemo();
    }
    
    public Data(
    double wind_speed_horizontal_,
    double wind_direction_,
    double release_height_,
    double source_strength_,
    double refflection_co_,
    int stability_class_num_,
    double z0_,
    double a_,
    double b_,
    double p_,
    double q_,
    double lon_,
    double lat_,
    double ... optional) throws IOException {
        double area_dimension_ = optional.length > 0 ? optional[0] : DEFAULT_AREA_DIMENSION;
        double grid_ = optional.length > 1 ? optional[1] : DEFAULT_GRID;
        
        init(wind_speed_horizontal_,
        wind_direction_,
        release_height_,
        source_strength_,
        refflection_co_,
        stability_class_num_,
        z0_,
        a_,
        b_,
        p_,
        q_,
        lon_, lat_,
        area_dimension_,
        grid_ );

    }
    
    private void init(
    double wind_speed_horizontal_,
    double wind_direction_,
    double release_height_,
    double source_strength_,
    double refflection_co_,
    int stability_class_num_,
    double z0_,
    double a_,
    double b_,
    double p_,
    double q_,
    double lon_,
    double lat_,
    double ... optional) throws IOException {
        this.wind_speed_horizontal = wind_speed_horizontal_;
        this.wind_direction = wind_direction_;
        this.release_height = release_height_;
        this.source_strength = source_strength_;
        this.refflection_co = refflection_co_;
        this.stability_class_num = stability_class_num_;
        this.a = a_;
        this.b = b_;
        this.p = p_;
        this.q = q_;
        this.z0 = z0_;
        this.lon = lon_;
        this.lat = lat_;
        
        double area_dimension_ = optional.length > 0 ? optional[0] : DEFAULT_AREA_DIMENSION;
        double grid_ = optional.length > 1 ? optional[1] : DEFAULT_GRID;

        this.area_dimension = area_dimension_;
        this.grid = grid_;
        this.N = (int) (area_dimension_/grid);
        this.d = release_height_*(3.0/4.0);

    }
    
    public void loadDemo(){
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
            double lon = (Double) jsonObject.get("lon");
            double lat = (Double) jsonObject.get("lat");
            double area_dimension = (Long) jsonObject.get("area_dimension");
            double grid = (Double) jsonObject.get("grid");
            
            init(wind_speed_horizontal, 
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
    
    public JSONArray getResult() {
        return result;
    }

    public double getWind_speed_horizontal() {
        return wind_speed_horizontal;
    }

    public void setWind_speed_horizontal(double wind_speed_horizontal) {
        this.wind_speed_horizontal = wind_speed_horizontal;
    }

    public double getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(double wind_direction) {
        this.wind_direction = wind_direction;
    }

    public double[] getStd_deviation_y() {
        return std_deviation_y;
    }

    public void setStd_deviation_y(double std_deviation_y[]) {
        this.std_deviation_y = std_deviation_y;
    }

    public double[] getStd_deviation_z() {
        return std_deviation_z;
    }

    public void setStd_deviation_z(double std_deviation_z[]) {
        this.std_deviation_z = std_deviation_z;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getRelease_height() {
        return release_height;
    }

    public void setRelease_height(double release_height) {
        this.release_height = release_height;
    }


    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        this.q = q;
    }

    public double getSource_strength() {
        return source_strength;
    }

    public void setSource_strength(double source_strength) {
        this.source_strength = source_strength;
    }

    public double getRefflection_co() {
        return refflection_co;
    }

    public void setRefflection_co(double refflection_co) {
        this.refflection_co = refflection_co;
    }

    public double getStability_class_num() {
        return stability_class_num;
    }

    public void setStability_class_num(int stability_class_num) {
        this.stability_class_num = stability_class_num;
    }

    public double getZ0() {
        return z0;
    }

    public void setZ0(double z0) {
        this.z0 = z0;
    }

    public double getGrid() {
        return grid;
    }

    public void setGrid(double grid) {
        this.grid = grid;
    }
    
    public void calculate(DIMENSION dim) throws IOException{
        calculateGauss(dim);
    }
        
        /**
     * The extension of the given file name is replaced with "ptx".
     * If the file with the resulting name does not exist, it is
     * compiled from the given file using NVCC. The name of the
     * PTX file is returned.
     *
     * @param cuFileName The name of the .CU file
     * @return The name of the PTX file
     * @throws IOException If an I/O error occurs
     */
    private static String preparePtxFile(String cuFileName) throws IOException
    {
        int endIndex = cuFileName.lastIndexOf('.');
        if (endIndex == -1)
        {
            endIndex = cuFileName.length()-1;
        }
        String ptxFileName = cuFileName.substring(0, endIndex+1)+"ptx";
        File ptxFile = new File(ptxFileName);
        if (ptxFile.exists())
        {
            return ptxFileName;
        }

        File cuFile = new File(cuFileName);
        if (!cuFile.exists())
        {
            throw new IOException("Input file not found: "+cuFileName);
        }
        String modelString = "-m"+System.getProperty("sun.arch.data.model");
        String command =
            "nvcc -lineinfo -v " + modelString + " -ptx "+
            cuFile.getPath()+" -o "+ptxFileName;

        System.out.println("Executing\n"+command);
        Process process = Runtime.getRuntime().exec(command);

        String errorMessage =
            new String(toByteArray(process.getErrorStream()));
        String outputMessage =
            new String(toByteArray(process.getInputStream()));
        int exitValue = 0;
        try
        {
            exitValue = process.waitFor();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IOException(
                "Interrupted while waiting for nvcc output", e);
        }

        if (exitValue != 0)
        {
            System.out.println("nvcc process exitValue "+exitValue);
            System.out.println("errorMessage:\n"+errorMessage);
            System.out.println("outputMessage:\n"+outputMessage);
            throw new IOException(
                "Could not create .ptx file: "+errorMessage);
        }

        System.out.println("Finished creating PTX file");
        return ptxFileName;
    }

    /**
     * Fully reads the given InputStream and returns it as a byte array
     *
     * @param inputStream The input stream to read
     * @return The byte array containing the data from the input stream
     * @throws IOException If an I/O error occurs
     */
    private static byte[] toByteArray(InputStream inputStream)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[8192];
        while (true)
        {
            int read = inputStream.read(buffer);
            if (read == -1)
            {
                break;
            }
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }
      /**
     * Entry point of this sample
     *
     * @param args Not used
     * @throws IOException If an IO error occurs
     */
    private void calculateGauss(DIMENSION dim) throws IOException{
    
        // Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);

        // Create the PTX file by calling the NVCC
        String absolutePath = Data.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/")+1);
        String ptxFileName =  preparePtxFile(absolutePath+"data/kernels/JCudaGaussKernel.cu");

        // Initialize the driver and create a context for the first device.
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        // Load the ptx file.
        CUmodule module = new CUmodule();
        cuModuleLoad(module, ptxFileName);

        // Obtain a function pointer to the "add" function.
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "gauss");

        int numElements = N*N*N/2;

        // Allocate the device input data, and copy the
        // host input data to the device
//        CUdeviceptr deviceInputSigY = new CUdeviceptr();
//        cuMemAlloc(deviceInputSigY, N * Sizeof.DOUBLE);
//        cuMemcpyHtoD(deviceInputSigY, Pointer.to(std_deviation_y),
//            N * Sizeof.DOUBLE);
//        CUdeviceptr deviceInputSigZ = new CUdeviceptr();
//        cuMemAlloc(deviceInputSigZ, N * Sizeof.DOUBLE);
//        cuMemcpyHtoD(deviceInputSigZ, Pointer.to(std_deviation_z),
//            N * Sizeof.DOUBLE);

        // Allocate device output memory
        CUdeviceptr deviceOutput = new CUdeviceptr();
        cuMemAlloc(deviceOutput, numElements * Sizeof.DOUBLE);
            

        // Set up the kernel parameters: A pointer to an array
        // of pointers which point to the actual values.
        Pointer kernelParameters = Pointer.to(
            Pointer.to(new int[]{N}),
            Pointer.to(new double[]{source_strength}),
            Pointer.to(new double[]{wind_speed_horizontal}),
            Pointer.to(new double[]{wind_direction}),
            Pointer.to(new int[]{stability_class_num}),
            Pointer.to(new double[]{grid}),
            Pointer.to(new double[]{release_height}),
            Pointer.to(deviceOutput)            
        );

        // Call the kernel function.
       int blockSizeX = 1024;
        int gridSizeX = (int)Math.ceil((double)numElements / blockSizeX);
        cuLaunchKernel(function,
            gridSizeX,  1, 1,      // Grid dimension
            blockSizeX, 1, 1,      // Block dimension
            0, null,               // Shared memory size and stream
            kernelParameters, null // Kernel- and extra parameters
        );
        cuCtxSynchronize();

        // Allocate host output memory and copy the device output
        // to the host.
        double hostOutput[] = new double[numElements];
        cuMemcpyDtoH(Pointer.to(hostOutput), deviceOutput,
            numElements * Sizeof.DOUBLE);
        cuMemcpyDtoH(Pointer.to(hostOutput), deviceOutput,
            numElements * Sizeof.DOUBLE);

        // Save the result
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("DD-MM_HH-mm");
        

        String csvFile3d = RESULT_PATH+sdf.format(cal.getTime()) +"3d.csv";
        String csvFile2d = RESULT_PATH+sdf.format(cal.getTime()) +"2d.csv";
        FileWriter writer = new FileWriter(csvFile3d);
        FileWriter writer2 = new FileWriter(csvFile2d);

        CSVUtils.writeLine(writer, Arrays.asList("x", "y", "z", "concentration"));
        CSVUtils.writeLine(writer2, Arrays.asList("x", "y", "concentration"));
        result = new JSONArray();
        
        BufferedImage img = new BufferedImage(N, N, BufferedImage.TYPE_INT_RGB);
        
        double [][][] result3d = new double[N][N][N];
        double [][] result2d = new double[N][N];
        double max = 0.0;
        
        for(int i = 0; i < N; i++){
            Arrays.fill(result2d[i], 0.0);
        }
        
        for(int i = 0; i < numElements - 1; i++)
        {
            JSONObject point = new JSONObject();
            
            int iz = (int) Math.floor((double)(i/(N*N)));
            int iy = (int) Math.floor((double)(i%(N*N)/N));
            int ix = (int) Math.floor((double)(i%(N*N)%N));
            
            int x = (int)((ix - (0.5*N))*grid);
            int y = (int)((iy - (0.5*N))*grid);
            int z = (int)(iz *grid);
            
            double coordinates[] = calculateNewCoords(lon, lat, x, y);
            
                //System.out.println(i+", "+j+", "+k+", "+hostOutput[index]); 
            if(hostOutput[i] != Global.Infinity && hostOutput[i] == hostOutput[i] ){
                
                if(dim == DIMENSION.THREE){
                    //Save to variable
                    point.put("lat", coordinates[0]);
                    point.put("lon", coordinates[1]);
                    point.put("z", z);
                    point.put("value", hostOutput[i]);
                    result.add(point);
                }    
                    //Save record to file
                CSVUtils.writeLine(writer, Arrays.asList(Double.toString(x), Double.toString(y), 
                                    Integer.toString(z), String.valueOf(hostOutput[i])));
               
                result3d[ix][iy][iz] = hostOutput[i];
                result2d[ix][iy] = result2d[ix][iy] + hostOutput[i];
                
                //Save max value
                if(result2d[ix][iy] > max )
                    max = result2d[ix][iy];
            }
                
        }
        int i = 0;
        for(int x1 = 0; x1 < N; x1++){
            for(int y1 = 0; y1 < N; y1++){
                
                JSONObject point = new JSONObject();
                
                int x = (int)((x1 - (0.5*N))*grid);
                int y = (int)((y1 - (0.5*N))*grid);
                
                double[] coordinates = calculateNewCoords(lon, lat, (double) x, (double) y);
                
                double value = result2d[x1][y1] / max;
                int [] color1 = {171, 255, 196};
                int [] color2 = {255, 34, 34};
                int colorRGB[] = getColor(color1, color2, value, 255);
                int rgb[] = new int[10];
                int color =  (colorRGB[1] << 16) | (colorRGB[2] << 8) | colorRGB[3];
                Arrays.fill(rgb, color);
                img.setRGB(x1, y1, color);
                
                point.put("lat", coordinates[0]);
                point.put("lon", coordinates[1]);
                point.put("value", result2d[x1][y1]);
                
                //Save record to file
                CSVUtils.writeLine(writer2, Arrays.asList(Double.toString(x), Double.toString(y), 
                         String.valueOf(result2d[x1][y1])));
                
                if(dim == DIMENSION.TWO){
                    //Save to variable
                    result.add(point);
                }
                i++;
    
            }
        }
        // retrieve image
    File outputfile = new File(RESULT_PATH+sdf.format(cal.getTime())+".bmp");
    ImageIO.write(img, "png", outputfile);
        
        writer.flush();
        writer.close();
        writer2.flush();
        writer2.close();

        // Clean up.
        cuMemFree(deviceOutput);  
        
    } 
    
    /**
     * Returns color between color1 and color2 
     * @param color1
     * @param color2
     * @param procent 
     * @param alpha  
     * @return 
     */
    public int[] getColor(int [] color1, int[] color2, double procent, int alpha) {
        double w1 = procent;
        double w2 = 1 - w1;
        int rgb[] = new int[4];
        rgb[0] = alpha;
        rgb[1] = (int) Math.round(color1[0] * w1 + color2[0] * w2);
        rgb[2] = (int) Math.round(color1[1] * w1 + color2[1] * w2);
        rgb[3] = (int) Math.round(color1[2] * w1 + color2[2] * w2);
        return rgb;
    }
    /**
     * Returns coordinates in degrees moved by x and y
     * @param lon_start degrees
     * @param lat_start degrees
     * @param x_distance x distance in meters
     * @param y_distance x distance in meters
     * @return [lat, lon]
     */
    public double[] calculateNewCoords(double lon_start, double lat_start, double x_distance, double y_distance){
        double degrees[] = new double[2];
        //Earth’s radius, sphere
        
//            degrees[0] = lat + (0.0000449 * yInMeters / Math.cos(lat));
        //latitude            
        degrees[0] = lat_start + y_distance/R * 180/Math.PI;
//            degrees[1] = lon + (0.0000449 * xInMeters) ;
        //longitude
        degrees[1] = lon_start + x_distance/(R*Math.cos(Math.PI*lat_start/180)) * 180/Math.PI;
            
        if(degrees[0] > 90.0){
            degrees[0] = degrees[0] % 90.0;
        }
        if(degrees[1] > 180.0){
            degrees[1] = degrees[1] % 180.0;
        }
        
        
     
            
        return degrees;
    }
}
