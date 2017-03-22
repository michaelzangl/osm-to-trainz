package data.height;

/**
 * This is a srtm 3 height data pack.
 * 
 * @author michael
 * 
 */
public class SRTMDatapackPosition {
	private final int lat;
	private final int lon;

	public SRTMDatapackPosition(int lat, int lon) {
		this.lat = lat;
		this.lon = lon;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SRTMDatapackPosition) {
			SRTMDatapackPosition other = (SRTMDatapackPosition) obj;
			return other.lat == lat && other.lon == lon;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return lat + lon << 16;
	}

	public String toFilename() {
		String part1 = String.format("%02d", Math.abs(lat));
		if (lat >= 0) {
			part1 = "N" + part1;
		} else {
			part1 = "S" + part1;
		}

		String part2 = String.format("%03d", Math.abs(lon));
		if (lon >= 0) {
			part2 = "E" + part2;
		} else {
			part2 = "W" + part2;
		}
		return part1 + part2 + ".hgt";
	}

	public int getLon() {
		return lon;
	}

	public int getLat() {
		return lat;
	}

	public static SRTMDatapackPosition fromLatLon(float lat, float lon) {
		return new SRTMDatapackPosition((int) Math.floor(lat), (int) Math.floor(lon));
	}
}
