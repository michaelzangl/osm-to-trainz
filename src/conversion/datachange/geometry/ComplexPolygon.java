package conversion.datachange.geometry;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import data.osm.OsmNode;
import data.osm.OsmRelation;
import data.osm.OsmRelation.OsmRoledWay;
import data.osm.OsmWay;
import data.position.local.GlobalToLocalConverter;
import data.position.local.LocalPoint;

public class ComplexPolygon implements Polygon {
	private final LinkedList<Polygon> outer;
	private final LinkedList<Polygon> inner;
	private final SimpleRectangle bounds;

	private ComplexPolygon(LinkedList<Polygon> outer, LinkedList<Polygon> inner) {
		if (outer.size() < 1) {
			throw new IllegalArgumentException(
			        "There has to be at least one polygon in a complex polygon.");
		}
		this.outer = outer;
		this.inner = inner;

		Iterator<Polygon> it = outer.iterator();
		SimpleRectangle bounds = it.next().getBounds();
		while (it.hasNext()) {
			Polygon next = it.next();
			bounds = bounds.union(next.getBounds());
		}
		this.bounds = bounds;
	}

	public static OsmWay findWayWithStarting(OsmNode node) {
		return null;
	}

	@Override
	public boolean contains(LocalPoint p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public SimpleRectangle getBounds() {
		return bounds;
	}

	@Override
	public boolean contains(double x, double y) {
		for (Polygon poly : inner) {
			if (poly.contains(x, y)) {
				return false;
			}
		}
		for (Polygon poly : outer) {
			if (poly.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Polygon generateUnion(GridPart grid) {
		// TODO Auto-generated method stub
		return null;
	}

	public static ComplexPolygon fromRelation(OsmRelation relation,
	        GlobalToLocalConverter converter) {
		LinkedList<Polygon> outer = getRings(relation, "outer", converter);
		LinkedList<Polygon> inner = getRings(relation, "inner", converter);

		if (outer.size() < 1) {
			return null;
		} else {
			return new ComplexPolygon(outer, inner);
		}
	}

	/**
	 * Gets rings of ways as polygons.
	 * 
	 * @param relation
	 * @param string
	 * @param converter
	 * @return
	 */
	private static LinkedList<Polygon> getRings(OsmRelation relation,
	        String role, GlobalToLocalConverter converter) {
		LinkedList<OsmWay> possibleWays = new LinkedList<OsmWay>();

		for (OsmRoledWay way : relation.getWays()) {
			possibleWays.add(way.getWay());
		}

		LinkedList<Polygon> result = new LinkedList<Polygon>();
		while (!possibleWays.isEmpty()) {
			LinkedList<OsmNode> outline = extractRing(possibleWays);
			LocalPoint[] points = new LocalPoint[outline.size()];
			int i = 0;
			for (OsmNode node : outline) {
				points[i] = converter.toLocal(node);
				i++;
			}
			result.add(SimplePolygon.fromArray(points));
		}
		return result;
	}

	private static LinkedList<OsmNode> extractRing(
	        LinkedList<OsmWay> possibleWays) {
		LinkedList<OsmNode> list = new LinkedList<OsmNode>();
		list.add(possibleWays.get(0).getFirstNode()); // entry point

		while (true) {
			OsmWay nextWay = removeWayStarting(list.getLast(), possibleWays);
			if (nextWay == null) {
				break;
			}
			LinkedList<OsmNode> newNodes = new LinkedList<OsmNode>();
			for (OsmNode node : nextWay) {
				newNodes.add(node);
			}
			if (nextWay.getLastNode() == list.getLast()) {
				Collections.reverse(newNodes);
			}
			for (OsmNode node : newNodes) {
				if (node != list.getFirst() && node != list.getLast()) {
					list.add(node);
				}
			}
		}
		return list;
	}

	private static OsmWay removeWayStarting(OsmNode node,
	        LinkedList<OsmWay> ways) {
		Iterator<OsmWay> it = ways.iterator();
		while (it.hasNext()) {
			OsmWay next = it.next();
			if (next.getFirstNode() == node || next.getLastNode() == node) {
				it.remove();
				return next;
			}
		}
		return null;
	}
}
