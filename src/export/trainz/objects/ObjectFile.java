package export.trainz.objects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import export.objects.ObjectLink;
import export.objects.TrainzObject;
import export.trainz.general.Kuid;
import export.trainz.general.writer.ByteWriter;


/**
 * This is a file that contains the object information and object position.
 * Koordinaten: x-Koordinate: Abstand in Meter Richtung Nord y-Koordinate:
 * Abstand in Meter Richtung West (the coordinates are not changed by the
 * world-origin object) <code>
 *  -> 4 Byte, (0x07, 0x00, 0x00, 0x00)
 *  -> 4 Byte, (0x04, 0x00, 0x00, 0x00)
 *    
 * For each object:
 *    -> 4 Byte, int, Number of object in file.
 *    -> 4 Byte, "JBOm"
 *    -> 4 Byte Kuid part 2
 *    -> 4 Byte Kuid part 1
 *    -> 4 Byte, (0x29, 0x00, 0x00, 0x00)
 *    -> 4 Byte, (0x03, 0x00)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 2 Byte, (0xff, 0xff)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 4 Byte: x (float)
 *    -> 4 Byte: y (float)
 *    -> 4 Byte, height (float)
 *    -> 4 Byte: rotation (float)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 4 Byte, height (float)    <- again?
 *    -> 4 Byte, (0x01, 0x00, 0x00, 0x00), station ored with: (0x10, 0x00, 0x00, 0x00)
 *    -> 1 Byte, (0x00)
 *    
 *  End:
 *  -> 4 Byte, (0xff, 0xff, 0xff, 0xff)
 *  -> 4 Byte, (0x0c, 0x00, 0x00, 0x00)
 *  -> 4 Byte, (0xEB, 0xCE, 0xED, 0xFE)
 *  -> 4 Byte, (0x01, 0x00, 0x00, 0x00)
 *  -> 4 Byte, (0xff, 0xff, 0xff, 0xff)
 * </code> Object definition for Station objects (alternative to normal
 * objects): <code>
 * 
 *    -> 4 Byte, int, Number of object in file.
 *    -> 4 Byte, "DNIm"
 *    -> 4 Byte Kuid part 2
 *    -> 4 Byte Kuid part 1
 *    -> 4 Byte, (0xD4, 0x00, 0x00, 0x00)
 *    -> 4 Byte, (0x03, 0x00)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 2 Byte, (0xff, 0xff)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 4 Byte: x (float)
 *    -> 4 Byte: y (float)
 *    -> 4 Byte, height (float)
 *    -> 4 Byte: rotation (float)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 4 Byte, height (float)    <- again?
 *    -> 4 Byte, bytecount of name (with 0)
 *    -> Name as String, 0-terminated
 *    -> Many ints and other stuff, always the same (?)
 *        00 00 00 00 
 *        08 00 00 00 
 *        00 00 00 00 
 *        01 00 00 00 
 *        02 00 00 00 
 *        03 00 00 00 
 *        04 00 00 00 
 *        05 00 00 00 
 *        06 00 00 00 
 *        07 00 00 00 
 *        06 00 00 00 
 *        00 00 00 00 
 *        01 00 00 00 
 *        02 00 00 00 
 *        03 00 00 00 
 *        04 00 00 00 
 *        05 00 00 00 
 *        08 00 00 00 
 *        00 00 00 00 
 *        01 00 00 00 
 *        01 00 00 00 
 *        02 00 00 00 
 *        02 00 00 00 
 *        02 00 00 00 
 *        03 00 00 00 
 *        01 00 00 00 
 *        04 00 00 00 
 *        01 00 00 00 
 *        05 00 00 00 
 *        02 00 00 00 
 *        06 00 00 00 
 *        02 00 00 00 
 *        07 00 00 00 
 *        01 00 00 00 
 *        01 00 00 00 
 *        00 00 80 3F <- float for 1
 *        02 00 00 00 
 *        00 00 00 00 
 *        01 01 00
 * </code>
 * 
 * Alternative for Trackside objects:
 * 
 * <code>
 *    -> 4 Byte, int, Number of object in file.
 *    -> 4 Byte, "2Ost"
 *    -> 4 Byte Kuid part 2
 *    -> 4 Byte Kuid part 1
 *    -> 4 Byte, (0x40, 0x00, 0x00, 0x00)
 *       -> other for priority
 *    -> 4 Byte, (0x03, 0x00, 0x00, 0x00)
 *    -> 4 Byte, (0x00, 0x00, 0xff, 0xff)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 4 Byte: x (float)
 *    -> 4 Byte: y (float)
 *    -> 4 Byte, height (float)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 4 Byte, (0x00, 0x00, 0x00, 0x00)
 *    -> 4 Byte, bytecount of name (with 0)
 *    -> Name as String, 0-terminated
 *    -> 9 Byte, 02 00 00 00 00 00 00 00 00
 *    -> 4 Byte, id of the track the object is for.
 *    -> 4 Byte: rotation (float) ?
 *    -> 4 Byte: Direction. 1 means forward, 0 backward.
 *    -> 6 Byte: 00 00
 *    
 * </code>
 * 
 * @author michael
 */
