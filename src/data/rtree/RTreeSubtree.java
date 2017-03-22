package data.rtree;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import conversion.datachange.geometry.Rectangle;
import conversion.datachange.geometry.SimpleRectangle;

public class RTreeSubtree<T extends Regionable> implements RTreeNode<T> {
	public static int CHILDREN_PER_NODE = 20;
	public static int SPLIT_BOUNDS = (int) Math.floor(.4f * CHILDREN_PER_NODE);
	

	private static abstract class RegionComparator implements
	        Comparator<RTreeNode<? extends Regionable>> {
		@Override
		public int compare(RTreeNode<? extends Regionable> o1,
		        RTreeNode<? extends Regionable> o2) {
			return Double.compare(getRectside(o1.getBounds()),
			        getRectside(o2.getBounds()));
		}

		public abstract double getRectside(SimpleRectangle bounds);
	}

	private static final RegionComparator[] AXES = new RegionComparator[] {
	        new RegionComparator() {
		        @Override
		        public double getRectside(SimpleRectangle bounds) {
			        return bounds.getMinX();
		        }
	        }, new RegionComparator() {
		        @Override
		        public double getRectside(SimpleRectangle bounds) {
			        return bounds.getMaxX();
		        }
	        }, new RegionComparator() {
		        @Override
		        public double getRectside(SimpleRectangle bounds) {
			        return bounds.getMinY();
		        }
	        }, new RegionComparator() {
		        @Override
		        public double getRectside(SimpleRectangle bounds) {
			        return bounds.getMaxY();
		        }
	        },
	};

	private SimpleRectangle bounds = null;

	/**
	 * A non-empty array to hold <code>fill</code> children.
	 * <p>
	 * Last element reserved for temporary use during split
	 */
	@SuppressWarnings("unchecked")
	private RTreeNode<T>[] children =
	        (RTreeNode<T>[]) new RTreeNode<?>[CHILDREN_PER_NODE + 1];

	private int fill = 0;

	public RTreeSubtree(RTreeNode<T>[] childrenBuffer, int start, int len) {
		if (len < 1) {
			throw new IllegalArgumentException(
			        "I need at least one child in the construction buffer");
		}
		if (len > CHILDREN_PER_NODE) {
			throw new IllegalArgumentException("Too many children in new node.");
		}
		
		for (int i = 0; i < len; i++) {
			children[i] = childrenBuffer[start + i];
		}
		fill = len;
		
		recomputeBounds();
	}

	public RTreeSubtree(RTreeNode<T> node1, RTreeNode<T> node2) {
		this.fill = 2;
		this.children[0] = node1;
		this.children[1] = node2;
		recomputeBounds();
    }

	/**
	 * Inserts a new node. If the node is split, then the new node is returned,
	 * null otherwise
	 * 
	 * @param item
	 * @return
	 */
	public RTreeSubtree<T> insert(T item) {
		SimpleRectangle itemBounds = item.getBounds();

		RTreeNode<T> bestChild = null;
		double bestRating = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < fill; i++) {
			RTreeNode<T> child = children[i];
			SimpleRectangle childBounds = child.getBounds();
			double rating =
			        childBounds.getArea()
			                / childBounds.union(itemBounds).getArea();
			if (rating > bestRating) {
				bestChild = child;
				bestRating = rating;
			}
		}

		assert bestChild != null;
		RTreeNode<T> newTree = bestChild.insert(item);
		if (newTree != null) {
			assert children.length > fill;
			this.children[fill] = newTree;
			fill++;
			if (children.length == fill) {
				return splitNode();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private RTreeSubtree<T> splitNode() {
		assert children.length == fill + 1;

		double bestAxisValue = Double.POSITIVE_INFINITY;
		Comparator<RTreeNode<? extends Regionable>> bestAxis = null;

		for (Comparator<RTreeNode<? extends Regionable>> axis : AXES) {
			Arrays.sort(children, axis);
			double axisValue = getMarginValues();
			if (axisValue < bestAxisValue) {
				bestAxisValue = axisValue;
				bestAxis = axis;
			}
		}

		Arrays.sort(children, bestAxis);
		int bestSplit = SPLIT_BOUNDS;
		double bestSplitOverlap = Double.POSITIVE_INFINITY;
		for (int i = SPLIT_BOUNDS; i < children.length - SPLIT_BOUNDS; i++) {
			double overlap = getOverlap(i);
			if (bestSplitOverlap > overlap) {
				bestSplit = i;
				bestSplitOverlap = overlap;
			}
		}

		fill = bestSplit;
		recomputeBounds();
		return new RTreeSubtree<T>(children, fill, children.length - fill);
	}

	private void recomputeBounds() {
		bounds = children[0].getBounds();
		for (int i = 1; i < fill; i++) {
			bounds = bounds.union(children[i].getBounds());
		}
	}

	private double getOverlap(int splitIndex) {
		SimpleRectangle rect1 = getUnionUntil(splitIndex);

		SimpleRectangle rect2 = getUnionFrom(splitIndex);

		SimpleRectangle cut = rect1.cut(rect2);
		return cut == null ? 0 : cut.getArea();
	}

	private double getMarginValues() {
		double margin = 0;
		for (int i = SPLIT_BOUNDS; i < children.length - SPLIT_BOUNDS; i++) {
			margin += getMarginValue(i);
		}
		return margin;
	}

	private double getMarginValue(int splitIndex) {
		// assert children is full
		assert splitIndex >= 1 && splitIndex < children.length - 1;

		SimpleRectangle rect1 = getUnionUntil(splitIndex);

		SimpleRectangle rect2 = getUnionFrom(splitIndex);

		return rect1.getMargin() + rect2.getMargin();
	}

	private SimpleRectangle getUnionFrom(int splitIndex) {
		SimpleRectangle rect2 = children[CHILDREN_PER_NODE - 1].getBounds();
		for (int i = splitIndex; i < children.length - 1; i++) {
			rect2 = rect2.union(children[i].getBounds());
		}
		return rect2;
	}

	private SimpleRectangle getUnionUntil(int splitIndex) {
		SimpleRectangle rect1 = children[0].getBounds();
		for (int i = 1; i < splitIndex; i++) {
			rect1 = rect1.union(children[i].getBounds());
		}
		return rect1;
	}

	@Override
	public SimpleRectangle getBounds() {
		return bounds;
	}

	@Override
    public void findElementsIntersecting(Rectangle rect, List<T> found) {
	    for (int i = 0; i < fill; i++) {
	    	if (children[i].getBounds().intersects(rect)) {
	    		children[i].findElementsIntersecting(rect, found);
	    	}
	    }
    }
}
