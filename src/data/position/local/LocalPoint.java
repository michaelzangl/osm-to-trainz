package data.position.local;

/**
 * This is a local point, who's distance is meassured in meters from some
 * origin.
 * <p>
 * <strong>Warning</strong> The coordinate system is here: <br>
 * x coordinate is soutwards <br>
 * y coordinate is eastwards.
 * <p>
 * It may have a height, if the height property is NaN, it is not set.
 * 
 * @author michael
 */
public class LocalPoint {
	private final double height;
	private final double y;
	private final double x;

	/**
	 * Creates a new local point with unset height.
	 * 
	 * @param x southwards in meters.
	 * @param y eastwards in meters.
	 */
	public LocalPoint(double x, double y) {
		this(x, y, Double.NaN);
	}

	/**
	 * Creates a new local point
	 * 
	 * @param x southwards in meters.
	 * @param y eastwards in meters.
	 * @param height
	 */
	public LocalPoint(double x, double y, double height) {
		this.x = x;
		this.y = y;
		this.height = height;
	}

	/**
	 * Gets the x coordinate, which is pointing southwards.
	 * @return The x coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the y coordinate, which is pointing eastwards.
	 * @return The y coordinate
	 */
	public double getY() {
		return y;
	}

	public double getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "LocalPoint[" + x + "," + y + "," + height + "]";
	}

	public LocalPoint withHeight(double height2) {
		return new LocalPoint(x, y, height2);
	}
}
