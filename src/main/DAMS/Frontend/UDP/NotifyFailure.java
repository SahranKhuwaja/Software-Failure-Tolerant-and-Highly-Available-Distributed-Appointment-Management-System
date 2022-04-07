package DAMS.Frontend.UDP;

import DAMS.Frontend.FaultTolerance.FaultTolerance;
import DAMS.Request.Request;
import DAMS.Response.Response;

import java.io.*;
import java.net.*;
import java.util.*;


public class NotifyFailure {

    DatagramSocket datagramSocket;
    ByteArrayOutputStream byteArrayOutputStream;
    ObjectOutputStream objectOutputStream;
    ByteArrayInputStream byteArrayInputStream;
    ObjectInputStream objectInputStream;
    final String HOST_IP = "192.168.2.12";
    final int PORT = 6821;
    List<Response> responseQueue;
    FaultTolerance faultTolerance;

    public NotifyFailure(){
        responseQueue = new ArrayList<Response>();
    }

    public void notifyReplicaManager(Request request) {
        Response replyFromRE = null;
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
