package conversion.landscape.height;

/**
 * This is a height function that may displace the ground height.
 * @author michael
 *
 */
public interface HeightFunction {
	public static HeightFunction IDENTITY = new HeightFunction() {
		@Override
		public float getDisplaced(float oldHeight, float x, float y) {
			return oldHeight;
		}
	};
	
	float getDisplaced(float oldHeight, float x, float y);
}
