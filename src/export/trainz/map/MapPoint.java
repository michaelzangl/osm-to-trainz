package export.trainz.map;

import export.ground.TextureLink;


/**
 * This is a map point of the texture/height grid
 * 
 * @author michael
 * 
 */
public class MapPoint {
	private float height = 0;
	private TextureLink texture = TextureLink.DEFAULT;

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public TextureLink getTexture() {
		return texture;
	}

	public void setTexture(TextureLink texture) {
		this.texture = texture;
	}
}
