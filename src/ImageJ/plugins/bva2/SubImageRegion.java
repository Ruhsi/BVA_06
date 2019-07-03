

public class SubImageRegion  {

    public int startX; //relative to the original image ==> pos of top left pixel
    public int startY;
    public int width;
    public int height;

    public int[][] subImgArr;

    public SubImageRegion(int startX, int startY, int width, int height, int[][] origImgArr) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;

        this.subImgArr = new int[width][height];
        int xSub = 0;
        int ySub = 0;
        // go from startX to startX+width
        for(int x = startX; x < width+startX; x++){
            for(int y = 0; y < height; y++){
                subImgArr[xSub][ySub] = origImgArr[x][y+startY];
                ySub++;
            }
            xSub++;
            ySub = 0;
        }
    }

    public void showImage(){
        bva2.ImageJUtility.showNewImage(subImgArr, width, height, "subimage");
    }

}
