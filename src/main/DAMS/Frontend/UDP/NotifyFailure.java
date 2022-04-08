package DAMS.Frontend.UDP;

import DAMS.Address.Address;
import DAMS.Notification.Notification;

import java.io.*;
import java.net.*;
import java.util.*;


public class NotifyFailure {

    LinkedHashMap<Integer, Address> addresses;
    DatagramSocket datagramSocket;
    ByteArrayOutputStream byteArrayOutputStream;
    ObjectOutputStream objectOutputStream;

    public NotifyFailure(){
        addresses = new LinkedHashMap<Integer, Address>();
        addresses.put(1,new Address("",1));
        addresses.put(2,new Address("",2));
        addresses.put(3,new Address("",3));
        addresses.put(4,new Address("",4));
    }

    public void notifyReplicaManager(String failureType, List<Integer> failedReplicas) {
        try {
              datagramSocket = new DatagramSocket();
              Notification notification = new Notification(failureType, failedReplicas);
              byte[] message = this.encodeToByteArray(notification);
              for(int i = 0; i < failedReplicas.size(); i++){
                  Address address = addresses.get(failedReplicas.get(i));
                  InetSocketAddress ip = new InetSocketAddress(address.getHOST_IP(), address.getPORT());
                  DatagramPacket requestPacket = new DatagramPacket(message, message.length, ip);
                  datagramSocket.send(requestPacket);
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

    private byte[] encodeToByteArray(Notification notification) throws IOException {
        byte[] message = null;
        try{
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(notification);
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
