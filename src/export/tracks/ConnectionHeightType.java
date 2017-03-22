package export.tracks;

public enum ConnectionHeightType {
	BRIDGE(8), EMBANKMENT(3), ON_GROUND(0), TUNNEL(Float.NaN), WHATEVER(Float.NaN);

	private final float offset;

	private ConnectionHeightType(float offset) {
		this.offset = offset;
	}

	public float getOffsetAboveGround() {
		return offset;
	}
}
