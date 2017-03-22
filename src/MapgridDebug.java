import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import export.trainz.general.reader.ByteReader;


public class MapgridDebug {
	private static final String FILE = "/media/2F35-CAAF/mapfile.gnd";

	/**
	 * Liest die Textur-Punkte richtig aus gnd files.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws FileNotFoundException,
	        IOException {
		ByteReader reader = new ByteReader(new FileInputStream(FILE));

		reader.skipTo(4);
		int count = reader.read32();
		int[] startAddresses = new int[count];
		int[][] positions = new int[count][2];
		int[] endAddresses = new int[count];
		for (int i = 0; i < count; i++) {
			positions[i][0] = reader.read32();
			positions[i][1] = reader.read32();
			startAddresses[i] = reader.read32();
			endAddresses[i] = reader.read32();
		}

		System.out.println("Found " + count + " blocks");

		for (int i = 0; i < count; i++) {
			System.out.println("Found map height/texture block "
			        + positions[i][0] + "," + positions[i][1] + ": "
			        + startAddresses[i] + "-" + endAddresses[i]);
			int entries = 0;

			reader.skipTo(startAddresses[i]);
			reader.read32();
			while (reader.available()
			        && reader.getReadBytes() < endAddresses[i]) {

				System.out.print("(" + (entries / 76) + "," + entries % 76
				        + ") ");
				int nextByte = reader.read8();
				if (nextByte == 0xfe) {
					// simple point
					int textureid = reader.read8();
					int rotation = reader.read8();
					System.out.println("Found simple tile: Textureid: "
					        + textureid + " rot: " + rotation + " at "
					        + reader.getReadBytes());
					entries++;
				} else if (nextByte == 0xfd) {
					// single texture point
					int textureid = reader.read8();
					reader.skip(1);
					float height = Float.intBitsToFloat(reader.read32());
					reader.skip(3);
					System.out.println("Found tile with texture " + textureid
					        + ", height " + height + " at "
					        + reader.getReadBytes());
					entries++;
				} else {
					// multi texture point
					int firstTextureid = nextByte;
					int firstBlendpart = reader.read8();
					int firstRot = reader.read8();
					int secondTextureId = reader.read8();
					int secondBlendpart = reader.read8();
					int secondRot = reader.read8();
					int thirdTextureId = reader.read8();
					int thirdBlendpart = reader.read8();
					int thirdRot = reader.read8();
					int fourthTextureId = reader.read8();
					int fourthBlendpart = reader.read8();
					int fourthRot = reader.read8();
					// firstBlendpart + secondBlendpart == 31999!
					reader.skip(8);
					System.out.println("Found tile with texture "
					        + firstTextureid + "(" + firstBlendpart + "%)/"
					        + secondTextureId + "(" + secondBlendpart
					        + "%)/"
					        + thirdTextureId + "(" + thirdBlendpart
					        + "%)/"
					        + fourthTextureId + "(" + fourthBlendpart
					        + "%) rot: " + firstRot + "/" + secondRot + " at "
					        + reader.getReadBytes());
					entries++;
				}
			}

			System.out.println("read " + entries + " points");
		}
	}
}
