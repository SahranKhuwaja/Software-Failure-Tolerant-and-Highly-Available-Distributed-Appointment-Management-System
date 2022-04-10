package DAMS.Replicas.Replica3;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;

import DAMS.Replicas.Replica1.AppointmentSlots.AppointmentSlot;
import DAMS.Replicas.Replica3.Binding.StartMTL;
import DAMS.Replicas.Replica3.Binding.StartQUE;
import DAMS.Replicas.Replica3.Binding.StartSHE;

/**
 * Class to handle the restart of servers.
 */
public class LocalReplicaManager implements Runnable {

    private final int recoverRequestPort = 6923;
    private DatagramSocket recoverSocket;

    private final int getAllDataRequestPort = 6931;
    private DatagramSocket getAllDataSocket;

    private final String[] localRmIps = { "X.X.X.X", "X.X.X.X", "X.X.X.X", "X.X.X.X" };
    private final int[] localRmPorts = { 6921, 6922, 6923, 6924 };

    private Thread mtlThread;
    private Thread queThread;
    private Thread sheThread;

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
                killServers();
                startServers();
//                loadDataToServers(data);
            }
        };

        new Thread(listenGetAllDataRequest).start();
        new Thread(listenRecoverRequest).start();


    }

    private HashMap<String, HashMap<String, AppointmentSlot>>[] receiveRecoverRequest() {
        byte[] buf = new byte[32767];
        try {
            DatagramPacket udpPacket = new DatagramPacket(buf, buf.length);
            recoverSocket.receive(udpPacket);
            byte[] data = udpPacket.getData();
            return null;
        } catch (IOException e) {
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

    private void startServers() {
        this.mtlThread = new Thread(() -> {
            StartMTL.main(new String[0]);
        });
        this.queThread = new Thread(() -> {
            StartQUE.main(new String[0]);
        });
        this.sheThread = new Thread(() -> {
            StartSHE.main(new String[0]);
        });
        System.out.println("Replica 3 start MTL server");
        this.mtlThread.start();
        System.out.println("Replica 3 start QUE server");
        this.queThread.start();
        System.out.println("Replica 3 start SHE server");
        this.sheThread.start();

    }

    private void killServerOnPort(int port) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("cmd /c netstat -ano | findstr :" + port);
            System.out.println(proc.pid());

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            String s = null;
            if ((s = stdInput.readLine()) != null) {
                int index = s.lastIndexOf(" ");
                String sc = s.substring(index, s.length());
                System.out.println(sc);
                rt.exec("cmd /c Taskkill /PID" + sc + " /F");
            }
//            JOptionPane.showMessageDialog(null, "Server Stopped");
        } catch (Exception e) {
            System.out.println("Something Went wrong with server");
        }
    }


    private void killServers() {
        System.out.println("Replica 3 kill all servers");
        killServerOnPort(6821);
        killServerOnPort(6822);
        killServerOnPort(6823);

    }


    public static void main(String[] args) {
        new LocalReplicaManager().run();
    }

}
