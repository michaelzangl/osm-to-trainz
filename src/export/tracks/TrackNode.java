package export.tracks;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import conversion.tracks.height.rules.HeightRule;
import data.position.local.LocalPoint;

/**
 * This is a track node. It holds the reference to the track connections.
 * 
 * @author michael
 */
public class TrackNode {
	private static final double MAX_ANGULAR_DIFF = Math.PI / 6; // 30°
	private LinkedList<TrackConnection> forwardTracks =
	        new LinkedList<TrackConnection>();
	private LinkedList<TrackConnection> backwardTracks =
	        new LinkedList<TrackConnection>();
	private LinkedList<TrackConnection> tracks =
	        new LinkedList<TrackConnection>();

	private LinkedList<HeightRule> heightRules = new LinkedList<HeightRule>();

	private float forwardDirection;
	private LocalPoint position;
	private final int id;

	public TrackNode(int id, LocalPoint position) {
		this.id = id;
		this.position = position;
	}

	/**
	 * The backward track list only holds one object, that should normally be
	 * forward. But if it would be forward, we would not have a backward object
	 * => bad geometry.
	 * <p>
	 * May only be set if backwardTracks.size() == 1.
	 */
	private boolean backwardTrackDesiresForward = false;

	public synchronized void addTrack(TrackConnection connection,
	        float direction) {
		if (forwardTracks.isEmpty()) {
			forwardTracks.add(connection);
			forwardDirection = direction;
		} else {
			if (backwardTrackDesiresForward) {
				// we found an other idiot and we can face forward... :D
				forwardTracks.add(backwardTracks.poll());
			}

			double angularDiff =
			        Math.abs((direction - forwardDirection) % (2 * Math.PI));
			boolean shouldBeForward =
			        angularDiff < Math.PI / 2 || angularDiff > Math.PI * 3 / 2;
			if (backwardTracks.isEmpty()) {
				backwardTracks.add(connection);
				backwardTrackDesiresForward = shouldBeForward;
			} else if (shouldBeForward) {
				forwardTracks.add(connection);
			} else {
				backwardTracks.add(connection);
			}
		}
		assert backwardTracks.isEmpty() || !forwardTracks.isEmpty();
		tracks.add(connection);
	}

	private void removeConnection(TrackConnection connection) {
		if (forwardTracks.contains(connection)) {
			forwardTracks.remove(connection);
			if (forwardTracks.isEmpty()) {
				forwardTracks.addAll(backwardTracks);
				backwardTracks.clear();
				forwardDirection += Math.PI;
				forwardDirection %= 2 * Math.PI;
			}

		} else if (backwardTracks.contains(connection)) {
			backwardTracks.remove(connection);
			if (backwardTrackDesiresForward) {
				backwardTrackDesiresForward = false;
			}
			assert !forwardTracks.isEmpty();
			if (backwardTracks.isEmpty() && forwardTracks.size() > 1) {
				TrackConnection track = forwardTracks.remove(0);
				backwardTracks.add(track);
				backwardTrackDesiresForward = true;
			}
		}
		assert backwardTracks.isEmpty() || !forwardTracks.isEmpty();
		tracks.remove(connection);
	}

	public List<TrackConnection> getForwardTracks() {
		return Collections.unmodifiableList(forwardTracks);
	}

	public List<TrackConnection> getBackwardTracks() {
		return Collections.unmodifiableList(backwardTracks);
	}

	public List<TrackConnection> getTracks() {
		return Collections.unmodifiableList(tracks);
	}

	public LocalPoint getPosition() {
		return position;
	}

	public int getId() {
		return id;
	}

	public void setHeight(double newheight) {
		this.position = this.position.withHeight(newheight);
	}

