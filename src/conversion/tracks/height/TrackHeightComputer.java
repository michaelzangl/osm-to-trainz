package conversion.tracks.height;

import java.util.LinkedList;
import java.util.List;

import conversion.tracks.height.rules.HeightRule;
import conversion.tracks.height.rules.LinkNodesRule;
import export.tracks.TrackNetwork;
import export.tracks.TrackNode;

/**
 * This class applies heights of the track network to the points.
 * 
 * @author michael
 */
public class TrackHeightComputer {

	private static final int ITERATIONS = 20;

	/**
	 * Optimizes the node network.
	 * 
	 * @param network
	 *            The Network to optimize.
	 */
	public void optimizeNetworkHeights(TrackNetwork network) {
		float coolingfactor = .8f;
		for (int i = 0; i < ITERATIONS; i++) {
			for (TrackNode node : network.getNodes()) {
				optimizeNode(node, coolingfactor);
			}
			coolingfactor *= .95;
		}
	}

	/**
	 * trys to optimize the given node
	 * 
	 * @param node
	 *            The node
	 * @param coolingfactor
	 *            Current cooling factor. the smaller it is, the less the node
	 *            is changed.
	 */
	private void optimizeNode(TrackNode node, float coolingfactor) {
		List<TrackNode> nodes = getLinkeNodes(node);
		List<HeightRule> nodeRules = getAllRules(nodes);
		float height = (float) node.getPosition().getHeight();
		float heightdiff = getHeightDiff(height, nodeRules);
		float currentCooling = coolingfactor * heightdiff;

		float currentRating =
		        getRatingWithHeight(nodeRules, height + heightdiff);
		for (int i = 0; i < 4; i++) {
			float incRating =
			        getRatingWithHeight(nodeRules, height + heightdiff
			                + currentCooling);
			float decRating =
			        getRatingWithHeight(nodeRules, height + heightdiff
			                - currentCooling);
			if (incRating < currentRating && incRating < decRating) {
				heightdiff += currentCooling;
				currentRating = incRating;
			} else if (decRating < currentRating) {
				heightdiff -= currentCooling;
				currentRating = decRating;
			}
			currentCooling /= 3;
		}
		if (currentRating < getRatingWithHeight(nodeRules, height)) {
			node.setHeight(height + heightdiff);
		}
	}

	private List<HeightRule> getAllRules(List<TrackNode> nodes) {
		LinkedList<HeightRule> rules = new LinkedList<HeightRule>();
		for (TrackNode node : nodes) {
			rules.addAll(node.getHeightRules());
		}
		return rules;
	}

	/**
	 * Gets all nodes that are linked in one node group with ths node.
	 * 
	 * @param node
	 *            The base node
	 * @return All linked nodes.
	 */
	private List<TrackNode> getLinkeNodes(TrackNode node) {
		LinkedList<TrackNode> nodes = new LinkedList<TrackNode>();
		nodes.add(node);
		for (HeightRule rule : node.getHeightRules()) {
			if (rule instanceof LinkNodesRule) {
				TrackNode other = ((LinkNodesRule) rule).getLinkedNode();
				nodes.add(other);
			}
		}
		return nodes;
	}

	/**
	 * Gets the weighted average proposed height difference from the nodes.
	 * 
	 * @param height
	 *            The height of the node
	 * @param nodeRules
	 *            The node rules to use
	 * @return The height difference that should be added to the current height.
	 */
	private float getHeightDiff(float height, List<HeightRule> nodeRules) {
		float heightdiff = 0;
		float factors = 0;
		for (HeightRule rule : nodeRules) {
			factors += rule.getRatingFactor();
			heightdiff +=
			        rule.getHeightDifference(height) * rule.getRatingFactor();
		}
		if (factors <= 0 || Float.isNaN(factors) || Float.isNaN(heightdiff)) {
			return 0;
		} else {
			return heightdiff / factors;
		}
	}

	private float getRatingWithHeight(List<HeightRule> nodeRules, float height) {
		// float factors = 0;
		float ratings = 0;
		for (HeightRule rule : nodeRules) {
			// factors += rule.getRatingFactor();
			ratings += rule.getRating(height) * rule.getRatingFactor();
		}
		// if (factors <= 0) {
		// return 1;
		// } else {
		return ratings;// / factors;
		// }
	}
}
