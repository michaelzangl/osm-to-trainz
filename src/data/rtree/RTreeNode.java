package data.rtree;

import java.util.List;

import conversion.datachange.geometry.Rectangle;

public interface RTreeNode<T extends Regionable> extends Regionable{	
	RTreeNode<T> insert(T item);
	
	void findElementsIntersecting(Rectangle rect, List<T> found);
}
