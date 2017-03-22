package export.trainz.map;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashSet;

import conversion.landscape.LandscapeTextureProvider;
import data.height.LocalHeightDataProvider;
import data.position.local.LocalPoint;
import export.ground.TextureLink;
import export.trainz.general.Kuid;

/**
 * This is a block (720 x 720m), divided in 10m points.
 * 
 * @author michael
 */
public class MapGridPart {

	private static final int TEXTURE_PART_SUM = 255;
	private static final byte ROT_SETTING = (byte) 0x00;
	private static final int SIZE = 73;
	private static final int MARGIN_TOP_LEFT = 2;
	private static final int MARGIN_BOTTOM_RIGHT = 1;
	private static final int DATASIZE = 76;
	private static final int GRIDSPACING = 10;

	private MapPoint[][] points = new MapPoint[DATASIZE][DATASIZE];
	private final int segmentx;
	private final int segmenty;
	private Color[][] colors = new Color[128][128];

	/**
	 * This is a part of the map grid.
	 * 
	 * @param segmentx
	 *            The x coordinate of the segment, as defined in
	 *            {@link GridFile}. It increases southwards.
	 * @param segmenty
	 *            The y coordinate of the segment, increases eastwards.
	 */
	public MapGridPart(int segmentx, int segmenty) {
		this.segmentx = segmentx;
		this.segmenty = segmenty;
		for (int x = 0; x < DATASIZE; x++) {
			for (int y = 0; y < DATASIZE; y++) {
				points[x][y] = new MapPoint();
			}
		}
	}

	public void loadHeightsForm(LocalHeightDataProvider provider) {
		// don't load first row/column
		for (int x = 1; x < DATASIZE; x++) {
			for (int y = 1; y < DATASIZE; y++) {
				loadHeightForm(provider, x, y);
			}
		}
	}

	private void loadHeightForm(LocalHeightDataProvider provider, int x, int y) {
		points[x][y]
		        .setHeight(provider.getHeight(getPositionOfGridpoint(x, y)));
	}

	public void loadTexturesFrom(LandscapeTextureProvider provider) {
		for (int x = 1; x < DATASIZE; x++) {
			for (int y = 1; y < DATASIZE; y++) {
				loadTexturesForm(provider, x, y);
			}
		}
		for (int x = 0; x < 128; x++) {
			for (int y = 0; y < 128; y++) {
				float lx =
				        segmentx * MapGrid.SEGMENTSIZE + 1 / 128f
				                * MapGrid.SEGMENTSIZE * x;
				float ly =
				        segmenty * MapGrid.SEGMENTSIZE + 1 / 128f
				                * MapGrid.SEGMENTSIZE * y;
				colors[x][y] = provider.getTextureAt(new LocalPoint(lx,
				        ly)).getColor();
			}
		}
	}

	private void loadTexturesForm(LandscapeTextureProvider provider, int x,
	        int y) {
		points[x][y].setTexture(provider.getTextureAt(getPositionOfGridpoint(x,
		        y)));
	}

	public LocalPoint getPositionOfGridpoint(int x, int y) {
		int worldx =
		        segmentx * MapGrid.SEGMENTSIZE + (x - MARGIN_TOP_LEFT)
		                * GRIDSPACING;
		int worldy =
		        segmenty * MapGrid.SEGMENTSIZE + (y - MARGIN_TOP_LEFT)
		                * GRIDSPACING;
		return new LocalPoint(worldx, worldy);
	}

	/**
	 * Writes the grid table to the output stream.
	 * <p>
	 * With two tiles margin top and left and one tile margin right and bottom.
	 * 
	 * @param outstream
	 *            The stream to write to.
	 * @param mapper
	 * @throws IOException
	 */
	public void writeToFile(OutputStream outstream, TextureSlotMapper mapper)
	        throws IOException {
		outstream.write(new byte[] {
		        0, 0, 0, 0
		});
		for (int y = 0; y < DATASIZE; y++) {
			for (int x = 0; x < DATASIZE; x++) {
				MapPoint point = points[x][y];
				if (point.getHeight() == 0) {
					writeSimpleTile(outstream, point, mapper);
				} else {
					writeTile(outstream, point, mapper);
				}
				mapper.increasTextureUsage(point.getTexture());
			}
		}

	}

