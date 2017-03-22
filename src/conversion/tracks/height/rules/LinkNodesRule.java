package conversion.tracks.height.rules;

import export.tracks.TrackNode;

/**
 * This rule lets you link two nodes.
 * <p>
 * The rule always returns optimal rating and has a factor of 0.
 * <p>
 * It is more an indicator that two nodes should be handled as one node.
 * <p>
 * This rule is expected to be added to all nodes that are linked to each other
 * node it is linked with, you should use
 * {@link #addNodeLink(TrackNode, TrackNode)} to add a correct link.
 * 
 * @author michael
 */
public class LinkNodesRule implements HeightRule {
	private final TrackNode linkedNode;

	public LinkNodesRule(TrackNode linkedNode) {
		this.linkedNode = linkedNode;
	}

	@Override
	public float getRatingFactor() {
		return 0;
	}

	@Override
	public float getRating(float currentheight) {
		return 0;
	}

	@Override
	public float getHeightDifference(float currentheight) {
		return 0;
	}

	public TrackNode getLinkedNode() {
		return linkedNode;
	}

	public static void addNodeLink(TrackNode node1, TrackNode node2) {
		link(node1, node2);
		link(node2, node1);
	}

	private static void link(TrackNode from, TrackNode to) {
		for (HeightRule rule : from.getHeightRules()) {
			if (rule instanceof LinkNodesRule) {
				TrackNode other = ((LinkNodesRule) rule).getLinkedNode();
				other.addHeightRule(new LinkNodesRule(to));
			}
		}
		from.addHeightRule(new LinkNodesRule(to));
	}
}
