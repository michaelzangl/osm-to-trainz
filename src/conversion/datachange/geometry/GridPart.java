package conversion.datachange.geometry;

import java.util.ArrayList;
import java.util.List;

import data.position.local.LocalPoint;

/**
 * This is a rectanguar part of the main data grid.
 * <p>
 * For better handling, all polygons are split on the borders of this grid.
 * <p>
 * The grid is combined in 720x720m squares around a central point.
 * 
 * @author michael
 */
public class GridPart {
	public static int GRID_SPACING = 720;
	private final int gridx;
	private final int gridy;

	public GridPart(int gridx, int gridy) {
		this.gridx = gridx;
		this.gridy = gridy;
	}

	public float getMinX() {
		return gridx * GRID_SPACING;
	}

	public float getMinY() {
		return gridy * GRID_SPACING;
	}

	public float getMaxX() {
		return getMinX() + GRID_SPACING;
	}

	public float getMaxY() {
		return getMinY() + GRID_SPACING;
	}

	public boolean equals(Object obj) {
		if (obj instanceof GridPart) {
			GridPart other = (GridPart) obj;
			return other.gridx == gridx && other.gridy == gridy;
		} else {
			return false;
		}
	};

	@Override
	public int hashCode() {
		return gridx * 1223 + gridy * 2430691;
	}

	public LocalPoint getCutWithBorder(LocalPoint lastPoint,
	        LocalPoint polyPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets all grid parts that are contained by the rectangle
	 * 
	 * @param bounds
	 * @return
	 */
	public static List<GridPart> getInRectangle(SimpleRectangle bounds) {
		int minx = (int) Math.floor(bounds.getMinX() / GRID_SPACING);
		int maxx = (int) Math.ceil(bounds.getMaxX() / GRID_SPACING);
		int miny = (int) Math.floor(bounds.getMinY() / GRID_SPACING);
		int maxy = (int) Math.ceil(bounds.getMaxY() / GRID_SPACING);

		ArrayList<GridPart> list =
		        new ArrayList<GridPart>((maxx - minx) * (maxy - miny));
		for (int x = minx; x < maxx; x++) {
			for (int y = miny; y < maxy; y++) {
				list.add(new GridPart(x, y));
			}
		}
		return list;
	}

	public static GridPart getUnder(LocalPoint p) {
		int x = (int) Math.floor(p.getX() / GRID_SPACING);
		int y = (int) Math.floor(p.getY() / GRID_SPACING);
		return new GridPart(x, y);
    }

}
