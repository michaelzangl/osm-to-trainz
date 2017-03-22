package data.height;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class SRTMImporter implements HeightDataProvider {
	List<File> lookupPaths = new LinkedList<File>();

	private Hashtable<SRTMDatapackPosition, SRTMDatapack> datapacks =
	        new Hashtable<SRTMDatapackPosition, SRTMDatapack>();

	@Override
	public float getHeight(double lat, double lon) {
		SRTMDatapackPosition position =
		        SRTMDatapackPosition.fromLatLon((float) lat, (float) lon);
		SRTMDatapack datapack = getDatapack(position);

		if (datapack != null) {
			return datapack.getData((float) lat, (float) lon);
		} else {
			return 0;
		}
	}

	private synchronized SRTMDatapack getDatapack(SRTMDatapackPosition position) {
		try {
			SRTMDatapack datapack = datapacks.get(position);
			if (datapack == null) {
				File file = findFile(position.toFilename());
				if (file != null) {
					datapack = SRTMDatapack.readFile(position, file);
					datapacks.put(position, datapack);
				} else {
					datapacks.put(position, SRTMDatapack.emptyPack(position));
				}
			}
			return datapack;
		} catch (IOException e) {
			return null;
		}
	}

	private File findFile(String filename) {
		for (File directory : lookupPaths) {
			File file = new File(directory, filename);
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}

	public synchronized void setLookupPaths(List<File> lookupPaths) {
		this.lookupPaths = lookupPaths;
	}

}
