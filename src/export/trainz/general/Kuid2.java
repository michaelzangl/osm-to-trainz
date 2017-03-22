package export.trainz.general;

public class Kuid2 extends Kuid {

	public Kuid2(int part1, int part2, int part3) {
		super(part1 + part3 << 25, part2);
	}

	@Override
	public String toString() {
		int realPart1 = getPart1() & 0x01ffffff;
		int realPart3 = getPart1() >> 25;
		return "<kuid:" + realPart1 + ":" + getPart2() + ":" + realPart3 + ">";
	}
}
