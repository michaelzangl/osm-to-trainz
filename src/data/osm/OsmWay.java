package data.osm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import data.position.local.GlobalToLocalConverter;
import data.position.local.LatLon;
import data.position.local.LocalPoint;

public class OsmWay implements Iterable<OsmNode>, Propertyable {
	private final LinkedList<OsmNode> nodes = new LinkedList<OsmNode>();
	private final Properties properties = new Properties();
	private float area = Float.NaN;

	public OsmWay(List<OsmNode> nodes, Properties properties) {
		if (nodes.size() < 2) {
			throw new IllegalArgumentException("A way must have at least two nodes");
		}
		for (OsmNode node : nodes) {
			this.nodes.add(node);
		}
		this.properties.putAll(properties);
	}

	public String getProperty(String name) {
		return properties.getProperty(name);
	}

	@Override
	public Iterator<OsmNode> iterator() {
		return nodes.iterator();
	}

	public boolean isArea() {
		return nodes.getFirst().equals(nodes.getLast());
	}
	
	public float getArea() {
		if (isArea() && area == Float.NaN) {
			area = computeArea();
		}
		return area;
	}

	/**
	 * Uses the fact that first == last
	 * @return
	 */
	private float computeArea() {
		float areaSum = 0;
		GlobalToLocalConverter converter = new GlobalToLocalConverter(getCenter());
		
		Iterator<OsmNode> it = iterator();
		LocalPoint previous = null;
		while (it.hasNext()) {
			OsmNode current = it.next();
			LocalPoint local = converter.toLocal(current.getLat(), current.getLon());
			
			if (previous != null) {
				areaSum += previous.getX() * local.getY() - previous.getY() * local.getX();
			}
			previous = local;
		}
		return Math.abs(areaSum);
	}

	/**
	 * gets the approx center of the way
	 * @return The center.
	 */
	public LatLon getCenter() {
		Iterator<OsmNode> it = iterator();
		if (isArea()) {
			it.next();
		}
		float latSum = 0;
		float lonSum = 0;
		while (it.hasNext()) {
			OsmNode current = it.next();
			latSum += current.getLat();
			lonSum += current.getLon();
		}
		
		int count = isArea() ? nodes.size() - 1 : nodes.size();
		return new LatLon(latSum / count, lonSum / count);
	}

	public Iterator<OsmWaySegment> getSegments() {
	    return new WaySegmentIterator();
    }
	
	private class WaySegmentIterator implements Iterator<OsmWaySegment> {
		OsmNode currentNode;
		Iterator<OsmNode> myIterator;
		
		private WaySegmentIterator() {
			myIterator = iterator();
			currentNode = myIterator.next();
		}
		
		@Override
        public boolean hasNext() {
	        return myIterator.hasNext();
        }

		@Override
        public OsmWaySegment next() {
	        OsmNode end = myIterator.next();
	        OsmWaySegment segment = new OsmWaySegment(currentNode, end);
	        currentNode = end;
	        return segment;
        }

		@Override
        public void remove() {
	        throw new UnsupportedOperationException();
        }
		
	}
	
	@Override
	public String toString() {
	    String string = "OsmWay(" + nodes.size() + "nodes)[";
	    boolean first = true;
	    for (Entry<Object, Object> entry : properties.entrySet()) {
	    	if (first) {
	    		first = false;
	    	} else {
	    		string += ",";
	    	}
	    	string += entry.getKey() + "=" + entry.getValue();
	    }
	    return string + "]";
	}
	
	public boolean isBridge() {
		return isPropertyTrue("bridge");
	}

	public boolean isEmbarkment() {
		return isPropertyTrue("embankment");
	}

	public boolean isTunnel() {
		return isPropertyTrue("tunnel");
	}

	public boolean isCutting() {
		return isPropertyTrue("cutting");
	}


	private boolean isPropertyTrue(String name) {
	    String property = getProperty(name);
	    return property != null && !"no".equalsIgnoreCase(property);
    }

	public int getNodeCount() {
	    return nodes.size();
    }

	@Override
    public Set<String> getPropertyKeys() {
	    return properties.stringPropertyNames();
    }

	public void addRelation(OsmRelation osmRelation) {
	    // TODO Auto-generated method stub
	    
    }

	public OsmNode getFirstNode() {
	    return nodes.getFirst();
    }
	
	public OsmNode getLastNode() {
	    return nodes.getLast();
    }
}
