package conversion.tracks.types;

import java.util.Arrays;
import java.util.List;

import conversion.tracks.OsmWayTyper;
import conversion.tracks.height.ConnectionHeightType;
import data.osm.OsmWay;
import export.tracks.Subnetwork;
import export.tracks.TrackType;
import export.trainz.general.Kuid;

/**
 * This class specifies the track type for osm tracks.
 * 
 * @author michael
 */
public class OsmRailwayTyper implements OsmWayTyper {

	private static final TrackType BETON_DIRTY_BRIDGE = new TrackType(new Kuid(
	        41462, 38800), Subnetwork.RAIL);
	private static final TrackType BETON_DIRTY_TRACK = new TrackType(new Kuid(
	        36713, 38800), Subnetwork.RAIL);

	private static final TrackType BETON_NEW_BRIDGE = new TrackType(new Kuid(
	        41462, 38010), Subnetwork.RAIL);
	private static final TrackType BETON_NEW_TRACK = new TrackType(new Kuid(
	        38802, 38802), Subnetwork.RAIL);

	// Gelände 1G,<kuid:36713:38814>
	private static final TrackType OLD_TRACK = new TrackType(new Kuid(36713,
	        38814), Subnetwork.RAIL);

	private static final List<String> RAILWAY_TYPES = Arrays.asList("rail",
	        "tram", "light_rail");

	@Override
	public TrackType getTrack(OsmWay way) {
		String railway = way.getProperty("railway");
		if (railway != null) {
			return getRailway(way);
		}

		return null;
	}

	private TrackType getRailway(OsmWay way) {
		String railway = way.getProperty("railway");
		if (RAILWAY_TYPES.contains(railway)) {
			if (way.isBridge()) {
				return BETON_DIRTY_BRIDGE;
			} else {
				return BETON_DIRTY_TRACK;
			}
		} else if ("disused".equals(railway)){
			return OLD_TRACK;
		} else {
			return null;
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
