package export.trainz.map;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import conversion.landscape.LandscapeTextureProvider;
import data.height.LocalHeightDataProvider;
import export.trainz.general.Kuid;

/**
 * This is the grid that is normally contained by a map grid file.
 * @author michael
 *
 */
public class MapGrid {
	public static final int SEGMENTSIZE = 720; // in meters.

	private LinkedList<MapGridPart> parts = new LinkedList<MapGridPart>();

	public MapGrid() {
	}
	
	public synchronized void assertPartIsUnder(float worldx, float worldy) {
		int x = (int) Math.floor(worldx / SEGMENTSIZE);
		int y = (int) Math.floor(worldy / SEGMENTSIZE);
		if (getPart(x, y) == null) {
			addPart(new MapGridPart(x, y));
		}
	}

	public synchronized void addPart(MapGridPart part) {
		if (part == null) {
			throw new NullPointerException();
		}
		for (MapGridPart old : parts) {
			if (old.hasSameCoordinates(part)) {
				throw new IllegalArgumentException("Part already added");
			}
		}
		parts.add(part);
	}

	public synchronized MapGridPart getPart(int segmentx, int segmenty) {
		for (MapGridPart part : parts) {
			if (part.hasCoordinates(segmentx, segmenty)) {
				return part;
			}
		}
		return null;
	}

	public MapGridPart getPartUnder(float worldx, float worldy) {
		int x = (int) Math.floor(worldx / SEGMENTSIZE);
		int y = (int) Math.floor(worldy / SEGMENTSIZE);
		return getPart(x, y);
	}
	
	public synchronized void loadHeightsForm(LocalHeightDataProvider provider) {
		for (MapGridPart part : parts) {
			part.loadHeightsForm(provider);
		}
	}
	
	public synchronized void loadTexturesFrom(LandscapeTextureProvider provider) {
		for (MapGridPart part : parts) {
			part.loadTexturesFrom(provider);
		}}

	public List<MapGridPart> getParts() {
	    return Collections.unmodifiableList(parts);
    }

	public Collection<Kuid> getUsedKuids() {
		HashSet<Kuid> used = new HashSet<Kuid>();
		for (MapGridPart part : parts) {
			used.addAll(part.getUsedKuids());
		}
		return used;
	}

}
