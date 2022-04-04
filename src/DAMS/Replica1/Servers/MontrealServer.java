package DAMS.Replica1.Servers;

import java.net.DatagramSocket;
import java.net.SocketException;

import DAMS.Replica1.RemoteDistributedAppointment.RemoteDistributedAppointment;
import DAMS.Replica1.UDP.IPCReply;
import DAMS.Replica1.Binding.RemoteDistributedAppointmentBinding;
import DAMS.Replica1.UDP.IPCReplyToFE;

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

