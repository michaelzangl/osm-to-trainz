package conversion.tracks;

import conversion.tracks.height.ConnectionHeightType;
import data.osm.OsmWay;
import export.tracks.TrackType;

public interface OsmWayTyper {
	TrackType getTrack(OsmWay way);
	
	/**
	 * Gets the height of the way.
	 * @param way A way, for which getTrack(way) != null
	 * @return The height type, never null.
	 */
	ConnectionHeightType getHeightType(OsmWay way);
}
