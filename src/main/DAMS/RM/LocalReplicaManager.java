package DAMS.RM;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import DAMS.Notification.Notification;
import DAMS.Replicas.Replica1.Servers.MontrealServer;
import DAMS.Replicas.Replica1.Servers.QuebecServer;
import DAMS.Replicas.Replica1.Servers.SherbrookeServer;

/**
 * Class to handle the restart of servers.
 */
public class LocalReplicaManager implements Runnable {

    private final int port = 9999;
    private DatagramSocket notificationSocket;
    private int errorCounter = 0;

    public LocalReplicaManager() {
        try {
            this.notificationSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        MontrealServer.initServer();
        QuebecServer.initServer();
        SherbrookeServer.initServer();
        while (true) {
            Notification notification = receiveNotification();
            assert notification != null;
            String failType = notification.getFailureType();
            List<Integer> failedReplicas = notification.getFailedReplicas();
            int goodReplica = 0;
            for (int i = 1; i <= 4; i++) {
                if (!failedReplicas.contains(i)) {
                    goodReplica = i;
                }
            }
            if ("crash".equals(failType)) {
                restoreReplicaWithDataFrom(goodReplica);
            } else if ("software failure".equals(failType)) {
                errorCounter++;
                if (errorCounter > 2) {
                    restoreReplicaWithDataFrom(goodReplica);
                }
            }
        }

    }

    private Notification receiveNotification() {
        byte[] buf = new byte[32767];
        try {
            DatagramPacket udpPacket = new DatagramPacket(buf, buf.length);
            notificationSocket.receive(udpPacket);
            byte[] notificationPayload = udpPacket.getData();
            ObjectInputStream objectInputStream =
                    new ObjectInputStream(new ByteArrayInputStream(notificationPayload));
            return (Notification) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: each replica should have an endpoint to share all of its data
    // The data can be in a format of an array of type HashMap<String, HashMap<String, Appointment>> of size 3
    // because there are 3 servers in each replica and each recovered server should be loaded different data.
    // It seems to be a lot of work here.
    public void restoreReplicaWithDataFrom(int replica) {
        byte[] buf = new byte[32767];
    }

    public static void main(String[] args) {
        new LocalReplicaManager().run();
    }

}
