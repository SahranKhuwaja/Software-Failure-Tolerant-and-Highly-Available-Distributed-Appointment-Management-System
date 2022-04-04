package DAMS.Replicas.Replica1.Servers;


public class ActivateAllServers {

	public static void main(String args[]) {
		MontrealServer.initServer();
		QuebecServer.initServer();
		SherbrookeServer.initServer();
	}

}
