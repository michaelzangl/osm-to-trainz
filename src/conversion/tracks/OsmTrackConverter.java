package conversion.tracks;

import java.util.Hashtable;
import java.util.Iterator;

import conversion.ConversionData;
import conversion.landscape.Landscape;
import conversion.tracks.types.BarrierTyper;
import conversion.tracks.types.ContactWireTyper;
import conversion.tracks.types.OsmHighwayTyper;
import conversion.tracks.types.OsmRailwayTyper;
import conversion.tracks.types.OsmWaterwayTyper;
import data.osm.OsmDatapack;
import data.osm.OsmNode;
import data.osm.OsmWay;
import data.osm.OsmWaySegment;
import data.position.local.GlobalToLocalConverter;
import data.position.local.LocalPoint;
import export.tracks.TrackConnection;
import export.tracks.TrackNetwork;
import export.tracks.TrackNode;
import export.tracks.TrackType;

/**
 * Converts an osm dataset to a track network.
 * 
 * @see #addToNetwork()
 * @author michael
 */
public class OsmTrackConverter {
	private final TrackNetwork network;
	private final Hashtable<OsmNode, TrackNode> knownNodes =
	        new Hashtable<OsmNode, TrackNode>();
	private final ConversionData data;

	public OsmTrackConverter(ConversionData data) {
		this.data = data;
		this.network = data.getNetwork();
	}

	public void addToNetwork() {
		applyTyper(new OsmRailwayTyper());
		applyTyper(new OsmHighwayTyper());
		applyTyper(new ContactWireTyper());
		applyTyper(new OsmWaterwayTyper());
		applyTyper(new BarrierTyper());
	}

	private void applyTyper(OsmWayTyper typer) {
		for (OsmWay way : data.getOsmData().getWays()) {
			loadWay(way, typer);
		}
	}

	private void loadWay(OsmWay way, OsmWayTyper typer) {
		TrackType tracktype = typer.getTrack(way);
		if (tracktype == null) {
			return;
		}

		Iterator<OsmWaySegment> it = way.getSegments();
		while (it.hasNext()) {
			OsmWaySegment next = it.next();

			TrackNode start = getNode(next.getStart());
			TrackNode end = getNode(next.getEnd());

			if (start != null && end != null) {
				TrackConnection connection = network.connectNodes(start, end);
				connection.setTracktype(tracktype);
				connection.setHeightType(typer.getHeightType(way));
			}
		}
	}

	private TrackNode getNode(OsmNode start) {
		TrackNode knownNode = knownNodes.get(start);
		if (knownNode != null) {
			return knownNode;
		} else {
			LocalPoint position =
			        data.getConverter().toLocal(start.getLat(), start.getLon());

			if (data.getLandscape().isLandUnder(position)) {
				TrackNode node = network.addNode(position);
				knownNodes.put(start, node);
				return node;
			} else {
				return null;
			}
		}
	}

}
