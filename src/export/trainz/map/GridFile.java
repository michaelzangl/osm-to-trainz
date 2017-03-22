package export.trainz.map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import export.ground.TextureLink;
import export.trainz.general.writer.ByteWriter;


/**
 * This class represents the tile grid file, which holds textures and heights.
 * <p>
 * It is mainly backed by a tile grid.
 * </p>
 * <h2>Grid coordinates</h2> The grid is divided into blocks. Each block has a
 * integer coordinate.
 * <p>
 * The blocks are aranged like:
 * </p>
 * <code>
 *      |   ...    |   ...    |   ...    |
 *   ---+----------+----------+----------+--- 
 *  ... | (-1, -1) | (-1, 0)  | (-1, 1)  | ...
 *   ---+----------+----------+----------+--- 
 *  ... |  (0, -1) |  (0, 0)  |  (0, 1)  | ...
 *   ---+----------+----------+----------+--- 
 *  ... |  (1, -1) |  (1, 0)  |  (1, 1)  | ...
 *   ---+----------+----------+----------+--- 
 *      |   ...    |   ...    |   ...    |
 * </code> Each tile consists of 76 * 76 data points (indexed form 0 to 75). <br>
 * Tile indexes increase east- and southwards. <br>
 * row/column 0 is always 0. <br>
 * row/column 1 is overlapping from the neighboring tile. ¹ <br>
 * row/column 2 exactly the border to the other tile. <br>
 * row/column 74 exactly the border to the other tile. <br>
 * row/column 75 is overlapping from the neighboring tile. ¹
 * <p>
 * ¹ If there is no such tile, the height defaults to 0.
 * </p>
 * <h2>gnd files</h2>
 * <p>
 * (all numbers little endian).
 * </p>
 * <code>
 * File identifier:
 *  -> 3 byte: "GND"
 *  -> 1 Byte
 *  -> 4 Byte: number of grid segments
 *  For each segment:
 *      -> 4 Byte: x position as int, 0 is start tile
 *      -> 4 Byte: y position as int, 0 is start tile
 *      -> 4 Byte: start address of texture map header
 *      -> 4 Byte: exact start address of texture map image data header.
 *  -> 4 Byte: small number, 7 (?)
 *  -> 4 Byte Kuid part 2 (of the sky)
 *  -> 4 Byte Kuid part 1
 *  -> 4 Byte: 0
 *  -> 4 Byte: a float (0 < x < 1?)
 *  -> 4 Byte: a float (0 < x < 1?)
 *  -> 12 Byte: 0
 *  Multiple floats, here a list (each 4 Bytes): (maybe Sorveyor settings)
 *      1, 0.4, 0.4, 0.6, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0.8, 0.8, 1, 1, 0, 0, 0, 1, 0.76, 0.6, 0.6, 0.6, 1, 0.6, 0.5, 0.4, 1, 0, 0, 0, 0, 0, 1, 0.8, 0.7, 1, 0.8, 0.8, 1,1,0.5, 0.4, 0.3, 1, 0.78, 1, 1, 1, 1, 1, 0.9, 0.7, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0.8, 0.8, 1, 1, 0.7, 0.8, 0.9, 1, 0, 1,1,1,1,1,1,1,1,0,0,0,0,0,1, 1, 1, 1, 0.8, 0.8, 1, ...
 *    All in all, 183 numbers
 * 
 *  -> 4 Byte: -1
 *  -> 4 Byte: 4
 *  -> 4 Byte: -1
 *  -> 4 Byte: 12
 *  -> 4 Byte, (0xEB, 0xCE, 0xED, 0xFE)
 *  -> 4 Byte: 1
 *  -> 4 Byte: -1
 *  
 *  Texture block
 *  -> 4 Byte: Texture slot count (always 250)
 * 250 times texture slot:
 *    -> 4 Byte: a float stating how many points in the final file use that texture (including invisible points)
 *    -> 4 Byte kuid part2
 *    -> 4 Byte kuid part 1
 * 
 *  
 * Texture and height data:
 *  -> 4 Byte: 0
 *  : For all 76 columns, ordered from west to east:
 *     : for all 76 rows, ordered form north to south.
 *         (Texture definition, see below)
 * 
 * 
 * Image Data:
 *  -> 18 Byte image data header (?)
 * 65536 Bytes of map image data (only ground color):
 *  : For 128 rows:
 *     : For 128 Colums:
 *        -> 1 Byte red
 *        -> 1 byte green
 *        -> 1 byte blue
 *        -> 1 byte 0x00
 * </code> There are 3 methods to define a textured point:
 * <p>
 * 1: <code>
 *  -> 1 Byte: 0xfe 
 *  -> 1 Byte: texture id of the tile. Normally 0.
 *  -> 1 Byte: Rotation/scale
 * </code>
 * <p>
 * 2: <code>
 *  -> 1 Byte: 0xfd
 *  -> 1 Byte: texture id of the tile.
 *  -> 1 Byte: Rotation/scale
 *  -> 4 Byte: height as float
 *  -> 2 Byte: 0x0000
 *  -> 1 Byte: 0x7f
 *  
 * </code>
 * <p>
 * 3: <code>
 *  For all 4 texture slots
 *     -> 1 Byte: texture id of first texture.
 *     -> 1 Byte: 8 bit unsigned int that specifies the blendpart of the 
 *                texture.
 *     -> 1 Byte: Rotation/scale data
 *  -> 4 Byte: height as float
 *  -> 1 Byte: 0x7f
 * </code>
 * The sum of all blendparts must be 255;
 * <p>
 * <h2> Rotation/scale </h2>
 * TODO :-(. just use 0x00
 * 
 * @author michael
 */

