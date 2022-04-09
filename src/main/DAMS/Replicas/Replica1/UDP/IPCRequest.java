package DAMS.Replicas.Replica1.UDP;


import DAMS.Replicas.Replica1.Interfaces.UDPRequest;

import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;



public class IPCRequest implements UDPRequest {
	DatagramSocket datagramSocket = null;
	Logger logger;
	final String HOST_IP = "172.20.10.2";
	
	public IPCRequest(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public HashMap<String, String> sendRequestAndGetReply(String serverName, int port, 
			String operation, String appointmentType, String patientID, 
			String appointmentID) {
		HashMap<String, String> appointments = new HashMap<String, String>();
		try {
			datagramSocket = new DatagramSocket();
			byte[] message = null;
			InetSocketAddress ip = new InetSocketAddress(HOST_IP,port);

			if (operation.equals("LIST")) {
				message = Arrays.asList(operation, appointmentType).toString().getBytes();
			} else if (operation.equals("BOOK")) {
				message = Arrays.asList(operation, appointmentType, patientID, appointmentID).toString().getBytes();
			}
			else if (operation.equals("GETSCHEDULE")) {
				message = Arrays.asList(operation, patientID).toString().getBytes();
			}
			else if(operation.equals("CANCEL")) {
				message = Arrays.asList(operation, appointmentType, patientID, appointmentID).toString().getBytes();
			}
			else if(operation.equals("SWAPBOOKCHECK")) {
				message = Arrays.asList(operation, appointmentType, patientID, appointmentID).toString().getBytes();
			}
			else if(operation.equals("SWAPSLOTAVAILABILITYCHECK")) {
				message = Arrays.asList(operation, appointmentType, patientID, appointmentID).toString().getBytes();
			}
			
			DatagramPacket request = new DatagramPacket(message, message.length, ip);
			datagramSocket.send(request);

			byte[] replyBytes = new byte[2000];
			DatagramPacket reply = new DatagramPacket(replyBytes, replyBytes.length);
			datagramSocket.receive(reply);

			switch (operation) {
				case "LIST":
					appointments = this.decodeReplyForListing(reply);
					break;
				case "BOOK":
					appointments = this.decodeReplyForBookingAndCancel(reply);
					break;
				case "GETSCHEDULE":
					appointments = this.decodeReplyForGetAppointmentSchedule(reply);
					break;
				case "CANCEL":
					appointments = this.decodeReplyForBookingAndCancel(reply);
					break;
				case "SWAPBOOKCHECK":
					appointments = this.decodeReplyForSwappingAppointmentsBookCheck(reply);
					break;
				case "SWAPSLOTAVAILABILITYCHECK":
					appointments = this.decodeReplyForSwappingAppointmentsSlotAvailabilityCheck(reply);
					break;
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
		return appointments;

	}
	
	private HashMap<String, String> decodeReplyForListing(DatagramPacket reply) {
		HashMap<String, String> appointments = new HashMap<String, String>();
		String receivedReply = new String(reply.getData()).trim();
		String decodedReply = receivedReply.substring(1, receivedReply.length() - 1);
		String[] keyPairs = decodedReply.split(",");
		for (String arr : keyPairs) {
			String[] map = arr.split("=");
			appointments.put(map[0], map[1]);
		}
		return appointments;
	}
	
	private HashMap<String,String> decodeReplyForBookingAndCancel (DatagramPacket reply){
		HashMap<String, String> appointments = new HashMap<String, String>();
		String receivedReply = new String(reply.getData()).trim();
		String decodedReply = receivedReply.substring(1, receivedReply.length() - 1);
		appointments.put("Message", decodedReply.split("=")[1]);
		return appointments;
	}
	
	private HashMap<String,String> decodeReplyForGetAppointmentSchedule 
				(DatagramPacket reply){
		HashMap<String, String> appointments = new HashMap<String, String>();
		String receivedReply = new String(reply.getData()).trim();
		appointments.put(receivedReply.substring(1,9), receivedReply
				.substring(10).replace("}", ""));
		return appointments;	
	}
	
	private HashMap<String, String> decodeReplyForSwappingAppointmentsBookCheck(DatagramPacket reply) {
		HashMap<String, String> appointments = new HashMap<String, String>();
		String receivedReply = new String(reply.getData()).trim();
		String decodedReply = receivedReply.substring(1, receivedReply.length() - 1);
		appointments.put("Message", decodedReply.split("=")[1]);
		return appointments;
	}
	
	private HashMap<String, String> decodeReplyForSwappingAppointmentsSlotAvailabilityCheck(DatagramPacket reply) {
		HashMap<String, String> appointments = new HashMap<String, String>();
		String receivedReply = new String(reply.getData()).trim();
		String decodedReply = receivedReply.substring(1, receivedReply.length() - 1);
		appointments.put("Message", decodedReply.split("=")[1]);
		return appointments;
	}

}
