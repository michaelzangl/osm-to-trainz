package data.osm;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * This is a pseudo property pack.
 * 
 * @author michael
 */
public class SimpleProperties implements Propertyable {

	private final String[] keyvalues;

	/**
	 * Generates a new property table
	 * 
	 * @param keyvalues
	 *            An array of an even number of strings. The strings are assumed
	 *            to be key and value pairs. Each even element is a key, the
	 *            following one a value.
	 */
	public SimpleProperties(String... keyvalues) {
		if (keyvalues.length % 2 != 0) {
			throw new IllegalArgumentException(
			        "There are more keys than values");
		}
		this.keyvalues = keyvalues;
	}

	@Override
	public String getProperty(String name) {
		for (int i = 0; i < keyvalues.length; i += 2) {
			if (name.equals(keyvalues[i])) {
				return keyvalues[i + 1];
			}
		}
		return null;
	}

	@Override
	public Set<String> getPropertyKeys() {
		HashSet<String> keys = new HashSet<String>();
		for (int i = 0; i < keyvalues.length; i += 2) {
			keys.add(keyvalues[i]);
		}
		return keys;
	}

	public static SimpleProperties fromHashtable(Properties props) {
		Set<String> keys = props.stringPropertyNames();
		String[] keyvalues = new String[keys.size() * 2];
		int i = 0;
		for (String k : keys) {
			keyvalues[i] = k;
			keyvalues[i + 1] = props.getProperty(k);
			i += 2;
		}
		return new SimpleProperties(keyvalues);
	}
}
