package data.rtree;

import java.util.ArrayList;
import java.util.List;

import conversion.datachange.geometry.Rectangle;

public class RTree<T extends Regionable> {

	private RTreeNode<T> root = null;

	public void add(T object) {
		if (root == null) {
			root = new RTreeLeaf<T>(object);
		} else {
			RTreeNode<T> overflow = root.insert(object);
			if (overflow != null) {
				root = new RTreeSubtree<T>(overflow, root);
			}
		}
	}

	public List<T> findElementsIntersecting(Rectangle rect) {
		List<T> list = new ArrayList<T>();
		if (root != null) {
			root.findElementsIntersecting(rect, list);
		}
		return list;
	}
}
