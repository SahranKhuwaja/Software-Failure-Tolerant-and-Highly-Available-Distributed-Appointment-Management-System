package DAMS.Frontend.UDP;

import DAMS.Frontend.Request.Request;
import com.sun.mail.iap.ByteArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class IPCRequest {

    DatagramSocket datagramSocket;
    ByteArrayOutputStream byteArrayOutputStream;
    ObjectOutputStream objectOutputStream;

    public void sendRequestToSequencer(Request request) throws IOException {
        try {
            datagramSocket = new DatagramSocket();
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            byte[] message =byteArrayOutputStream.toByteArray();
            int port = 6801;
            InetSocketAddress ip = new InetSocketAddress("192.168.2.12",port);
            DatagramPacket requestPacket = new DatagramPacket(message, message.length, ip);
            datagramSocket.send(requestPacket);

        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
                byteArrayOutputStream.close();
            }
        }

    }


}
