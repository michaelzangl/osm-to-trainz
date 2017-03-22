package export.tracks;

import export.trainz.general.Kuid;
import export.trainz.general.KuidObject;

/**
 * This class defines the type a track has.
 * <p>
 * It also defines some properties.
 * 
 * @author michael
 */
public class TrackType extends KuidObject {
	public static TrackType DEFAULT = new TrackType(new Kuid(-1, 15),
	        Subnetwork.RAIL);
	private final Subnetwork subnetwork;
	private final float minIncline;
	private final float maxIncline;

	public TrackType(Kuid kuid, Subnetwork subnetwork) {
		this(kuid, subnetwork, subnetwork.getMinIncline(), subnetwork
		        .getMaxIncline());
	}

	/**
	 * Generates a new track type definition.
	 * @param kuid The kuid of the track object.
	 * @param subnetwork The subnetwork it belongs to.
	 * @param minIncline The min incline value
	 * @param maxIncline The max incline value.
	 * @see TrackConnection#getIncline()
	 */
	public TrackType(Kuid kuid, Subnetwork subnetwork, float minIncline,
	        float maxIncline) {
		super(kuid);
		this.subnetwork = subnetwork;
		this.minIncline = minIncline;
		this.maxIncline = maxIncline;
	}

	public Subnetwork getSubnetwork() {
		return subnetwork;
	}

	/**
	 * gets the minimal incline the track should have. May be negative to
	 * indicate a decline.
	 * 
	 * @return A float
	 */
	public float getMinIncline() {
		return minIncline;
	}

	/**
	 * gets the maximal incline the track should have. May be negative to
	 * indicate a decline.
	 * 
	 * @return A float
	 */
	public float getMaxIncline() {
		return maxIncline;
	}
}
