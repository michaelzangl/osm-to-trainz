package data.osm;

import java.util.Set;

public interface Propertyable {
	public String getProperty(String name);
	
	public Set<String> getPropertyKeys();
}
