package export.objects;

import conversion.datachange.geometry.SimpleRectangle;
import data.position.local.LocalPoint;
import data.rtree.Regionable;
import export.trainz.general.Kuid;
import export.trainz.general.KuidObject;

public class ObjectLink implements Regionable {
	private final LocalPoint position;
	private final float rotation;
	private final ObjectData data;

	public ObjectLink(ObjectData data, LocalPoint position, float rotation) {
		this.data = data;
		this.position = position;
		this.rotation = rotation;
	}
	
	public LocalPoint getPosition() {
	    return position;
    }
	
	public float getRotation() {
	    return rotation;
    }
	
	@Override
	public SimpleRectangle getBounds() {
	    return data.getBounds().getDisplaced(position.getX(), position.getY());
	}

	public ObjectData getData() {
	    return data;
    }
}
