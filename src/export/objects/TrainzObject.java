package export.objects;

import conversion.datachange.geometry.SimpleRectangle;
import export.trainz.general.Kuid;
import export.trainz.general.KuidObject;

public class TrainzObject extends KuidObject implements ObjectData {

	private static final SimpleRectangle DEFAULT_BOUNDS = new SimpleRectangle(
	        -3, -3, 3, 3);

	public TrainzObject(Kuid kuid) {
		super(kuid);
	}

	@Override
	public SimpleRectangle getBounds() {
		return DEFAULT_BOUNDS;
	}

}
