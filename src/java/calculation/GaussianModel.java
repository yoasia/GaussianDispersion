/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import static constants.Configuration.CUDA_BLOCK_SIZE_X;
import constants.DIMENSION;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.toIntExact;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import javax.imageio.ImageIO;
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
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import static jcuda.driver.JCudaDriver.cuModuleLoad;
import jdk.nashorn.internal.objects.Global;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static constants.Configuration.MAX_AREA_DIMENSION;
import static constants.Configuration.MAX_AREA_DIMENSION;
import static constants.Configuration.MAX_NUMBER_OF_POINTS;
import static constants.Configuration.NO_OUTPUT_HEIGHT;
import static constants.Configuration.RESOURCE_PATH;
import static constants.Configuration.RESULT_PATH;
import static constants.Configuration.NUMBER_OF_DATA_RANGES;
import static constants.Configuration.SAVE_RESULT_IMAGE;
import static java.lang.Double.parseDouble;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static constants.Configuration.DEFAULT_AREA_HEIGHT;
import static constants.Configuration.MAX_NUMBER_RETURNED_POINTS;
import static constants.Configuration.MIN_CONCENTRATION;

/**
 *
 * @author Joanna
 */
public class GaussianModel {

    private static final int R = 6378137;

    private double wind_speed_horizontal;
    private double wind_direction;
    private double release_height;
    private double source_strength;
    private int stability_class_num;
    private double area_dimension;
    private double grid;
    private double lon;
    private double lat;
    private double output_height;
    private int N;
    private JSONArray result;
    private JSONObject resultRanges;
    private double max_value = Double.NEGATIVE_INFINITY;

    public GaussianModel() {
        loadDemo();
    }

    public GaussianModel(
            double wind_speed_horizontal_,
            double wind_direction_,
            double release_height_,
            double source_strength_,
            double refflection_co_,
            int stability_class_num_,
            double z0_,
            double lon_,
            double lat_,
            double output_h_,
            double... optional) throws IOException {
        double area_dimension_ = optional.length > 0 ? optional[0] : MAX_AREA_DIMENSION;
        double _output_h = output_h_;
        double grid_ = calculateGrid(area_dimension_, _output_h);

        init(wind_speed_horizontal_,
                wind_direction_,
                release_height_,
                source_strength_,
                stability_class_num_,
                lon_, lat_,
                output_h_,
                area_dimension_,
                grid_);
    }

