package conversion.tracks;

import data.filter.Filter;
import data.osm.OsmWay;

public class OsmWayFilter implements Filter<OsmWay> {
	private final OsmWayTyper typer;
	
	public OsmWayFilter(OsmWayTyper typer) {
		this.typer = typer;
	}
	
	@Override
	public boolean matches(OsmWay way) {
		return typer.getTrack(way) != null;
	}

}
