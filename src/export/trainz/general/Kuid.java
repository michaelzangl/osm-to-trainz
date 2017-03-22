package export.trainz.general;

public class Kuid {

	private final int part1;
	private final int part2;

	public Kuid(int part1, int part2) {
		this.part1 = part1;
		this.part2 = part2;
	}

	/**
	 * Checks against kuids and KuidObject
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof KuidObject) {
			return this.equals(((KuidObject) obj).getKuid());
		} else if (obj instanceof Kuid) {
			Kuid kuid = (Kuid) obj;
			return part1 == kuid.part1 && part2 == kuid.part2;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.part1 + this.part2 << 16 + this.part2 >> 16;
	}

	public int getPart1() {
		return part1;
	}

	public int getPart2() {
		return part2;
	}

	@Override
	public String toString() {
		return "<kuid:" + part1 + ":" + part2 + ">";
	}
}
