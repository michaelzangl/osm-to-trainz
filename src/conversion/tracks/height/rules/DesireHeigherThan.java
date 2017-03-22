package conversion.tracks.height.rules;

import export.tracks.TrackNode;

/**
 * Desires the node to be a given amount higher than the other node.
 * 
 * @author michael
 */
public class DesireHeigherThan implements HeightRule {
	private final TrackNode node;
	private final float height;
	private final float ratingFactor;

	public DesireHeigherThan(TrackNode node, float height, float ratingFactor) {
		this.node = node;
		this.height = height;
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
	    	return DesireHeightRule.getRating(diff, height / 10 + .1f);
	    }
    }

	@Override
    public float getHeightDifference(float currentheight) {
	    float otherHeight = (float) node.getPosition().getHeight();
	    return otherHeight - height - currentheight;
    }
	
	
}