public class GridFile {
	private static final int TEXTURES = 250;
	private final MapGrid grid;

	private static final byte[] SETTINGS_HEADER = new byte[] {
	        (byte) 0x07,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0xBE,
	        (byte) 0xAD,
	        (byte) 0x01,
	        (byte) 0x00,
	        (byte) 0xff,
	        (byte) 0xff,
	        (byte) 0xff,
	        (byte) 0xff,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0xAB,
	        (byte) 0xAA,
	        (byte) 0x6A,
	        (byte) 0x3F,
	        (byte) 0xEC,
	        (byte) 0x51,
	        (byte) 0x38,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0xCC,
	        (byte) 0x3E,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0xCC,
	        (byte) 0x3E,
	        (byte) 0x9A,
	        (byte) 0x99,
	        (byte) 0x19,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x5C,
	        (byte) 0x8F,
	        (byte) 0x42,
	        (byte) 0x3F,
	        (byte) 0x9A,
	        (byte) 0x99,
	        (byte) 0x19,
	        (byte) 0x3F,
	        (byte) 0x9A,
	        (byte) 0x99,
	        (byte) 0x19,
	        (byte) 0x3F,
	        (byte) 0x9A,
	        (byte) 0x99,
	        (byte) 0x19,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x9A,
	        (byte) 0x99,
	        (byte) 0x19,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0xCC,
	        (byte) 0x3E,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0xCC,
	        (byte) 0x3E,
	        (byte) 0x9A,
	        (byte) 0x99,
	        (byte) 0x99,
	        (byte) 0x3E,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x14,
	        (byte) 0xAE,
	        (byte) 0x47,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x66,
	        (byte) 0x66,
	        (byte) 0x66,
	        (byte) 0x3F,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x66,
	        (byte) 0x66,
	        (byte) 0x66,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x66,
	        (byte) 0x66,
	        (byte) 0x66,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3E,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x66,
	        (byte) 0x66,
	        (byte) 0x66,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x66,
	        (byte) 0x66,
	        (byte) 0x66,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x71,
	        (byte) 0x3D,
	        (byte) 0x8A,
	        (byte) 0x3E,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x3F,
	        (byte) 0x9A,
	        (byte) 0x99,
	        (byte) 0x19,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x33,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0xCC,
	        (byte) 0x3E,
	        (byte) 0x9A,
	        (byte) 0x99,
	        (byte) 0x99,
	        (byte) 0x3E,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xE1,
	        (byte) 0x7A,
	        (byte) 0x94,
	        (byte) 0x3E,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0xCC,
	        (byte) 0x3E,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0xCC,
	        (byte) 0x3E,
	        (byte) 0x9A,
	        (byte) 0x99,
	        (byte) 0x19,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x80,
	        (byte) 0x3F,
	        (byte) 0x8D,
	        (byte) 0x8C,
	        (byte) 0x8C,
	        (byte) 0x3E,
	        (byte) 0x88,
	        (byte) 0x87,
	        (byte) 0x07,
	        (byte) 0x3F,
	        (byte) 0x8D,
	        (byte) 0x8C,
	        (byte) 0x0C,
	        (byte) 0x3F,
	        (byte) 0xCD,
	        (byte) 0xCC,
	        (byte) 0x4C,
	        (byte) 0x3F,
	        (byte) 0xC6,
	        (byte) 0x18,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0xFF,
	        (byte) 0xFF,
	        (byte) 0xFF,
	        (byte) 0xFF,
	        (byte) 0x04,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0xFF,
	        (byte) 0xFF,
	        (byte) 0xFF,
	        (byte) 0xFF,
	        (byte) 0x0C,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0xEB,
	        (byte) 0xCE,
	        (byte) 0xED,
	        (byte) 0xFE,
	        (byte) 0x01,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0x00,
	        (byte) 0xFF,
	        (byte) 0xFF,
	        (byte) 0xFF,
	        (byte) 0xFF
	};

