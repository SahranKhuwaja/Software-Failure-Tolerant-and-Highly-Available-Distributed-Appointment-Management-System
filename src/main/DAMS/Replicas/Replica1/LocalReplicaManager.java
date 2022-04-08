package DAMS.Replicas.Replica1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

import DAMS.Replicas.Replica1.AppointmentSlots.AppointmentSlot;
import DAMS.Replicas.Replica1.Servers.MontrealServer;
import DAMS.Replicas.Replica1.Servers.QuebecServer;
import DAMS.Replicas.Replica1.Servers.SherbrookeServer;

/**
 * Class to handle the restart of servers.
 */
public class LocalReplicaManager implements Runnable {

    private final int port = 6921;
    private DatagramSocket recoverSocket;

    public LocalReplicaManager() {
        try {
            this.recoverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startServers();
        while (true) {
            HashMap<String, HashMap<String, AppointmentSlot>>[] data = receiveRecoverRequest();
            startServers();
            loadDataToServers(data);
        }

    }

    private HashMap<String, HashMap<String, AppointmentSlot>>[] receiveRecoverRequest() {
        byte[] buf = new byte[32767];
        try {
            DatagramPacket udpPacket = new DatagramPacket(buf, buf.length);
            recoverSocket.receive(udpPacket);
            byte[] data = udpPacket.getData();
            ObjectInputStream objectInputStream =
                    new ObjectInputStream(new ByteArrayInputStream(data));
            return (HashMap<String, HashMap<String, AppointmentSlot>>[]) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void restoreReplicaWithDataFrom(int replica) {
        byte[] buf = new byte[32767];
    }

    private static void startServers() {
        MontrealServer.initServer();
        QuebecServer.initServer();
        SherbrookeServer.initServer();
    }

    // The data can be in a format of an array of type HashMap<String, HashMap<String, Appointment>> of size 3
    private void loadDataToServers(HashMap<String, HashMap<String, AppointmentSlot>>[] data) {
        // load data[0] to mon server
        // load data[1] to que server
        // load data[2] to she server
    }

    public static void main(String[] args) {
        new LocalReplicaManager().run();
    }

}
