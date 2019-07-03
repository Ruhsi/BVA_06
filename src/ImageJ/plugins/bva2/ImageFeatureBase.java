
public abstract class ImageFeatureBase {

    public String description = "";

    public abstract double CalcFeatureVal(SubImageRegion imgRegion, int FG_val);

}
