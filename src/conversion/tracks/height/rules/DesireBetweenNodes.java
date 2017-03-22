package conversion.tracks.height.rules;

import export.tracks.TrackNode;

/**
 * This rule desires the node to have a height between the two given nodes, with
 * a given ratio.
 * <p>
 * Let the node heights be h(A) and h(B). The desired height is:
 * <code> h = ratio * h(A) + (1-ratio) * h(B) </code>
 * 
 * @author michael
 */
public class DesireBetweenNodes implements HeightRule {
	private final TrackNode node1;
	private final TrackNode node2;
	private final float ratio;
	private final float ratingFactor;

	/**
	 * Lets the node have a height between two other nodes.
	 * 
	 * @param node1
	 *            The first node
	 * @param node2
	 *            The second node
	 * @param ratio
	 *            The ratio between the first and second node where we are.
	 * @param ratingFactor
	 *            The rating factor of this rule.
	 */
	public DesireBetweenNodes(TrackNode node1, TrackNode node2, float ratio,
	        float ratingFactor) {
		this.node1 = node1;
		this.node2 = node2;
		this.ratio = ratio;
		this.ratingFactor = ratingFactor;
	}

	@Override
	public float getRatingFactor() {
		return ratingFactor;
	}

	@Override
	public float getRating(float currentheight) {
		float diff = getHeightDifference(currentheight);
		if (diff == 0) {
			return 1;
		} else {
			float diffBetweenNodes =
			        (float) Math.abs(node1.getPosition().getHeight()
                    - node2.getPosition().getHeight());
			return DesireHeightRule.getRating(diff, diffBetweenNodes / 4 + .1f);
		}
	}

	@Override
	public float getHeightDifference(float currentheight) {
		float otherHeight =
		        (float) (ratio * node1.getPosition().getHeight() + (1 - ratio)
		                * node2.getPosition().getHeight());
		if (Float.isNaN(otherHeight)) {
			return 0;
		} else {
			return otherHeight - currentheight;
		}
	}
	
	public TrackNode getNode1() {
	    return node1;
    }
	public TrackNode getNode2() {
	    return node2;
    }
}
