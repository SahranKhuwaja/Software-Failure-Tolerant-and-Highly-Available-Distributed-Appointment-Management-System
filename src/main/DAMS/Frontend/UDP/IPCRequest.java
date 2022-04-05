package DAMS.Frontend.UDP;

import DAMS.Frontend.Request.Request;
import DAMS.Replicas.Replica1.Response.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class IPCRequest {

    DatagramSocket datagramSocket;
    ByteArrayOutputStream byteArrayOutputStream;
    ObjectOutputStream objectOutputStream;
    ByteArrayInputStream byteArrayInputStream;
    ObjectInputStream objectInputStream;
    final String HOST_IP = "192.168.2.12";
    final int PORT = 6821;

    public static void main(String[] args) throws IOException {
        Request r = new Request("MTL","MTLA2046", "test");
        IPCRequest rr = new IPCRequest();
        rr.sendRequestToSequencerAndGetReplyFromFE(r);
    }

    public Response sendRequestToSequencerAndGetReplyFromFE(Request request) {
        Response replyFromRE = null;
        try {
            datagramSocket = new DatagramSocket();
            byte[] message = this.encodeToByteArray(request);
            InetSocketAddress ip = new InetSocketAddress(HOST_IP,PORT);
            DatagramPacket requestPacket = new DatagramPacket(message, message.length, ip);
            datagramSocket.send(requestPacket);
            datagramSocket = new DatagramSocket(6802);
            byte[] replyBytes = new byte[2000];
            DatagramPacket reply = new DatagramPacket(replyBytes, replyBytes.length);
            datagramSocket.receive(reply);
            replyFromRE = this.decodeMessage(reply);
            System.out.println("Received!");

        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
        return replyFromRE;
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

    private Response decodeMessage(DatagramPacket reply) throws IOException {
        Response responseFromRE = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(reply.getData());
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            responseFromRE  = (Response) objectInputStream.readObject();
        }catch (SocketException e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
        }
        return responseFromRE;
    }



}
