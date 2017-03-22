package conversion.landscape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import conversion.datachange.geometry.ComplexPolygon;
import conversion.datachange.geometry.GridPart;
import conversion.datachange.geometry.Polygon;
import conversion.datachange.geometry.SimplePolygon;
import data.osm.OsmDatapack;
import data.osm.OsmRelation;
import data.osm.OsmWay;
import data.osm.Propertyable;
import data.position.local.GlobalToLocalConverter;
import data.position.local.LocalPoint;

/**
 * This class holds the landscape data.
 * <p>
 * It consists of a grid of squares ({@link GridPart}) and polygons contained by
 * the part.
 * 
 * @author michael
 */
public class Landscape {
	private Hashtable<GridPart, List<LandscapePolygon>> polygons =
	        new Hashtable<GridPart, List<LandscapePolygon>>();

	private Set<GridPart> unsorted = new HashSet<GridPart>();

	public static Landscape generateFormOsmData(OsmDatapack data,
	        GlobalToLocalConverter converter, Collection<GridPart> gridparts) {
		Landscape landscape = new Landscape();
		
		for (GridPart part : gridparts) {
			landscape.activateGridPart(part);
		}

		for (OsmWay way : data.getWays()) {
			if (way.isArea()) {
				Polygon poly = SimplePolygon.fromWay(way, converter);
				//TODO: skip if it is an outer part of a multipolygon!
				landscape.addPolygon(way, poly);
			}
		}
		
		for (OsmRelation relation : data.getRelations()) {
			if ("multipolygon".equals(relation.getProperty("type"))) {
				Polygon poly = ComplexPolygon.fromRelation(relation, converter);
				landscape.addPolygon(relation, poly);
			}
		}

		landscape.sortAll();
		// landscape.debug();
		return landscape;
	}

	public synchronized void addPolygon(Propertyable way, Polygon poly) {
		double sortindex = poly.getBounds().getArea();
		List<GridPart> parts = GridPart.getInRectangle(poly.getBounds());
		for (GridPart part : parts) {
			Polygon partpoly = poly.generateUnion(part);
			if (partpoly != null) {
				LandscapePolygon l =
				        new LandscapePolygon(sortindex, way, partpoly);
				addGridpartPolygon(part, l);
			}
		}
	}

	private void debug() {
		for (Entry<GridPart, List<LandscapePolygon>> entry : polygons
		        .entrySet()) {
			System.out.println("Polygons in Block " + entry.getKey().getMinX()
			        + ", " + entry.getKey().getMinY());
			for (LandscapePolygon poly : entry.getValue()) {
				System.out.println("   - area defined by " + poly);
			}
		}
	}

	/**
	 * Sorts all entrys of the table.
	 */
	private void sortAll() {
		for (List<LandscapePolygon> list : polygons.values()) {
			Collections.sort(list);
		}
	}

	private synchronized void addGridpartPolygon(GridPart part,
	        LandscapePolygon polygon) {
		assert polygon != null;
		List<LandscapePolygon> list = polygons.get(part);
		// if (list == null) {
		// list = new LinkedList<LandscapePolygon>();
		// polygons.put(part, list);
		// }
		if (list != null) {
			list.add(polygon);
			unsorted.add(part);
		}
	}

	public synchronized LandscapePolygon getWayUnder(LocalPoint p) {
		GridPart part = GridPart.getUnder(p);
		List<LandscapePolygon> list = polygons.get(part);
		if (list == null) {
			return null;
		}
		if (unsorted.contains(part)) {
			Collections.sort(list);
			unsorted.remove(part);
		}

		for (LandscapePolygon poly : list) {
			if (poly.getPolygon().contains(p)) {
				return poly;
			}
		}
		return null;
	}

	/**
	 * Gets a list of all blocks to be converted
	 * 
	 * @return A set of those blocks
	 */
	public synchronized Set<GridPart> getConvertedBlocks() {
		return polygons.keySet();
	}

	public synchronized List<LandscapePolygon> getPolygonsOnBlock(GridPart b) {
		List<LandscapePolygon> list = polygons.get(b);
		if (list == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(list);
		}
	}

	public synchronized void activateGridPart(GridPart part) {
		if (!polygons.contains(part)) {
			polygons.put(part, new ArrayList<LandscapePolygon>());
		}
	}

	public synchronized void deactivateGridPart(GridPart part) {
		polygons.remove(part);
	}

	public synchronized boolean isLandUnder(LocalPoint position) {
	    GridPart part = GridPart.getUnder(position);
	    return polygons.containsKey(part);
    }
}
