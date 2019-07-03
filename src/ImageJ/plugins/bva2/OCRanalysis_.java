package bva2;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.util.Vector;


public class OCRanalysis_ implements PlugInFilter {


    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about"))
        {showAbout(); return DONE;}


        return DOES_8G+DOES_STACKS+SUPPORTS_MASKING;
    } //setup

    //-------- the defined features ----------------
    public static int F_FGcount = 0;
    public static int F_MaxDistX= 1;
    public static int F_MaxDistY= 2;
    public static int F_AvgDistanceCentroide= 3;
    public static int F_MaxDistanceCentroide= 4;
    public static int F_MinDistanceCentroide= 5;
    public static int F_Circularity = 6;
    public static int F_CentroideRelPosX = 7;
    public static int F_CentroideRelPosY = 8;
    //----------------------------------------------


    public void run(ImageProcessor ip) {
        Vector<ImageFeatureBase> featureVect = new Vector<ImageFeatureBase>();
        featureVect.add(new ImageFeatureF_FGcount());
        //TODO: build up feature vector


        byte[] pixels = (byte[])ip.getPixels();
        int width = ip.getWidth();
        int height = ip.getHeight();
        int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);

        //(1) at first do some binarization
        int FG_VAL = 0;
        int BG_VAL = 255;
        int MARKER_VAL = 127;
        int thresholdVal = -1;//?;

        int[] binaryThreshTF = bva2.ImageTransformationFilter.GetBinaryThresholdTF(255, MARKER_VAL, thresholdVal, FG_VAL, BG_VAL);
        int[][] binaryImgArr = bva2.ImageTransformationFilter.GetTransformedImage(inDataArrInt, width, height, binaryThreshTF);

        ImageJUtility.showNewImage(binaryImgArr, width, height, "binary image at threh = " + thresholdVal);

        //(2) split the image according to fire-trough or multiple region growing
        Vector<Vector<SubImageRegion>> splittedCharacters = splitCharacters(binaryImgArr, width, height, BG_VAL, FG_VAL);

        //TODO: for reasons of testing, visualize some of the split characters

        //TODO: let the user specify the target character
        int tgtCharRow = 0;
        int tgtCharCol = 4;
        SubImageRegion charROI = splittedCharacters.get(tgtCharRow).get(tgtCharCol);
        ImageJUtility.showNewImage(charROI.subImgArr, charROI.width, charROI.height, "char at pos " + tgtCharRow + " / " + tgtCharCol);

        //calculate features of reference character
        double[] featureResArr = calcFeatureArr(charROI, BG_VAL, featureVect);
        printoutFeatureRes(featureResArr, featureVect);

        //TODO calculate mean values for all features based on all characters
        //==> required for normalization
        double[] normArr = calculateNormArr(splittedCharacters, BG_VAL, featureVect);
        printoutFeatureRes(normArr, featureVect);

        int hitCount = 0; //count the number of detected characters

        //TODO: now detect all matching characters
        //forall SubImageRegion sir in splittedCharacters
        //if isMatchingChar(..,sir,..) then markRegionInImage(..,sir,..)


        IJ.log("# of letters detected = " + hitCount);

        ImageJUtility.showNewImage(binaryImgArr, width, height, "result image with marked letters");

    } //run

    public int[][] markRegionInImage(int[][] inImgArr, SubImageRegion imgRegion, int colorToReplace, int tgtColor) {
        //TODO: implementation required
        return inImgArr;
    }

    boolean isMatchingChar(double[] currFeatureArr, double[] refFeatureArr, double[] normFeatureArr) {
        double CORR_COEFFICIENT_LIMIT = -1;//?;

        //TODO: implementation required


        return false;
    }


    void printoutFeatureRes(double[] featureResArr, Vector<ImageFeatureBase> featuresToUse) {
        IJ.log("========== features =========");
        for(int i = 0; i < featuresToUse.size(); i++) {
            IJ.log("res of F " + i + ", " + featuresToUse.get(i).description + " is " + featureResArr[i]);
        }
    }


    double[] calcFeatureArr(SubImageRegion region, int FGval, Vector<ImageFeatureBase> featuresToUse) {
        //TODO implementation required
        double[] featureResArr = new double[featuresToUse.size()];
        //foreach feature f in featuresToUse resultVal = f.CalcFeatureVal(...)

        return featureResArr;
    }

    double[] calculateNormArr(Vector<Vector<SubImageRegion>> inputRegions, int FGval, Vector<ImageFeatureBase> featuresToUse) {
        //calculate the average per feature to allow for normalization
        double[] returnArr = new double[featuresToUse.size()];
        //TODO implementation required

        return returnArr;
    }

    //outer Vector ==> lines, inner vector characters per line, i.e. columns
    public Vector<Vector<SubImageRegion>> splitCharacters(int[][] inImg, int width, int height, int BG_val, int FG_val) {
        Vector<Vector<SubImageRegion>> returnCharMatrix = new Vector<Vector<SubImageRegion>>();

        //TODO: implementation required

        return returnCharMatrix;
    }

    public Vector<SubImageRegion> splitCharactersVertically(SubImageRegion rowImage, int BG_val, int FG_val, int[][] origImg) {
        Vector<SubImageRegion> returnCharArr = new Vector<SubImageRegion>();

        //TODO: implementation required

        return returnCharArr;
    }

    //probably useful helper method
    public boolean isEmptyRow(int[][] inImg, int width, int rowIdx, int BG_val) {
        for(int x = 0; x < width; x++) {
            if(inImg[x][rowIdx] != BG_val) {
                return false;
            }
        }

        return true;
    }

    //probably useful helper method
    public boolean isEmptyColumn(int[][] inImg, int height, int colIdx, int BG_val) {
        for(int y = 0; y < height; y++) {
            if(inImg[colIdx][y] != BG_val) {
                return false;
            }
        }

        return true;
    }


    void showAbout() {
        IJ.showMessage("About Template_...",
                "this is a RegionGrowing_ template\n");
    } //showAbout


    //the features to implement



    class ImageFeatureF_FGcount extends ImageFeatureBase {

        public ImageFeatureF_FGcount() {
            this.description = "Pixelanzahl";
        }

        public double CalcFeatureVal(SubImageRegion imgRegion, int FG_val) {
            return -1; //TODO implementation required
        }
    }

    class ImageFeatureF_MaxDistX extends ImageFeatureBase {

        public ImageFeatureF_MaxDistX() {
            this.description = "maximale Ausdehnung in X-Richtung";
        }

        public double CalcFeatureVal(SubImageRegion imgRegion, int FG_val) {
            return -1; //TODO implementation required
        }

    }

    class ImageFeatureF_MaxDistY extends ImageFeatureBase {

        public ImageFeatureF_MaxDistY() {
            this.description = "maximale Ausdehnung in Y-Richtung";
        }

        public double CalcFeatureVal(SubImageRegion imgRegion, int FG_val) {
            return -1; //TODO implementation required
        }

    }

    class ImageFeatureF_AvgDistanceCentroide extends ImageFeatureBase {

        public ImageFeatureF_AvgDistanceCentroide() {
            this.description = "mittlere Distanz zum Centroide";
        }

        public double CalcFeatureVal(SubImageRegion imgRegion, int FG_val) {
            return -1; //TODO implementation required
        }
    }

    class ImageFeatureF_MaxDistanceCentroide extends ImageFeatureBase {

        public ImageFeatureF_MaxDistanceCentroide() {
            this.description = "maximale Distanz zum Centroide";
        }

        public double CalcFeatureVal(SubImageRegion imgRegion, int FG_val) {
            return -1; //TODO implementation required
        }
    }

    class ImageFeatureF_MinDistanceCentroide extends ImageFeatureBase {

        public ImageFeatureF_MinDistanceCentroide() {
            this.description = "minimale Distanz zum Centroide";
        }

        public double CalcFeatureVal(SubImageRegion imgRegion, int FG_val) {
            return -1; //TODO implementation required
        }

    }

    class ImageFeatureF_Circularity extends ImageFeatureBase {

        public ImageFeatureF_Circularity() {
            this.description = "Circularit�t";
        }

        public double CalcFeatureVal(SubImageRegion imgRegion, int FG_val) {
            return -1; //TODO implementation required
        }

    }

    class ImageFeatureF_CentroideRelPosX extends ImageFeatureBase {

        public ImageFeatureF_CentroideRelPosX() {
            this.description = "relative x-Position des Centroide";
        }

        public double CalcFeatureVal(SubImageRegion imgRegion, int FG_val) {
            return -1; //TODO implementation required
        }

    }

    class ImageFeatureF_CentroideRelPosY extends ImageFeatureBase {

        public ImageFeatureF_CentroideRelPosY() {
            this.description = "relative y-Position des Centroide";
        }

        public double CalcFeatureVal(SubImageRegion imgRegion, int FG_val) {
            return -1; //TODO implementation required
        }

    }

} //class OCRanalysisTemplate



