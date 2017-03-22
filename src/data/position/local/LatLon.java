package data.position.local;

/**
 * 
 * @author michael
 *
 */
public class LatLon implements LatLonObject{
	
	private final double lat;
	private final double lon;

	public LatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLon() {
		return lon;
	}
	
	@Override
	public String toString() {
	    return "LatLon[lat=" + lat + ",lon=" + lon + "]";
	}
}
