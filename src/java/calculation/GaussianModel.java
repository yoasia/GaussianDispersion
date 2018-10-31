/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import static constants.Configuration.CO;
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
import static constants.Configuration.SO2;
import constants.GAS_TYPE;
import static java.lang.Math.tan;

/**
 *
 * @author Joanna
 */
public class GaussianModel {

    private static final int R = 6378137;

    private double wind_speed;
    private double wind_direction;
    private double release_height;
    private double source_strength;
    private int stability_class_num;
    private double area_dimension;
    private double grid;
    private double lon;
    private double lat;
    private double dimension_height;
    private int N;
    private JSONArray result;
    private JSONObject resultRanges;
    private double max_value = Double.NEGATIVE_INFINITY;
    private double output_height;

    public GaussianModel() {
        loadDemo();
    }

    public GaussianModel(
            double wind_speed_horizontal_,
            double wind_direction_,
            double release_height_,
            double source_strength_,
            int stability_class_num_,
            double lon_,
            double lat_,
            double dimension_h_,
            double output_h_,
            int maxPointNumber,
            double... optional) throws IOException {
        double area_dimension_ = optional.length > 0 ? optional[0] : MAX_AREA_DIMENSION;
        double _dimension_h_ = dimension_h_;
        double _output_h__ = output_h_;
        double grid_ = calculateGrid(area_dimension_, _dimension_h_, maxPointNumber);

        init(wind_speed_horizontal_,
                wind_direction_,
                release_height_,
                source_strength_,
                stability_class_num_,
                lon_, lat_,
                dimension_h_,
                area_dimension_,
                grid_,
                output_h_);
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
            double dimension_h_,
            double area_dimension_,
            double grid_,
            double output_h_
    ) throws IOException {
        this.wind_speed = wind_speed_horizontal_;
        this.wind_direction = wind_direction_;
        this.release_height = release_height_;
        this.source_strength = source_strength_;
        this.stability_class_num = stability_class_num_;
        this.lon = lon_;
        this.lat = lat_;
        this.dimension_height = dimension_h_;
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
                    grid,
                    NO_OUTPUT_HEIGHT);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getRangedResult(String gas) {
        JSONObject returnObject = new JSONObject();
        JSONArray ranges;

        //create ranges
        if (gas.equals("CO")) {
            resultRanges = createRanges(result, max_value, GAS_TYPE.CO);
            ranges = new JSONArray();
            for (int i = 0; i < CO.length - 1; i++) {
                ranges.add(CO[i]);

            }
        } else if (gas.equals("SO2")) {
            resultRanges = createRanges(result, max_value, GAS_TYPE.SO2);
            ranges = new JSONArray();
            for (int i = 0; i < SO2.length - 1; i++) {
                ranges.add(SO2[i]);

            }
        } else {
            resultRanges = createRanges(result, max_value, GAS_TYPE.OTHER);
            double rangeValue = max_value / NUMBER_OF_DATA_RANGES;
            ranges = new JSONArray();
            for (int i = 0; i < NUMBER_OF_DATA_RANGES; i++) {
                ranges.add(i * rangeValue);
            }
        }

        //json with input parameters
        JSONObject input = new JSONObject();
        input.put("wind_speed", wind_speed);
        input.put("wind_direction", wind_direction);
        input.put("release_height", release_height);
        input.put("source_strength", source_strength);
        input.put("stability_class_num", stability_class_num);
        input.put("lon", lon);
        input.put("lat", lat);
        input.put("output_height", dimension_height);

        returnObject.put("input_parameters", input);
        returnObject.put("result", resultRanges);
        returnObject.put("max_value", max_value);
        returnObject.put("unit", "µg");
        returnObject.put("grid", grid);
        returnObject.put("ranges", ranges);

        return returnObject;
    }

