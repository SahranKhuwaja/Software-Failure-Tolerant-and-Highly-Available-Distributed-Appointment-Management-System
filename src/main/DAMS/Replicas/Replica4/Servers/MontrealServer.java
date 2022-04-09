package DAMS.Replicas.Replica4.Servers;

import java.net.DatagramSocket;
import java.net.SocketException;

import DAMS.Replicas.Replica4.RemoteDistributedAppointment.RemoteDistributedAppointment;
import DAMS.Replicas.Replica4.UDP.IPCReply;
import DAMS.Replicas.Replica4.Binding.RemoteDistributedAppointmentBinding;

public class MontrealServer{

	public static void main(String[] args) {
		initServer();
	}
	
	public static void initServer() {
		RemoteDistributedAppointmentBinding op = new RemoteDistributedAppointmentBinding();
		int port = 6821;
		int UDPPort = port+10;
		String name = "Montreal (MTL)";
		RemoteDistributedAppointment rda = op.binding(port, name);
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket(UDPPort);
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		}
		new IPCReply(datagramSocket, name, UDPPort, rda);
	}
}

