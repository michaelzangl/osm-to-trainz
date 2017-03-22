package conversion.datachange;

import conversion.landscape.Landscape;
import conversion.landscape.LandscapePolygon;
import data.height.HeightDisplacer;
import data.osm.Propertyable;
import data.position.local.LocalPoint;

public class LandscapeDisplacer implements HeightDisplacer {

	private final Landscape landscape;

	public LandscapeDisplacer(Landscape landscape) {
		this.landscape = landscape;
	}

	@Override
	public double getDisplacement(LocalPoint p) {
		LandscapePolygon wayUnder = landscape.getWayUnder(p);
		if (wayUnder != null) {
			return getDisplacement(wayUnder.getOriginal());
		} else {
			return 0;
		}
	}

	private double getDisplacement(Propertyable wayUnder) {
		String landuse = wayUnder.getProperty("landuse");
		if ("forest".equals(landuse)) {
			return -1; // TODO: more, but not so hard borders
		}
		if ("riverbed".equals(landuse)) {
			return -4;
		}
		if ("residential".equals(landuse)) {
			return -1;
		}
		return 0;
	}
}
