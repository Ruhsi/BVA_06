package bva2;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.*;
import java.util.Vector;


public class AdaptiveThreshold_ implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            showAbout();
            return DONE;
        }
        return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
    } //setup


    public void run(ImageProcessor ip) {
        byte[] pixels = (byte[]) ip.getPixels();
        int width = ip.getWidth();
        int height = ip.getHeight();
        int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);

        int FG_VAL = 255; //max value
        int BG_VAL = 0;
        int Tmin = 100;
        int Tmax = 240;

        //get transform array
        int[] tfArray = ImageTransformationFilter.GetBinaryThresholdTF(FG_VAL, Tmin, Tmax, FG_VAL, BG_VAL);
        int[][] resultImg = ImageTransformationFilter.GetTransformedImage(inDataArrInt, width, height, tfArray);

        ImageJUtility.showNewImage(resultImg, width, height, "inverted image");

    } //run

    public static int[][] AdaptiveOptimalThreshold(int[][] inImg, int width, int height, int size,
                                                   int con) {
        int mean = 0;
        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                mean = 0;
                count = 0;
                for(int i = 0; i < size; i++){
                    for(int j = 0; j < size; j++){
                        try {
                            mean = mean + inImg[(x-((int)(size/2))+i)]
                                    [(y-((int)(size/2))+j)];
                            count++;
                        } catch (ArrayIndexOutOfBoundsException e){

                        }
                    }
                }
            }
        }
        return inImg;
    }

    void showAbout() {
        IJ.showMessage("About Template_...",
                "Inverts color values\n");
    } //showAbout

} //class Invert_

