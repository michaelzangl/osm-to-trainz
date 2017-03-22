package data.position.local;

/**
 * This class allows you to convert global to local positions (and the other way
 * around).
 * 
 * @author michael
 */
public class GlobalToLocalConverter {
	private final float EARTH_RADIUS = 6371000.0f;
	private final float EARTH_CIRCUMFERENCE = 40007860f;

	private final LatLon center;

	// The lat where our cooridnate system is oriented on, for scaling.
	private final int latCenter;
	private final double centerCircumference;

	public GlobalToLocalConverter(LatLon center) {
		this.center = center;
		int lat = (int) Math.abs(Math.round(center.getLat() / 10) * 10);
		if (lat > 80) {
			latCenter = 80;
		} else {
			latCenter = lat;
		}
		centerCircumference =
		        EARTH_CIRCUMFERENCE * Math.cos(Math.toRadians(lat));
	}

	/**
	 * Converts a y coordinate (0..1) to lat.
	 * 
	 * @source OSM wiki
	 * @param aY
	 * @return
	 */
	public static double y2lat(double aY) {
		return Math.toDegrees(2 * Math.atan(Math.exp(Math.toRadians(aY)))
		        - Math.PI / 2);
	}

	public static double lat2y(double aLat) {
		return Math.toDegrees(Math.log(Math.tan(Math.PI / 4
		        + Math.toRadians(aLat) / 2)));
	}

	/**
	 * Converts a lat/lon-point to a local coordinate
	 * 
	 * @param lat
	 * @param lon
	 * @return
	 */
	public LocalPoint toLocal(double lat, double lon) {
		double dlat = lat - center.getLat();
		double x = -dlat / 360 * EARTH_CIRCUMFERENCE;
		double dlon = lon - center.getLon();
		double y = dlon / 360 * centerCircumference;
		return new LocalPoint(x, y);
	}

	/**
	 * Converts a local point back to global.
	 * 
	 * @param localPoint
	 * @return
	 */
	public LatLon toGlobal(LocalPoint localPoint) {
		double lat =
		        -localPoint.getX() * 360 / EARTH_CIRCUMFERENCE
		                + center.getLat();
		double lon =
		        localPoint.getY() * 360 / centerCircumference + center.getLon();
		return new LatLon(lat, lon);
	}

	public LocalPoint toLocal(LatLonObject global) {
		return toLocal(global.getLat(), global.getLon());
	}
}
