package DAMS.Frontend.UDP;

import DAMS.Frontend.Request.Request;

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
    final String HOST_IP = "192.168.2.12";
    final int PORT = 6821;

    public static void main(String[] args) throws IOException {
        Request r = new Request("MTL","MTLA2046", "test");
        IPCRequest rr = new IPCRequest();
        rr.sendRequestToSequencer(r);
    }

    public void sendRequestToSequencer(Request request) {
        try {
            datagramSocket = new DatagramSocket();
            byte[] message = this.encodeToByteArray(request);
            InetSocketAddress ip = new InetSocketAddress(HOST_IP,PORT);
            DatagramPacket requestPacket = new DatagramPacket(message, message.length, ip);
            datagramSocket.send(requestPacket);

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

    public byte[] encodeToByteArray(Request request) throws IOException {
        byte[] message = null;
        try{
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            message = byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            if(byteArrayOutputStream!=null){
                byteArrayOutputStream.close();
            }
        }
        return message;
    }


}
