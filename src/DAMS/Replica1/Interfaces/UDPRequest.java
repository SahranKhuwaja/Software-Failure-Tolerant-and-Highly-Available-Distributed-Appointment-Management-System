package DAMS.Replica1.Interfaces;

import java.util.HashMap;

public interface UDPRequest {
	public HashMap<String, String> sendRequestAndGetReply(String serverName, int port, String operation,
			String appointmentType, String patientID, String appointmentID);
}
