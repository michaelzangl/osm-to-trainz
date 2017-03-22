package export.ground;

import java.awt.Color;

import export.trainz.general.Kuid;
import export.trainz.general.KuidObject;


public class TextureLink extends KuidObject {
	public static final TextureLink DEFAULT = new TextureLink(new Kuid(-1, 6270));
	private Color color;
	
	public TextureLink(Kuid kuid) {
		this(kuid, Color.BLACK);
	}
	
	public TextureLink(Kuid kuid, Color color) {
		super(kuid);
		this.color = color;
	}

	public Color getColor() {
	    return color;
    }
}
