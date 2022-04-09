package DAMS.RMs.RM1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

import DAMS.Notification.Notification;
import DAMS.Request.Request;

public class ReplicaManager implements Runnable {
    // TODO: multicast group message ip and port
    private final String groupIp = "230.0.0.1";
    private final int groupPort = 1421;


    // TODO: ip address and port of local replica manager
    private final int thisReplicaId = 0;
    private final String[] localRmIps = { "172.20.10.2", "172.20.10.4", "172.20.10.3", "172.20.10.5" };
    private final int[] localRmPorts = { 6921, 6922, 6923, 6924};

    // TODO: port to receive failure notification
    private final int failureDetectionPort = 2000 + thisReplicaId;
    private final int recoverDataPort = 3000 + thisReplicaId;

    private final PriorityQueue<Request> holdBackQueue;

    private MulticastSocket multicastSocket;
    private DatagramSocket notificationSocket;
    private DatagramSocket forwardNotificationSocket;
    private int nextSeqNum = 1;

    private int errorCounter = 0;

    public ReplicaManager() {
        this.holdBackQueue = new PriorityQueue<>(Comparator.comparingInt(Request::getSequenceNumber));
        try {
            this.multicastSocket = new MulticastSocket(groupPort);
            this.notificationSocket = new DatagramSocket(failureDetectionPort);
            this.forwardNotificationSocket = new DatagramSocket();
            InetAddress group = InetAddress.getByName(groupIp);
            multicastSocket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        Runnable listenNotification = () -> {
            while (true) {
                Notification notification = this.receiveNotification();
                System.out.println("RM " + (thisReplicaId + 1) + " Received failure notification");
                byte[] notificationBytes = toByteArray(notification);
                String failType = notification.getFailureType();
                List<Integer> failedReplicas = notification.getFailedReplicas();
                int goodReplica = 0;
                for (int i = 1; i <= 4; i++) {
                    if (!failedReplicas.contains(i)) {
                        goodReplica = i;
                    }
                }
                if ("Crash Failure".equals(failType)) {
                    // send data to local rm
                    byte[] data = getDataFromReplica(goodReplica);
                    DatagramPacket dataPacket = new DatagramPacket(
                            data,
                            data.length,
                            new InetSocketAddress(localRmIps[thisReplicaId], localRmPorts[thisReplicaId]));
                    try {
                        forwardNotificationSocket.send(dataPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if ("Software Failure".equals(failType)) {
                    errorCounter++;
                    if (errorCounter > 2) {
                        errorCounter = 0;
                        byte[] data = getDataFromReplica(goodReplica);
                        // send data to local rm
                        DatagramPacket dataPacket = new DatagramPacket(
                                data,
                                data.length,
                                new InetSocketAddress(localRmIps[thisReplicaId], localRmPorts[thisReplicaId]));
                        try {
                            forwardNotificationSocket.send(dataPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        Runnable listenRequest = () -> {
            while (true) {
                Request incomingRequest = this.receiveRequest();
                System.out.println("RM" + thisReplicaId + "receive request");
                holdBackQueue.add(incomingRequest);
                assert holdBackQueue.peek() != null;
                if (nextSeqNum == holdBackQueue.peek().getSequenceNumber()) {
                    System.out.println("RM " + thisReplicaId + ": sequence number matched");
                    deliverRequest(Objects.requireNonNull(holdBackQueue.poll()));
                    this.nextSeqNum++;
                }
            }

        };
        new Thread(listenNotification).start();
        new Thread(listenRequest).start();
    }

    private Request receiveRequest() {
        byte[] buf = new byte[32767];
        try {
            DatagramPacket udpPacket = new DatagramPacket(buf, buf.length);
            System.out.println("RM " + thisReplicaId + "Waiting for multicast request");
            multicastSocket.receive(udpPacket);
            byte[] responsePayload = udpPacket.getData();
            ObjectInputStream objectInputStream =
                    new ObjectInputStream(new ByteArrayInputStream(responsePayload));
            return (Request) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Notification receiveNotification() {
        byte[] buf = new byte[32767];
        try {
            DatagramPacket udpPacket = new DatagramPacket(buf, buf.length);
            System.out.println("RM " + (thisReplicaId + 1) + " Waiting for failure notification");
            notificationSocket.receive(udpPacket);
            System.out.println("RM " + (thisReplicaId + 1) + " received failure notification");
            byte[] notificationPayload = udpPacket.getData();
            ObjectInputStream objectInputStream =
                    new ObjectInputStream(new ByteArrayInputStream(notificationPayload));
            return (Notification) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void deliverRequest(Request request) {
        // TODO: ip and port of replica
        String hostIp = localRmIps[thisReplicaId];
        int port = 0;
        switch (request.getServerCode()) {
            case "MTL":
                port = 6821;
                break;
            case "QUE":
                port = 6822;
                break;
            case "SHE":
                port = 6823;
                break;
        }
        System.out.println("RM " + thisReplicaId + "deliver request to server" + request.getServerCode());
        try {
            DatagramSocket udpSocket = new DatagramSocket();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            byte[] requestAsBytes = byteArrayOutputStream.toByteArray();
            InetSocketAddress ip = new InetSocketAddress(hostIp, port);
            DatagramPacket requestPacket = new DatagramPacket(requestAsBytes, requestAsBytes.length, ip);
            udpSocket.send(requestPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] toByteArray(Object obj) {
        byte[] message = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
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

    // TODO: to implement
    private byte[] getDataFromReplica(int replica) {
        try {
            DatagramSocket udpSocket = new DatagramSocket(recoverDataPort);
            byte[] buf = new byte[32767];
            DatagramPacket requestPacket = new DatagramPacket(
                    buf,
                    buf.length,
                    new InetSocketAddress(localRmIps[replica], localRmPorts[replica]));
            udpSocket.send(requestPacket);
            DatagramPacket responsePacket = new DatagramPacket(buf, buf.length);
            udpSocket.receive(responsePacket);
            return responsePacket.getData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
