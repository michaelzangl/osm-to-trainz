package conversion.tracks.height.rules;

/**
 * This is a height rule for a given node.
 * <p>
 * Each node can have mutliple such rules, by which an optimal height of all
 * nodes is computed.
 * <p>
 * The rule can rate the current state of the node (given the nodes height) and
 * also has a rating factor (normal: 1) that states how important the
 * fulfillment of this rule is.
 * <p>
 * Each rule provides a function to compute how much the height of the node
 * should be increased to be optimal
 * 
 * @author michael
 */
public interface HeightRule {

	/**
	 * Gets the factor this rule should have in the general rule set. It should
	 * be bigger than 0 (approx. 0.1). A normal factor is 1, a factor for a
	 * strong rule is 3.
	 * 
	 * @return
	 */
	float getRatingFactor();

	/**
	 * Gets the rating of the rule, if the node had the given height.
	 * 
	 * @param currentheight
	 *            The height of the node.
	 * @return The rating. 0 is very good, a bigger value is more bad.
	 */
	float getRating(float currentheight);

	/**
	 * Gets the height difference that should be added to the current height to
	 * fulfill this rule as good as possible. May be approximated.
	 * 
	 * @param currentheight
	 *            The height of the node.
	 * @return The height difference.
	 */
	float getHeightDifference(float currentheight);
}
