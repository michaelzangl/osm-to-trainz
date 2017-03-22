package data.osm;

public class OsmWaySegment {
	private final OsmNode start;
	private final OsmNode end;

	public OsmWaySegment( OsmNode start, OsmNode end) {
		this.start = start;
		this.end = end;
	}
	
	public OsmNode getStart() {
	    return start;
    }
	
	public OsmNode getEnd() {
	    return end;
    }
}
