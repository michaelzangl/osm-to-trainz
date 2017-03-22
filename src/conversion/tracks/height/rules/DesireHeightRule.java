package conversion.tracks.height.rules;

/**
 * This rule just desires to have a given height.
 * 
 * @author michael
 */
public class DesireHeightRule implements HeightRule {
	private final float height;
	private final float normaldiff;
	private final float ratingFactor;

	/**
	 * @param height
	 *            The height of this node
	 * @param normaldiff
	 *            A difference that can be tolerated.
	 * @param ratingFactor
	 *            the rating status of the rule, see
	 *            {@link HeightRule#getRating(float)}.
	 */
	public DesireHeightRule(float height, float normaldiff, float ratingFactor) {
		this.height = height;
		this.normaldiff = normaldiff;
		this.ratingFactor = ratingFactor;
	}

	@Override
	public float getRatingFactor() {
		return ratingFactor;
	}

	@Override
	public float getRating(float currentheight) {
		float diff = getHeightDifference(currentheight);
		return getRating(diff, normaldiff);
	}
	
	public static float getRating(float heightdiff, float meandistance) {
		return heightdiff * heightdiff / meandistance / meandistance;
				//(float) Math.pow(Math.E, -heightdiff * heightdiff / meandistance / meandistance);
	}

	@Override
	public float getHeightDifference(float currentheight) {
		return height - currentheight;
	}
}