    public GaussianModel(double wind_speed_horizontal, double wind_direction, double release_height, double source_strength, double refflection_co, int stability_class_num, double z0, double a, double b, double p, double q, double lon, double lat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void init(
            double wind_speed_horizontal_,
            double wind_direction_,
            double release_height_,
            double source_strength_,
            int stability_class_num_,
            double lon_,
            double lat_,
            double output_h_,
            double area_dimension_,
            double grid_
    ) throws IOException {
        this.wind_speed_horizontal = wind_speed_horizontal_;
        this.wind_direction = wind_direction_;
        this.release_height = release_height_;
        this.source_strength = source_strength_;
        this.stability_class_num = stability_class_num_;
        this.lon = lon_;
        this.lat = lat_;
        this.output_height = output_h_;

        this.area_dimension = area_dimension_;
        this.grid = grid_;
        this.N = (int) (area_dimension_ / grid);

    }

    public void loadDemo() {
        JSONParser parser = new JSONParser();

        try {
            String absolutePath = RESOURCE_PATH + "input.json"; //relative path

            Object obj = parser.parse(new FileReader(absolutePath));

            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);

            double wind_speed_horizontal = (Long) jsonObject.get("wind_speed_horizontal");
            double wind_direction = (Long) jsonObject.get("wind_direction");
            double release_height = (Long) jsonObject.get("release_height");
            double source_strength = (Double) jsonObject.get("source_strength");
            int stability_class_num = toIntExact((Long) jsonObject.get("stability_class_num"));
            double z0 = (Double) jsonObject.get("z0");
            double lon = (Double) jsonObject.get("lon");
            double lat = (Double) jsonObject.get("lat");
            double area_dimension = (Long) jsonObject.get("area_dimension");
            double grid = (Double) jsonObject.get("grid");

            init(wind_speed_horizontal,
                    wind_direction,
                    release_height,
                    source_strength,
                    stability_class_num,
                    lon,
                    lat,
                    NO_OUTPUT_HEIGHT,
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

    public JSONObject getRangedResult() {
        JSONObject returnObject = new JSONObject();
        returnObject.put("result", resultRanges);
        returnObject.put("max_value", max_value);
        returnObject.put("unit", "µg");
        returnObject.put("grid", grid);

        return returnObject;
    }

    public JSONObject getSortedResult() {
        JSONObject returnObject = new JSONObject();
        returnObject.put("result", result);
        returnObject.put("max_value", max_value);
        returnObject.put("unit", "µg");
        return returnObject;
    }

    public JSONObject getGeoJSON() {
        JSONObject returnObject = new JSONObject();
        JSONArray features = createGeoJSON();
        returnObject.put("features", features);

        return returnObject;
    }

    private JSONArray createGeoJSON() {
        JSONObject geoJSON = new JSONObject();
        geoJSON.put("type", "FeatureCollection");

        JSONArray features = new JSONArray();
        final Map<String, Object> data2d = new HashMap<>();

        //Add up data (geoJSON is in 2d) 
        for (int index = 0; index < result.size(); index++) {
            JSONObject element = (JSONObject) result.get(index);

            if (data2d.get(element.get("lat") + "," + element.get("lon")) != null) {
                data2d.put(data2d.get("lat") + "," + data2d.get("lon"), (double) data2d.get("value") + (double) element.get("value"));
            } else {
                data2d.put(data2d.get("lat") + "," + data2d.get("lon"), (double) element.get("value"));
            }
        }

        //Create geoJSON content
        for (Map.Entry<String, Object> point : data2d.entrySet()) {
            String key = point.getKey();
            double lat, lon;
            String[] parts = key.split(",");
            lat = parseDouble(parts[0]);
            lon = parseDouble(parts[1]);
            Object value = point.getValue();

//          Feature format:
//          {
//                "type": "Feature",
//                "geometry": {
//                  "type": "Point",
//                  "coordinates": [Lon, Lat]
//                },
//                "properties": {
//                  "value": [value]
//                }
//          } 
            JSONObject feature = new JSONObject();
            JSONObject geometry = new JSONObject();
            JSONObject properties = new JSONObject();

            geometry.put("type", "Point");
            geometry.put("coordinates", "[" + lon + "," + lat + "]");
            properties.put("value", value);

            feature.put("type", "Feature");
            feature.put("geometry", geometry);
            feature.put("properties", properties);

            features.add(feature);
        }

        return features;
    }

    /**
     * The extension of the given file name is replaced with "ptx". If the file
     * with the resulting name does not exist, it is compiled from the given
     * file using NVCC. The name of the PTX file is returned.
     *
     * @param cuFileName The name of the .CU file
     * @return The name of the PTX file
     * @throws IOException If an I/O error occurs
     */
    private static String preparePtxFile(String cuFileName) throws IOException {
        int endIndex = cuFileName.lastIndexOf('.');
        if (endIndex == -1) {
            endIndex = cuFileName.length() - 1;
        }
        String ptxFileName = cuFileName.substring(0, endIndex + 1) + "ptx";
        File ptxFile = new File(ptxFileName);
        if (ptxFile.exists()) {
            return ptxFileName;
        }

        File cuFile = new File(cuFileName);
        if (!cuFile.exists()) {
            throw new IOException("Input file not found: " + cuFileName);
        }
        String modelString = "-m" + System.getProperty("sun.arch.data.model");
        String command
                = "nvcc -lineinfo -v " + modelString + " -ptx "
                + cuFile.getPath() + " -o " + ptxFileName;

        System.out.println("Executing\n" + command);
        Process process = Runtime.getRuntime().exec(command);

        String errorMessage
                = new String(toByteArray(process.getErrorStream()));
        String outputMessage
                = new String(toByteArray(process.getInputStream()));
        int exitValue = 0;
        try {
            exitValue = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(
                    "Interrupted while waiting for nvcc output", e);
        }

        if (exitValue != 0) {
            System.out.println("nvcc process exitValue " + exitValue);
            System.out.println("errorMessage:\n" + errorMessage);
            System.out.println("outputMessage:\n" + outputMessage);
            throw new IOException(
                    "Could not create .ptx file: " + errorMessage);
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
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[8192];
        while (true) {
            int read = inputStream.read(buffer);
            if (read == -1) {
                break;
            }
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }

    private static double[] calculateTranslation(double angle, double area_dimension) {
        double result[] = new double[2];
        double max = area_dimension / 2;

        if (angle < 0) {
            angle += 360;
        }

        //calculate translation vector
        if (angle <= 135 && angle >= 45) {
            if (angle > 90) {
                result[0] = max * Math.tan((angle - 90) * Math.PI / 180);
            } else {
                result[0] = 0.0 - max * Math.tan((90 - angle) * Math.PI / 180);
            }
            result[1] = 0.0 - max;
        } else if (angle < 225 && angle >= 135) {
            if (angle > 180) {
                result[0] = max;
                result[1] = max * Math.tan((angle - 180) * Math.PI / 180);
            } else {
                result[0] = max;
                result[1] = 0.0 - max * Math.tan((180 - angle) * Math.PI / 180);;
            }

        } else if (angle < 315 && angle >= 225) {
            if (angle > 270) {
                result[0] = 0.0 - max * Math.tan((angle - 270) * Math.PI / 180);
            } else {
                result[0] = max * Math.tan((270 - angle) * Math.PI / 180);
            }
            result[1] = max;
        } else {
            if (angle < 90) {
                result[0] = 0.0 - max;
                result[1] = 0.0 - max * Math.tan((angle) * Math.PI / 180);;
            } else {
                result[0] = 0.0 - max;
                result[1] = max * Math.tan((360 - angle) * Math.PI / 180);
            }
        }

        return result;
    }

    /**
     * Entry point of this sample
     *
     * @param dim 
     * @throws IOException If an IO error occurs
     */
    public void calculate(DIMENSION dim) throws IOException {

        double x_tmp, y_tmp, max;
        double angle;
        double translation_vector[] = new double[2];
        int tmp[] = new int[6];
        // Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);

        // Create the PTX file by calling the NVCC
        String absolutePath = GaussianModel.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/") + 1);
        String ptxFileName = preparePtxFile(absolutePath + "cuda/kernels/JCudaGaussKernel.cu");

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

        int numElements = MAX_NUMBER_OF_POINTS;

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
        int blockSizeX = CUDA_BLOCK_SIZE_X;
        int gridSizeX = (int) Math.ceil((double) numElements / blockSizeX);
        cuLaunchKernel(function,
                gridSizeX, 1, 1, // Grid dimension
                blockSizeX, 1, 1, // Block dimension
                0, null, // Shared memory size and stream
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

        String csvFile3d = RESULT_PATH + sdf.format(cal.getTime()) + "3d.csv";
        String csvFile2d = RESULT_PATH + sdf.format(cal.getTime()) + "2d.csv";
        FileWriter writer = new FileWriter(csvFile3d);
        FileWriter writer2 = new FileWriter(csvFile2d);

        CSVUtils.writeLine(writer, Arrays.asList("x", "y", "z", "concentration"));
        CSVUtils.writeLine(writer2, Arrays.asList("x", "y", "concentration"));
        result = new JSONArray();

        BufferedImage img = new BufferedImage(N, N, BufferedImage.TYPE_INT_RGB);

        double[][][] result3d = new double[N][N][N];
        double[][] result2d = new double[N][N];

        //fill array for result with nulls
        for (int i = 0; i < N; i++) {
            Arrays.fill(result2d[i], 0.0);
        }

        //calculate dimension of area 
        max = (N * grid) / 2;
        //if angle > 360
        angle = (90 - wind_direction) % 360;
        //if angle < 0
        angle = (angle < 0) ? (angle + 360 * Math.floor(1 + (-1) * angle / 360)) : angle;

        //calculate translation
        translation_vector = calculateTranslation(angle, max * 2);

        for (int i = 0; i < numElements - 1; i++) {
            if (hostOutput[i] != Global.Infinity && hostOutput[i] == hostOutput[i] && hostOutput[i] > MIN_CONCENTRATION) {

                JSONObject point = new JSONObject();

                int iz = (int) Math.floor((double) (i / (N * N)));
                int iy = (int) Math.floor((double) (i % (N * N) / N));
                int ix = (int) Math.floor((double) (i % (N * N) % N));

                double x = (int) ((ix - 0.5 * N) * grid);
                double y = (int) (iy * grid);
                int z = (int) (iz * grid);

                x_tmp = x;
                y_tmp = y;

                //point rotation
                x = x_tmp * Math.cos(-wind_direction * Math.PI / 180) - y_tmp * Math.sin(-wind_direction * Math.PI / 180);
                y = x_tmp * Math.sin(-wind_direction * Math.PI / 180) + y_tmp * Math.cos(-wind_direction * Math.PI / 180);

                x_tmp = x;
                y_tmp = y;

                x += translation_vector[0];
                y += translation_vector[1];

                double coordinates[] = calculateNewCoords(lon, lat, x, y);

                if (dim == DIMENSION.THREE && hostOutput[i] > 0) {
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
                if (hostOutput[i] > max_value) {
                    max_value = hostOutput[i];
                }

                if (hostOutput[i] < 5) {
                    tmp[0] += 1;
                } else if (hostOutput[i] < 100) {
                    tmp[1] += 1;
                } else if (hostOutput[i] < 500) {
                    tmp[2] += 1;
                } else if (hostOutput[i] < 1000) {
                    tmp[3] += 1;
                } else if (hostOutput[i] < 5000) {
                    tmp[4] += 1;
                } else {
                    tmp[5] += 1;
                }
            }
        }

        result.sort(new ResultComparator());

        int i = 0;
        for (int x1 = 0; x1 < N; x1++) {
            for (int y1 = 0; y1 < N; y1++) {

                JSONObject point = new JSONObject();

                double x = (int) (x1 - (0.5 * N) * grid);
                double y = (int) (y1 * grid);

                x_tmp = x;
                y_tmp = y;

                x = x_tmp * Math.cos(wind_direction - 180) - y_tmp * Math.sin(wind_direction - 180);
                y = x_tmp * Math.sin(wind_direction - 180) + y_tmp * Math.cos(wind_direction - 180);

                x_tmp = x;
                y_tmp = y;

                //point translation
                x = x_tmp - max * Math.sin(wind_direction - 180);
                y = y_tmp - max * Math.cos(wind_direction - 180);

                double[] coordinates = calculateNewCoords(lon, lat, (double) x, (double) y);

                double value = result2d[x1][y1] / max_value;
                int[] color1 = {171, 255, 196};
                int[] color2 = {255, 34, 34};
                int colorRGB[] = getColor(color1, color2, value, 255);
                int rgb[] = new int[10];
                int color = (colorRGB[1] << 16) | (colorRGB[2] << 8) | colorRGB[3];
                Arrays.fill(rgb, color);
                img.setRGB(x1, y1, color);

                point.put("lat", coordinates[0]);
                point.put("lon", coordinates[1]);
                point.put("value", result2d[x1][y1]);

                //Save record to file
                CSVUtils.writeLine(writer2, Arrays.asList(Double.toString(x), Double.toString(y),
                        String.valueOf(result2d[x1][y1])));

                if (dim == DIMENSION.TWO) {
                    //Save to variable
                    result.add(point);
                }
                i++;

            }
        }

        //sort results
        result = sortResult(result);

        //reduce number of points
        result = reduceNumberOfPoints(result);

        //create ranges
        resultRanges = createRanges(result, max_value);

        // retrieve image
        if (SAVE_RESULT_IMAGE) {
            File outputfile = new File(RESULT_PATH + sdf.format(cal.getTime()) + ".bmp");
            ImageIO.write(img, "png", outputfile);
        }

        writer.flush();
        writer.close();
        writer2.flush();
        writer2.close();

        // Clean up.
        cuMemFree(deviceOutput);

    }

    /**
     * Returns color between color1 and color2
     *
     * @param color1
     * @param color2
     * @param procent
     * @param alpha
     * @return
     */
    private int[] getColor(int[] color1, int[] color2, double procent, int alpha) {
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
     *
     * @param lon_start degrees
     * @param lat_start degrees
     * @param x_distance x distance in meters
     * @param y_distance x distance in meters
     * @return [lat, lon]
     */
    private double[] calculateNewCoords(double lon_start, double lat_start, double x_distance, double y_distance) {
        double degrees[] = new double[2];
        //Earth’s radius, sphere

//            degrees[0] = lat + (0.0000449 * yInMeters / Math.cos(lat));
        //latitude            
        degrees[0] = lat_start + y_distance / R * 180 / Math.PI;
//            degrees[1] = lon + (0.0000449 * xInMeters) ;
        //longitude
        degrees[1] = lon_start + x_distance / (R * Math.cos(Math.PI * lat_start / 180)) * 180 / Math.PI;

        if (degrees[0] > 90.0) {
            degrees[0] = degrees[0] % 90.0;
        }
        if (degrees[1] > 180.0) {
            degrees[1] = degrees[1] % 180.0;
        }

        return degrees;
    }

    private JSONArray sortResult(JSONArray jsonArr) {

        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();

        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add((JSONObject) jsonArr.get(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "value";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                double valA = 0.0;
                double valB = 0.0;

                valA = (double) a.get(KEY_NAME); //do something
                valB = (double) b.get(KEY_NAME);

                if (valA < valB) {
                    return 1;
                } else if (valA > valB) {
                    return -1;
                } else {
                    return 0;
                }

                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray;

    }

    private JSONObject createRanges(JSONArray jsonArr, double maxValue) {
        JSONObject resultWithRanges = new JSONObject();
        JSONArray rangeData[] = new JSONArray[NUMBER_OF_DATA_RANGES + 2];

        //create array with ranges
        for (int i = 0; i < rangeData.length; i++) {
            rangeData[i] = new JSONArray();
        }

        double rangeValue = maxValue / NUMBER_OF_DATA_RANGES;

        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject obj = (JSONObject) jsonArr.get(i);

//            if((double)(obj.get("value"))<= 1.0){
//                rangeData[0].add(jsonArr.get(i));
//            } else  {
            double indexD = (double) (obj.get("value")) / rangeValue;
            int index = (int) Math.floor(indexD + 1);
            rangeData[index].add(obj);
//            }

        }

        for (int i = 0; i < NUMBER_OF_DATA_RANGES + 2; i++) {
            resultWithRanges.put("range" + String.valueOf(NUMBER_OF_DATA_RANGES + 1 - i), rangeData[i]);
        }

        return resultWithRanges;
    }

    private static double calculateGrid(double area_dimension, double output_h) {

        double cloudHeight = 1;

        if (output_h == NO_OUTPUT_HEIGHT) {
            cloudHeight = DEFAULT_AREA_HEIGHT;
        }

        double Volume = area_dimension * area_dimension * cloudHeight;
        double pointVolume = Volume / MAX_NUMBER_OF_POINTS;

        double grid = Math.pow((pointVolume * area_dimension) / cloudHeight, 1.0 / 3.0);

        return Math.round(grid);
    }

    private static JSONArray reduceNumberOfPoints(JSONArray jsonArr) {

        int jump = jsonArr.size() / MAX_NUMBER_RETURNED_POINTS;

        int dontDeleteOnJump = jump;

        for (int i = 0; i < jsonArr.size(); i++) {
            if (dontDeleteOnJump != jump) {
                jsonArr.remove(i);
            }

            dontDeleteOnJump = (dontDeleteOnJump >= jump ? 0 : dontDeleteOnJump++);
        }

        return jsonArr;
    }

}
