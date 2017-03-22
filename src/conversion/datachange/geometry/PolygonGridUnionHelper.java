package conversion.datachange.geometry;

import java.util.ArrayList;
import java.util.List;

import data.position.local.LocalPoint;

public class PolygonGridUnionHelper {
	public static List<LocalPoint> getClipped(GridPart grid, SimplePolygon polygon) {
		List<LocalPoint> points = polygon.getPoints();
		ClipHelper[] helper =
		        new ClipHelper[] {
		                new HorizontalClipHelper(grid.getMinX(), false),
		                new HorizontalClipHelper(grid.getMaxX(), true),
		                new VerticalClipHelper(grid.getMinY(), false),
		                new VerticalClipHelper(grid.getMaxY(), true)
		        };
		for (int i = 0; i < helper.length && points.size() > 2; i++) {
			points = cutWithHelper(points, helper[i]);
		}
		return points;
	}

	private static List<LocalPoint> cutWithHelper(List<LocalPoint> points,
	        ClipHelper helper) {
		ArrayList<LocalPoint> generated = new ArrayList<LocalPoint>();

		LocalPoint lastPoint = points.get(points.size() - 1);
		for (LocalPoint polyPoint : points) {
			if (helper.shouldBeCut(lastPoint)) {
				if (helper.shouldBeCut(polyPoint)) {
					// nothing to do
				} else {
					// outside => inside
					generated
					        .add(helper.getCutWithBorder(lastPoint, polyPoint));
					generated.add(polyPoint);
				}
			} else {
				if (helper.shouldBeCut(polyPoint)) {
					// we are getting outside
					generated
					        .add(helper.getCutWithBorder(lastPoint, polyPoint));
				} else {
					generated.add(polyPoint);
				}
			}
			lastPoint = polyPoint;
		}
		return generated;
	}

	private interface ClipHelper {
		public LocalPoint getCutWithBorder(LocalPoint p1, LocalPoint p2);

		// !contains()
		public boolean shouldBeCut(LocalPoint p1);
	}

	private static class HorizontalClipHelper implements ClipHelper {
		private final double x;
		private final boolean ismax;

		public HorizontalClipHelper(double x, boolean ismax) {
			this.x = x;
			this.ismax = ismax;
		}

		@Override
		public LocalPoint getCutWithBorder(LocalPoint p1, LocalPoint p2) {
			assert p1.getX() != p2.getX();

			double m = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
			double c = p1.getY() - m * p1.getX();
			return new LocalPoint(x, m * x + c);
		}

		@Override
		public boolean shouldBeCut(LocalPoint p1) {
			return (p1.getX() > x) == ismax;
		}

	}

	private static class VerticalClipHelper implements ClipHelper {
		private final double y;
		private final boolean ismax;

		public VerticalClipHelper(double y, boolean ismax) {
			this.y = y;
			this.ismax = ismax;
		}

		@Override
		public LocalPoint getCutWithBorder(LocalPoint p1, LocalPoint p2) {
			assert p1.getY() != p2.getY();

			double m = (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
			double c = p1.getX() - m * p1.getY();
			return new LocalPoint(m * y + c, y);
		}

		@Override
		public boolean shouldBeCut(LocalPoint p1) {
			return (p1.getY() > y) == ismax;
		}

	}
}