	private void writeTile(OutputStream outstream, MapPoint point,
	        TextureSlotMapper mapper) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put((byte) 0xfd);
		buffer.put((byte) mapper.getTextureId(point.getTexture()));
		buffer.put(ROT_SETTING); // TODO: what's this

		buffer.putFloat(point.getHeight());

		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x7f);
		outstream.write(buffer.array());
	}

	private void writeCombined(OutputStream outstream, MapPoint point,
	        TextureSlotMapper mapper) throws IOException {
		TextureLink[] textures = new TextureLink[4];
		int[] usage = new int[4];
		// TODO: fill
		for (int i = 0; i < 4; i++) {
			outstream.write(mapper.getTextureId(textures[i]));
			outstream.write(ROT_SETTING);
			outstream.write(usage[i]);
		}
		// TODO: write height instead of 0
		outstream.write(new byte[] {
		        0x00, 0x00, 0x00, 0x00
		});
		outstream.write(0x7f);
	}

	private void writeSimpleTile(OutputStream outstream, MapPoint point,
	        TextureSlotMapper mapper) throws IOException {
		outstream.write(0xfe);
		outstream.write(mapper.getTextureId(point.getTexture()));
		outstream.write(ROT_SETTING);
	}

	/**
	 * Writes the 128px * 128px preview image with header.
	 * <p>
	 * Currently just fills the map green.
	 * 
	 * @param outstream
	 * @throws IOException
	 */
	public void writePreviewImage(OutputStream outstream) throws IOException {
		// TODO: find out if this header is right
		outstream.write(new byte[] {
		        0x00,
		        0x00,
		        0x02,
		        0x00,
		        0x00,
		        0x00,
		        0x00,
		        0x00,
		        0x00,
		        0x00,
		        0x00,
		        0x00,
		        (byte) 0x80,
		        0x00,
		        (byte) 0x80,
		        0x00,
		        0x20,
		        0x08
		});
		for (int x = 0; x < 128; x++) {
			for (int y = 0; y < 128; y++) {
				Color color = colors[x][y];
				if (color == null) {
					color = Color.BLACK;
				}
				outstream.write(color.getRed());
				outstream.write(color.getGreen());
				outstream.write(color.getBlue());
				outstream.write(0);
			}
		}
	}

	public byte[] getTileData(TextureSlotMapper mapper) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			writeToFile(out, mapper);
		} catch (IOException e) {
			// this should never happen...
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public byte[] getPreviewImage() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			writePreviewImage(out);
		} catch (IOException e) {
			// this should never happen...
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public boolean hasSameCoordinates(MapGridPart part) {
		return segmentx == part.segmentx && segmenty == part.segmenty;
	}

	public boolean hasCoordinates(int x, int y) {
		return segmentx == x && segmenty == y;
	}

	public int getX() {
		return segmentx;
	}

	public int getY() {
		return segmenty;
	}
	
	public int getMinWorldX() {
		return segmentx * MapGrid.SEGMENTSIZE;
	}
	public int getMinWorldY() {
		return segmentx * MapGrid.SEGMENTSIZE;
	}
	public int getMaxWorldX() {
		return (segmenty + 1) * MapGrid.SEGMENTSIZE;
	}
	public int getMaxWorldY() {
		return (segmenty + 1) * MapGrid.SEGMENTSIZE;
	}

	public Collection<Kuid> getUsedKuids() {
		HashSet<Kuid> used = new HashSet<Kuid>();

		for (int x = 0; x < DATASIZE; x++) {
			for (int y = 0; y < DATASIZE; y++) {
				MapPoint point = points[x][y];
				used.add(point.getTexture().getKuid());
			}
		}
		return used;
	}

}
