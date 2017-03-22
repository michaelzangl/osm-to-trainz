package conversion.tracks;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import conversion.datachange.geometry.SimpleRectangle;

import data.position.local.LocalPoint;
import export.tracks.Subnetwork;
import export.tracks.TrackConnection;
import export.tracks.TrackNetwork;

/**
 * This class finds close tracks to a point.
 * 
 * @author michael
 */
public final class CloseTrackFinder {
	private final TrackNetwork network;

	public CloseTrackFinder(TrackNetwork network) {
		this.network = network;

	}

	/**
	 * Gets the closest track on the layout.
	 * 
	 * @param subnet
	 *            The subnetwork the track should be in.
	 * @return The closest track connection, or null if there are no connections
	 *         in the network.
	 */
	public TrackConnection getClosestConnection(LocalPoint point,
	        Subnetwork subnet) {
		double currentMinDistance = Double.POSITIVE_INFINITY;
		TrackConnection closest = null;

		for (TrackConnection c : network.getConnections()) {
			if (subnet != null
			        && !subnet.equals(c.getTracktype().getSubnetwork())) {
				continue;
			}
			double distance = getDistanceSquared(point, c);
			if (distance < currentMinDistance) {
				closest = c;
				currentMinDistance = distance;
			}
		}

		return closest;
	}

	public List<TrackConnection> getCloseConnections(LocalPoint point,
	        Subnetwork subnet, double maxSidewards, double maxOverhead) {
		double radius = Math.max(maxSidewards, maxSidewards);
		SimpleRectangle rect =
		        new SimpleRectangle(point.getX() - radius, point.getY()
		                - radius, point.getX() + radius, point.getY() + radius);

		ArrayList<TrackConnection> result = new ArrayList<TrackConnection>();

		
		List<TrackConnection> connections = network.getConnectionsIn(rect);
		for (TrackConnection c : connections) {
			if (subnet != null
			        && !subnet.equals(c.getTracktype().getSubnetwork())) {
				continue;
			}
			Distance distance = getDistances(point, c);
			if (distance.getOverhead() <= maxOverhead
			        && Math.abs(distance.getSideward()) < maxSidewards) {
				result.add(c);
			}
		}

		return result;
	}

	public List<TrackConnection> getCrossing(TrackConnection toTest) {
		ArrayList<TrackConnection> result = new ArrayList<TrackConnection>();

		double x1 = toTest.getStart().getPosition().getX();
		double y1 = toTest.getStart().getPosition().getY();
		double x2 = toTest.getEnd().getPosition().getX();
		double y2 = toTest.getEnd().getPosition().getY();

		for (TrackConnection c : network.getConnections()) {
			double x3 = c.getStart().getPosition().getX();
			double y3 = c.getStart().getPosition().getY();
			double x4 = c.getEnd().getPosition().getX();
			double y4 = c.getEnd().getPosition().getY();
			if (Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
				result.add(c);
			}
		}
		return result;
	}

	public double getDistance(LocalPoint point, TrackConnection connection) {
		return Math.sqrt(getDistanceSquared(point, connection));
	}

	public double getDistanceSquared(LocalPoint point,
	        TrackConnection connection) {
		Distance d = getDistances(point, connection);
		return d.getOverhead() * d.getOverhead() + d.getSideward()
		        + d.getSideward();
	}

	public Distance getDistances(LocalPoint point, TrackConnection connection) {
		// TODO: store direction vectors with connection
		double length = connection.getLength();
		double startx = connection.getStart().getPosition().getX();
		double endx = connection.getEnd().getPosition().getX();
		double dx = (startx - endx) / length;

		double starty = connection.getStart().getPosition().getY();
		double endy = connection.getEnd().getPosition().getY();
		double dy = (starty - endy) / length;

		// forward from start of track
		double pointx = startx - point.getX();
		double pointy = starty - point.getY();
		double forward;
		double sideward;
		if (dy > 0.0001) {
			forward = (pointx / dy + pointy) / (dy - dx / dy);
			sideward = (pointx - forward * dx) / dy;
		} else {
			forward = pointx;
			sideward = pointy;
		}

		double realForward;
		if (forward < 0) {
			realForward = -forward;
		} else if (forward > length) {
			realForward = forward - length;
		} else {
			realForward = 0;
		}

		return new Distance(forward, sideward, realForward);
	}

	public final class Distance {
		/**
		 * Forward from start of connction
		 */
		private final double forward;
		/**
		 * Sideward
		 */
		private final double sideward;

		/**
		 * More than the ends. Positive.
		 */
		private final double overhead;

		private Distance(double forward, double sideward, double overhead) {
			this.forward = forward;
			this.sideward = sideward;
			this.overhead = overhead;

		}

		public double getOverhead() {
			return overhead;
		}

		public double getForward() {
			return forward;
		}

		public double getSideward() {
			return sideward;
		}
	}
}
