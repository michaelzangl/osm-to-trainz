package conversion.datachange.geometry;

import data.position.local.LocalPoint;

public interface Polygon {
	public boolean contains(LocalPoint p);

	public SimpleRectangle getBounds();

	boolean contains(double x, double y);
	
	public Polygon generateUnion(GridPart grid);
}
