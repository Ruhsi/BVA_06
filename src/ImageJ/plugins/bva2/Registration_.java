import com.sun.imageio.plugins.common.ImageUtil;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Stream;

public class Registration_ implements PlugInFilter {

    int bgVal = 255;

    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            showAbout();
            return DONE;
        } //if
        return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
    } //setup


    //returns left or right sub-image via vertical split
    public double[][] splitImageVertically(double[][] imgData, int width, int height, boolean getLeftImg) {
        int startX = getLeftImg ? 0 : (width / 2);
        startX += width % 2 == 1 ? 1 : 0;
        int endX = getLeftImg ? (width / 2) : width;

        double[][] resultImg = new double[endX - startX][height];

        for (int x = 0; x < endX - startX; x++) {
            for (int y = 0; y < height; y++) {
                resultImg[x][y] = imgData[x + startX][y];
            }
        }
        return resultImg;
    }

    public double getBilinearInterpolatedValue(double[][] imgData, int width, int height, double idxX, double idxY) {
        //for example 9.11
        int x1 = (int) idxX; //9
        int x2 = x1 + 1; //10
        double delta = idxX - x1; //0.11 always in range [0.0; 1.0[

        return -1.0;
    } //getBilinearInterpolatedValue

    //nearest neighbour interpolation
    public double getNNValue(double[][] imgData, int width, int height, double idxX, double idxY) {
        int xPos = (int) (idxX + 0.5);
        int yPos = (int) (idxY + 0.5);

        //check range
        if (xPos >= 0 && yPos >= 0 && xPos < width && yPos < height) {
            return imgData[xPos][yPos];
        }

        return -1.0;
    } //getNNValue


    public double getImgDiffSSE(int[][] imgData1, int[][] imgData2, int width, int height) {
        double actErr = 0.0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int val1 = imgData1[x][y];
                int val2 = imgData2[x][y];

                actErr += (val1 - val2) * (val1 - val2);
            }
        }

        return actErr;
    } //getImageDiffSS

    public double getImgDiffDistanceMap(int[][] distMap, Vector<Point> edgePointArr, int width, int height) {
        double actErr = 0.0;

        //TODO: implementation

        return actErr;
    } //getImgDiffDistanceMap

    public double[][] GetDistanceMap(int[][] imgData1, int width, int height, int edgeCalcMode) {
        //first perform edge detection, then calc distance map

        return null;
    }

    public void scaleAndPlotGrayValues(double[][] grayValues) {
        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                if(grayValues[x][y] > 0){
                    grayValues[x][255-y] = 255;
                }
            }
        }

        bva2.ImageJUtility.showNewImage(grayValues, 256, 256, "GrayValues");
    }

    private double scaleValueBetween(double value, double from, double to, double min, double max) {
        return (to - from) * ((value - min) / (max - min)) + from;
    }

    public double getMutualInformation2D(int[][] imgData1, int[][] imgData2, int width, int height, double miA, double miB) {
        double actMI = 0.0;
        double[][] grayValueProbabilities = new double[256][256];

        // normally double array defaults to zeros
        // but to get sure we initialize them by ourselves
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grayValueProbabilities[imgData1[x][y]][imgData2[x][y]] = 0.0;
            }
        }


        // increment for each y given x
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grayValueProbabilities[imgData1[x][y]][imgData2[x][y]]++;
            }
        }

        // get possibilities and calculate H(x,y)
        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                grayValueProbabilities[x][y] /= width * height;
                // log2(0) == -infinity
                if (grayValueProbabilities[x][y] != 0) {
                    actMI += grayValueProbabilities[x][y] *
                            log(grayValueProbabilities[x][y], 2);
                }
            }
        }
        actMI *= -1;
        System.out.println("H(a, b): " + actMI);

        // calculate I(x, y)
        actMI = miA + miB - actMI;

        scaleAndPlotGrayValues(grayValueProbabilities);

        return actMI;
    } //getMutualInformation2D


    public double getMutualInformation1D(int[][] imgData1, int width, int height) {
        double actMI = 0.0;
        double[] grayValueProbabilities = new double[256];
        // initialize array with zeros
        Arrays.fill(grayValueProbabilities, 0.0);


        // increment gray value counter for each pixel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grayValueProbabilities[imgData1[x][y]]++;
            }
        }

        // divide by width * height to get probability
        // and calculate actMI with formula:
        // ha = - sum(pi * log2(pi))
        for (int i = 0; i < 256; i++) {
            grayValueProbabilities[i] /= (width * height);
            actMI += grayValueProbabilities[i] * log(grayValueProbabilities[i], 2);
        }

        return -actMI;
    } //getMutualInformation1D


    public int[][] transformImg(double[][] inImg, int width, int height, double transX, double transY, double rotAngle) {
        int[][] retArr = new int[width][height];

        double cosTheta = Math.cos(Math.toRadians(rotAngle));
        double sinTheta = Math.sin(Math.toRadians(rotAngle));

        double widthHalf = width / 2.0;
        double heightHalf = height / 2.0;

        //use backward mapping ==> iterate over B-image and get values from transformed
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double posX = x;
                double posY = y;

                //a) move to center
                posX -= widthHalf;
                posY -= heightHalf;

                //b) rotate
                double rotatedX = posX * cosTheta + posY * sinTheta;
                double rotatedY = -posX * sinTheta + posY * cosTheta;
                posX = rotatedX;
                posY = rotatedY;

                //c)
                posX += widthHalf;
                posY += heightHalf;

                //d) move according to translation
                posX += transX;
                posY += transY;

                //finally assign value with NN interpolation
                retArr[x][y] = (int) (getNNValue(inImg, width, height, posX, posY) + 0.5);
            }
        }

        return retArr;
    } //transformImg

    public Vector<Point> transformCoordinates(Vector<Point> coordinates, int width, int height, double transX, double transY, double rotAngle) {
        //TODO implementation required

        return null;
    }

    //returns array with 4 double values, namely:
    //[0] ==> bestTx
    //[1] ==> bestTy
    //[2] ==> bestR
    //[3] ==> total error value
    double[] performOptimizationRun(int[][] imgData1, int[][] imgData2, int width, int height,
                                    double midTx, double midTy, double midR, double stepTx, double stepTy, double stepR,
                                    double currentHighestFitness) {

        double[] fitness = new double[4];
        int[][] transformedImg = transformImg(bva2.ImageJUtility.convertToDoubleArr2D(imgData2, width, height),
                width, height, stepTx, stepTy, stepR);
        double err = getImgDiffSSE(imgData1, transformedImg, width, height);
        fitness = new double[]{stepTx, stepTy, stepR, err};

        return fitness;
    }

    public void run(ImageProcessor ip) {
        byte[] pixels = (byte[]) ip.getPixels();
        int width = ip.getWidth();
        int height = ip.getHeight();

        int[][] inDataArrInt = bva2.ImageJUtility.convertFrom1DByteArr(pixels, width, height);
        double[][] inDataArrDouble = bva2.ImageJUtility.convertToDoubleArr2D(inDataArrInt, width, height);


        //first problem setup
        double tXToApply = 11.111;
        double tYToApply = -3.14159;
        double rotToApply = 8.123;

        int[][] transformedImg = transformImg(inDataArrDouble, width, height, tXToApply, tYToApply, rotToApply);

        double[][] transformedImgDouble = bva2.ImageJUtility.convertToDoubleArr2D(transformedImg, width, height);

        double initError = getImgDiffSSE(inDataArrInt, transformedImg, width, height);
        IJ.log("Init Error " + initError);

        int[][] correctedImg = transformImg(transformedImgDouble, width, height, -110, -4, -8);

        double finalError = getImgDiffSSE(inDataArrInt, correctedImg, width, height);
        IJ.log("Final Error " + finalError);

        bva2.ImageJUtility.showNewImage(transformedImg, width, height, "init transformed");
        double[][] halfImage = splitImageVertically(transformedImgDouble, width, height, true);
        bva2.ImageJUtility.showNewImage(halfImage, width / 2, height, "half image");

        ip.findEdges();


        int[][] cannyImg = ip.getIntArray();
        bva2.ImageJUtility.showNewImage(cannyImg, width, height, "canny");
        double[][] cannyImgDouble = bva2.ImageJUtility.convertToDoubleArr2D(cannyImg, width, height);
        double[][] leftCannyImg = splitImageVertically(cannyImgDouble, width, height, true);
        double[][] rightCannyImg = splitImageVertically(cannyImgDouble, width, height, false);
        int[][] leftCannyImgInt = bva2.ImageJUtility.convertToIntArr2D(leftCannyImg, width / 2, height);
        int[][] rightCannyImgInt = bva2.ImageJUtility.convertToIntArr2D(rightCannyImg, width / 2, height);

        bva2.ImageJUtility.showNewImage(leftCannyImgInt, width / 2, height, "left canny");
        bva2.ImageJUtility.showNewImage(rightCannyImgInt, width / 2, height, "right canny");

        //split the images

        //--------- user defined -----------
        double stepX = 2.0;
        double stepY = 2.0;
        double stepR = 0.5;
        double searchScale = 0.75; //to reduce search radius
        int numOfOptimizationRuns = 700;
        //-------------------
        Random random = new Random();


        int currBestTx = 0;
        int currBestTy = 0;
        int currBestR = 0;
        double currHighestFitness = Double.MAX_VALUE;

        boolean gotBetter = true;


        int mu = 10; // number of parents
        int lambda = 15; // number of children
        double delta = 1.5; // delta for mutating values
        double initialDelta = delta; // initial value of delta for updating
        double deltaStep = 0.25; // update value for delta
        int initialNrOfPopulation = 5000;
        double[][] population = new double[initialNrOfPopulation][4];
        double[][] parents = new double[mu][4];
        double[][] children = new double[lambda][4];


        int minWidth = -width / 4;
        int maxWidth = width / 4;
        int maxHeight = height / 2;
        int minHeight = -height / 2;
        int maxR = 15;
        int minR = -15;

        // init population
        for (int i = 0; i < initialNrOfPopulation; i++) {
            double[] parent = performOptimizationRun(leftCannyImgInt, rightCannyImgInt, width / 2, height,
                    currBestTx, currBestTy, currBestR,
                    random.nextInt(maxWidth + 1 - minWidth) + minWidth,
                    random.nextInt(maxHeight + 1 - minHeight) + minHeight,
                    random.nextInt(maxR + 1 - minR) + minR,
                    currHighestFitness);
            population[i] = parent;
        }


        // perform optimization
        for (int i = 0; i < numOfOptimizationRuns; i++) {
            // select mu parents from current population
            parents = selectNParents(population, mu);

            // mutate parents and get lambda children from mu parents
            children = mutate(parents, delta, mu, lambda, width, height);

            //evaluate the children
            for (int j = 0; j < lambda + mu; j++) {
                children[j] = performOptimizationRun(leftCannyImgInt, rightCannyImgInt, width / 2, height,
                        currBestTx, currBestTy, currBestR, children[j][0], children[j][1], children[j][2], children[j][3]);
            }

            // sort children to get best mu parents
            Arrays.sort(children, (first, second) -> Double.compare(first[3], second[3]));
            population = new double[mu][4];
            for (int j = 0; j < mu; j++) {
                population[j] = children[j];
            }


            // update delta to not run into local optima
            if (population[0][3] < currHighestFitness) gotBetter = true;

            //update parameters for next run
            if (gotBetter && i % 20 == 0) {
                gotBetter = false;
                delta = initialDelta;
            } else if (!gotBetter && i % 20 == 0) {
                delta += deltaStep;
                gotBetter = false;
            }

            // update current best
            currBestTx = (int) population[0][0];
            currBestTy = (int) population[0][1];
            currBestR = (int) population[0][2];
            currHighestFitness = (int) population[0][3];


            System.out.println("Tx: " + currBestTx + " Ty: " + currBestTy + " R: " + currBestR + " fitness: " + currHighestFitness + " delta: " + delta);
        }

        // get error of best
        double err = getImgDiffSSE(leftCannyImgInt, leftCannyImgInt, width / 2, height);
        System.out.println(err);

        // show best image
        int[][] finalImg = transformImg(rightCannyImg, width / 2, height, currBestTx, currBestTy, currBestR);
        bva2.ImageJUtility.showNewImage(finalImg, width / 2, height, "best transformation");

        // apply Mutual Information
        double ha = getMutualInformation1D(leftCannyImgInt, width / 2, height);
        double hb = getMutualInformation1D(finalImg, width / 2, height);

        double Iab = getMutualInformation2D(leftCannyImgInt, finalImg, width / 2, height, ha, hb);

        System.out.println("H(a): " + ha);
        System.out.println("H(b): " + hb);
        System.out.println("I(a, b): " + Iab);

    } //run

    double[][] mutate(double[][] parents, double delta, int mu, int lambda, int width, int height) {
        Random random = new Random();

        double[][] children = new double[lambda + mu][4];

        int j = 0;
        for (int i = 0; i < lambda; i++) {
            children[i] = new double[4];
            // calculate the child with the parent and the gaussian with the delta
            // in order not to exceed the limit, we modulo through the max values
            children[i][0] = (parents[j][0] + random.nextGaussian() * delta) % (width / 4);
            children[i][1] = (parents[j][1] + random.nextGaussian() * delta) % (height / 2);
            children[i][2] = (parents[j][2] + random.nextGaussian() * delta) % 15;
            children[i][3] = parents[j][3];
            j++;
            // when all parents are done, begin again with the first one
            if (j == mu) j = 0;
        }

        j = 0;
        for (int i = lambda; i < lambda + mu; i++) {
            children[i] = parents[j];
            j++;
        }
        return children;
    }

    double[][] selectNParents(double[][] population, int mu) {
        // select the best mu parents from the population
        // a[3] is fitness
        Arrays.stream(population).sorted(Comparator.comparing(a -> (Double) a[3])).limit(mu);
        return population;
    }

    private double log(double x, int base) {
        return Math.log(x) / Math.log(2);
    }

    void showAbout() {
        IJ.showMessage("About Registration_Template_...", "core registration functionality ");
    } //showAbout


} //class RegistrationTemplate_

