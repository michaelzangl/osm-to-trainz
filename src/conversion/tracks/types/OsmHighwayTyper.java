package conversion.tracks.types;

import conversion.tracks.OsmWayTyper;
import conversion.tracks.height.ConnectionHeightType;
import data.osm.OsmWay;
import export.tracks.Subnetwork;
import export.tracks.TrackType;
import export.trainz.general.Kuid;
import export.trainz.general.Kuid2;

public class OsmHighwayTyper implements OsmWayTyper {

	private static final TrackType MOTORWAY_BRIDGE = new TrackType(new Kuid(
	        41462, 38103), Subnetwork.STREET);
	private static final TrackType MOTORWAY_TRACK = new TrackType(new Kuid(
	        36713, 37302), Subnetwork.STREET);

	private static final TrackType RESIDENTIAL_BRIDGE = new TrackType(new Kuid(
	        41462, 38101), Subnetwork.STREET);
	private static final TrackType RESIDENTIAL_TRACK = new TrackType(new Kuid(
	        36713, 37301), Subnetwork.STREET);

	private static final TrackType NORMAL_BRIDGE = new TrackType(new Kuid(
	        41462, 38102), Subnetwork.STREET);
	private static final TrackType NORMAL_TRACK = new TrackType(new Kuid(36713,
	        37300), Subnetwork.STREET);

	private static final TrackType FILED_TRACK = new TrackType(new Kuid2(68787,
	        37502, 2), Subnetwork.STREET);

	@Override
	public TrackType getTrack(OsmWay way) {
		if (way.getProperty("highway") != null) {
			return getHighway(way);
		}

		return null;
	}

	private TrackType getHighway(OsmWay way) {
		String highway = way.getProperty("highway");
		if ("residential".equals(highway)) {
			if (way.isBridge()) {
				return RESIDENTIAL_BRIDGE;
			} else {
				return RESIDENTIAL_TRACK;
			}
		} else if ("primary".equals(highway)
		        || "2".equals(way.getProperty("lanes"))) {
			if (way.isBridge()) {
				return MOTORWAY_BRIDGE;
			} else {
				return MOTORWAY_TRACK;
			}
		} else if ("track".equals(highway)) {
			return FILED_TRACK;
		} else {
			if (way.isBridge()) {
				return NORMAL_BRIDGE;
			} else {
				return NORMAL_TRACK;
			}
		}
	}

	@Override
    public ConnectionHeightType getHeightType(OsmWay way) {
	    if (way.isBridge()) {
	    	return ConnectionHeightType.BRIDGE;
	    } else if (way.isTunnel()) {
	    	return ConnectionHeightType.TUNNEL;
	    } else if (way.isEmbarkment()) {
	    	return ConnectionHeightType.EMBANKMENT;
	    } else {
	    	return ConnectionHeightType.ON_GROUND;
	    }
    }
}
