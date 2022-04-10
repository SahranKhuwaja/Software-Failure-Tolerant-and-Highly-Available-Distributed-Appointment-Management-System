package DAMS.Replicas.Replica1.UDP;

import DAMS.Request.Request;
import DAMS.Replicas.Replica1.Interfaces.UDPReplyToFE;
import DAMS.Replicas.Replica1.RemoteDistributedAppointment.RemoteDistributedAppointment;
import DAMS.Response.Response;
import DAMS.ResponseWrapper.ResponseWrapper;

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
    ByteArrayOutputStream byteArrayOutputStream;
    ObjectOutputStream objectOutputStream;
    final String HOST_IP = "172.20.10.2";
    final int PORT = 6802;
    final int REPLICA = 1;

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
                System.out.println("received");
                Request requestFromFE = this.decodeMessage(request);
                Response wrappedMessage = this.wrapMessage(requestFromFE);
                byte[] replyBytes = this.encodeToByteArray(wrappedMessage);
                InetSocketAddress ip = new InetSocketAddress(HOST_IP, PORT);
                DatagramPacket reply = new DatagramPacket(replyBytes, replyBytes.length, ip);
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

    private Response wrapMessage(Request request){
        Response response = null;
        boolean success;
        String message;
        String[] messages;
        ResponseWrapper responseWrapper;

        switch (request.getOperation()){
            case "GetAppointmentTypes":
                messages = rda.getAppointmentTypes();
                success = messages.length!=0?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, messages);
                break;
            case "GetTimeSlots":
                messages = rda.getTimeSlots();
                success = messages.length!=0?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, messages);
                break;
            case "AddAppointment":
                message = rda.addAppointment(request.getAppointmentID(), request.getAppointmentType(), request.getAppointmentDescription(), request.getCapacity());
                success = message.toLowerCase().contains("success")?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;
            case "ViewAppointment":
                responseWrapper = rda.viewAppointment(request.getAppointmentType());
                responseWrapper.setReplica(REPLICA);
                success = responseWrapper.getData()!=null?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, responseWrapper);
                break;
            case "RemoveAppointment":
                message = rda.removeAppointment(request.getAppointmentID(), request.getAppointmentType());
                success = message.toLowerCase().contains("success")?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;
            case "ListAppointmentAvailability":
                responseWrapper = rda.listAppointmentAvailability(request.getAppointmentType());
                responseWrapper.setReplica(REPLICA);
                success = responseWrapper.getData()!=null?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, responseWrapper);
                break;
            case "BookAppointment":
                message = rda.bookAppointment(request.getPatientID(),request.getAppointmentID(), request.getAppointmentType());
                success = message.toLowerCase().contains("success")?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;
            case "GetAppointmentSchedule":
                messages = rda.getAppointmentSchedule(request.getPatientID());
                success = messages.length!=0?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, messages);
                break;
            case "CancelAppointment":
                message = rda.cancelAppointment(request.getPatientID(), request.getAppointmentID(), request.getAppointmentType());
                success = message.toLowerCase().contains("success")?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;
            case "SwapAppointment":
                message = rda.swapAppointment(request.getPatientID(),request.getOldAppointmentID(), request.getOldAppointmentType(), request.getAppointmentID(), request.getAppointmentType());
                success = message.toLowerCase().contains("success")?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;
        }
        response.setReplica(REPLICA);
        return response;

    }

    private byte[] encodeToByteArray(Response response) throws IOException {
        byte[] message = null;
        try{
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(response);
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
