package conversion.landscape;

import conversion.datachange.geometry.Polygon;
import data.osm.Propertyable;

public class LandscapePolygon implements
        Comparable<LandscapePolygon> {

	private final double sortindex;
	private final Propertyable original;
	private final Polygon polygon;

	LandscapePolygon(double sortindex, Propertyable original,
	        Polygon polygon) {
		this.sortindex = sortindex;
		this.original = original;
		this.polygon = polygon;

	}

	@Override
    public int compareTo(LandscapePolygon o) {
        return Double.compare(sortindex, o.sortindex);
    }

	public Polygon getPolygon() {
	    return polygon;
    }

	public Propertyable getOriginal() {
	    return original;
    }
	
	@Override
	public String toString() {
	    return "LanscapePolygon[" + polygon + "]";
	}
}