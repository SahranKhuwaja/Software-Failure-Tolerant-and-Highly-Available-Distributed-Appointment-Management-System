package DAMS.Replicas.Replica1.ResponseWrapper;

import javax.xml.bind.annotation.XmlAttribute;

class CutomHashMap {
	@XmlAttribute
	public String key;
	@XmlAttribute
	public String value;

	public CutomHashMap() {
		
	}
	public CutomHashMap(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
