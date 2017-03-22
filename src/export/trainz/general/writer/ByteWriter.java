package export.trainz.general.writer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This is a little endian byte writer
 * @author michael
 *
 */
public class ByteWriter extends OutputStream {
	private final OutputStream out;
	
	private final byte[] tempArray = new byte[8];

	public ByteWriter(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}
	
	/**
	 * Writes an int to the output stream.
	 * @param i The int to write
	 * @throws IOException 
	 */
	public void writeInt(int i) throws IOException {
		tempArray[0] = (byte) i;
		tempArray[1] = (byte) (i >> 8);
		tempArray[2] = (byte) (i >> 16);
		tempArray[3] = (byte) (i >> 24);
		write(tempArray, 0, 4);
	}
	
	public void writeFloat(float f) throws IOException {
		writeInt(Float.floatToIntBits(f));
	}
}
