package conversion.datachange.geometry;

import data.position.local.LocalPoint;

public interface Rectangle {
	public double getMinX();

	public double getMinY();

	public double getMaxX();

	public double getMaxY();
	
	public boolean contains(LocalPoint point);
}
