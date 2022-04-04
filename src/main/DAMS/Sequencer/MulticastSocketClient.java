package DAMS.Sequencer;

import DAMS.Frontend.Request.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastSocketClient {

    final static String INET_ADDR = "230.0.0.1";
    final static int PORT = 1421;

    public static void main(String[] args) throws UnknownHostException {
        // Get the address that we are going to connect to.
        InetAddress address = InetAddress.getByName(INET_ADDR);

        try (MulticastSocket clientSocket = new MulticastSocket(PORT)){
            //Joint the Multicast group.
            clientSocket.joinGroup(address);


            while (true) {
                // Receive the information and print it.
                byte[] requestBytes = new byte[2000];
                DatagramPacket request = new DatagramPacket(requestBytes, requestBytes.length);
                clientSocket.receive(request);
                ByteArrayInputStream in = new ByteArrayInputStream(request.getData());
                ObjectInputStream is = new ObjectInputStream(in);
                Request b = (Request) is.readObject();
                System.out.println("Hello received");
                System.out.println(b.getSequenceNumber());
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}