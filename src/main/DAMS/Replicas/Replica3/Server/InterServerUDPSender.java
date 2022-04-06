package DAMS.Replicas.Replica3.Server;

import java.io.IOException;
import java.net.*;

public class InterServerUDPSender {



    public static String listAppointment(int portNumber,String appType) throws IOException{
        String msg  ="err";
        DatagramSocket clientSocket = null;
        InetAddress IPAddress = null;

        try {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName("localhost");
        }catch(Exception e) {
            System.out.println("error from client socket creation"+e);
        }

        byte[] outData = new byte[1024];
        byte[] inData = new byte[1024];

        try {

            String sendData = "type app" + "," + appType + ",";
            outData = sendData.getBytes();
            DatagramPacket outPacket = new DatagramPacket(outData, outData.length, IPAddress, portNumber);
            clientSocket.send(outPacket);


            DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
            clientSocket.receive(inPacket);
            msg = new String(inPacket.getData());
            msg = msg.trim();
            clientSocket.close();
        }catch(Exception e) {
            System.out.println("Error from client socket"+e);
        }

        return msg;

    }





    public static String bookAppointment(int portNumber,String patientId, String appointmentId, String appType) throws IOException{
        String msg  ="err";
        DatagramSocket clientSocket = null;
        InetAddress IPAddress = null;

        try {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName("localhost");
        }catch(Exception e) {
            System.out.println("error from client socket creation"+e);
        }

        byte[] outData = new byte[1024];
        byte[] inData = new byte[1024];

        try {

            String sendData = "book app" + "," + patientId+","+appointmentId+","+appType + ",";
            outData = sendData.getBytes();
            DatagramPacket outPacket = new DatagramPacket(outData, outData.length, IPAddress, portNumber);
            clientSocket.send(outPacket);


            DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
            clientSocket.receive(inPacket);
            msg = new String(inPacket.getData());
            msg = msg.trim();
            clientSocket.close();
        }catch(Exception e) {
            System.out.println("Error from client socket"+e);
        }

        return msg;

    }







    public static String cancelAppointment(int portNumber,String patientId, String appointmentId, String appType ) throws IOException{
        String msg  ="err";
        DatagramSocket clientSocket = null;
        InetAddress IPAddress = null;

        try {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName("localhost");
        }catch(Exception e) {
            System.out.println("error from client socket creation"+e);
        }

        byte[] outData = new byte[1024];
        byte[] inData = new byte[1024];

        try {

            String sendData = "cancel app" + "," + patientId+","+appointmentId+","+appType+",";
            outData = sendData.getBytes();
            DatagramPacket outPacket = new DatagramPacket(outData, outData.length, IPAddress, portNumber);
            clientSocket.send(outPacket);


            DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
            clientSocket.receive(inPacket);
            msg = new String(inPacket.getData());
            msg = msg.trim();
            clientSocket.close();
        }catch(Exception e) {
            System.out.println("Error from client socket"+e);
        }

        return msg;

    }


    public static String checkAvailability(int portNumber,String appointmentId, String appType) throws IOException{
        String msg  ="err";
        DatagramSocket clientSocket = null;
        InetAddress IPAddress = null;

        try {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName("localhost");
        }catch(Exception e) {
            System.out.println("error from client socket creation"+e);
        }

        byte[] outData = new byte[1024];
        byte[] inData = new byte[1024];

        try {

            String sendData = "check" +","+appointmentId+","+appType+",";
            outData = sendData.getBytes();
            DatagramPacket outPacket = new DatagramPacket(outData, outData.length, IPAddress, portNumber);
            clientSocket.send(outPacket);


            DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
            clientSocket.receive(inPacket);
            msg = new String(inPacket.getData());
            msg = msg.trim();
            clientSocket.close();
        }catch(Exception e) {
            System.out.println("Error from client socket"+e);
        }

        return msg;

    }


    public static String remove(int portNumber,String patientId, String appointmentId) throws IOException{
        String msg  ="err";
        DatagramSocket clientSocket = null;
        InetAddress IPAddress = null;

        try {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName("localhost");
        }catch(Exception e) {
            System.out.println("error from client socket creation"+e);
        }

        byte[] outData = new byte[1024];
        byte[] inData = new byte[1024];

        try {

            String sendData = "remove" +","+patientId+","+appointmentId+",";
            outData = sendData.getBytes();
            DatagramPacket outPacket = new DatagramPacket(outData, outData.length, IPAddress, portNumber);
            clientSocket.send(outPacket);


            DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
            clientSocket.receive(inPacket);
            msg = new String(inPacket.getData());
            msg = msg.trim();
            clientSocket.close();
        }catch(Exception e) {
            System.out.println("Error from client socket"+e);
        }

        return msg;

    }




}