	/**
	 * Extracts some unwanted tracks.
	 * <p>
	 * Apply it multiple times to extract all of them.
	 * <p>
	 * You might also want to apply it to the new node ;-)
	 * 
	 * @return A new node with the unwanted connections bound to it or
	 *         <code>null</code> if no such connections exist.
	 */
	public TrackNode extractUnwanted(int newId) {
		if (tracks.size() < 1) {
			return null;
		}

		TrackNode newNode = null;

		List<DirectionTrack> list = getDirectionedTracks();

		// filter by subnetwork
		Subnetwork subnetwork = tracks.get(0).getTracktype().getSubnetwork();
		LinkedList<DirectionTrack> wrongNetwork =
		        getTracksWithWringNetwork(list, subnetwork);

		if (!wrongNetwork.isEmpty()) {
			newNode = moveToNewNode(newId, wrongNetwork);
		} else {
			LinkedList<DirectionTrack> removed =
			        removeWithDirection(list, list.get(0).direction);
			if (list.isEmpty()) {
				// all nodes have same direction and are in removed list
				// onliest thing that could happen: all face same direction.
				if (backwardTrackDesiresForward) {
					newNode = new TrackNode(newId, this.position);
					TrackConnection backwardTrack = getBackwardTracks().get(0);
					removeConnection(backwardTrack);
					newNode.addTrack(backwardTrack,
					        getDirectionOfConnection(backwardTrack));
					changeConnectionsToNewNode(newNode);
				} else if (removed.size() > 4) {
					for (int i = 0; i < 4; i++) {
						// let some of them stay
						removed.remove();
					}
					newNode = moveToNewNode(newId, removed);
				}
			} else if (!removed.isEmpty()) {
				newNode = moveToNewNode(newId, removed);
			}
		}

		assert !tracks.isEmpty() : "Track list should not be empty here";
		assert newNode == null || !newNode.tracks.isEmpty() : "Track list should not be empty here";
		return newNode;
	}

	private LinkedList<DirectionTrack> getTracksWithWringNetwork(
	        List<DirectionTrack> list, Subnetwork subnetwork) {
		LinkedList<DirectionTrack> wrongNetwork =
		        new LinkedList<DirectionTrack>();
		for (DirectionTrack d : list) {
			TrackConnection connection = d.connection;
			if (!connection.getTracktype().getSubnetwork().equals(subnetwork)) {
				wrongNetwork.add(d);
			}
		}
		return wrongNetwork;
	}

	private void changeConnectionsToNewNode(TrackNode newNode) {
		for (TrackConnection connection : newNode.tracks) {
			if (connection.getStart() == this) {
				connection.setStart(newNode);
			}
			if (connection.getEnd() == this) {
				connection.setEnd(newNode);
			}
		}
	}

	private TrackNode moveToNewNode(int newId,
	        LinkedList<DirectionTrack> removed) {
		TrackNode newNode;
		newNode = new TrackNode(newId, this.position);
		while (!removed.isEmpty()) {
			DirectionTrack newConnection = removed.poll();
			removeConnection(newConnection.connection);
			newNode.addTrack(newConnection.connection, newConnection.direction);
		}
		changeConnectionsToNewNode(newNode);
		return newNode;
	}

	/**
	 * removes all connections from the list with apporx. the same direciton.
	 * 
	 * @param list
	 * @param direction
	 * @return
	 */
	private LinkedList<DirectionTrack> removeWithDirection(
	        List<DirectionTrack> list, float direction) {
		LinkedList<DirectionTrack> removed = new LinkedList<DirectionTrack>();
		Iterator<DirectionTrack> it = list.iterator();
		while (it.hasNext()) {
			DirectionTrack t = it.next();
			double diff = Math.abs((t.direction - direction) % Math.PI);
			if (diff > MAX_ANGULAR_DIFF && diff < Math.PI - MAX_ANGULAR_DIFF) {
				it.remove();
				removed.add(t);
			}
		}

		return removed;
	}

	private List<DirectionTrack> getDirectionedTracks() {
		LinkedList<DirectionTrack> list = new LinkedList<DirectionTrack>();
		for (TrackConnection connection : tracks) {
			list.add(new DirectionTrack(connection));
		}
		return list;
	}

	private class DirectionTrack {
		private final TrackConnection connection;
		private final float direction;

		private DirectionTrack(TrackConnection connection) {
			this.connection = connection;
			this.direction = getDirectionOfConnection(connection);
		}

	}

	private float getDirectionOfConnection(TrackConnection connection) {
		LocalPoint start = getPosition();
		LocalPoint end =
		        connection.getOppositeNode(TrackNode.this).getPosition();
		return (float) Math.atan2(start.getY() - end.getY(),
		        start.getX() - end.getX());
	}

	public List<TrackConnection> getOppositeTracks(TrackConnection connection) {
		if (forwardTracks.contains(connection)) {
			return getBackwardTracks();
		} else if (backwardTracks.contains(connection)) {
			return getForwardTracks();
		} else {
			throw new IllegalArgumentException(
			        "The connection is not contained in this node.");
		}
	}

	public LinkedList<HeightRule> getHeightRules() {
		return heightRules;
	}

	public void addHeightRule(HeightRule rule) {
		heightRules.add(rule);
	}
}
