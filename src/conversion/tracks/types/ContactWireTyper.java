package conversion.tracks.types;

import conversion.tracks.OsmWayTyper;
import conversion.tracks.height.ConnectionHeightType;
import data.osm.OsmWay;
import export.tracks.Subnetwork;
import export.tracks.TrackType;
import export.trainz.general.Kuid;

public class ContactWireTyper implements OsmWayTyper {

	private static final TrackType CONTACT_WIRE = new TrackType(new Kuid(-1, 110004), Subnetwork.CONTACT_WIRE);

	@Override
	public TrackType getTrack(OsmWay way) {
		if ("contact_line".equals(way.getProperty("electrified"))) {
			return CONTACT_WIRE;
		}
		return null;
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
