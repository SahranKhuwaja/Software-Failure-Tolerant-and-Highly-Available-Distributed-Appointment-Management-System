package DAMS.RM;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import DAMS.Replicas.Replica1.Servers.MontrealServer;
import DAMS.Replicas.Replica1.Servers.QuebecServer;
import DAMS.Replicas.Replica1.Servers.SherbrookeServer;

/**
 * Class to handle the restart of servers.
 * Attach this class to each replica and modify the "restartReplica" method to start
 * your server processes.
 *
 */
public class ReplicaActivator {

  public static void main(String[] args) {
    restartReplica();
    try {
      // TODO: port to receive failure detection message
      DatagramSocket udpSocket = new DatagramSocket(9999); // port to receive restart request
      while (true) {
        byte[] buf = new byte[1023];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        udpSocket.receive(packet);
        restartReplica();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * TODO: start the server processes here
   */
  public static void restartReplica() {
    MontrealServer.initServer();
    QuebecServer.initServer();
    SherbrookeServer.initServer();
  }
}
