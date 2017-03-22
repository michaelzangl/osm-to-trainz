package conversion.objects;

import java.util.Random;

import data.osm.OsmNode;
import data.osm.OsmWay;
import export.objects.ObjectData;
import export.objects.TrainzObject;
import export.objects.XMLObject;
import export.trainz.general.Kuid;
import export.trainz.general.Kuid2;

public class OsmObjectTyper {

	private static final Kuid BENCH = new Kuid2(95148, 10084, 1);
	private static final ObjectData SMALL_BUILDING = new XMLObject("small_building");
	private static final ObjectData MEDIUM_BUILDING = new XMLObject("small_building");
	private static Kuid[] PINE_TREES = new Kuid[] {
	        // Pine 4,<kuid:52597:22904>
	        new Kuid(52597, 22904),
	        // Pine 5,<kuid:52597:22905>
	        new Kuid(52597, 22905),
	        // Pine 9,<kuid:52597:22909>
	        new Kuid(52597, 22909),
	        // Pinie10,<kuid:68787:22560>
	        new Kuid(68787, 22560),
	        // Pinie11,<kuid:68787:22561>
	        new Kuid(68787, 22561),
	        // Pinie12,<kuid:68787:22562>
	        new Kuid(68787, 22562),
	        // Pinie13,<kuid:68787:22563>
	        new Kuid(68787, 22563),
	        // Pinie6,<kuid:68787:22540>
	        new Kuid(68787, 22540),
	};

	private static Kuid[] LEAFED_TREES = new Kuid[] {
	        // Linde,<kuid:68787:22072>
	        new Kuid(68787, 22072),
	        // Linde2,<kuid:68787:22555>
	        new Kuid(68787, 22555),
	        // Oak 1,<kuid:52597:22661>
	        new Kuid(52597, 22661),
	        // Oak 2,<kuid:52597:22662>
	        new Kuid(52597, 22662),
	        // oak20,<kuid:71619:22003>
	        new Kuid(71619, 22003),
	};

	private static Kuid[] UNLEAFED_TREES = new Kuid[] {
	        // Kiefer3,<kuid:68787:22033>
	        new Kuid(68787, 22033),
	        // Kiefer4,<kuid:68787:22035>
	        new Kuid(68787, 22035),
	        // Kiefer5,<kuid:68787:22036>
	        new Kuid(68787, 22036),
	        // Kiefer6,<kuid:68787:22037>
	        new Kuid(68787, 22037),

	        // Fichte6,<kuid:68787:22553>
	        new Kuid(68787, 22553),
	        // Fichte7,<kuid:68787:22206>
	        new Kuid(68787, 22206),
	        // Fichte9,<kuid:68787:22208>
	        new Kuid(68787, 22208)
	};

	private static Kuid[] SCRUB_PLANTS = new Kuid[] {
	        // Busch5m3,<kuid:68787:22086>
	        new Kuid(68787, 22086),
	        // Busch6,<kuid:68787:1076>
	        new Kuid(68787, 1076),
	        // Grasflaeche1,<kuid:68787:1172>
	        new Kuid(68787, 1172),
	        // Gras5m,<kuid:68787:22425>
	        new Kuid(68787, 22425),
	        // Strauch1,<kuid:68787:22098>
	        new Kuid(68787, 22098),
	};
	
	public ObjectData getObjectForPoint(OsmNode node, Random rand) {
		Kuid kuid = getKuidForPoint(node, rand);
		if (kuid != null) {
			return new TrainzObject(kuid);
		}
		return null;
	}
	
	public ObjectData getObjectForArea(OsmWay area, Random rand) {
		if (area.getProperty("building") != null) {
			return convertToBuilding(area);
		}
		return null;
	}
	
	
	private ObjectData convertToBuilding(OsmWay area) {
	    if (area.getArea() < 50) {
	    	return SMALL_BUILDING;
	    } else if (area.getArea() < 100) {
	    	return MEDIUM_BUILDING;
	    } else {
	    	return null;
	    }
    }

	private Kuid getKuidForPoint(OsmNode node, Random rand) {
		if (node.getProperty("natural") != null) {
			return getNatural(node, rand);
		} else if (node.getProperty("amenity") != null) {
			return getAmenity(node, rand);
		} else {
			return null;
		}
	}

	private Kuid getNatural(OsmNode node, Random rand) {
		String natural = node.getProperty("natural");
		if (natural.equals("tree")) {
			String type = node.getProperty("type");
			if ("conifer".equals(type)) {
				return UNLEAFED_TREES[rand.nextInt(UNLEAFED_TREES.length)];
			} else {// broad_leafed
				return LEAFED_TREES[rand.nextInt(LEAFED_TREES.length)];
			}
		} else if (natural.equals("scrub")) {
			return SCRUB_PLANTS[rand.nextInt(SCRUB_PLANTS.length)];
		}
		return null;
	}

	private Kuid getAmenity(OsmNode node, Random rand) {
		String amenity = node.getProperty("amenity");
		if ("bench".equals(amenity)) {
			return BENCH;
		} else {
			return null;
		}
	}
}
