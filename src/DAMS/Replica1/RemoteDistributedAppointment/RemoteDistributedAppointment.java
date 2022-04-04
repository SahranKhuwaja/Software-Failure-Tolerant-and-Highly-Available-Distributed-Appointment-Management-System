package DAMS.Replica1.RemoteDistributedAppointment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import DAMS.Replica1.AppointmentSlots.AppointmentSlot;
import DAMS.Replica1.Booking.Booking;
import DAMS.Replica1.Interfaces.DistributedAppointment;
import DAMS.Replica1.ResponseWrapper.ResponseWrapper;
import DAMS.Replica1.UDP.IPCRequest;

public class RemoteDistributedAppointment implements DistributedAppointment {

	private String serverName;
	private String path;
	public Logger logger;
	private String serverCode;
	private String userID;
	private String role;
	public HashMap<String, HashMap<String, AppointmentSlot>> database = new HashMap<String, HashMap<String, AppointmentSlot>>();
	private List<String> timeSlots = Arrays.asList("M", "A", "E");
	private HashMap<String, Integer> UDPPorts = new HashMap<String, Integer>();
	private List<String> serverCodes = Arrays.asList("MTL", "QUE", "SHE");
	private HashMap<String, ArrayList<Booking>> bookings = new HashMap<String, ArrayList<Booking>>();
	private HashMap<String, Integer> foreignBookingCount = new HashMap<String, Integer>();
	IPCRequest ipc;


	public RemoteDistributedAppointment(String serverName, String path, Logger logger) throws Exception {
		super();
		this.serverName = serverName;
		this.path = path;
		this.logger = logger;
		this.generateDatabase();
		this.setupUDPPorts();
		ipc = new IPCRequest(logger);
	}

	@Override
	public String generateId(String role) {
		Random rd = new Random();
		logger.info("Generating " + role + "ID ...");
		String id = serverCode + role.substring(0, 1) + rd.nextInt(9000);
		logger.info(role + " ID '" + id + "' has been generated successfully!");
		this.saveID(role, id);
		return id;
	}

	private String generateAppointmentId() {
		Random rd = new Random();
		logger.info("Generating " + "Appointment ID ...");
		String id = serverCode + "M" + rd.nextInt(900000);
		logger.info("Appointment ID '" + id + "' has been generated successfully!");
		return id;
	}

	@Override
	public String serverResponse() {
		logger.info("Client successfully connected to the '" + this.serverName + "' Server!");
		return "You are successfully connected to the " + this.serverName + " Server!";

	}

