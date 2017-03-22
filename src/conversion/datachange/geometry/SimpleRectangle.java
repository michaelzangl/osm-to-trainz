package conversion.datachange.geometry;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import sun.applet.GetWindowPluginCallRequest;

import data.position.local.LocalPoint;

/**
 * A simple rectangle
 * 
 * @author michael
 */
public class SimpleRectangle implements Rectangle {
	private final double minX;
	private final double minY;
	private final double maxX;
	private final double maxY;

	public SimpleRectangle(double minX, double minY, double maxX, double maxY) {
		if (maxX < minX || maxY < minY) {
			throw new IllegalArgumentException(
			        "max needs to be equal/bigger than min.");
		}

		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;

	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public boolean contains(LocalPoint point) {
		return point.getX() >= minX && point.getX() <= maxX
		        && point.getY() >= minY && point.getY() <= maxY;
	}

	public boolean intersects(Rectangle rect) {
		return !(rect.getMinX() > maxX || rect.getMaxX() < minX
		        || rect.getMinY() > maxY || rect.getMaxY() < minY);
	}

	public static SimpleRectangle bounds(List<LocalPoint> points) {
		if (points.size() < 1) {
			throw new IllegalArgumentException(
			        "Cannot create bounds of empty point list");
		}
		double minx = Double.POSITIVE_INFINITY;
		double maxx = Double.NEGATIVE_INFINITY;
		double miny = Double.POSITIVE_INFINITY;
		double maxy = Double.NEGATIVE_INFINITY;

		for (LocalPoint local : points) {
			if (local.getX() < minx) {
				minx = local.getX();
			}
			if (local.getX() > maxx) {
				maxx = local.getX();
			}
			if (local.getY() < miny) {
				miny = local.getY();
			}
			if (local.getY() > maxy) {
				maxy = local.getY();
			}
		}
		return new SimpleRectangle(minx, miny, maxx, maxy);
	}

	public double getArea() {
		return (maxX - minX) * (maxY - minY);
	}

	public List<LocalPoint> getPoints(double pointdistance, Random rand) {
		List<LocalPoint> points = new LinkedList<LocalPoint>();
		long pointCount =
		        Math.min(
		                Math.round(getArea() / (pointdistance * pointdistance)),
		                1000);
		for (int i = 0; i < pointCount; i++) {
			double x = minX + (maxX - minX) * rand.nextDouble();
			double y = minY + (maxY - minY) * rand.nextDouble();
			points.add(new LocalPoint(x, y));
		}
		return points;
	}

	public SimpleRectangle union(SimpleRectangle other) {
		return new SimpleRectangle(Math.min(minX, other.minX), Math.min(minY,
		        other.minY), Math.max(maxX, other.maxX), Math.max(maxY,
		        other.maxY));
	}

	public double getWidth() {
		return maxX - minX;
	}

	public double getHeight() {
		return maxY - minY;
	}

	public double getMargin() {
		return 2 * getWidth() + 2 * getHeight();
	}

	/**
	 * Computes the cut of both rectangles.
	 * 
	 * @param rect2
	 * @return The cut rectangle, or null if the rectangles do not intersect.
	 */
	public SimpleRectangle cut(SimpleRectangle rect2) {
		if (!intersects(rect2)) {
			return null;
		} else {
			return new SimpleRectangle(Math.max(this.minX, rect2.minX),
			        Math.max(this.minY, rect2.minY), Math.min(this.maxX,
			                rect2.maxX), Math.min(this.maxY, rect2.maxY));
		}
	}

	public SimpleRectangle getDisplaced(double dx, double dy) {
		return new SimpleRectangle(dx + minX, dy + minY, dx + maxX, dy + maxY);
    }
}
