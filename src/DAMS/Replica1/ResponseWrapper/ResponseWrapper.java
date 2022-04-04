package DAMS.Replica1.ResponseWrapper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.HashMap;

@XmlRootElement
public class ResponseWrapper implements Serializable {

	
	private static final long serialVersionUID = 1L;
	@XmlJavaTypeAdapter(MapAdapter.class)
	HashMap<String,String> hashData;
	
	public ResponseWrapper() {
		
	}
	
	public ResponseWrapper(HashMap<String, String> data) {
		super();
		hashData = data;
	}

	public HashMap<String, String> getData() {
		return hashData;
	}

	public void setData(HashMap<String, String> data) {
		hashData = data;
	}
	
	
	
}
