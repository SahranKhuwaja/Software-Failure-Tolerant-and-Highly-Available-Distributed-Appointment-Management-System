package DAMS.Replicas.Replica4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
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

    private final int recoverRequestPort = 6921;
    private DatagramSocket recoverSocket;

    private final int getAllDataRequestPort = 6931;
    private DatagramSocket getAllDataSocket;

    private final String[] localRmIps = { "X.X.X.X", "X.X.X.X", "X.X.X.X", "X.X.X.X" };
    private final int[] localRmPorts = { 6921, 6922, 6923, 6924 };

    public LocalReplicaManager() {
        try {
            this.recoverSocket = new DatagramSocket(recoverRequestPort);
            this.getAllDataSocket = new DatagramSocket(getAllDataRequestPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startServers();
        Runnable listenGetAllDataRequest = () -> {
            while (true) {
                int targetReplicaId = receiveGetAllDataRequest();
                byte[] data = getDataFromServers();
                sendDataToReplica(data, targetReplicaId);
            }
        };

        Runnable listenRecoverRequest = () -> {
            while (true) {
                HashMap<String, HashMap<String, AppointmentSlot>>[] data = receiveRecoverRequest();
                startServers();
                loadDataToServers(data);
            }
        };

        listenGetAllDataRequest.run();
        listenRecoverRequest.run();


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

    private int receiveGetAllDataRequest() {
        int targetReplicaId = -1;
        try {
            DatagramPacket p = new DatagramPacket(new byte[4], 4);
            getAllDataSocket.receive(p);
            targetReplicaId = (int) p.getData()[3] & 0xff;
            return targetReplicaId;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetReplicaId;
    }

    private void sendDataToReplica(byte[] data, int targetReplicaId) {
        try {
            DatagramSocket udpSocket = new DatagramSocket();
            DatagramPacket dataPacket = new DatagramPacket(
                    data,
                    data.length,
                    new InetSocketAddress(localRmIps[targetReplicaId], localRmPorts[targetReplicaId]));
            udpSocket.send(dataPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // The data can be in a format of an array of type HashMap<String, HashMap<String, Appointment>> of size 3
    private void loadDataToServers(HashMap<String, HashMap<String, AppointmentSlot>>[] data) {
        // load data[0] to mon server
        // load data[1] to que server
        // load data[2] to she server
    }

    // TODO
    private byte[] getDataFromServers() {
        HashMap<String, HashMap<String, AppointmentSlot>>[] maps = new HashMap[3];
        // maps[0] <- data from montreal server
        // maps[1] <- data from quebec server
        // maps[2] <- data from sherbrooke server
        return toByteArray(maps);

    }

    private byte[] toByteArray(Object obj) {
        byte[] message = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream.flush();
            message = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    private static void startServers() {
        MontrealServer.initServer();
        QuebecServer.initServer();
        SherbrookeServer.initServer();
    }


    public static void main(String[] args) {
        new LocalReplicaManager().run();
    }

}
