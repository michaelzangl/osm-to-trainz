package export.objects;

import conversion.datachange.geometry.SimpleRectangle;

public class XMLObject implements ObjectData {
	private String name;
	private static final SimpleRectangle DEFAULT_BOUNDS = new SimpleRectangle(
	        -3, -3, 3, 3);

	public XMLObject(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the linked Object
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	@Override
	public SimpleRectangle getBounds() {
		return DEFAULT_BOUNDS;
	}
}
