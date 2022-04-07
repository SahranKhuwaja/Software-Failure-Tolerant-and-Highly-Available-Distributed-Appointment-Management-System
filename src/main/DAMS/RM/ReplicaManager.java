package DAMS.RM;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

import DAMS.Request.Request;

public class ReplicaManager implements Runnable{
  // TODO: multicast group message ip and port
  private final String groupIp = "230.0.0.1";
  private final int groupPort = 1421;

  private final PriorityQueue<Request> holdBackQueue;

  private MulticastSocket multicastSocket;
  private int nextSeqNum;

  public ReplicaManager() {
    this.holdBackQueue = new PriorityQueue<>(Comparator.comparingInt(Request::getSequenceNumber));
    this.nextSeqNum = 1;
  }


  public void run() {
    try {
      this.multicastSocket = new MulticastSocket(groupPort);
      // TODO: group ip address
      InetAddress group = InetAddress.getByName(groupIp);
      multicastSocket.joinGroup(group);
      while (true) {
        Request incomingRequest = this.receive();
        holdBackQueue.add(incomingRequest);
        assert holdBackQueue.peek() != null;
        if (nextSeqNum == holdBackQueue.peek().getSequenceNumber()) {
          deliverRequest(Objects.requireNonNull(holdBackQueue.poll()));
          this.nextSeqNum++;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Request receive() {
    byte[] buf = new byte[32767];
    try {
      DatagramPacket udpPacket = new DatagramPacket(buf, buf.length);
      multicastSocket.receive(udpPacket);
      byte[] responsePayload = udpPacket.getData();
      ObjectInputStream objectInputStream =
          new ObjectInputStream(new ByteArrayInputStream(responsePayload));
      return (Request) objectInputStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void deliverRequest(Request request) {
    // TODO: ip and port of replica
    String hostIp = "";
    int port = 0;
    switch (request.getServerCode()) {
      case "MTL":
        hostIp = "1.1.1.1";
        port = 6821;
        break;
      case "QUE":
        hostIp = "1.1.1.1";
        port = 6822;
        break;
      case "SHE":
        hostIp = "1.1.1.1";
        port = 6823;
        break;
    }
    try {
      DatagramSocket udpSocket = new DatagramSocket();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
      objectOutputStream.writeObject(request);
      objectOutputStream.flush();
      byte[] requestAsBytes = byteArrayOutputStream.toByteArray();
      InetSocketAddress ip = new InetSocketAddress(hostIp, port);
      DatagramPacket requestPacket = new DatagramPacket(requestAsBytes, requestAsBytes.length, ip);
      udpSocket.send(requestPacket);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
