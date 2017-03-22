package conversion.datachange.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import data.osm.OsmNode;
import data.osm.OsmWay;
import data.position.local.GlobalToLocalConverter;
import data.position.local.LocalPoint;

public class SimplePolygon implements Polygon, Iterable<LocalPoint> {

	private final List<LocalPoint> points;

	private final SimpleRectangle bounds;

	private SimplePolygon(List<LocalPoint> points) {
		assert points.size() > 2 : "A polygon needs to have at least 3 Points.";
		this.points = Collections.unmodifiableList(points);
		this.bounds = SimpleRectangle.bounds(points);
	}

	public boolean contains(LocalPoint p) {
		if (!getBounds().contains(p)) {
			return false;
		}
		return contains(p.getX(), p.getY());
	}

	@Override
	public Iterator<LocalPoint> iterator() {
		return points.iterator();
	}

	/**
	 * Generates the union of this polygon with a landscape part.
	 * 
	 * @param grid
	 * @return
	 */
	public SimplePolygon generateUnion(GridPart grid) {
		List<LocalPoint> clipped =
		        PolygonGridUnionHelper.getClipped(grid, this);
		if (clipped.size() > 2) {
			return new SimplePolygon(clipped);
		} else {
			return null;
		}
	}

	public List<LocalPoint> getPoints() {
		return points;
	}

	public SimpleRectangle getBounds() {
		return bounds;
	}

	public boolean contains(double x, double y) {
		LocalPoint lastPoint = points.get(points.size() - 1);
		boolean contained = false;
		for (LocalPoint point : points) {
			if ((point.getY() > y) != (lastPoint.getY() > y)) {
				double m =
				        (lastPoint.getX() - point.getX())
				                / (lastPoint.getY() - point.getY());
				if (x < m * (y - point.getY()) + point.getX()) {
					contained = !contained;
				}
			}
			lastPoint = point;
		}
		return contained;
	}

	@Override
	public String toString() {
		String string = "Polygon[";
		Iterator<LocalPoint> iterator = points.iterator();
		string += iterator.next();
		while (iterator.hasNext()) {
			string += ", ";
			string += iterator.next();
		}
		return string + "]";
	}

	/**
	 * Converts the way to a polygon.
	 * 
	 * @param way
	 * @param converter
	 * @return
	 */
	public static Polygon fromWay(OsmWay way, GlobalToLocalConverter converter) {
		List<LocalPoint> points = new ArrayList<LocalPoint>();

		for (OsmNode node : way) {
			LocalPoint local = converter.toLocal(node.getLat(), node.getLon());
			if (points.size() < 1 || !local.equals(points.get(0))) {
				points.add(local);
			}
		}
		return new SimplePolygon(points);
	}

	public static Polygon fromArray(LocalPoint[] points2) {
		List<LocalPoint> points = new ArrayList<LocalPoint>(points2.length);
		for (LocalPoint p : points2) {
			points.add(p);
		}
		return new SimplePolygon(points);
	}

}
