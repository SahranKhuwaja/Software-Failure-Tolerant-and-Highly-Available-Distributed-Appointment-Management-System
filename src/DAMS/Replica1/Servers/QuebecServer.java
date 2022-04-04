package DAMS.Replica1.Servers;

import java.net.DatagramSocket;
import java.net.SocketException;

import DAMS.Replica1.RemoteDistributedAppointment.RemoteDistributedAppointment;
import DAMS.Replica1.UDP.IPCReply;
import DAMS.Replica1.Binding.RemoteDistributedAppointmentBinding;

public class QuebecServer {
	
	public static void main(String[] args) {
		initServer();
	}
	
	public static void initServer() {
		RemoteDistributedAppointmentBinding op = new RemoteDistributedAppointmentBinding();
		int port = 6822;
		int UDPPort = port+10;
		String name = "Quebec (QUE)";
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
