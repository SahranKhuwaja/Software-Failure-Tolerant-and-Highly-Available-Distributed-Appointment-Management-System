package DAMS.Replicas.Replica3.Binding;

import DAMS.Request.Request;
import DAMS.Response.Response;
import DAMS.ResponseWrapper.ResponseWrapper;
import DAMS.Replicas.Replica3.Server.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class MTLReplyToFE extends Thread{

    DatagramSocket datagramSocket;
    String serverName;
    int port;
    MontrealImpl MTL;

    ByteArrayInputStream byteArrayInputStream;
    ObjectInputStream objectInputStream;
    ByteArrayOutputStream byteArrayOutputStream;
    ObjectOutputStream objectOutputStream;
    final String HOST_IP = "192.168.2.12";
    final int PORT = 6802;


    public MTLReplyToFE(DatagramSocket datagramSocket, String serverName, int port, MontrealImpl rda) {
        this.datagramSocket = datagramSocket;
        this.serverName = serverName;
        this.port = port;
        this.MTL = rda;
        this.start();
    }


    @Override
    public void run(){
        this.getRequestAndSendReply();
    }

    public void getRequestAndSendReply() {
        try {
            while (true) {
                byte[] requestBytes = new byte[2000];
                DatagramPacket request = new DatagramPacket(requestBytes, requestBytes.length);
                datagramSocket.receive(request);
                Request requestFromFE = this.decodeMessage(request);
                Response wrappedMessage = this.wrapMessage(requestFromFE);
                byte[] replyBytes = this.encodeToByteArray(wrappedMessage);
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



    public Response wrapMessage(Request request){
        Response response = null;
        boolean success;
        String message;
        String[] messages;
        ResponseWrapper responseWrapper;

        switch (request.getOperation()){
            case "GetAppointmentTypes":
                messages = MTL.getAppointmentTypes();
                success = messages.length!=0?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, messages);
                break;
            case "GetTimeSlots":
                messages = MTL.getTimeSlots();
                success = messages.length!=0?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, messages);
                break;

            case "AddAppointment":
                message = MTL.addAppointment(request.getAppointmentID().toUpperCase(), request.getAppointmentType().substring(0,1).toUpperCase(), request.getCapacity());
                success = message.toLowerCase().contains("yes")?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;


            case "RemoveAppointment":
                message = MTL.removeAppointment(request.getAppointmentID().toUpperCase(), request.getAppointmentType().substring(0,1).toUpperCase());
                success = message.toLowerCase().contains("yes")?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;

            case "ListAppointmentAvailability":
                message = MTL.listAppointmentAvailability(request.getAppointmentType().substring(0,1).toUpperCase());
                success = message.length()!=0?true:false;
                System.out.println(message);
                ResponseWrapper rw = new ResponseWrapper();
                rw.setMessage(message);
                rw.setReplica(3);
                response = new Response(request.getOperation(), request.getOperation(), success, rw);
                break;

            case "BookAppointment":
                message = MTL.bookAppointment(request.getPatientID().toUpperCase(),request.getAppointmentID().toUpperCase(), request.getAppointmentType().substring(0,1).toUpperCase());
                success = message.toLowerCase().contains("yes")?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;

            case "GetAppointmentSchedule":
                message = MTL.getAppointmentSchedule(request.getPatientID().toUpperCase());
                success = message.length()!=0?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;

            case "CancelAppointment":
                message = MTL.cancelAppointment(request.getPatientID().toUpperCase(), request.getAppointmentID().toUpperCase(), request.getAppointmentType().substring(0,1).toUpperCase());
                success = message.toLowerCase().contains("yes")?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;

            case "SwapAppointment":
                message = MTL.swapAppointment(request.getPatientID().toUpperCase(),request.getOldAppointmentID().toUpperCase(), request.getOldAppointmentType().substring(0,1).toUpperCase(), request.getAppointmentID().toUpperCase(), request.getAppointmentType().substring(0,1).toUpperCase());
                success = message.toLowerCase().contains("yes")?true:false;
                response = new Response(request.getOperation(), request.getOperation(), success, message);
                break;
        }
        response.setReplica(3);
        return response;

    }

    public byte[] encodeToByteArray(Response response) throws IOException {
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
