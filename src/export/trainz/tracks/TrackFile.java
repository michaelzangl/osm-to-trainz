package export.trainz.tracks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import export.tracks.TrackConnection;
import export.tracks.TrackNetwork;
import export.tracks.TrackNode;
import export.trainz.general.writer.ByteWriter;


/**
 * This is a file that contains the tack information. <code>
 *  -> 4 Byte, (0x02, 0x00, 0x00, 0x00)
 *  -> 4 Byte, (0x04, 0x00, 0x00, 0x00)
 *  
 *  For each segment: (each 44 bytes)
 *    -> 4 Byte: segment id
 *    -> 4 Byte: object type, "tSkt"
 *    -> 4 Byte: Kuid part 2
 *    -> 4 Byte: Kuid part 1
 *    -> 4 Byte ? (0x18, 0x00, 0x00, 0x00)
 *    -> 4 Byte ? (0x04, 0x00, 0x00, 0x00)
 *    -> 4 Byte start node id
 *    -> 4 Byte end node id
 *    -> 4 Byte: Kuid part 2 (again)
 *    -> 4 Byte: Kuid part 1
 *    -> 4 Byte: (0x00, 0x00, 0x00, 0x00)
 *         If track has fixed height, ored with: (0x10, 0x00, 0x00, 0x00)
 *         If track is straight, ored with: (0x20, 0x00, 0x00, 0x00)
 *         At station, this is ored with: (0x54, 0x00, 0x00, 0x00)
 *  
 *  -> 4 Byte, (0xff, 0xff, 0xff, 0xff) (mark end of list) 
 *  
 *  -> 4 Byte, (0x0c, 0x00, 0x00, 0x00)
 *  -> 4 Byte, (0xEB, 0xCE, 0xED, 0xFE)
 *  -> 4 Byte, (0x01, 0x00, 0x00, 0x00)
 *  -> 4 Byte, (0xff, 0xff, 0xff, 0xff)
 *  -> 4 Byte, (0x04, 0x00, 0x00, 0x00)
 *  
 *  For each track node: (each 61 bytes)
 *    -> 4 Byte: node id
 *    -> 4 Byte: type "xVkt" (0x78 0x56 0x6B 0x74)
 *    -> ?, observed:  FE FF FF FF FF FF FF FF 29 00 00 00 03 00 00 00 00 00 00 00
 *    -> 4 Byte: x (float)
 *    -> 4 Byte: y (float)
 *    -> 4 Byte: height (float)
 *    Connected track table
 *    For each of the 4 track slots:
 *      -> 4 Byte: track ID, if no track is connected: -1,
 *      -> 1 Byte, 0x00 or 0x01, depending on the direction
 *    -> 1 Byte, 0x00
 *  
 *  -> 4 Byte, (0xff, 0xff, 0xff, 0xff)
 *  -> 4 Byte, (0x0c, 0x00, 0x00, 0x00)
 *  -> 4 Byte, (0xEB, 0xCE, 0xED, 0xFE)
 *  -> 4 Byte, (0x01, 0x00, 0x00, 0x00)
 *  -> 4 Byte, (0xff, 0xff, 0xff, 0xff)
 * </code>
 * 
 * Special code for bridge segment:
 * <code>
 *    -> 4 Byte: segment id
 *    -> 4 Byte: object type, "rBkt"
 *    -> 4 Byte: Kuid part 2
 *    -> 4 Byte: Kuid part 1
 *    -> 4 Byte ? (0x58, 0x00, 0x00, 0x00)
 *    -> 4 Byte ? (0x02, 0x00, 0x00, 0x00)
 *    -> 4 Byte ? (0x04, 0x00, 0x00, 0x00)
 *    -> 4 Byte start node id
 *    -> 4 Byte end node id
 *    -> 4 Byte: Kuid part 2 (again)
 *    -> 4 Byte: Kuid part 1
 *    -> 00 00 00 00 00 00 00 00 00 00 
 *    -> FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF
 *    -> 4 Byte start node id
 *    -> FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF
 *    -> 4 Byte end node id
 *    -> FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF
 * </code>
 * 
 * 
 * @author michael
 */
public class TrackFile {

	private final TrackNetwork network;

	public TrackFile(TrackNetwork network) {
		this.network = network;
	}

	public void writeToFile(File file) throws IOException {
		ByteWriter out = new ByteWriter(new FileOutputStream(file));

		out.write(new byte[] {
		        0x02, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00
		});

		for (TrackConnection connection : network.getConnections()) {
			out.writeInt(connection.getId());
			out.write(new byte[] {
			        't', 'S', 'k', 't'
			});
			out.writeInt(connection.getTracktype().getKuid().getPart2());
			out.writeInt(connection.getTracktype().getKuid().getPart1());
			out.write(new byte[] {
			        0x18, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00
			});
			out.writeInt(connection.getStart().getId());
			out.writeInt(connection.getEnd().getId());
			out.writeInt(connection.getTracktype().getKuid().getPart2());
			out.writeInt(connection.getTracktype().getKuid().getPart1());

			int settings = 0;
			if (connection.isStraight()) {
				settings |= 0x00000020;
			}
			if (connection.isHeightSet()) {
				settings |= 0x00000010;
			}
			out.writeInt(settings);
		}

		out.write(new byte[] {
		        (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff
		});

		out.write(new byte[] {
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
		        (byte) 0xff,
		        0x04,
		        0x00,
		        0x00,
		        0x00
		});

		for (TrackNode node : network.getNodes()) {
			out.writeInt(node.getId());
			out.write(new byte[] {
			        'x', 'V', 'k', 't'
			});
			out.write(new byte[] {
			        (byte) 0xFE,
			        (byte) 0xFF,
			        (byte) 0xFF,
			        (byte) 0xFF,
			        (byte) 0xFF,
			        (byte) 0xFF,
			        (byte) 0xFF,
			        (byte) 0xFF,
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
			        0x00,
			        0x00
			});
			out.writeFloat((float) node.getPosition().getX());
			out.writeFloat((float) node.getPosition().getY());
			double height = node.getPosition().getHeight();
			boolean heightUnset = Double.isNaN(height);
			if (heightUnset) {
				out.writeFloat(0.0f);
			} else {
				out.writeFloat((float) height);
			}

			int connectionsWritten = 0;

			for (TrackConnection connection : node.getForwardTracks()) {
				if (connectionsWritten < 4) {
					out.writeInt(connection.getId());
					out.write(0);
				}
				connectionsWritten++;
			}

			for (TrackConnection connection : node.getBackwardTracks()) {
				if (connectionsWritten < 4) {
					out.writeInt(connection.getId());
					out.write(1);
				}
				connectionsWritten++;
			}

			while (connectionsWritten < 4) {
				out.writeInt(-1);
				out.write(0);
				connectionsWritten++;
			}

			out.write(0);
		}

		out.write(new byte[] {
		        (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff
		});

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
		out.close();
	}
	
	

	public static TrackFile constructByNetwork(TrackNetwork network) {
		return new TrackFile(network);
	}
}
