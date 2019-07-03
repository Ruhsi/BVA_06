package bva2;

public class ConvolutionFilter {

    public static double[][] ConvolveDoubleNorm(double[][] inputImg, int width, int height, double[][] kernel, int radius, int numOfIterations) {
        double[][] returnImg = inputImg;
        for (int iterCount = 0; iterCount < numOfIterations; iterCount++) {
            returnImg = ConvolutionFilter.ConvolveDoubleNorm(returnImg, width, height, kernel, radius);
        }

        return returnImg;
    }

    public static double[][] ConvolveDoubleNorm(double[][] inputImg, int width, int height, double[][] kernel, int radius) {
        double[][] returnImg = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double newPixelVal = 0;
                double kernelSum = 0.0;

                //iterate over all kernel elements (yellow mask)
                for (int xOffset = -radius; xOffset <= radius; xOffset++) {
                    for (int yOffset = -radius; yOffset <= radius; yOffset++) {
                        int nbX = x + xOffset;
                        int nbY = y + yOffset;

                        //check range
                        if(nbX >= 0 && nbX < width && nbY >= 0 && nbY < height){
                            double imgVal = inputImg[nbX][nbY];
                            newPixelVal += imgVal * kernel[xOffset + radius][yOffset + radius];
                            kernelSum += kernel[xOffset + radius][yOffset + radius];
                        }

                    }
                }

                //if inside the image, kernelSum should be 1.0 now
                //in border areas, we get less ===> normalize


                //assign result
                returnImg[x][y] = newPixelVal / kernelSum;
            }
        }

        return returnImg;
    }

    public static double[][] ConvolveDouble(double[][] inputImg, int width, int height, double[][] kernel, int radius) {
        double[][] returnImg = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double newPixelVal = 0;

                //iterate over all kernel elements (yellow mask)
                for (int xOffset = -radius; xOffset <= radius; xOffset++) {
                    for (int yOffset = -radius; yOffset <= radius; yOffset++) {
                        int nbX = x + xOffset;
                        int nbY = y + yOffset;

                        //check range
                        if(nbX >= 0 && nbX < width && nbY >= 0 && nbY < height){
                            double imgVal = inputImg[nbX][nbY];
                            newPixelVal += imgVal * kernel[xOffset + radius][yOffset + radius];
                        }

                    }
                }
                //assign result
                returnImg[x][y] = newPixelVal;
            }
        }

        return returnImg;
    }

    public static double[][] GetMeanMask(int tgtRadius) {
        int size = 2 * tgtRadius + 1;
        double[][] kernelImg = new double[size][size];
        double coefficient = 1.0 / (size * size);
        System.out.println(coefficient);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                kernelImg[x][y] = coefficient;
            }
        }

        return kernelImg;
    }

    public static double[][] GetGaussMask(int tgtRadius, double sigma) {
        int size = 2 * tgtRadius + 1;
        double[][] kernelImg = new double[size][size];

        return kernelImg;
    }

    public static double[][] ApplySobelEdgeDetection(double[][] inputImg, int width, int height) {
        double[][] returnImg = new double[width][height];
        double[][] sobelV = new double[][]{{1.0, 0.0, -1.0}, {2.0, 0.0, -2.0}, {1.0, 0.0, -1.0}};
        double[][] sobelH = new double[][]{{1.0, 2.0, 1.0}, {0.0, 0.0, 0.0}, {-1.0, -2.0, -1.0}};

        int radius = 1;
        double maxGradient = 0.0;

        return returnImg;
    }


}