    public JSONObject getSortedResult() {
        JSONObject returnObject = new JSONObject();

        //json with  input parameters
        JSONObject input = new JSONObject();
        input.put("wind_speed", wind_speed);
        input.put("wind_direction", wind_direction);
        input.put("release_height", release_height);
        input.put("source_strength", source_strength);
        input.put("stability_class_num", stability_class_num);
        input.put("lon", lon);
        input.put("lat", lat);
        input.put("output_height", dimension_height);

        returnObject.put("input_parameters", input);

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
    protected static String preparePtxFile(String cuFileName) throws IOException {
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

    protected static double[] calculateTranslation(double angle, double area_dimension) {
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
    public void calculate(DIMENSION dim, int maxPointNumber) throws IOException {

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

        // Allocate device output memory
        CUdeviceptr deviceOutput = new CUdeviceptr();
        cuMemAlloc(deviceOutput, maxPointNumber * Sizeof.DOUBLE);

        // Set up the kernel parameters: A pointer to an array
        // of pointers which point to the actual values.
        Pointer kernelParameters = Pointer.to(Pointer.to(new int[]{N}),
                Pointer.to(new double[]{source_strength}),
                Pointer.to(new double[]{wind_speed}),
                Pointer.to(new double[]{wind_direction}),
                Pointer.to(new int[]{stability_class_num}),
                Pointer.to(new double[]{grid}),
                Pointer.to(new double[]{release_height}),
                Pointer.to(deviceOutput)
        );

        // Call the kernel function.
        int blockSizeX = CUDA_BLOCK_SIZE_X;
        int gridSizeX = (int) Math.ceil((double) maxPointNumber / blockSizeX);
        cuLaunchKernel(function,
                gridSizeX, 1, 1, // Grid dimension
                blockSizeX, 1, 1, // Block dimension
                0, null, // Shared memory size and stream
                kernelParameters, null // Kernel- and extra parameters
        );
        cuCtxSynchronize();

        // Allocate host output memory and copy the device output
        // to the host.
        double hostOutput[] = new double[N*N*N];
        cuMemcpyDtoH(Pointer.to(hostOutput), deviceOutput,
                maxPointNumber * Sizeof.DOUBLE);


        prepareResult(hostOutput, dim, maxPointNumber);

        // Clean up.
        cuMemFree(deviceOutput);

    }

    public void calculateWithoutCUDA(DIMENSION dim, int maxPointNumber) throws IOException {
        double hostOutput[] = new double[N*N*N];

        for (int i = 0; i < N*N*N; i++) {
            hostOutput[i] = pseudoKernel(N, source_strength, wind_speed, wind_direction, stability_class_num, grid, release_height, i);
        }

        prepareResult(hostOutput, dim, maxPointNumber);
    }

    void prepareResult(double hostOutput[], DIMENSION dim, int maxPointNumber) throws IOException {

        double x_tmp, y_tmp, max;
        double angle, wind_to;
        double translation_vector[] = new double[2];
        int tmp[] = new int[6];

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
        max = (N * grid);
        //if angle > 360
        angle = (90 - wind_direction) % 360;
        wind_to = angle - 180;
        //if angle < 0
        angle = (angle < 0) ? (angle + 360 * Math.floor(1 + (-1) * angle / 360)) : angle;
        wind_to = (wind_to < 0) ? (wind_to + 360 * Math.floor(1 + (-1) * wind_to / 360)) : wind_to;

        //calculate translation
        translation_vector = calculateTranslation(angle, max * 2);

        for (int i = 0; i < N*N*N - 1; i++) {
            if (hostOutput[i] != Global.Infinity && hostOutput[i] == hostOutput[i] && hostOutput[i] > MIN_CONCENTRATION) {
//            if (hostOutput[i] != Global.Infinity && hostOutput[i] == hostOutput[i]) {

                JSONObject point = new JSONObject();

                int iz = (int) Math.floor((double) (i / (N * N)));
                int iy = (int) Math.floor((double) (i % (N * N) / N));
                int ix = (int) Math.floor((double) (i % (N * N) % N));

                double x = (int) (ix  * grid);
                double y = (int) ((iy - 0.5 * N) * grid);
                int z = (int) (iz * grid);

                x_tmp = x;
                y_tmp = y;

                //point rotation
                x = x_tmp * Math.cos(wind_to * Math.PI / 180) - y_tmp * Math.sin(wind_to * Math.PI / 180);
                y = x_tmp * Math.sin(wind_to * Math.PI / 180) + y_tmp * Math.cos(wind_to * Math.PI / 180);

                x_tmp = x;
                y_tmp = y;

//                x += translation_vector[0];
//                y += translation_vector[1];

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

                double x = (int) ((x1 - (0.5 * N)) * grid);
                double y = (int) ((y1 - (0.5 * N)) * grid);

                x_tmp = x;
                y_tmp = y;

                x = x_tmp * Math.cos(wind_to * Math.PI / 180) - y_tmp * Math.sin(wind_to * Math.PI / 180);
                y = x_tmp * Math.sin(wind_to * Math.PI / 180) + y_tmp * Math.cos(wind_to * Math.PI / 180);

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
        //result = reduceNumberOfPoints(result);

        // retrieve image
        if (SAVE_RESULT_IMAGE) {
            File outputfile = new File(RESULT_PATH + sdf.format(cal.getTime()) + ".bmp");
            ImageIO.write(img, "png", outputfile);
        }

        writer.flush();
        writer.close();
        writer2.flush();
        writer2.close();
    }

    double pseudoKernel(int n, double Q, double u, double wind_dir, int stability, double grid, double H, int index) {

        double x, y, z; //Point coordinates
        double sigY, sigZ; //Sigmas
        double PI = 3.1415926535897;
        double distance;
        double windX, windY;
        double theta;
        double downwind, crosswind;
        double a = 0, b = 0, c = 0, d = 0;
        double beta;
        double x_tmp;
        double y_tmp;
        double max;
        double translation_x = 0.0;
        double translation_y = 0.0;
        double wind_dir_deg = (double) (((90 - (int) wind_dir)) % 360);
        double winds_to = (double) (wind_dir_deg + 180);

        if (wind_dir_deg < 0) {
            wind_dir_deg += 360;
        }

        //Number of thread
        int iz = (int) Math.floor((double) (index / (n * n)));
        int iy = (int) Math.floor((double) (index % (n * n) / n));
        int ix = (int) Math.floor((double) (index % (n * n) % n));

        //calculate point coordinates in m
        x = ix  * grid;
        y = (iy - 0.5 * n) * grid;
        z = iz * grid;

        x_tmp = x;
        y_tmp = y;

        // //point rotation
        x = x_tmp * Math.cos(winds_to * PI / 180) - y_tmp * Math.sin(winds_to * PI / 180);
        y = x_tmp * Math.sin(winds_to * PI / 180) + y_tmp * Math.cos(winds_to * PI / 180);

        max = (n * grid);

        int resultIndex = (iz * n * n) + (iy * n) + ix;

        //calculate wind x and wind y (it winds to wind_dir -180)
        windX = u * Math.cos(winds_to * PI / 180);
        windY = u * Math.sin(winds_to * PI / 180);

        //distance vector
        distance = Math.sqrt(x * x + y * y);

        //calculate wind_dir_deg between wind vector and position vector
        theta = Math.acos((x * windX + y * windY) / (u * distance));

        //scalar projection
        downwind = distance * Math.cos(theta);
        crosswind = distance * Math.sin(theta);

        //Definition of parametrs a, b, c and d
        switch (stability) {
            case 1:
                if (downwind < 100 & downwind > 0) {
                    a = 122.800;
                    b = 0.94470;
                } else if (downwind >= 100 & downwind < 150) {
                    a = 158.080;
                    b = 1.05420;
                } else if (downwind >= 100 & downwind < 0) {
                    a = 170.220;
                    b = 1.09320;
                } else if (downwind >= 200 & downwind < 250) {
                    a = 179.520;
                    b = 1.12620;
                } else if (downwind >= 250 & downwind < 300) {
                    a = 217.410;
                    b = 1.26440;
                } else if (downwind >= 300 & downwind < 400) {
                    a = 258.89;
                    b = 1.40940;
                } else if (downwind >= 400 & downwind < 500) {
                    a = 346.75;
                    b = 1.7283;
                } else if (downwind >= 500 & downwind < 3110) {
                    a = 453.85;
                    b = 2.1166;
                } else if (downwind >= 3110) {
                    a = 453.85;
                    b = 2.1166;
                }

                c = 24.1670;
                d = 2.5334;

                break;
            case 2:
                // vertical
                if (downwind < 200 && downwind > 0) {
                    a = 90.673;
                    b = 0.93198;
                }
                if (downwind >= 200 && downwind < 400) {
                    a = 98.483;
                    b = 0.98332;
                }
                if (downwind >= 400) {
                    a = 109.3;
                    b = 1.09710;
                }
                // cross wind
                c = 18.3330;
                d = 1.8096;
                break;
            case 3:
                // vertical
                a = 61.141;
                b = 0.91465;
                // cross wind
                c = 12.5;
                d = 1.0857;
                break;
            case 4:
                // vertical
                if (downwind < 300 && downwind > 0) {
                    a = 34.459;
                    b = 0.86974;
                }
                if (downwind >= 300 && downwind < 1000) {
                    a = 32.093;
                    b = 0.81066;
                }
                if (downwind >= 1000 && downwind < 3000) {
                    a = 32.093;
                    b = 0.64403;
                }
                if (downwind >= 3000 && downwind < 10000) {
                    a = 33.504;
                    b = 0.60486;
                }
                if (downwind >= 10000 && downwind < 30000) {
                    a = 36.650;
                    b = 0.56589;
                }
                if (downwind >= 30000) {
                    a = 44.053;
                    b = 0.51179;
                }
                // cross wind
                c = 8.3330;
                d = 0.72382;
                break;
            case 5:
                // vertical
                if (downwind < 100 && downwind > 0) {
                    a = 24.26;
                    b = 0.83660;
                }
                if (downwind >= 100 && downwind < 300) {
                    a = 23.331;
                    b = 0.81956;
                }
                if (downwind >= 300 && downwind < 1000) {
                    a = 21.628;
                    b = 0.75660;
                }
                if (downwind >= 1000 && downwind < 2000) {
                    a = 21.628;
                    b = 0.63077;
                }
                if (downwind >= 2000 && downwind < 4000) {
                    a = 22.534;
                    b = 0.57154;
                }
                if (downwind >= 4000 && downwind < 10000) {
                    a = 24.703;
                    b = 0.50527;
                }
                if (downwind >= 10000 && downwind < 20000) {
                    a = 26.970;
                    b = 0.46713;
                }
                if (downwind >= 20000 && downwind < 40000) {
                    a = 35.420;
                    b = 0.37615;
                }
                if (downwind >= 40000) {
                    a = 47.618;
                    b = 0.29592;
                }
                // cross wind
                c = 6.25;
                d = 0.54287;
                break;
            case 6:
                // vertical
                if (downwind < 200 && downwind > 0) {
                    a = 15.209;
                    b = 0.81558;
                }
                if (downwind >= 200 && downwind < 700) {
                    a = 14.457;
                    b = 0.78407;
                }
                if (downwind >= 700 && downwind < 1000) {
                    a = 13.953;
                    b = 0.68465;
                }
                if (downwind >= 1000 && downwind < 2000) {
                    a = 13.953;
                    b = 0.63227;
                }
                if (downwind >= 2000 && downwind < 3000) {
                    a = 14.823;
                    b = 0.54503;
                }
                if (downwind >= 3000 && downwind < 7000) {
                    a = 16.187;
                    b = 0.46490;
                }
                if (downwind >= 7000 && downwind < 15000) {
                    a = 17.836;
                    b = 0.41507;
                }
                if (downwind >= 15000 && downwind < 30000) {
                    a = 22.651;
                    b = 0.32681;
                }
                if (downwind >= 30000 && downwind < 60000) {
                    a = 27.074;
                    b = 0.27436;
                }
                if (downwind >= 60000) {
                    a = 34.219;
                    b = 0.21716;
                }
                // cross wind
                c = 4.1667;
                d = 0.36191;
                break;
            default:
                break;
        }

        //calculate sigmaX and sigmaY
        sigZ = Math.pow(a * (downwind / 1000), b);
        if (sigZ > 5000) {
            sigZ = 5000;
        }

        beta = 0.017453293 * (c - d * Math.log(downwind / 1000));
        sigY = 465.11628 * downwind / 1000 * tan(beta);

        return Q / (2 * u * PI * sigY * sigZ)
                * //1
                Math.exp((-1.0) * Math.pow(crosswind, 2) / (2.0 * Math.pow(sigY, 2)))
                * //2
                (Math.exp((-1.0) * Math.pow(z - H, 2) / (2.0 * Math.pow(sigZ, 2)))
                + //3a
                Math.exp((-1.0) * Math.pow(z + H, 2) / 2.0 * Math.pow(sigZ, 2))) * 1000000; //3b
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

        //latitude            
        degrees[0] = lat_start + y_distance / R * 180 / Math.PI;
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

    private JSONObject createRanges(JSONArray jsonArr, double maxValue, GAS_TYPE gas) {
        JSONObject resultWithRanges = new JSONObject();
        JSONArray rangeData[] = new JSONArray[NUMBER_OF_DATA_RANGES + 2];

        //create array with ranges
        for (int i = 0; i < rangeData.length; i++) {
            rangeData[i] = new JSONArray();
        }

        double rangeValue = maxValue / NUMBER_OF_DATA_RANGES;

        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject obj = (JSONObject) jsonArr.get(i);
            double value = (double) obj.get("value");

            if (gas == GAS_TYPE.OTHER) {
//              if((double)(obj.get("value"))<= 1.0){
//                  rangeData[0].add(jsonArr.get(i));
//              } else  {
                double indexD = value / rangeValue;
                int index = (int) Math.floor(indexD + 1);
                rangeData[index].add(obj);
//              }
            } else if (gas == GAS_TYPE.CO) {

                for (int j = 1; j < CO.length; j++) {
                    double minC = CO[j - 1] * 1000; //minimal concentration in range 
                    double maxC = CO[j] * 1000; //maximal concentration

                    if (value > minC && value <= maxC) {
                        rangeData[j - 1].add(obj);
                    }
                }

            } else {
                for (int j = 1; j < SO2.length; j++) {
                    double minC = SO2[j - 1] * 1000; //minimal concentration in j-1 range 
                    double maxC = SO2[j] * 1000; //maximal concentration in j-1 range 

                    if (value > minC && value <= maxC) {
                        rangeData[j - 1].add(obj);
                    }
                }
            }
        }

        for (int i = 0; i < NUMBER_OF_DATA_RANGES + 2; i++) {
            resultWithRanges.put("range" + String.valueOf(NUMBER_OF_DATA_RANGES + 1 - i), rangeData[i]);
        }

        return resultWithRanges;
    }

    protected static double calculateGrid(double area_dimension, double output_h, int maxPointNumber) {

        double cloudHeight = 1;

        if (output_h == NO_OUTPUT_HEIGHT) {
            cloudHeight = DEFAULT_AREA_HEIGHT;
        }

        double Volume = area_dimension * area_dimension * cloudHeight;
        double pointVolume = Volume / maxPointNumber;

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