	private void saveID(String role, String id) {
		String subPath = null;
		if (role.equals("Admin")) {
			subPath = "/Generated Files/Database/Admins.properties";
		} else {
			subPath = "/Generated Files/Database/Patients.properties";
		}
		try {
			BufferedReader bR = new BufferedReader(new FileReader(path + subPath));
			Properties prop = new Properties();
			prop.load(bR);
			bR.close();
			List<String> userIDs = new ArrayList<String>(
					Arrays.asList(prop.getProperty(serverCode).replace("[", "").replace("]", "").trim().split(", ")));

			BufferedWriter bW = new BufferedWriter(new FileWriter(path + subPath));
			userIDs.add(id);
			prop.setProperty(serverCode, userIDs.toString());
			prop.store(bW, null);
			bW.close();

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			logger.severe(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.severe(e.getMessage());
		}
	}

	private void generateDatabase() {
		logger.info("Generating database...");
		serverCode = serverName.toUpperCase().split(" ")[1].replace("(", "").replace(")", "");
		database.put("Physician", new HashMap<String, AppointmentSlot>());
		database.put("Surgeon", new HashMap<String, AppointmentSlot>());
		database.put("Dental", new HashMap<String, AppointmentSlot>());
		this.userID = "System";
		this.role = "System";
		addAppointment(serverCode + "M100222", "Surgeon", "Appointment Time Slot", 2);
		addAppointment(serverCode + "A100222", "Surgeon", "Appointment Time Slot", 5);
		addAppointment(serverCode + "E100222", "Surgeon", "Appointment Time Slot", 10);
		addAppointment(serverCode + "M100222", "Physician", "Appointment Time Slot", 2);
		addAppointment(serverCode + "A100222", "Physician", "Appointment Time Slot", 5);
		addAppointment(serverCode + "E100222", "Physician", "Appointment Time Slot", 10);
		addAppointment(serverCode + "M100222", "Dental", "Appointment Time Slot", 2);
		addAppointment(serverCode + "A100222", "Dental", "Appointment Time Slot", 5);
		addAppointment(serverCode + "E100222", "Dental", "Appointment Time Slot", 10);
		logger.info("Database generated");
	}

	private void setupUDPPorts() {
		logger.info("Setting up UDP ports... ");
		UDPPorts.put("Montreal (MTL)", 6811);
		UDPPorts.put("Quebec (QUE)", 6812);
		UDPPorts.put("Sherbrooke (SHE)", 6813);
		logger.info("UDP ports setted up");

	}

	@Override
	public boolean authenticateUser(String userID, String role) {
		boolean isValid = false;
		logger.info("Authenticating " + role + " with " + role + " ID '" + userID + "'");
		this.userID = userID;
		this.role = role;
		if (role.equals("Admin")) {
			isValid = this.authenticateAdmin(userID);
		} else {
			isValid = this.authenticatePatient(userID);
		}
		return isValid;
	}

	private boolean authenticateAdmin(String userId) {

		try {
			BufferedReader bR = new BufferedReader(
					new FileReader(path + "/Generated Files/Database/Admins.properties"));
			Properties prop = new Properties();
			prop.load(bR);
			bR.close();
			List<String> admins = Arrays
					.asList(prop.getProperty(serverCode).replace("[", "").replace("]", "").trim().split(", "));
			if (admins.contains(userId)) {
				logger.info("Admin with Admin ID '" + userId + "' is successfully authenticated!");
				return true;
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			logger.severe(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.severe(e.getMessage());
		}
		logger.warning("Admin with Admin ID '" + userId + "' is not successfully authenticated!");
		return false;

	}

	private boolean authenticatePatient(String userId) {

		try {
			BufferedReader bR = new BufferedReader(
					new FileReader(path + "/Generated Files/Database/Patients.properties"));
			Properties prop = new Properties();
			prop.load(bR);
			bR.close();
			List<String> patients = Arrays
					.asList(prop.getProperty(serverCode).replace("[", "").replace("]", "").trim().split(", "));
			if (patients.contains(userId)) {
				logger.info("Patient with Patient ID '" + userId + "' is successfully authenticated!");
				return true;
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		logger.info("Patient with Patient ID '" + userId + "' is not successfully authenticated!");
		return false;

	}

	@Override
	public String[] getAppointmentTypes() {
		logger.info(role + " with " + role + " ID '" + userID + "' is requesting " + "to get Appointment Types");
		logger.info("Appointment Types have been sent to the " + role + " with " + role + " ID '" + userID + "'");
		return database.keySet().toArray(String[]::new);

	}

	@Override
	public String[] getTimeSlots() {
		logger.info(role + " with " + role + " ID '" + userID + "' is requesting " + "to get Time Slots");
		logger.info("Time Slots have been sent to the " + role + " with " + role + " ID '" + userID + "'");
		return timeSlots.stream().toArray(String[]::new);
	}

	@Override
	public String addAppointment(String appointmentID, String appointmentType, String appointmentDescription,
			int capacity) {
		appointmentID = appointmentID.toUpperCase();
		String timeSlot = appointmentID.substring(3, 4).toUpperCase();
		logger.info(role + " with " + role + " ID '" + userID + "' is requesting "
				+ "to add new Appointment Slot in the Appointment Type " + appointmentType + " with Appointment ID '"
				+ appointmentID + "'  , Description '" + appointmentDescription + "' , and Capacity" + " '" + capacity
				+ "'");
		if (appointmentID.length() != 10) {
			logger.warning("Length of the Appointment ID should not be less or greater than 10!");
			return "Length of the Appointment ID should not be less or greater than 10!";
		} else if (appointmentID.length() == 0 || appointmentID.equals("")) {
			logger.warning("Appointment ID shouldn't be null!");
			return "Appointment ID shouldn't be null!";
		} else if (appointmentType.length() == 0 || appointmentType.equals("")) {
			logger.warning("Appointment Type shouldn't be null!");
			return "Appointment Type shouldn't be null!";
		} else if (!appointmentID.substring(0, 3).equals(serverCode)) {
			logger.warning("Appointment ID '" + appointmentID + "' contains invalid city code, which doesn't match"
					+ "current city server!");
			return "Appointment ID " + appointmentID + " contains invalid city code, which doesn't match"
					+ "current city server!";
		} else if (!timeSlots.contains(timeSlot)) {
			logger.warning("Appointment ID '" + appointmentID + "' contains invalid time slot!");
			return "Appointment ID " + appointmentID + " contains invalid time slot!";
		} else if (!database.containsKey(appointmentType)) {
			logger.warning("Appointment Type '" + appointmentType + "' is invalid!");
			return "Appointment Type " + appointmentType + " is invalid!";
		} else if ((capacity < 0)) {
			logger.warning("Capacity shouldn't be less than 0!");
			return "Capacity shouldn't be less than 0";
		} else {
			try {
				Integer.parseInt(appointmentID.substring(4, 10));
			} catch (NumberFormatException e) {
				logger.warning("Appointment ID '" + appointmentID + "' should contain proper date in numeric "
						+ "format (DDMMYY)!");
				return "Appointment ID '" + appointmentID + "' should contain proper date in numeric format (DDMMYY)";
			}

			HashMap<String, AppointmentSlot> slots = database.get(appointmentType);
			if (slots.containsKey(appointmentID)) {
				logger.warning("Appointment Slot with this Appointment ID '" + appointmentID
						+ "' already exists! Appointment ID should be unique!");
				return "Appointment Slot with this Appointment ID " + appointmentID + " already exists! \n"
						+ "Appointment ID should be unique!";
			}
			AppointmentSlot appointmentSlot = new AppointmentSlot(appointmentDescription, capacity);
			slots.put(appointmentID, appointmentSlot);
			database.put(appointmentType, slots);
			logger.info(
					"Appointment Slot with Appointment ID '" + appointmentID + "' has" + " been created successfully!");
			return "Appointment Slot with Appointment ID " + appointmentID + " has" + " been created\nsuccessfully!";
		}
	}

	@Override
	public ResponseWrapper viewAppointment(String appointmentType) {
		logger.info(role + " with " + role + " ID '" + userID + "' is requesting "
				+ "to view Appointment Slots in the Appointment Type '" + appointmentType + "'");
		logger.info("Appointment Slots have been sent to the " + role + " with " + role + " ID '" + userID + "'");
		return new ResponseWrapper((HashMap<String,String>)database.get(appointmentType).entrySet().stream()
				.collect(Collectors.toMap(a -> a.getKey(), b -> b.getValue().toString())));
				
	}

	@Override
	public String removeAppointment(String appointmentID, String appointmentType) {
		appointmentID = appointmentID.toUpperCase();
		logger.info(role + " with " + role + " ID '" + userID + "' is requesting "
				+ "to delete/remove Appointment Slot in the Appointment Type '" + appointmentType + "'"
				+ "with the Appointment ID '" + appointmentID + "' ");
		if (appointmentID.length() != 10) {
			logger.warning("Length of the Appointment ID should not be less or greater than 10!");
			return "Length of the Appointment ID should " + "not be less or greater than 10! Please re-enter!";
		} else if (!appointmentID.substring(0, 3).equals(serverCode)) {
			logger.warning("Appointment ID '" + appointmentID + "' contains invalid city code, which doesn't match"
					+ "current city server!");
			return "Appointment ID " + appointmentID + " contains invalid city code, "
					+ "which doesn't match current city server! " + "Please re-enter!";
		} else if (!timeSlots.contains(appointmentID.substring(3, 4))) {
			logger.warning("Appointment ID '" + appointmentID + "' contains invalid time slot!");
			return "Appointment ID " + appointmentID + " contains invalid " + "time slot! Please re-enter!";
		} else if (!database.containsKey(appointmentType)) {
			logger.warning("Invalid Appointment Type " + appointmentType + "!");
			return "Invalid Appointment Type " + appointmentType + "!";
		} else {
			try {
				Integer.parseInt(appointmentID.substring(4, 10));
			} catch (NumberFormatException e) {
				logger.warning("Appointment ID '" + appointmentID
						+ "' should contain proper date in numeric format (DDMMYY)!");
				return "Appointment ID " + appointmentID + " should contain proper date in numeric format (DDMMYY)!";
			}

			HashMap<String, AppointmentSlot> slots = database.get(appointmentType);
			if (!slots.containsKey(appointmentID)) {
				logger.warning("Appointment Slot with this Appointment ID '" + appointmentID + "' doesn't exists!");
				return "Appointment Slot with this Appointment ID " + appointmentID + " doesn't \nexists!";

			}

			Set<String> patients = bookings.keySet();
			if (patients.size() != 0) {
				String specialAppointmentId;
				while (true) {
					specialAppointmentId = this.generateAppointmentId();
					if (slots.containsKey(specialAppointmentId)) {
						continue;
					}
					break;
				}
				this.addAppointment(specialAppointmentId, appointmentType, "Special Appointment Time Slot", patients.size());
				for (String patient : patients) {
					String check = this.checkAlreadyBooked(patient, appointmentID, appointmentType);
					if (check == null) {
						//this.cancelAppointment(patient, appointmentID, appointmentType);
						this.swapAppointment(patient, appointmentID, appointmentType, specialAppointmentId, appointmentType);
					}
				}
			}

			slots.remove(appointmentID);
			database.put(appointmentType, slots);
			logger.info("Appointment Slot with this Appointment ID '" + appointmentID + "' has been deleted/removed "
					+ "successfully!");
			return "Appointment Slot with this Appointment ID " + appointmentID + " has been \ndeleted/removed "
					+ "successfully!";
		}

	}

	@Override
	public ResponseWrapper listAppointmentAvailability(String appointmentType) {
		logger.info(role + " with " + role + " ID '" + userID + "' is requesting "
				+ "to list Appointment Slots Availability from all servers in the" + " Appointment Type '"
				+ appointmentType + "'");

		List<String> udpServerNames = new ArrayList<String>();
		List<Integer> otherServerUDPPorts = UDPPorts.entrySet().stream().filter(a -> !a.getKey().equals(serverName))
				.map(a -> {
					udpServerNames.add(a.getKey());
					return a.getValue();
				}).collect(Collectors.toList());

		logger.info("Getting Appointment Slots from current server '" + serverName + "'...");

		HashMap<String, String> availability = (HashMap<String, String>)database.get(appointmentType).entrySet().stream()
				.collect(Collectors.toMap(a -> a.getKey(), b -> b.getValue().toString()));
		logger.info("Appointment Slots has been gotten from current server '" + serverName + "'");

		logger.info("Calling server '" + udpServerNames.get(0) + "' via UDP on port " + otherServerUDPPorts.get(0)
				+ " for Inter Process Communication " + "to get Appointment Availability...");

		HashMap<String, String> availabilityAnotherServer1 = ipc.sendRequestAndGetReply(serverName,
				otherServerUDPPorts.get(0), "LIST", appointmentType, null, null);

		logger.info("Calling server '" + udpServerNames.get(1) + "' via UDP on port " + otherServerUDPPorts.get(1)
				+ " for Inter Process Communication " + "to get Appointment Availability...");

		HashMap<String, String> availabilityAnotherServer2 = ipc.sendRequestAndGetReply(serverName,
				otherServerUDPPorts.get(1), "LIST", appointmentType, null, null);

		availability.putAll(availabilityAnotherServer1);
		logger.info("Appointment Slots from server '" + udpServerNames.get(0) + "' has been " + "received");
		availability.putAll(availabilityAnotherServer2);
		logger.info("Appointment Slots from server '" + udpServerNames.get(1) + "' has been " + "received");

		logger.info("Appointment Slots from all server for the Appointment Type '" + appointmentType
				+ "' have been sent to the " + role + " with " + role + " ID '" + userID + "'");

		return new ResponseWrapper(availability);

	}

	public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
		appointmentID = appointmentID.toUpperCase();
		logger.info(role + " with " + role + " ID '" + patientID + "' is requesting "
				+ "to book an Appointment in the Appointment Type '" + appointmentType + "' with an Appointment ID '"
				+ appointmentID + "' ");
		String code = appointmentID.substring(0, 3);
		if (appointmentID.length() != 10) {
			logger.warning("Length of the Appointment ID should not be less or greater than 10!");
			return "Length of the Appointment ID should " + "not be less or greater than 10! Please re-enter!";
		} else if (!serverCodes.contains(code) && patientID.substring(3, 4).toUpperCase().equals("P")) {
			logger.warning("Appointment ID '" + appointmentID + "' contains invalid city code, which doesn't match"
					+ "current city server!");
			return "Appointment ID " + appointmentID + " contains invalid city code, "
					+ "which doesn't match cities servers! " + "Please re-enter!";
		} else if (!database.containsKey(appointmentType)) {
			logger.warning("Invalid Appointment Type " + appointmentType + "!");
			return "Invalid Appointment Type " + appointmentType + "!";
		} else {
			try {
				Integer.parseInt(appointmentID.substring(4, 10));
			} catch (NumberFormatException e) {
				logger.warning("Appointment ID '" + appointmentID
						+ "' should contain proper date in numeric format (DDMMYY)!");
				return "Appointment ID " + appointmentID + " should contain proper date in numeric format (DDMMYY)!";

			}
			if (code.equals(serverCode)) {
				final String id = appointmentID;
				HashMap<String, AppointmentSlot> slots = database.get(appointmentType);
				AppointmentSlot slot = slots.get(appointmentID);
				if (slot == null) {
					logger.warning("Appointment ID '" + appointmentID + "' doesn't exists"
							+ " in the Appointment Type '" + appointmentType + "'!");
					return "Appointment ID " + appointmentID + " doesn't exists" + " in the Appointment Type\n"
							+ appointmentType + "!";
				}
				int capacity = slot.getCapacity();
				if (capacity == 0) {
					logger.warning("No empty slots available for Appointment ID '" + appointmentID + "' in the "
							+ "Appointment Type '" + appointmentType + "'! Book with another Appointment ID!");
					return "No empty slots available for Appointment ID " + appointmentID + " in the "
							+ "Appointment Type " + appointmentType + "!\n" + "Book with another Appointment ID!";
				}
				ArrayList<Booking> booking = bookings.get(patientID);
				if (booking != null) {
					long find = booking.stream().filter(e -> e.getAppointmentID().equals(id))
							.filter(e -> e.getAppointmentType().equals(appointmentType)).count();
					if (find != 0) {
						logger.warning("Appointment ID '" + appointmentID + "' in same " + "Appointment Type '"
								+ appointmentType + "' already booked! Cannot book twice!");
						return "Appointment ID " + appointmentID + " in same Appointment Type " + appointmentType
								+ " \nalready booked! " + "Cannot book twice!";
					}
					booking.add(new Booking(appointmentID, appointmentType));
				} else {
					booking = new ArrayList<Booking>();
					booking.add(new Booking(appointmentID, appointmentType));
				}
				logger.info("Appointment ID '" + appointmentID + "' is a current " + "server '" + serverName
						+ "' Appointment ID");
				logger.info("Booking an Appointment in the current server '" + serverName + "'...");
				bookings.put(patientID, booking);
				slot.setCapacity(capacity - 1);
				slots.put(appointmentID, slot);
				database.put(appointmentType, slots);
				logger.info("Appointment ID '" + appointmentID + "' in the Appointment Type '" + appointmentType
						+ "' has been booked successfully!");
				return "Appointment ID " + appointmentID + " in the Appointment Type " + appointmentType
						+ "\nhas been booked successfully!";

			} else {
				int count = 0;
				if (foreignBookingCount.containsKey(patientID)) {
					count = foreignBookingCount.get(patientID);
					if (count == 3) {
						logger.warning("You can't book other servers' slots more than 3 times"
								+ " with same PatientID '" + patientID + "'");
						return "You can't book other servers' slots more than 3 times" + " with same PatientID "
								+ patientID;
					}
				}
				logger.info("Appointment ID '" + appointmentID + "' is a foreign " + "server Appointment ID");
				logger.info("Identifying foreign server...");

				List<String> udpServerName = new ArrayList<String>();
				int otherServerUDPPort = UDPPorts.entrySet().stream().filter(a -> a.getKey().contains(code)).map(a -> {
					udpServerName.add(a.getKey());
					return a.getValue();
				}).findAny().orElse(null);
				logger.info("Identified Server '" + udpServerName.get(0) + "'");
				logger.info("Calling server '" + udpServerName.get(0) + "' via UDP on port " + otherServerUDPPort
						+ " for Inter Process Communication " + "to book an Appointment...");

				HashMap<String, String> bookOtherServers = ipc.sendRequestAndGetReply(serverName, otherServerUDPPort,
						"BOOK", appointmentType, patientID, appointmentID);
				String response = bookOtherServers.get("Message");
				if (response.toLowerCase().contains("successfully")) {
					if (count == 0) {
						foreignBookingCount.put(patientID, 1);
					} else {
						foreignBookingCount.put(patientID, count + 1);
					}
				}
				logger.info("Foreign server " + udpServerName.get(0) + "' Response: " + response);
				return response;
			}
		}
	}

	@Override
	public String[] getAppointmentSchedule(String patientID) {
		logger.info(role + " with " + role + " ID '" + patientID + "' is requesting "
				+ "to get his/her Appointment Schedule");
		ArrayList<String> appointmentSchedule = new ArrayList<String>();
		logger.info("Checking whether there is any Appointment Schedule for the " + role + " with the " + role + " ID '"
				+ patientID + "' in the current" + " server '" + serverName + "'...");
		if (bookings.containsKey(patientID)) {
			logger.info("Getting Appointment Schedule from current server '" + serverName + "'...");
			ArrayList<Booking> currentBooking = bookings.get(patientID);
			for (Booking book : currentBooking) {
				appointmentSchedule.add("[" + book.toString() + "]");
			}
			logger.info("Appointment Schedule for " + role + " with " + role + " ID '" + patientID
					+ "' has been gotten from current server '" + serverName + "'");
		}

		if (patientID.substring(0, 3).equals(serverCode)) {
			logger.info("Checking whether " + role + " with the " + role + " ID '" + patientID
					+ "' had booked appointments in foreign servers...");
			if (foreignBookingCount.containsKey(patientID)) {
				List<String> udpServerNames = new ArrayList<String>();
				List<Integer> otherServerUDPPorts = UDPPorts.entrySet().stream()
						.filter(a -> !a.getKey().equals(serverName)).map(a -> {
							udpServerNames.add(a.getKey());
							return a.getValue();
						}).collect(Collectors.toList());

				logger.info(
						"Calling server '" + udpServerNames.get(0) + "' via UDP on port " + otherServerUDPPorts.get(0)
								+ " for Inter Process Communication " + "to get Appointment Schedule (if any)...");

				HashMap<String, String> anotherServer1 = ipc.sendRequestAndGetReply(serverName,
						otherServerUDPPorts.get(0), "GETSCHEDULE", null, patientID, null);

				logger.info(
						"Calling server '" + udpServerNames.get(1) + "' via UDP on port " + otherServerUDPPorts.get(1)
								+ " for Inter Process Communication " + "to get Appointment Schedule (if any)...");
				HashMap<String, String> anotherServer2 = ipc.sendRequestAndGetReply(serverName,
						otherServerUDPPorts.get(1), "GETSCHEDULE", null, patientID, null);

				String server1 = anotherServer1.get(patientID);
				logger.info("Appointment Schedule from server '" + udpServerNames.get(0) + "' has been received");

				String server2 = anotherServer2.get(patientID);
				logger.info("Appointment Slots from server '" + udpServerNames.get(1) + "' has been received");

				if (!server1.isEmpty()) {
					if (server1.split(", \\[") != null) {
						String[] temp = server1.split(", \\[");
						for (String a : temp) {
							appointmentSchedule.add(a);
						}
					} else {
						appointmentSchedule.add(server1);
					}

				}
				if (!server2.isEmpty()) {
					if (server2.split(", \\[") != null) {
						String[] temp = server2.split(", \\[");
						for (String a : temp) {
							appointmentSchedule.add(a);
						}
					} else {
						appointmentSchedule.add(server2);
					}
				}

			}

		}
		logger.info("Appointment Schedule has been sent to the " + role + " with " + role + " ID '" + userID + "'");
		return appointmentSchedule.toArray(String[]::new);
	}

	@Override
	public String cancelAppointment(String patientID, String appointmentID, String appointmentType) {
		appointmentID = appointmentID.toUpperCase();
		logger.info(role + " with " + role + " ID '" + patientID + "' is requesting "
				+ "to cancel an Appointment in the Appointment Type '" + appointmentType + "' with an Appointment ID '"
				+ appointmentID + "' ");
		String code = appointmentID.substring(0, 3);
		if (appointmentID.length() != 10) {
			logger.warning("Length of the Appointment ID should " + "not be less or greater than 10! Please re-enter!");
			return "Length of the Appointment ID should " + "not be less or greater than 10! Please re-enter!";
		} else if (!serverCodes.contains(code) && patientID.substring(3, 4).toUpperCase().equals("P")) {
			logger.warning("Appointment ID '" + appointmentID + "' contains invalid city code, "
					+ "which doesn't match cities servers! " + "Please re-enter!");
			return "Appointment ID " + appointmentID + " contains invalid city code, "
					+ "which doesn't match cities servers! " + "Please re-enter!";
		} else if (!database.containsKey(appointmentType)) {
			logger.warning("Invalid Appointment Type '" + appointmentType + "'!");
			return "Invalid Appointment Type " + appointmentType + "!";
		} else {
			try {
				Integer.parseInt(appointmentID.substring(4, 10));
			} catch (NumberFormatException e) {
				logger.warning("Appointment ID '" + appointmentID
						+ "' should contain proper date in numeric format (DDMMYY)!");
				return "Appointment ID " + appointmentID + " should contain proper date in numeric format (DDMMYY)!";

			}
			if (code.equals(serverCode)) {
				logger.info("Checking whether there is any Appointment for the " + role + " with the " + role + " ID '"
						+ patientID + "' in the current" + " server '" + serverName + "'...");
				if (bookings.containsKey(patientID)) {
					final String id = appointmentID;
					logger.info("Checking whether there is any Appointment with Appointment" + "ID '" + appointmentID
							+ "' in the Appointment Type '" + appointmentType + "' for the " + role + " with the "
							+ role + " ID '" + patientID + "' in the current" + " server '" + serverName + "'...");

					ArrayList<Booking> patientBookings = bookings.get(patientID);
					Booking booking = patientBookings.stream()
							.filter(e -> e.getAppointmentType().equals(appointmentType))
							.filter(e -> e.getAppointmentID().equals(id)).findAny().orElse(null);
					if (booking == null) {
						logger.warning("No booking appointment schedule found of the Patient ID '" + patientID
								+ "for the Appointment ID '" + appointmentID + "' in the Appointment Type "
								+ appointmentType + "'");
						return "No booking appointment schedule found of the Patient ID " + patientID
								+ "\nfor the Appointment ID " + appointmentID + " in the Appointment Type "
								+ appointmentType;
					}
					int index = patientBookings.indexOf(booking);
					patientBookings.remove(index);

					bookings.put(patientID, patientBookings);
					HashMap<String, AppointmentSlot> slots = database.get(appointmentType);
					AppointmentSlot slot = slots.get(appointmentID);
					slot.setCapacity(slot.getCapacity() + 1);
					slots.put(appointmentID, slot);
					database.put(appointmentType, slots);
					logger.info("Booking appointment schedule of the Patient ID " + patientID
							+ " for the Appointment ID " + appointmentID + " in the Appointment Type " + appointmentType
							+ " has been " + " cancelled successfully!");
					return "Booking appointment schedule of the Patient ID " + patientID + " for the \nAppointment ID "
							+ appointmentID + " in the Appointment Type " + appointmentType + " has been "
							+ " cancelled successfully!";
				} else {
					logger.warning("No booking appointment schedule found of the Patient ID '" + patientID + "'");
					return "No booking appointment schedule found of the Patient ID " + patientID;
				}

			} else {
				logger.info("Appointment ID '" + appointmentID + "' is a foreign " + "server Appointment ID");
				logger.info("Checking whether " + role + " with " + role + " ID '" + patientID
						+ "' had booked any appointments in foreign server or " + "not");
				if (!foreignBookingCount.containsKey(patientID) || foreignBookingCount.get(patientID) == 0) {
					logger.warning("No booking appointment schedule found in the foreign server of the Patient ID '"
							+ patientID + "' with Appointment ID '" + appointmentID + "' in the Appointment Type '"
							+ appointmentType + "'");
					return "No booking appointment schedule found in the foreign server of the Patient ID " + patientID
							+ " with Appointment ID " + appointmentID + " in the Appointment Type " + appointmentType;
				}
				logger.info("Identifying foreign server...");

				List<String> udpServerName = new ArrayList<String>();
				int otherServerUDPPort = UDPPorts.entrySet().stream().filter(a -> a.getKey().contains(code)).map(a -> {
					udpServerName.add(a.getKey());
					return a.getValue();
				}).findAny().orElse(null);
				logger.info("Identified Server '" + udpServerName.get(0) + "'");
				logger.info("Calling server '" + udpServerName.get(0) + "' via UDP on port " + otherServerUDPPort
						+ " for Inter Process Communication " + "to cancel an Appointment...");

				HashMap<String, String> bookOtherServers = ipc.sendRequestAndGetReply(serverName, otherServerUDPPort,
						"CANCEL", appointmentType, patientID, appointmentID);
				String response = bookOtherServers.get("Message");
				if (response.toLowerCase().contains("successfully")) {
					int count = foreignBookingCount.get(patientID);
					foreignBookingCount.put(patientID, count - 1);
				}
				logger.info("Foreign server " + udpServerName.get(0) + "' Response: " + response);
				return response;

			}
		}
	}

	@Override
	public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType,
			String newAppointmentID, String newAppointmentType) {
		oldAppointmentID = oldAppointmentID.toUpperCase();
		newAppointmentID = newAppointmentID.toUpperCase();
		logger.info(role + " with " + role + " ID '" + patientID + "' is requesting "
				+ "to swap an old Appointment ID '" + oldAppointmentID + "' " + "in the Appointment Type '"
				+ oldAppointmentType + "' with a new" + " Appointment ID '" + newAppointmentID + "' in the"
				+ " Appointment Type '" + newAppointmentType + "'");
		String code1 = oldAppointmentID.substring(0, 3);
		String code2 = newAppointmentID.substring(0, 3);
		if (oldAppointmentID.length() != 10) {
			logger.warning(
					"Length of the  old Appointment ID should " + "not be less or greater than 10! Please re-enter!");
			return "Length of the old Appointment ID should " + "not be less or greater than 10! Please re-enter!";
		} else if (newAppointmentID.length() != 10) {
			logger.warning(
					"Length of the  new Appointment ID should " + "not be less or greater than 10! Please re-enter!");
			return "Length of the new Appointment ID should " + "not be less or greater than 10! Please re-enter!";
		} else if (!serverCodes.contains(code1) && patientID.substring(3, 4).toUpperCase().equals("P")) {
			logger.warning("Old Appointment ID '" + oldAppointmentID + "' contains invalid city code, "
					+ "which doesn't match cities servers! " + "Please re-enter!");
			return "Old Appointment ID " + oldAppointmentID + " contains invalid city code, "
					+ "which doesn't match cities servers! " + "Please re-enter!";
		} else if (!serverCodes.contains(code2) && patientID.substring(3, 4).toUpperCase().equals("P")) {
			logger.warning("New Appointment ID '" + newAppointmentID + "' contains invalid city code, "
					+ "which doesn't match cities servers! " + "Please re-enter!");
			return "New Appointment ID " + newAppointmentID + " contains invalid city code, "
					+ "which doesn't match cities servers! " + "Please re-enter!";
		} else if (!database.containsKey(oldAppointmentType)) {
			logger.warning("Invalid old Appointment Type '" + oldAppointmentType + "'!");
			return "Invalid old Appointment Type " + oldAppointmentType + "!";
		} else if (!database.containsKey(newAppointmentType)) {
			logger.warning("Invalid new Appointment Type '" + newAppointmentType + "'!");
			return "Invalid new Appointment Type " + newAppointmentType + "!";
		} else {
			try {
				Integer.parseInt(oldAppointmentID.substring(4, 10));
			} catch (NumberFormatException e) {
				logger.warning("Old Appointment ID '" + oldAppointmentID
						+ "' should contain proper date in numeric format (DDMMYY)!");
				return "Old Appointment ID " + oldAppointmentID
						+ " should contain proper date in numeric format (DDMMYY)!";

			}
			try {
				Integer.parseInt(newAppointmentID.substring(4, 10));
			} catch (NumberFormatException e) {
				logger.warning("New Appointment ID '" + newAppointmentID
						+ "' should contain proper date in numeric format (DDMMYY)!");
				return "New Appointment ID " + newAppointmentID
						+ " should contain proper date in numeric format (DDMMYY)!";

			}

			if (oldAppointmentID.equals(newAppointmentID) && oldAppointmentType.equals(newAppointmentType)) {

				logger.warning("Old Appointment ID '" + oldAppointmentID + "' with the old Appointment Type '"
						+ oldAppointmentType + "' and new Appointment ID '" + newAppointmentID
						+ "' with the new Appointment Type '" + newAppointmentType + "' are same! Can't be swapped!");
				return "Old Appointment ID " + oldAppointmentID + " with the old Appointment Type " + oldAppointmentType
						+ "\nand new Appointment ID " + newAppointmentID + " with the new Appointment Type "
						+ newAppointmentType + " are same! Can't be swapped!";
			}
			String response;
			if (code1.equals(serverCode)) {
				response = this.checkAlreadyBooked(patientID, oldAppointmentID, oldAppointmentType);
				if (response != null) {
					logger.info("Server Response: " + response);
					return response;
				}
			} else {
				logger.info("Old Appointment ID '" + oldAppointmentID + "' is a foreign " + "server Appointment ID");
				logger.info("Checking whether " + role + " with " + role + " ID '" + patientID
						+ "' had booked any appointments in foreign server or " + "not");
				if (!foreignBookingCount.containsKey(patientID) || foreignBookingCount.get(patientID) == 0) {
					logger.warning("No booking appointment schedule found in the foreign server of the Patient ID '"
							+ patientID + "' with Appointment ID '" + oldAppointmentID + "' in the Appointment Type '"
							+ oldAppointmentType + "'");
					return "No booking appointment schedule found in the foreign server of the Patient ID " + patientID
							+ " with Appointment ID " + oldAppointmentID + " in the Appointment Type "
							+ oldAppointmentType;
				}
				List<String> udpServerName = new ArrayList<String>();
				int otherServerUDPPort = UDPPorts.entrySet().stream().filter(a -> a.getKey().contains(code1)).map(a -> {
					udpServerName.add(a.getKey());
					return a.getValue();
				}).findAny().orElse(null);
				logger.info("Identified Server '" + udpServerName.get(0) + "'");
				logger.info("Calling server '" + udpServerName.get(0) + "' via UDP on port " + otherServerUDPPort
						+ " for Inter Process Communication " + "to check booking for swapping an Appointment...");

				HashMap<String, String> bookOtherServers = ipc.sendRequestAndGetReply(serverName, otherServerUDPPort,
						"SWAPBOOKCHECK", oldAppointmentType, patientID, oldAppointmentID);
				response = bookOtherServers.get("Message");
				if (response.trim().length() > 4) {
					logger.info("Foreign server " + udpServerName.get(0) + "' Response: " + response);
					return response;
				}
			}
			if (code2.equals(serverCode)) {
				response = this.checkSlotsAvailability(newAppointmentID, newAppointmentType);
				if (response != null) {
					logger.info("Server Response: " + response);
					return response;
				}
			} else {
				logger.info("New Appointment ID '" + newAppointmentID + "' is a foreign " + "server Appointment ID");
				logger.info("Identifying foreign server...");
				List<String> udpServerName = new ArrayList<String>();
				int otherServerUDPPort = UDPPorts.entrySet().stream().filter(a -> a.getKey().contains(code2)).map(a -> {
					udpServerName.add(a.getKey());
					return a.getValue();
				}).findAny().orElse(null);
				logger.info("Identified Server '" + udpServerName.get(0) + "'");
				logger.info("Calling server '" + udpServerName.get(0) + "' via UDP on port " + otherServerUDPPort
						+ " for Inter Process Communication "
						+ "to check slot availability for swapping an Appointment...");

				HashMap<String, String> bookOtherServers = ipc.sendRequestAndGetReply(serverName, otherServerUDPPort,
						"SWAPSLOTAVAILABILITYCHECK", newAppointmentType, patientID, newAppointmentID);
				response = bookOtherServers.get("Message");
				if (response.trim().length() > 4) {
					logger.info("Foreign server " + udpServerName.get(0) + "' Response: " + response);
					return response;
				}
			}
			response = this.cancelAppointment(patientID, oldAppointmentID, oldAppointmentType);
			if (response.toLowerCase().contains("success")) {
				response = this.bookAppointment(patientID, newAppointmentID, newAppointmentType);
				if (response.toLowerCase().contains("success")) {
					logger.info("Old Appointment ID '" + oldAppointmentID + "' in the old Appointment Type '"
							+ oldAppointmentType + "' has been swapped  with new Appointment ID '" + newAppointmentID
							+ "' in the new Appointment Type '" + newAppointmentType + "' successfully!");
					response = "Old Appointment ID " + oldAppointmentID + " in the old Appointment Type\n"
							+ oldAppointmentType + " has been swapped  with new Appointment ID " + newAppointmentID
							+ " in the new \nAppointment Type " + newAppointmentType + " successfully!";
				}
			}
			return response;
		}
	}

	public String checkAlreadyBooked(String patientID, String oldAppointmentID, String oldAppointmentType) {
		logger.info("Checking whether there is any old Appointment for the " + role + " with the " + role + " ID '"
				+ patientID + "' in the current" + " server '" + serverName + "'...");
		if (bookings.containsKey(patientID)) {
			final String id = oldAppointmentID;
			logger.info("Checking whether there is any Appointment with old Appointment" + "ID '" + oldAppointmentID
					+ "' in the old Appointment Type '" + oldAppointmentType + "' for the " + role + " with the " + role
					+ " ID '" + patientID + "' in the current" + " server '" + serverName + "'...");

			ArrayList<Booking> patientBookings = bookings.get(patientID);
			Booking booking = patientBookings.stream().filter(e -> e.getAppointmentType().equals(oldAppointmentType))
					.filter(e -> e.getAppointmentID().equals(id)).findAny().orElse(null);
			if (booking == null) {
				logger.warning("No booking appointment schedule found of the Patient ID '" + patientID
						+ "for the old Appointment ID '" + oldAppointmentID + "' in the old Appointment Type '"
						+ oldAppointmentType + "'");
				return "No booking appointment schedule found of the Patient ID " + patientID
						+ "\nfor the old Appointment ID " + oldAppointmentID + " in the old Appointment Type "
						+ oldAppointmentType;
			}
		} else {
			logger.warning("No booking appointment schedule found of the Patient ID '" + patientID + "'");
			return "No booking appointment schedule found of the Patient ID " + patientID;
		}
		return null;
	}

	public String checkSlotsAvailability(String newAppointmentID, String newAppointmentType) {
		HashMap<String, AppointmentSlot> slots = database.get(newAppointmentType);
		AppointmentSlot slot = slots.get(newAppointmentID);
		if (slot == null) {
			logger.warning("New Appointment ID '" + newAppointmentID + "' doesn't exists"
					+ " in the new Appointment Type '" + newAppointmentType + "'!");
			return "New Appointment ID " + newAppointmentID + " doesn't exists" + " in the new Appointment Type\n"
					+ newAppointmentType + "!";
		}
		int capacity = slot.getCapacity();
		if (capacity == 0) {
			logger.warning("No empty slots available for new Appointment ID '" + newAppointmentID + "' in the new "
					+ "Appointment Type '" + newAppointmentType + "'! Swap with another Appointment ID!");
			return "No empty slots available for new Appointment ID " + newAppointmentID + " in the new "
					+ "Appointment Type " + newAppointmentType + "!\n" + "Swap with another Appointment ID!";
		}
		return null;
	}
	
}
