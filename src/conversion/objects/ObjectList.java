package conversion.objects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import conversion.datachange.geometry.Rectangle;

import data.rtree.RTree;

import export.objects.ObjectLink;

public class ObjectList {
	private LinkedList<ObjectLink> objects = new LinkedList<ObjectLink>();
	private List<ObjectLink> unmodifyableObjects = Collections.unmodifiableList(objects);
	
	private RTree<ObjectLink> positionedObjects = new RTree<ObjectLink>();
	
	public void add(ObjectLink link) {
		objects.add(link);
		positionedObjects.add(link);
	}
	
	public List<ObjectLink> getObjects() {
		return unmodifyableObjects;
	}
	
	public List<ObjectLink> getObjectsIn(Rectangle bounds) {
		return positionedObjects.findElementsIntersecting(bounds);
	}
}
