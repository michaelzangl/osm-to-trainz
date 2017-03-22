package data.height;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class SRTMDatapack {
	/**
	 * How big is a tile in x or y direction, not including the border.
	 */
	private static final int TILESIZE = 1200;
	private static final int DATASIZE = (TILESIZE + 1) * (TILESIZE + 1);
	private final short[] data;
	private final SRTMDatapackPosition position;

	private SRTMDatapack(SRTMDatapackPosition position, short[] data) {
		this.position = position;
		this.data = data;
		assert data == null || data.length == DATASIZE;
	}

	public float getData(float lat, float lon) {
		if (data == null) {
			return 0.0f;
		}
		
		float deast = (lon - position.getLon()) * TILESIZE;
		int tileeast = (int) Math.floor(deast);
		float tileEastPart = deast - tileeast;
		float dnorth = (lat - position.getLat()) * TILESIZE;
		int tilenorth = (int) Math.floor(dnorth);
		float tileNorthPart = dnorth - tilenorth;

		if (tileeast < 0 || tileeast >= TILESIZE || tilenorth < 0
		        || tilenorth >= TILESIZE) {
			throw new IllegalArgumentException(
			        "Coordinates are outside this tile");
		}

		float height00 = getDataXY(tilenorth, tileeast);
		float height10 = getDataXY(tilenorth + 1, tileeast);
		float height01 = getDataXY(tilenorth, tileeast + 1);
		float height11 = getDataXY(tilenorth + 1, tileeast + 1);

		float height0 = height00 * (1 - tileEastPart) + height01 * tileEastPart;
		float height1 = height10 * (1 - tileEastPart) + height11 * tileEastPart;

		return height0 * (1 - tileNorthPart) + height1 * tileNorthPart;
	}

	private float getDataXY(int tilenorth, int tileeast) {
		return data[(TILESIZE - tilenorth) * (TILESIZE + 1) + tileeast];
	}

	public static SRTMDatapack readFile(SRTMDatapackPosition position, File file)
	        throws IOException {
		int buffersize = DATASIZE * 2;
		ByteBuffer buffer = ByteBuffer.allocate(buffersize);
		FileInputStream in = new FileInputStream(file);
		int realyRead = in.read(buffer.array());
		in.close();

		if (realyRead != buffersize) {
			throw new IOException("Did not read enough bytes! (File to short)");
		}
		ShortBuffer sbuffer =
		        buffer.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
		short[] shorts = new short[DATASIZE];
		sbuffer.rewind();
		sbuffer.get(shorts);
		return new SRTMDatapack(position, shorts);
	}

	public static SRTMDatapack emptyPack(SRTMDatapackPosition position) {
		return new SRTMDatapack(position, null);
	}
}
