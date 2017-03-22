package data.osm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import data.position.local.LatLonObject;

public class OsmNode implements Iterable<OsmWay>, LatLonObject, Propertyable {

	private final double lat;
	private final double lon;
	private final SimpleProperties properties;

	private final List<OsmWay> ways = new LinkedList<OsmWay>();

	public OsmNode(double lat, double lon, Properties properties) {
		this(lat, lon, SimpleProperties.fromHashtable(properties));
	}

	public OsmNode(double lat, double lon, SimpleProperties properties) {
		this.lat = lat;
		this.lon = lon;
		this.properties = properties;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public String getProperty(String name) {
		return properties.getProperty(name);
	}

	protected void addWay(OsmWay way) {
		synchronized (ways) {
			ways.add(way);
		}
	}

	@Override
	public Iterator<OsmWay> iterator() {
		return ways.iterator();
	}

	@Override
    public Set<String> getPropertyKeys() {
	    return properties.getPropertyKeys();
    }

	public void addRelation(OsmRelation osmRelation) {
	    // TODO Auto-generated method stub
	    
    }
}
