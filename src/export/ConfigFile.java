package export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;

import data.position.local.LatLon;
import export.trainz.general.Kuid;

public class ConfigFile {
	private static final Kuid DEFAULTREGION = new Kuid(-1, 7808);
	private static final Kuid DEFAULTWATER = new Kuid(-1, 6342);

	private final Hashtable<String, Object> properties =
	        new Hashtable<String, Object>();

	private HashSet<Kuid> usedKuids = new HashSet<Kuid>();
	
	private LatLon origin = new LatLon(0, 0);

	public ConfigFile(Kuid kuid, String name) {
		properties.put("kuid", kuid);
		properties.put("region", DEFAULTREGION);
		usedKuids.add(DEFAULTREGION);
		properties.put("kind", "map");
		properties.put("username", name);
		properties.put("username-de", name);

		properties.put("workingscale", "0");
		properties.put("workingunits", "0");
		properties.put("water", DEFAULTWATER);
		properties.put("description-de", "");
		properties.put("trainz-build", "2.6");
	}

	public void addKuids(Collection<Kuid> kuids) {
		usedKuids.addAll(kuids);
	}
	
	public String getString() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<String, Object> entry : properties.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append("\t");
			buffer.append(entry.getValue().toString());
			buffer.append("\r\n");
		}
		
		buffer.append("world-origin {\r\n");
		buffer.append("\tlatitude\t" + toDegreeMinutes(origin.getLat()) + "\r\n");
		buffer.append("\tlongitude\t" + toDegreeMinutes(origin.getLon()) + "\r\n");
		buffer.append("\taltitude\t0\r\n");
		buffer.append("}\r\n");
		
		buffer.append("string-table {\r\n");
		buffer.append("}\r\n");
		buffer.append("kuid-table {\r\n");
		int index = 0;
		for (Kuid kuid : usedKuids) {
			buffer.append("\t");
			buffer.append(index);
			buffer.append("\t");
			buffer.append(kuid);
			buffer.append("\r\n");
			index++;
		}
		buffer.append("}\r\n");
		
		return buffer.toString();
	}

	private String toDegreeMinutes(double lat) {
	    int degree = (int) lat;
	    double rest = (lat - degree) * 60;
	    int minutes = (int) rest;
	    double rest2 = (rest - minutes) * 60;
	    int seconds = (int) rest2;
	    return degree + "," + minutes + "," + seconds;
    }

	public void writeToFile(File file) throws FileNotFoundException {
	    PrintStream stream = new PrintStream(file);
	    stream.append(getString());
	    stream.close();
    }
}