public class ObjectFile {

	private final List<ObjectLink> objects;

	private ObjectFile(List<ObjectLink> objects) {
		this.objects = objects;
	}

	public static ObjectFile constructByList(List<ObjectLink> objects) {
		return new ObjectFile(objects);
	}

	public void writeToFile(File file) throws IOException {
		ByteWriter out = new ByteWriter(new FileOutputStream(file));
		out.write(new byte[] {
		        0x07, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00
		});
		int objectNumber = 0;
		for (ObjectLink object : objects) {
			if (!(object.getData() instanceof TrainzObject)) {
				continue;
			}
			Kuid kuid = ((TrainzObject)object.getData()).getKuid();
			
			out.writeInt(objectNumber);
			out.write(new byte[] {
			        'J', 'B', 'O', 'm'
			});
			out.writeInt(kuid.getPart2());
			out.writeInt(kuid.getPart1());
			out.write(new byte[] {
			        0x29,
			        0x00,
			        0x00,
			        0x00,
			        0x03,
			        0x00,
			        0x00,
			        0x00,
			        0x00,
			        0x00,
			        (byte) 0xff,
			        (byte) 0xff,
			        0x00,
			        0x00,
			        0x00,
			        0x00
			});
			out.writeFloat((float) object.getPosition().getX());
			out.writeFloat((float) object.getPosition().getY());
			float height = (float) object.getPosition().getHeight();
			if (Float.isNaN(height)) {
				height = 0;
			}
			out.writeFloat(height);
			out.writeFloat(object.getRotation());
			out.writeInt(0);
			out.writeFloat(height);
			out.writeInt(1);
			out.write(0x00);

			objectNumber++;
		}

		out.write(new byte[] {
		        (byte) 0xff,
		        (byte) 0xff,
		        (byte) 0xff,
		        (byte) 0xff,
		        0x0c,
		        0x00,
		        0x00,
		        0x00,
		        (byte) 0xEB,
		        (byte) 0xCE,
		        (byte) 0xED,
		        (byte) 0xFE,
		        0x01,
		        0x00,
		        0x00,
		        0x00,
		        (byte) 0xff,
		        (byte) 0xff,
		        (byte) 0xff,
		        (byte) 0xff
		});
	}

	public Collection<Kuid> getUsedKuids() {
	    HashSet<Kuid> used = new HashSet<Kuid>();
	    for (ObjectLink object : objects) {
			if (!(object.getData() instanceof TrainzObject)) {
				continue;
			}
			Kuid kuid = ((TrainzObject)object.getData()).getKuid();
	    	used.add(kuid);
	    }
	    return used;
    }
}
