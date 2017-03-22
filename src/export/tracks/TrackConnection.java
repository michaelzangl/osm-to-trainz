package export.tracks;

import conversion.datachange.geometry.SimpleRectangle;
import conversion.tracks.height.ConnectionHeightType;
import data.rtree.Regionable;

/**
 * This is a track segment between two track nodes.
 * <p>
 * TODO: move heightType to tracktype?
 * 
 * @author michael
 */
public class TrackConnection implements Regionable {
	private boolean straight = false;

	private TrackType tracktype = TrackType.DEFAULT;

	private final int id;

	private TrackNode end;

	private TrackNode start;

	private SimpleRectangle bounds;

	ConnectionHeightType heightType = ConnectionHeightType.WHATEVER;

	private HeightPlane heightPlane;

	/**
	 * Only constructs the connection, does not register it with start/end.
	 * 
	 * @param id
	 * @param start
	 * @param end
	 */
	public TrackConnection(int id, TrackNode start, TrackNode end) {
		this.id = id;
		this.start = start;
		this.end = end;
		recomputeBounds();
	}

	private void recomputeBounds() {
	    double startx = start.getPosition().getX();
		double endx = end.getPosition().getX();
		double starty = start.getPosition().getY();
		double endy = end.getPosition().getY();
		
		double minX = Math.min(startx, endx);
		double minY = Math.min(starty, endy);
		double maxX = Math.max(startx, endx);
		double maxY = Math.max(starty, endy);
		bounds = new SimpleRectangle(minX, minY, maxX, maxY);
    }

	public boolean isStraight() {
		return straight;
	}

	public void setStraight(boolean straight) {
		this.straight = straight;
	}

	public TrackType getTracktype() {
		return tracktype;
	}

	public void setTracktype(TrackType tracktype) {
		this.tracktype = tracktype;
	}

	public int getId() {
		return id;
	}

	public TrackNode getStart() {
		return start;
	}

	public TrackNode getEnd() {
		return end;
	}
	
	@Override
	public SimpleRectangle getBounds() {
	    return bounds;
    }

	/**
	 * Computes the length of this segment
	 * 
	 * @return The distance between both nodes
	 */
	public double getLength() {
		return Math.hypot(
		        start.getPosition().getX() - end.getPosition().getX(), start
		                .getPosition().getY() - end.getPosition().getY());
	}

	public boolean isHeightSet() {
		return !Double.isNaN(start.getPosition().getHeight())
		        || !Double.isNaN(start.getPosition().getHeight());
	}

	/**
	 * Gets the opposite node of the current node.
	 * 
	 * @param current
	 *            The node at one side
	 * @return The node at the other side.
	 * @throws IllegalArgumentException
	 *             If the node is not contained by this connection.
	 */
	public TrackNode getOppositeNode(TrackNode current) {
		if (start.equals(current)) {
			return end;
		} else if (end.equals(current)) {
			return start;
		} else {
			throw new IllegalArgumentException("The node " + current
			        + " does not belong to this track.");
		}
	}

	protected void setStart(TrackNode start) {
		this.start = start;recomputeBounds();
	}

	protected void setEnd(TrackNode end) {
		this.end = end;recomputeBounds();
	}

	public ConnectionHeightType getHeightType() {
		return heightType;
	}

	public void setHeightType(ConnectionHeightType heightType) {
		if (heightType == null) {
			throw new NullPointerException();
		}
		this.heightType = heightType;
	}

	/**
	 * Gets the incline from the start point to the end point, as ratio.
	 * <p>
	 * Computes: <code>(end.height - start.height) / length</code>
	 * 
	 * @return The incline
	 */
	public float getIncline() {
		double length = getLength();
		double heightdiff =
		        end.getPosition().getHeight() - start.getPosition().getHeight();
		return (float) (heightdiff / length);
	}

	public HeightPlane getHeightPlane() {
		return heightPlane;
	}

	public void setHeightPlane(HeightPlane heightPlane) {
		this.heightPlane = heightPlane;
	}
}
