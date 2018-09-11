
#include<stdlib.h>
#include<math.h>
extern "C"

__global__ void gauss(int n, double Q, double u, double wind_dir, int stability, double grid, double H,  double *result )
{

    double x, y, z; //Point coordinates
    double sigY, sigZ; //Sigmas
    double PI = 3.1415926535897;
    double distance;
    double windX, windY;
    double alpha;
    double downwind, crosswind;
    double a, b, c, d;
    double theta;
    double x_tmp;
    double y_tmp;
    double max;
    double translation_x = 0.0;
    double translation_y = 0.0;
    double wind_dir_deg = (double)(((90 - (int) wind_dir)) % 360);
    
    if(wind_dir_deg < 0)
        wind_dir_deg+= 360;

    //Number of thread
    int index = (blockIdx.x * blockDim.x) + threadIdx.x;
    int iz = floor((double)(index/(n*n)));
    int iy = floor((double)(index%(n*n)/n));
    int ix = floor((double)(index%(n*n)%n));


    //calculate point coordinates in m
    x = (ix - (0.5*n))*grid;
    y = (iy - (0.5*n))*grid;
    z = iz * grid;


    x_tmp = x;
    y_tmp = y;

    //point rotation
    x = x_tmp*cos(-wind_dir*PI/180) - y_tmp*sin(-wind_dir*PI/180);
    y = x_tmp*sin(-wind_dir*PI/180) + y_tmp*cos(-wind_dir*PI/180);

    x_tmp = x;
    y_tmp = y;
    max = (n * grid)/2;

    //point translation
    if(wind_dir_deg <= 135 && wind_dir_deg >= 45){
        if(wind_dir_deg > 90){
            translation_x =  max * tan((wind_dir_deg - 90)*PI/180);
        }
        else{
            translation_x =  0.0 - max * tan((90 - wind_dir_deg ) * PI/180);
        }
         translation_y =  0.0 - max;
    }
    else if(wind_dir_deg < 225 && wind_dir_deg >= 135){
        if(wind_dir_deg > 180){
            translation_x = max;
             translation_y = max * tan((wind_dir_deg - 180)*PI/180);
        }
        else{
            translation_x = max ;
            translation_y = 0.0 - max * tan((180 - wind_dir_deg ) * PI/180);;
        }

    }
    else if(wind_dir_deg < 315 && wind_dir_deg >= 225){
        if(wind_dir_deg > 270){
            translation_x =  0.0 - max * tan((wind_dir_deg - 270 )*PI/180);
        }
        else{
            translation_x =  max * tan((270 - wind_dir_deg) * PI/180);
        }
         translation_y =  max;
    }
    else{
        if(wind_dir_deg < 90){
            translation_x =  0.0 - max;
            translation_y =  0.0 - max * tan((wind_dir_deg)*PI/180);;
        }
        else{
            translation_x = 0.0 - max;
             translation_y =  max * tan((360 - wind_dir_deg ) * PI/180);
        }
    }
    if(translation_x > 0){
            translation_x -= 1000;
    }
    else
        translation_x += 1000;
    
    if(translation_y > 0){
        translation_y -= 1000;
    }
    else
        translation_y += 1000;

    //move point
    x += translation_x;
    y += translation_y;


    int resultIndex = (iz * n * n) + (iy * n) + ix;

    //calculate wind x and wind y (it winds from wind_dir -180)
    windX = u * sin((wind_dir - 180)*PI/180);
    windY = u * cos((wind_dir - 180)*PI/180);

    //distance vector
    distance = sqrt(x*x + y*y);
    
    //calculate wind_dir_deg between wind vector and position vector
    alpha = acos((x*windX + y*windY)/(u*distance));

    //scalar projection
    downwind = distance * cos(alpha);
    crosswind = distance * sin(alpha);

    //Definition of parametrs a, b, c and d
    switch(stability){
        case 1:
            if(downwind < 100 & downwind > 0){
                a = 122.800;
                b = 0.94470;
            }
            else if(downwind >= 100 & downwind < 150){
                a = 158.080;
                b = 1.05420;
            }
            else if(downwind >= 100 & downwind < 0){
                a = 170.220;
                b = 1.09320;
            }
            else if(downwind >= 200 & downwind < 250){
                a = 179.520;
                b = 1.12620;
            }
            else if(downwind >= 250 & downwind < 300){
                a = 217.410;
                b = 1.26440;
            }
            else if(downwind >= 300 & downwind < 400){
                a = 258.89;
                b = 1.40940;
            }
            else if(downwind >= 400 & downwind < 500){
                a = 346.75;
                b = 1.7283;
            }
            else if(downwind >= 500 & downwind < 3110){
                a = 453.85;
                b = 2.1166;
            }
            else if(downwind >= 3110){
                a = 453.85;
                b = 2.1166;
            }

            c = 24.1670;
            d = 2.5334;

            break;
        case 2:
            // vertical
            if(downwind<200 && downwind>0){
                a=90.673;
                b=0.93198;
            }
            if(downwind>=200 && downwind<400){
                a=98.483;
                b=0.98332;
            }        
            if(downwind>=400){
                a=109.3;
                b=1.09710;
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
            if(downwind<300 && downwind>0){
                a=34.459;
                b=0.86974;
            }
            if(downwind>=300 && downwind<1000){
                a=32.093;
                b=0.81066;
            }
            if(downwind>=1000 && downwind<3000){
                a=32.093;
                b=0.64403;
            }
            if(downwind>=3000 && downwind<10000){
                a=33.504;
                b=0.60486;
            }
            if(downwind>=10000 && downwind<30000){
                a=36.650;
                b=0.56589;
            }    
            if(downwind>=30000){
                a=44.053;
                b=0.51179;
            }
            // cross wind
            c = 8.3330;
            d = 0.72382;
            break;
        case 5:
            // vertical
            if(downwind<100 && downwind>0){
                a=24.26;
                b=0.83660;
            }
            if(downwind>=100 && downwind<300){
                a=23.331;
                b=0.81956;
            }
            if(downwind>=300 && downwind<1000){
                a=21.628;
                b=0.75660;
            }
            if(downwind>=1000 && downwind<2000){
                a=21.628;
                b=0.63077;
            }
            if(downwind>=2000 && downwind<4000){
                a=22.534;
                b=0.57154;
            }
            if(downwind>=4000 && downwind<10000){
                a=24.703;
                b=0.50527;
            }
            if(downwind>=10000 && downwind<20000){
                a=26.970;
                b=0.46713;
            }
            if(downwind>=20000 && downwind<40000){
                a=35.420;
                b=0.37615;
            }
            if(downwind>=40000){
                a=47.618;
                b=0.29592;
            }
            // cross wind
            c = 6.25;
            d = 0.54287;
            break;
        case 6:
            // vertical
            if(downwind<200 && downwind>0){
                a=15.209;
                b=0.81558;
            }
            if(downwind>=200 && downwind<700){
                a=14.457;
                b=0.78407;
            }
            if(downwind>=700 && downwind<1000){
                a=13.953;
                b=0.68465;
            }
            if(downwind>=1000 && downwind<2000){
                a=13.953;
                b=0.63227;
            }
            if(downwind>=2000 && downwind<3000){
                a=14.823;
                b=0.54503;
            }
            if(downwind>=3000 && downwind<7000){
                a=16.187;
                b=0.46490;
            }
            if(downwind>=7000 && downwind<15000){
                a=17.836;
                b=0.41507;
            }
            if(downwind>=15000 && downwind<30000){
                a=22.651;
                b=0.32681;
            }
            if(downwind>=30000 && downwind<60000){
                a=27.074;
                b=0.27436;
            }
            if(downwind>=60000){
                a=34.219;
                b=0.21716;
            }
            // cross wind
            c = 4.1667;
            d = 0.36191;
            break;
        default:
            break;
    }

    //calculate sigmaX and sigmaY
    sigZ=pow(a*(downwind/1000),b);
    if(sigZ > 5000)
        sigZ = 5000;
    
    theta=0.017453293*(c-d*log(downwind/1000));
    sigY=465.11628*downwind/1000*tan(theta);

    if (ix<n && iy<n && iz<n)
    {
        result[resultIndex] = Q/(2*u*PI*sigY*sigZ) * //1
        exp((-1.0)*pow(crosswind, 2)/(2.0*pow(sigY, 2))) * //2
        (exp((-1.0)*pow(z - H, 2)/(2.0*pow(sigZ, 2))) + //3a
        exp((-1.0)*pow(z + H, 2)/2.0*pow(sigZ, 2))) * 1000000; //3b
    }  

}





