package export.tracks;

/**
 * This enum defines the subnetwork for a track type.
 * <p>
 * Each track is on a subnetwork, and after generation of the main network, the
 * subnetworks are separated. But there can be height rules between the
 * networks.
 * 
 * @author michael
 */
public enum Subnetwork {
	/**
	 * A normal Railway
	 */
	RAIL(-.05f, .05f),
	/**
	 * Normal Streets
	 */
	STREET(-.5f, .5f),
	/**
	 * Fences and other stuff
	 */
	BARRIER(-1, 1),
	/**
	 * Waterways like river, ...
	 */
	WATERWAY(-.3f, 0),
	/**
	 * The contact wire for an electrified way. More loose incline constraints
	 * than normal tracks, since it is always linked to one.
	 */
	CONTACT_WIRE(-.3f, .3f);

	private final float minIncline;
	private final float maxIncline;

	private Subnetwork(float minIncline, float maxIncline) {
		this.minIncline = minIncline;
		this.maxIncline = maxIncline;
	}

	/**
	 * Gets the default minimal incline value for this network.
	 * 
	 * @return
	 */
	public float getMinIncline() {
		return minIncline;
	}

	public float getMaxIncline() {
		return maxIncline;
	}
}
