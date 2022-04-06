package DAMS.Sequencer;


import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import DAMS.Request.Request;
import java.net.InetAddress;


public class Sequencer{
    public static int sequencerNumber = 1;
    public static void main(String[] args){
        DatagramSocket aSocket = null;
        DatagramSocket datagramSocket = null;
        try {
            System.out.println("Running");
            datagramSocket = new DatagramSocket(6801);
            while (true) {
                byte[] requestBytes = new byte[2000];
                DatagramPacket request = new DatagramPacket(requestBytes, requestBytes.length);
                datagramSocket.receive(request);
                ByteArrayInputStream in = new ByteArrayInputStream(request.getData());
                ObjectInputStream is = new ObjectInputStream(in);
                Request b = (Request) is.readObject();
                System.out.println("Hello receiverd");
                System.out.println(b.getAppointmentID());
                b.setSequenceNumber(sequencerNumber);
                sequencerNumber++;

                multicastRequest(b);

            }
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
    }

    public static void multicastRequest(Request r){

        ByteArrayOutputStream byteArrayOutputStream;
        ObjectOutputStream objectOutputStream;
        int port=1421;
        DatagramSocket aSocket = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(r);
            objectOutputStream.flush();

            byte[] message =byteArrayOutputStream.toByteArray();
            aSocket = new DatagramSocket();

            InetAddress aHost = InetAddress.getByName("230.0.0.1");

            DatagramPacket request = new DatagramPacket(message, message.length, aHost, port);
            aSocket.send(request);
            System.out.println("message sent");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