	public GridFile(MapGrid grid) {
		this.grid = grid;
	}

	/**
	 * TODO: grid may not be modified during this operation.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void writeToFile(File file) throws IOException {

		TextureSlotMapper mapper = new TextureSlotMapper();
		List<SegmentData> blocks = new LinkedList<SegmentData>();
		for (MapGridPart part : grid.getParts()) {
			blocks.add(new SegmentData(part, mapper));
		}
		if (blocks.isEmpty()) {
			throw new IOException("Grid output file would be empty!");
		}

		// texture block after converting grid parts!
		byte[] textures = getTexturesBlock(mapper);
		byte[] settings = getSettingsBlock();

		// relative to start of settings
		int currentStart = textures.length + settings.length;
		for (SegmentData block : blocks) {
			block.setStart(currentStart);
			currentStart += block.getLength();
		}

		byte[] header = getBlockTable(blocks);

		ByteWriter out = new ByteWriter(new FileOutputStream(file));
		out.write(header);
		out.write(settings);
		out.write(textures);
		for (SegmentData blockData : blocks) {
			out.write(blockData.tileData);
			out.write(blockData.imageData);
		}
	}

	private byte[] getBlockTable(List<SegmentData> blocks) {
		byte[] array = new byte[8 + 16 * blocks.size()];
		ByteBuffer buffer = ByteBuffer.wrap(array);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(new byte[] {
		        'G', 'N', 'D', 0x0b
		});
		buffer.putInt(blocks.size());

		for (SegmentData blockData : blocks) {
			buffer.putInt(blockData.part.getX());
			buffer.putInt(blockData.part.getY());
			buffer.putInt(array.length + blockData.getStart());
			buffer.putInt(array.length + blockData.getImageStart());
		}

		return array;
	}

	private class SegmentData {
		private final byte[] tileData;
		private final byte[] imageData;
		private int start;
		private final MapGridPart part;

		public SegmentData(MapGridPart part, TextureSlotMapper mapper) {
			this.part = part;
			tileData = part.getTileData(mapper);
			imageData = part.getPreviewImage();
		}

		public int getLength() {
			return tileData.length + imageData.length;
		}

		/**
		 * Gets the start position of this block relative to the start of the
		 * settings block.
		 * 
		 * @return The start address.
		 */
		public int getStart() {
			return start;
		}

		public int getImageStart() {
			return start + tileData.length;
		}

		public void setStart(int start) {
			this.start = start;
		}

	}

	private byte[] getSettingsBlock() {
		return SETTINGS_HEADER;
	}

	private byte[] getTexturesBlock(TextureSlotMapper mapper) {
		byte[] array = new byte[4 + 12 * TEXTURES];
		ByteBuffer buffer = ByteBuffer.wrap(array);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		buffer.putInt(TEXTURES);
		for (int i = 0; i < TEXTURES; i++) {
			TextureLink texture = mapper.getSlot(i);
			float usage = mapper.getTextureUsage(texture);
			buffer.putInt(Float.floatToIntBits(usage));
			buffer.putInt(texture.getKuid().getPart2());
			buffer.putInt(texture.getKuid().getPart1());
		}

		return array;
	}

	public static GridFile constructByGrid(MapGrid grid) {
		return new GridFile(grid);
	}
}
