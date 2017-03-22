package export.tracks;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import conversion.datachange.geometry.Rectangle;
import conversion.tracks.height.rules.LinkNodesRule;
import data.position.local.LocalPoint;
import data.rtree.RTree;
import export.trainz.general.Kuid;

/**
 * This class defines a network of tracks.
 * 
 * @author michael
 */
public class TrackNetwork {
	private static final double STRAIGHT_LENGTH_FACTOR = 2.5;
	private LinkedList<TrackNode> nodes = new LinkedList<TrackNode>();
	private LinkedList<TrackConnection> connections =
	        new LinkedList<TrackConnection>();
	private int nodeIdCounter = 0;
	private int connectionIdCounter = 0;
	
	private RTree<TrackConnection> connectionTree = new RTree<TrackConnection>();

	public TrackNetwork() {
	}

	public synchronized TrackNode addNode(LocalPoint point) {
		TrackNode node = new TrackNode(nodeIdCounter, point);
		nodes.add(node);
		nodeIdCounter++;
		return node;
	}

	/**
	 * Connects two nodes, but does not add any rules.
	 * 
	 * @param node1
	 *            The start node
	 * @param node2
	 *            The end node
	 * @return The connection that was generated.
	 * @throws IllegalArgumentException
	 *             If the nodes are not contained in this network.
	 */
	public synchronized TrackConnection connectNodes(TrackNode node1,
	        TrackNode node2) {
		if (!nodes.contains(node1) || !nodes.contains(node2)) {
			throw new IllegalArgumentException(
			        "One of the nodes to connect is not contained in the network");
		}

		TrackConnection connection =
		        new TrackConnection(connectionIdCounter, node1, node2);
		double dy = node1.getPosition().getY() - node2.getPosition().getY();
		double dx = node1.getPosition().getX() - node2.getPosition().getX();
		float direction = (float) Math.atan2(dy, dx);

		node1.addTrack(connection, direction);
		node2.addTrack(connection, direction + (float) Math.PI);
		
		connections.add(connection);
		connectionTree.add(connection);
		
		connectionIdCounter++;
		return connection;
	}
	
	/**
	 * gets all connections that intersect the rectangle, maby more.
	 * @param rect
	 * @return
	 */
	public List<TrackConnection> getConnectionsIn(Rectangle rect) {
		return connectionTree.findElementsIntersecting(rect);
	}

	public List<TrackNode> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public List<TrackConnection> getConnections() {
		return Collections.unmodifiableList(connections);
	}

	/**
	 * Gets a list of used Kuids in this network
	 * 
	 * @return The list.
	 */
	public Collection<Kuid> getUsedKuids() {
		HashSet<Kuid> used = new HashSet<Kuid>();
		for (TrackConnection link : getConnections()) {
			used.add(link.getTracktype().getKuid());
		}
		return used;
	}

	/**
	 * Cleans up the node network and splits nodes with bad geometry.
	 */
	public void cleanUp() {
		Iterator<TrackNode> it = nodes.iterator();

		while (it.hasNext()) {
			TrackNode node = it.next();
			if (node.getTracks().size() == 0) {
				it.remove();
			}
		}
		Queue<TrackNode> toClean = new ConcurrentLinkedQueue<TrackNode>();

		toClean.addAll(nodes);
		while (!toClean.isEmpty()) {
			TrackNode node = toClean.poll();
			TrackNode cleaned = node.extractUnwanted(nodeIdCounter);
			if (cleaned != null) {
				LinkNodesRule.addNodeLink(node, cleaned);
				nodes.add(cleaned);
				nodeIdCounter++;
				toClean.add(cleaned);
				toClean.add(node);
			}
		}
	}

	/**
	 * Sets a straight-flag for all tracks that are long or are a junction.
	 */
	public void straightenLongLines() {
		for (TrackConnection connection : connections) {
			List<TrackConnection> side1 =
			        connection.getStart().getOppositeTracks(connection);
			List<TrackConnection> side2 =
			        connection.getEnd().getOppositeTracks(connection);

			if (side1.size() != 1 || side2.size() != 1) {
				connection.setStraight(true);
			} else {
				double length = connection.getLength() / STRAIGHT_LENGTH_FACTOR;
				if (length > side1.get(0).getLength()
				        && length > side2.get(0).getLength()) {
					connection.setStraight(true);
				} else {
					connection.setStraight(false);
				}
			}
		}
	}
}
