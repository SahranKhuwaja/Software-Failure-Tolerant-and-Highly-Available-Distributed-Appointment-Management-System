package DAMS.Replicas.Replica1.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import DAMS.Replicas.Replica1.AppointmentSlots.AppointmentSlot;
import DAMS.Replicas.Replica1.Interfaces.UDPReply;
import DAMS.Replicas.Replica1.RemoteDistributedAppointment.RemoteDistributedAppointment;

public class IPCReply extends Thread implements UDPReply {
	DatagramSocket datagramSocket = null;
	String serverName;
	int port;
	RemoteDistributedAppointment rda;

	public IPCReply(DatagramSocket datagramSocket, String serverName, int port, RemoteDistributedAppointment rda) {
		this.datagramSocket = datagramSocket;
		this.serverName = serverName;
		this.port = port;
		this.rda = rda;
		this.start();
		//this.getRequestAndSendReply();
	}

	@Override
	public void run() {
		this.getRequestAndSendReply();
	}

	@Override
	public void getRequestAndSendReply() {
		try {
			while (true) {
				byte[] requestBytes = new byte[2000];
				DatagramPacket request = new DatagramPacket(requestBytes, requestBytes.length);
				datagramSocket.receive(request);
				List<String> decodedMessage = this.decodeMessage(request);
				byte[] replyBytes = null;
				switch(decodedMessage.get(0)) {
					case "LIST":
						rda.logger.info("Received a request via Inter"
								+ " Process Communication to list Appointment Slots");
						replyBytes = this.getAppointments(decodedMessage.get(1).trim())
									.toString().getBytes();
						break;
					case "BOOK":
						rda.logger.info("Received a request via Inter"
								+ " Process Communication to book an Appointment Slot");
						replyBytes = this.bookAppointments(decodedMessage.get(1).trim(),
								decodedMessage.get(2).trim(), decodedMessage.get(3).trim())
								.toString().getBytes();
						break;
					case "GETSCHEDULE":
						rda.logger.info("Received a request via Inter"
								+ " Process Communication to get Appointment Schedule");
						replyBytes = this.getSchedule(decodedMessage.get(1).trim())
									.toString().getBytes();
						break;
					case "CANCEL":
						rda.logger.info("Received a request via Inter"
								+ " Process Communication to cancel an Appointment");
						replyBytes = this.cancelAppointments(decodedMessage.get(1).trim(),
								decodedMessage.get(2).trim(), decodedMessage.get(3).trim())
								.toString().getBytes();
						break;
					case "SWAPBOOKCHECK":
						rda.logger.info("Received a request via Inter"
								+ " Process Communication to check booking for swapping an Appointment");
						replyBytes = this.swappingAppointmentsBookCheck(decodedMessage.get(1).trim(),
								decodedMessage.get(2).trim(), decodedMessage.get(3).trim())
								.toString().getBytes();
						break;
					case "SWAPSLOTAVAILABILITYCHECK":
						rda.logger.info("Received a request via Inter"
								+ " Process Communication to check slot availability for swapping an Appointment");
						replyBytes = this.swappingAppointmentsSlotAvailabilityCheck(decodedMessage.get(1).trim(),
								decodedMessage.get(2).trim(), decodedMessage.get(3).trim())
								.toString().getBytes();
						break;
				}
				
				DatagramPacket reply = new DatagramPacket(replyBytes, replyBytes.length, request.getAddress(),
						request.getPort());

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
	
	private List<String> decodeMessage(DatagramPacket request){
		String receivedMessage = new String(request.getData()).trim();				
		List<String> decodedMessage = Arrays.asList(receivedMessage
				.substring(1,receivedMessage.length()-1).split(","));
		return decodedMessage;
	}
	
	
	
	private HashMap<String, AppointmentSlot> getAppointments(String appointmentType){

		return rda.database.get(appointmentType);
	}
	
	private HashMap<String,String> bookAppointments (String appointmentType, String 
		patientID, String appointmentID){
			
		HashMap<String,String> serverMessage = new HashMap<String,String>();
		try {
			String message = rda.bookAppointment(patientID, 
					appointmentID, appointmentType);
			serverMessage.put("Message", message);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return serverMessage;
		}
	private HashMap<String,String> getSchedule(String patientID){
		HashMap<String,String> schedule = new HashMap<String,String>();
		
		List<String> scheduleList = 
				Arrays.asList(rda.getAppointmentSchedule(patientID));
		if(scheduleList!=null) {
			schedule.put(patientID, scheduleList.toString()
					.substring(1,scheduleList.toString().length()-1));
		}
	return schedule.size()!=0?schedule:null;
	}
	
	private HashMap<String,String> cancelAppointments(String appointmentType, String 
			patientID, String appointmentID){
		HashMap<String,String> serverMessage = new HashMap<String,String>();
		try {
			String message = rda.cancelAppointment(patientID, 
					appointmentID, appointmentType);
			serverMessage.put("Message", message);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return serverMessage;
	}
	
	private Object swappingAppointmentsBookCheck(String appointmentType, 
			String patientID, String appointmentID) {
		HashMap<String,String> serverMessage = new HashMap<String,String>();
		try {
			String message = rda.checkAlreadyBooked(patientID, appointmentID, 
					appointmentType);
			serverMessage.put("Message", message);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return serverMessage;
	}
	
	private Object swappingAppointmentsSlotAvailabilityCheck(String appointmentType, 
			String patientID, String appointmentID) {
		HashMap<String,String> serverMessage = new HashMap<String,String>();
		try {
			String message = rda.checkSlotsAvailability(appointmentID, appointmentType);
			serverMessage.put("Message", message);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return serverMessage;
	}
}
