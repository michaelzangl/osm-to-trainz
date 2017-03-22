package conversion.tracks.height.rules;

import export.tracks.TrackNode;

/**
 * Desires an incline between two values
 * 
 * @author michael
 */
public class DesireInclineBetween implements HeightRule {
	private final float mean;
	private final float distance;
	private final float ratingFactor;
	private final TrackNode otherNode;
	private final float length;

	/**
	 * Creates a new incline rule
	 * 
	 * @param min
	 * @param max
	 * @param otherNode
	 *            The other node
	 * @param length
	 *            The distance between our node and the other one.
	 * @param ratingFactor
	 *            The rating factor of this rule
	 */
	public DesireInclineBetween(float min, float max, TrackNode otherNode,
	        float length, float ratingFactor) {
		this.otherNode = otherNode;
		this.length = length;
		if (min > max) {
			throw new IllegalArgumentException("Minimum is bigger than maximum");
		}
		this.ratingFactor = ratingFactor;
		mean = (min + max) / 2;
		if (min > max + .0001f) {
			distance = .00005f;
		} else {
			distance = max - mean;
		}
	}

	@Override
	public float getRatingFactor() {
		return ratingFactor;
	}

	@Override
	public float getRating(float currentheight) {
		float incline =
		        ((float) otherNode.getPosition().getHeight() - currentheight)
		                / length;
		float x = (incline - mean) / distance;
		float x2 = x * x;
		return x2 * x2; // => x⁴
	}

	@Override
	public float getHeightDifference(float currentheight) {
		return (float) otherNode.getPosition().getHeight() - mean * length - currentheight;
	}
}
