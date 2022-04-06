package DAMS.ResponseWrapper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.HashMap;

@XmlRootElement
public class ResponseWrapper implements Serializable {

	
	private static final long serialVersionUID = 1L;
	@XmlJavaTypeAdapter(MapAdapter.class)
	HashMap<String,String> hashData;
	String message;
	int replica;
	public ResponseWrapper() {
		
	}
	
	public ResponseWrapper(HashMap<String, String> data) {
		super();
		hashData = data;
	}

	public ResponseWrapper(String message){
		super();
		this.message = message;
	}


	public HashMap<String, String> getData() {
		return hashData;
	}

	public void setData(HashMap<String, String> data) {
		hashData = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getReplica() {
		return replica;
	}

	public void setReplica(int replica) {
		this.replica = replica;
	}
}
