package data.rtree;

import java.util.List;

import conversion.datachange.geometry.Rectangle;
import conversion.datachange.geometry.SimpleRectangle;

public class RTreeLeaf<T extends Regionable> implements RTreeNode<T> {
    private final T item;
	
	public RTreeLeaf(T item) {
		this.item = item;
	}

	@Override
	public RTreeNode<T> insert(T item) {
		return new RTreeLeaf<T>(item);
	}

	@Override
    public SimpleRectangle getBounds() {
	    return item.getBounds();
    }

	@Override
    public void findElementsIntersecting(Rectangle rect, List<T> found) {
	    found.add(item);
    }
}
