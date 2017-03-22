package data.height;

import java.util.LinkedList;

import data.position.local.GlobalToLocalConverter;
import data.position.local.LatLon;
import data.position.local.LocalPoint;

public class LocalHeightDataProvider {
	private final GlobalToLocalConverter converter;
	private final HeightDataProvider provider;
	private final LinkedList<HeightDisplacer> displacers = new LinkedList<HeightDisplacer>();

	public LocalHeightDataProvider(GlobalToLocalConverter converter,
	        HeightDataProvider provider) {
		this.converter = converter;
		this.provider = provider;
	}
	
	public void addDisplacer(HeightDisplacer displacer) {
		displacers.add(displacer);
	}

	public float getHeight(float x, float y) {
		return getHeight(new LocalPoint(x, y));
	}

	public float getHeight(LocalPoint position) {
		LatLon pos = converter.toGlobal(position);
		float height = provider.getHeight(pos.getLat(), pos.getLon());
		for (HeightDisplacer displacer : displacers) {
			height += displacer.getDisplacement(position);
		}
		return height;

	}
}
