package export.trainz.general.reader;

import java.io.IOException;
import java.util.Properties;

import export.trainz.general.Kuid;


public class CDPFile {
	private final Properties properties;

	private CDPFile(Properties properties) {
		this.properties = properties;

	}

	public static CDPFile read(ByteReader stream) throws IOException {
		readHeader(stream);
		Properties props = readProperties(stream);
		return new CDPFile(props);
	}

	private static void readHeader(ByteReader stream) throws IOException {
		stream.assumeToRead(new byte[] { 'A', 'C', 'S', '$' });
		stream.read32();
		stream.read32();
		stream.read32();
	}

	private static Properties readProperties(ByteReader stream) throws IOException {
		Properties props = new Properties();
		while (stream.available()) {
			int overallLength = stream.read32();
			System.out.println("Start reading property at " + stream.getReadBytes() + ", length:" + overallLength);
			int keyLength = stream.read8();

			String key = stream.readTerminatedString(keyLength);

			int valuetype = stream.read8();
			if (valuetype == 0) {
				// null
				props.put(key, "");
				System.out.println("Found null property: " + key + "=null");

			} else if (valuetype == 5) {
				// unset string
				props.put(key, "");
				System.out.println("Found null property: " + key + "=null");

			} else if (valuetype == 3) {
				// string
				String value = stream.readTerminatedString(100000);
				props.put(key, value);
				//TODO: there might be empty strings! they are not null-terminated
				System.out.println("Found string property: " + key + "=" + value);

			} else if (valuetype == 13) {
				// kuid
				int part1 = stream.read32();
				int part2 = stream.read32();
				props.put(key, new Kuid(part1, part2));
				System.out.println("Found kuid property: " + key + "=" + part1 + ":" + part2);

			} else if (valuetype == 1) { // type 2 used for build
				// 32 bit int
				int value = stream.read32();
				System.out.println("Found int property: " + key + "=" + value);

			} else if (valuetype == 4) {
				//file
				int length = overallLength - keyLength - 2;
				System.out.println("Found file: " + key + ", length:" + length);
				stream.skipTo(stream.getReadBytes() + length);

			} else if (valuetype == 2) {
				//unknown
				int length = overallLength - keyLength - 2;
				System.out.println("Found unknown section: " + key + ", length:" + overallLength);
				stream.skipTo(stream.getReadBytes() + length);

			} else {
				System.err.println("Unknown value type " + valuetype + " for key " + key);
				break;
			}
		}
		return props;
	}

	public Properties getProperties() {
		return properties;
	}
}
