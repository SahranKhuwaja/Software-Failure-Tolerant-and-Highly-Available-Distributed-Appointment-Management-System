package DAMS.Clients.Patients;

import DAMS.Clients.Operations.Operations;
import DAMS.Clients.WebService.WebServiceClientOperations;

public class PatientClient {
	public static void main(String[] args) {
		while (true) {
			Operations dT = new Operations("Patient");
			String serverName = dT.askUserId();
			WebServiceClientOperations op = new WebServiceClientOperations("Patient", dT);
			op.lookup(dT.getIP(), dT.getPort(),serverName);
			if (Operations.userId != null) {
				op.isAuthenticated(Operations.userId);
			} else {
				op.generateNewID();
			}
		}
	}
}
