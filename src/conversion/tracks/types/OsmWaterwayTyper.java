package conversion.tracks.types;

import conversion.tracks.OsmWayTyper;
import conversion.tracks.height.ConnectionHeightType;
import data.osm.OsmWay;
import export.tracks.Subnetwork;
import export.tracks.TrackType;
import export.trainz.general.Kuid;

public class OsmWaterwayTyper implements OsmWayTyper {

	// Bergbach4,<kuid:68787:37115>
	private static final TrackType RIVER =
	        new TrackType(new Kuid(68787, 37115), Subnetwork.WATERWAY);

	@Override
	public TrackType getTrack(OsmWay way) {
		if (way.getProperty("waterway") != null) {
			if (!way.isTunnel()) {
				return RIVER;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
    public ConnectionHeightType getHeightType(OsmWay way) {
	    return ConnectionHeightType.ON_GROUND;
    }
}
