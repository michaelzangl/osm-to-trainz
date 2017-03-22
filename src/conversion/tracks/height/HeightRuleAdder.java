package conversion.tracks.height;

import java.util.List;

import conversion.tracks.CloseTrackFinder;
import conversion.tracks.CloseTrackFinder.Distance;
import conversion.tracks.height.rules.DesireBetweenNodes;
import conversion.tracks.height.rules.DesireHeightRule;
import conversion.tracks.height.rules.DesireInclineBetween;
import conversion.tracks.height.rules.HeightRule;
import data.height.LocalHeightDataProvider;
import data.position.local.LocalPoint;
import export.tracks.HeightPlane;
import export.tracks.TrackConnection;
import export.tracks.TrackNetwork;
import export.tracks.TrackNode;

/**
 * Adds all the different height rules to the tracks.
 * 
 * @author michael
 */
public class HeightRuleAdder {

	private static final float INCLINE_RANGE_FACTOR = 1;
	private static final float SMOOTH_INCLINE_FACTOR = .5f;
	private static final float DESIRE_HEIGHT_FACTOR = .2f;

	private static final float NEIGHBOURS_RATING = 2;
	private static final double MAX_SIDEWARD = 10;
	private static final double MAX_FORWAD = 1;
	private final TrackNetwork network;

	public HeightRuleAdder(TrackNetwork network) {
		this.network = network;
	}

	public void addRules(LocalHeightDataProvider hp) {
		for (TrackNode node : network.getNodes()) {
			float height = hp.getHeight(node.getPosition());
			if (Float.isNaN(height)) {
				System.err.println("Got NaN height from provider!");
			}
			setNodeHeight(node, height);
			addSmoothInclines(node);
			addInclineRules(node);
			addBetweenNeighboursRules(node);
		}
	}

	private void addBetweenNeighboursRules(TrackNode node) {
		CloseTrackFinder closeTrackFinder = new CloseTrackFinder(network);
		LocalPoint position = node.getPosition();
		List<TrackConnection> close =
		        closeTrackFinder.getCloseConnections(position, null,
		                MAX_SIDEWARD, MAX_FORWAD);

		for (TrackConnection connection : close) {
			if (!nodeIsoOnHeightPlane(node, connection.getHeightPlane())) {
				continue;
			}
			Distance distance =
			        closeTrackFinder.getDistances(position, connection);

			//TODO: check if there is a landscape border in between
			
			double relsideward =
			        Math.abs(distance.getSideward() / MAX_SIDEWARD);
			float ratingFactor = (float) (1 - relsideward) * NEIGHBOURS_RATING;
			node.addHeightRule(new DesireBetweenNodes(connection.getStart(),
			        connection.getEnd(),
			        (float) (distance.getForward() / connection.getLength()),
			        ratingFactor));
		}
	}

	private boolean nodeIsoOnHeightPlane(TrackNode node, HeightPlane heightPlane) {
		for (TrackConnection t : node.getTracks()) {
			if ((heightPlane == null && t.getHeightPlane() == null)
			        || (heightPlane != null && heightPlane.equals(t
			                .getHeightPlane()))) {
				return true;
			}
		}
		return false;
	}

	private void setNodeHeight(TrackNode node, float groundheight) {
		int usedconnections = 0;
		float connectionHeightSum = 0;
		for (TrackConnection t : node.getTracks()) {
			float offset = t.getHeightType().getOffsetAboveGround();
			if (!Float.isNaN(offset)) {
				connectionHeightSum += offset;
				usedconnections++;
			}
		}
		float height = groundheight;
		if (usedconnections > 0) {
			height += connectionHeightSum / usedconnections;
		}
		node.setHeight(height);

		float maxHeightDiff = 1;
		boolean useHeights = false;
		for (TrackConnection t : node.getTracks()) {
			if (t.getHeightType() == ConnectionHeightType.EMBANKMENT) {
				maxHeightDiff += 5;
				useHeights = true;
			} else if (t.getHeightType() == ConnectionHeightType.ON_GROUND) {
				useHeights = true;
			}
		}
		if (useHeights) {
			node.addHeightRule(new DesireHeightRule(height, maxHeightDiff,
			        DESIRE_HEIGHT_FACTOR));
		}
	}

	private void addSmoothInclines(TrackNode node) {
		for (TrackConnection con : node.getForwardTracks()) {
			TrackNode forwardNode = con.getOppositeNode(node);
			double forwardLength = con.getLength();
			for (TrackConnection con2 : node.getBackwardTracks()) {
				TrackNode backwardNode = con2.getOppositeNode(node);
				double ratio =
				        forwardLength / (forwardLength + con2.getLength());
				node.addHeightRule(new DesireBetweenNodes(forwardNode,
				        backwardNode, (float) ratio, SMOOTH_INCLINE_FACTOR));
			}
		}
	}

	private void addInclineRules(TrackNode node) {
		for (TrackConnection track : node.getTracks()) {
			float min;
			float max;
			TrackNode otherNode;
			if (track.getStart().equals(node)) {
				min = track.getTracktype().getMinIncline();
				max = track.getTracktype().getMaxIncline();
				otherNode = track.getEnd();
			} else {
				min = -track.getTracktype().getMaxIncline();
				max = -track.getTracktype().getMinIncline();
				otherNode = track.getStart();
			}
			HeightRule rule =
			        new DesireInclineBetween(min, max, otherNode,
			                (float) track.getLength(), INCLINE_RANGE_FACTOR);
			node.addHeightRule(rule);
		}
	}

}
