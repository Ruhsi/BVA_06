package bva2;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;


public class HoughTransformIrisDetection implements PlugInFilter {


    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about"))
        {showAbout(); return DONE;}
        return DOES_RGB +DOES_STACKS+SUPPORTS_MASKING+ROI_REQUIRED;
    } //setup


    public void run(ImageProcessor ip) {
        BufferedImage buffImage = ip.getBufferedImage();

        int width = ip.getWidth();
        int height = ip.getHeight();

        //convert to grayscale
        //TODO

        //apply convolution filter for smoothing
        //TODO

        //perform edge detection
        //TODO

        //now restrict to sub-image
        Rectangle roiSelection = ip.getRoi();
        int roiWidth = roiSelection.width;
        int roiHeight = roiSelection.height;

        //TODO
        //double[][] croppedImg = ImageJUtility.cropImage(edgeDetected, roiWidth, roiHeight, roiSelection);

        //now generate the hough space
        //TODO
        //HoughSpace houghSpace = genHoughSpace(croppedImg, roiWidth, roiHeight);

        //now chart the result ==> pixels in red
        //TODO
        //ImageJUtility.showNewImage(grayImg, width, height, "res img with marked circle ");

        //finally plot 2D image for best radius and MIP image in direction of the radius
        //TODO
        //plotBestRadiusSpace(houghSpace);
        //plotRadiusMIPSpace(houghSpace);
    } //run

    void showAbout() {
        IJ.showMessage("About Template_...",
                "this is a PluginFilter template\n");
    } //showAbout

    public void plotBestRadiusSpace(HoughSpace houghSpace) {
        //TODO implementation required
    }

    public void plotRadiusMIPSpace(HoughSpace houghSpace) {
        //TODO implementation required
    }


    public HoughSpace genHoughSpace(double[][] edgeImage, int width, int height) {
        //TODO first calculate the parameter range
        //TODO then evaluate fitness for each parameter permutation

        return null;
    }

    Vector<Point> getPointsOnCircle(int x, int y, int radius) {
        Vector<Point> returnVector = new Vector<Point>();

        //TODO: add points on circle
        //==> should lead to closed loop without redundant points

        return returnVector;
    }

    public class HoughSpace {
        double[][][] houghSpace;
        int width;
        int height;

        int bestX;
        int bestY;
        int bestR;

        int minRadius;
        int radiusRange;

        double bestWeight = 0.0;

        public HoughSpace(int width, int height, int radiusRange, int minRadius) {
            this.width = width;
            this.height = height;
            this.bestR = -1;
            this.bestX = -1;
            this.bestY = -1;
            this.bestWeight = 0.0;
            this.minRadius = minRadius;
            this.radiusRange = radiusRange;

            //initialize the array
            houghSpace = new double[width][height][radiusRange];
        }

    }


} //class HoughTransformIrisDetectionTemplate_

 