package conversion.objects;

import java.util.Random;

import conversion.ConversionData;
import data.osm.OsmNode;
import data.osm.OsmWay;
import data.position.local.LocalPoint;
import export.objects.ObjectData;
import export.objects.ObjectLink;

public class OsmObjectConverter {
	private final ConversionData data;

	public OsmObjectConverter(ConversionData data) {
		this.data = data;

	}

	public void doConversion(Random rand) {
		OsmObjectTyper typer = new OsmObjectTyper();
		for (OsmNode node : data.getOsmData().getNodes()) {
			ObjectData kuid = typer.getObjectForPoint(node, rand);
			if (kuid != null) {
				LocalPoint position = data.getConverter().toLocal(node);
				if (data.getLandscape().isLandUnder(position)) {
					ObjectLink object = new ObjectLink(kuid, position, 0);
					data.getObjects().add(object);
				}
			}
		}

		for (OsmWay way : data.getOsmData().getWays()) {
			if (way.isArea()) {
				ObjectData object = typer.getObjectForArea(way, rand);
				if (object != null) {
					LocalPoint position =
					        data.getConverter().toLocal(way.getCenter());
					if (data.getLandscape().isLandUnder(position)) {
						data.getObjects().add(
						        new ObjectLink(object, position, 0));
					}
				}
			}
		}

	}
}
