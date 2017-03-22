package export.trainz.map;

import java.util.Hashtable;
import java.util.Map.Entry;

import export.ground.TextureLink;


/**
 * This class mapps textures to the index in the file when writing a grid file.
 * 
 * @author michael
 */
public class TextureSlotMapper {
	private static final int MAX_SLOTS = 250;

	private final Hashtable<TextureLink, Slot> slots = new Hashtable<TextureLink, TextureSlotMapper.Slot>();

	private final TextureLink fallbackTexture = TextureLink.DEFAULT;

	public TextureSlotMapper() {
		slots.put(fallbackTexture, new Slot(0));
	}
	
	public int getTextureId(TextureLink texture) {
		return getTextureSlot(texture).id;
	}

	public void increasTextureUsage(TextureLink texture) {
		getTextureSlot(texture).usage += 1.0f;
	}

	public float getTextureUsage(TextureLink texture) {
		return getTextureSlot(texture).usage;
	}
	
	private Slot getTextureSlot(TextureLink texture) {
		Slot slot = slots.get(texture);
		if (slot == null) {
			if (slots.size() < MAX_SLOTS) {
				slot = new Slot(slots.size());
				slots.put(texture, slot);
			} else {
				//TODO: test
				slot = slots.get(fallbackTexture);
				assert slot != null;
			}
		}
		return slot;
	}

	private class Slot {
		private float usage = 0;
		private int id;

		private Slot(int id) {
			this.id = id;
		}
	}

	public TextureLink getSlot(int slot) {
		for (Entry<TextureLink, Slot> entry : slots.entrySet()) {
			if (entry.getValue().id == slot) {
				return entry.getKey();
			}
		}
		return fallbackTexture;
    }
}
