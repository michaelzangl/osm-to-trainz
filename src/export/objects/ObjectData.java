package export.objects;

import conversion.datachange.geometry.SimpleRectangle;

/**
 * This interfaces defines how object data may be accessed.
 * <p>
 * There are several object data types that should be supported:
 * <p>
 * Currently, only Links to XML-Files are supported.
 * <p>
 * TODO: Links to Blender files, links to Kuid files.
 * 
 * @author michael
 *
 */
public interface ObjectData {
	public SimpleRectangle getBounds();
}
