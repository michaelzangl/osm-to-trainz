package conversion.landscape;

import data.position.local.LocalPoint;
import export.ground.TextureLink;

public class LandscapeTextureProvider {
	private final Landscape landscape;
	private final TextureFinder finder = new TextureFinder();

	public LandscapeTextureProvider(Landscape landscape) {
		this.landscape = landscape;
	}

	public TextureLink getTextureAt(LocalPoint p) {
		LandscapePolygon found = landscape.getWayUnder(p);
		if (found != null) {
			return finder.getTextureFor(found.getOriginal());
		} else {
			return finder.getDefault();
		}
	}
}
