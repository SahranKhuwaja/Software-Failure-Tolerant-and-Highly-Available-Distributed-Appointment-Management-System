package DAMS.Clients.Admins;

import DAMS.Clients.Operations.Operations;
import DAMS.Clients.WebService.WebServiceClientOperations;

public class AdminClient {
	public static void main(String[] args) {
		while (true) {
			Operations dT = new Operations("Admin");
			String serverName = dT.askUserId();
			WebServiceClientOperations op = new WebServiceClientOperations("Admin", dT);
			op.lookup(dT.getIP(), dT.getPort(), serverName);
			System.out.println();
			if (Operations.userId != null) {
				op.isAuthenticated(Operations.userId);
			}
//			else {
//				op.generateNewID();
//			}
		}
	}
}
