package conversion.landscape;

import java.awt.Color;

import data.osm.Propertyable;
import export.ground.TextureLink;
import export.trainz.general.Kuid;
import export.trainz.general.Kuid2;

public class TextureFinder {
	
	//AUS_Gras,<kuid:-1:100248>
	private static final TextureLink DEFAULT = new TextureLink(new Kuid(-1, 100248), new Color(0x5cac2d));
	//AJS Grass 04,<kuid2:122285:1094:1>
	private static final TextureLink PARK = new TextureLink(new Kuid2(122285, 1094, 2), new Color(124, 252, 0));
	//pl_ballast2b,<kuid:9:35004>
	private static final TextureLink RAILWAY = new TextureLink(new Kuid(9, 35004), new Color(153, 102, 51));
	//asphalt,<kuid:69084:21002>
	private static final TextureLink INDUSTRIAL = new TextureLink(new Kuid(69084, 21002), Color.DARK_GRAY);
	//ger_forest,<kuid:-1:101064>
	private static final TextureLink FOREST = new TextureLink(new Kuid(-1, 101064), new Color(6, 71, 12));
	//AJS Grass 05,<kuid2:122285:1095:1>
	private static final TextureLink GRASS = new TextureLink(new Kuid2(122285, 1095, 1), new Color(153, 255 ,102));
	//pl_ballast2b,<kuid:9:35004>
	private static final TextureLink RIVERBED = new TextureLink(new Kuid(9, 35004), new Color(117, 187, 253));
	//trocken_Langgr_Streifen,<kuid:-1:624>
	private static final TextureLink FILED = new TextureLink(new Kuid(-1, 624), Color.YELLOW);
	private static final TextureLink WATER = RIVERBED;
	private static final TextureLink STREETGROUND = INDUSTRIAL;
	private static final TextureLink RESIDENTIAL = GRASS;

	public TextureLink getTextureFor(Propertyable area) {
		String landuse = area.getProperty("landuse");
		String natural = area.getProperty("natural");
		
		if ("railway".equals(landuse)) {
			return RAILWAY;
			
		} else if ("industrial".equals(landuse)) {
			return INDUSTRIAL;
			
		} else if ("residential".equals(landuse)) {
			return INDUSTRIAL;
			
		} else if ("wood".equals(natural) || "forest".equals(natural) || "forest".equals(landuse)) {
			return FOREST;
			
		} else if ("grass".equals(natural)) {
			return GRASS;
			
		} else if ("farm".equals(landuse)) {
			return FILED;
			
		} else if ("park".equals(natural)) {
			return PARK;
			
		} else if ("water".equals(natural)) {
			return WATER;
			
		} else if ("riverbed".equals(landuse)) {
			return RIVERBED;
			
		} else if ("streetground".equals(landuse)) {
			return STREETGROUND;
			
		} else {
			return DEFAULT;
		}
	}

	public TextureLink getDefault() {
	    return DEFAULT;
    }
}
