package DAMS.Replica1.UDP;

import DAMS.Frontend.Request.Request;
import DAMS.Replica1.Interfaces.UDPReplyToFE;
import DAMS.Replica1.RemoteDistributedAppointment.RemoteDistributedAppointment;
import DAMS.Replica1.Response.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class IPCReplyToFE extends Thread implements UDPReplyToFE {

    DatagramSocket datagramSocket;
    String serverName;
    int port;
    RemoteDistributedAppointment rda;
    ByteArrayInputStream byteArrayInputStream;
    ObjectInputStream objectInputStream;
    final String HOST_IP = "192.168.2.12";
    final int PORT = 6821;

    public IPCReplyToFE(DatagramSocket datagramSocket, String serverName, int port, RemoteDistributedAppointment rda) {
        this.datagramSocket = datagramSocket;
        this.serverName = serverName;
        this.port = port;
        this.rda = rda;
        this.start();
    }

    @Override
    public void run(){
        this.getRequestAndSendReply();
    }

    @Override
    public void getRequestAndSendReply() {
        try {
            while (true) {
                byte[] requestBytes = new byte[2000];
                DatagramPacket request = new DatagramPacket(requestBytes, requestBytes.length);
                datagramSocket.receive(request);
                Request requestFromFE = this.decodeMessage(request);
                byte[] replyBytes = request.getData();
                InetSocketAddress ip = new InetSocketAddress(HOST_IP, PORT);
                DatagramPacket reply = new DatagramPacket(replyBytes, replyBytes.length,ip);
                datagramSocket.send(reply);
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

    private Request decodeMessage(DatagramPacket request) throws IOException {
        Request requestFromRM = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(request.getData());
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            requestFromRM = (Request) objectInputStream.readObject();
        }catch (SocketException e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
        }
        return requestFromRM;
    }

    public Response encodeMessage(Request request){
        Response response = null;
        switch (request.getOperation()){
            case "GetAppointmentTypes":
                break;
            case "GetTimeSlots":
                break;
            case "AddAppointment":
                break;
            case "ViewAppointment":
                break;
            case "RemoveAppointment":
                break;
            case "ListAppointmentAvailability":
                break;
            case "BookAppointment":
                String message = rda.bookAppointment(request.getPatientID(),request.getAppointmentID(), request.getAppointmentType());
                boolean success = message.toLowerCase().contains("success")?true:false;
                response = new Response(request.getOperation(),message,success);
                break;
            case "GetAppointmentSchedule":
                break;
            case "CancelAppointment":
                break;
            case "SwapAppointment":
                break;
        }

        return response;

    }





}
