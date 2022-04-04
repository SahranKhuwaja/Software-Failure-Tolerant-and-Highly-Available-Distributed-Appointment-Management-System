package DAMS.Frontend.UDP;

import DAMS.Frontend.Request.Request;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class TestReceriver {

    public static void main(String[] args){
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
                System.out.println(b.getOperation());
//                ObjectInput in = null;
//                try {
//                    in = new ObjectInputStream(bis);
//                    Request r = in.readObject();
//
//                } finally {
//                    try {
//                        if (in != null) {
//                            in.close();
//                        }
//                    } catch (IOException ex) {
//                        // ignore close exception
//                    }
//                }

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


}
