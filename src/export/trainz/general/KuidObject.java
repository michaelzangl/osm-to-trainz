package export.trainz.general;


public class KuidObject {
	private Kuid kuid;
	
	public KuidObject(Kuid kuid) {
		setKuid(kuid);
	}

	public Kuid getKuid() {
		return kuid;
	}

	public void setKuid(Kuid kuid) {
		if (kuid == null) {
			throw new NullPointerException();
		}
		this.kuid = kuid;
	}
	
	@Override
	public boolean equals(Object obj) {
		return kuid.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return kuid.hashCode();
	}
	
	@Override
	public String toString() {
	    return "Object[" + kuid.toString() + "]";
	}
}
