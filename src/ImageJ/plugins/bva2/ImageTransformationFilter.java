package bva2;

public class ImageTransformationFilter {


    public static int[][] GetTransformedImage(int[][] inImg, int width, int height, int[] transferFunction) {
        int[][] returnImg = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                returnImg[x][y] = transferFunction[inImg[x][y]];
            }
        }

        return returnImg;
    }

    public static int[] GetInversionTF(int maxVal) {
        int[] transferFunction = new int[maxVal + 1];
        for (int i = 0; i <= maxVal; i++) {
            transferFunction[i] = maxVal - i;
        }

        return transferFunction;
    }

    public static int[] GetHistogram(int maxVal, int[][] inImg, int width, int height) {
        int[] histogram = new int[maxVal + 1];

        return histogram;
    }

    public static int[] GetGammaCorrTF(int maxVal, double gamma) {
        int[] transferFunction = new int[maxVal + 1];

        return transferFunction;
    }

    public static int[] GetBinaryThresholdTF(int maxVal, int Tmin, int Tmax, int FG_VAL, int BG_VAL) {
        int[] transferFunction = new int[maxVal + 1];
        for (int i = 0; i <= maxVal; i++){
            //check range
            if(i >= Tmin && i <= Tmax){
                transferFunction[i] = FG_VAL;
            } else {
                transferFunction[i] = BG_VAL;
            }
        }

        return transferFunction;
    }

    public static int[] GetHistogramEqualizationTF(int maxVal, int[][] inImg, int width, int height) {
        int[] returnTF = new int[maxVal + 1];

        return returnTF;
    }

}
