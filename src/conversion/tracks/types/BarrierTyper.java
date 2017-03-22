package conversion.tracks.types;

import conversion.tracks.OsmWayTyper;
import conversion.tracks.height.ConnectionHeightType;
import data.osm.OsmWay;
import export.tracks.Subnetwork;
import export.tracks.TrackType;
import export.trainz.general.Kuid;
import export.trainz.general.Kuid2;

public class BarrierTyper implements OsmWayTyper {

	private static final TrackType HEDGE = new TrackType(new Kuid(-1, 18),
	        Subnetwork.BARRIER);
	private static final TrackType NOISE_BARRIER = new TrackType(new Kuid2(
	        243555, 30040, 1), Subnetwork.BARRIER);
	private static final TrackType FENCE = new TrackType(
	        new Kuid(68787, 22126), Subnetwork.BARRIER);

	@Override
	public TrackType getTrack(OsmWay way) {
		String barrier = way.getProperty("barrier");
		if ("hedge".equals(barrier)) {
			return HEDGE;
		} else if ("fence".equals(barrier)) {
			return FENCE;
		} else if ("wall".equals(barrier)) {
			return NOISE_BARRIER;
		}
		return null;
	}

	@Override
	public ConnectionHeightType getHeightType(OsmWay way) {
		return ConnectionHeightType.ON_GROUND;
	}
}
