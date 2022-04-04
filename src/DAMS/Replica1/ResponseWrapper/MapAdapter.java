package DAMS.Replica1.ResponseWrapper;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class MapAdapter extends XmlAdapter<CutomHashMap[], Map<String, String>> {
	public MapAdapter() {

	}

	public CutomHashMap[] marshal(Map<String, String> data) throws Exception {
		CutomHashMap[] elements = new CutomHashMap[data.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : data.entrySet())
			elements[i++] = new CutomHashMap(entry.getKey(), entry.getValue());

		return elements;
	}

	public Map<String, String> unmarshal(CutomHashMap[] data) throws Exception {
		Map<String, String> hashData = new HashMap<String, String>();
		for (CutomHashMap element : data)
			hashData.put(element.key, element.value);
		return hashData;
	}
}
