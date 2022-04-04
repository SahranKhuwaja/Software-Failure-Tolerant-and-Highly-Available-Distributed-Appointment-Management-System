package DAMS.Clients.WebService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import DAMS.Clients.Interfaces.ClientLookup;
import DAMS.Clients.Operations.Operations;
import DAMS.Frontend.Interfaces.RemoteDistributedAppointmentFrontend;


public class WebServiceClientOperations implements ClientLookup {

	RemoteDistributedAppointmentFrontend rda;
	String role;
	Operations op;
	Logger logger;
	String serverCode;
	String[] appointmentTypes;
	String userID;
	String path;

	public WebServiceClientOperations(String role, Operations op) {
		this.role = role;
		this.op = op;
		this.getPath();
		this.generateDirectory();
	}

	@Override
	public void lookup(String ip, int port, String serverName) {
		try {
			URL url = new URL("http://" + ip + ":" + port +
					"/RemoteDistributedAppointmentFrontend?wsdl");
			QName serverUrl = new QName("http://WebService.Frontend.DAMS/",
					"RemoteDistributedAppointmentFrontendWebServiceService");
			Service service = Service.create(url,serverUrl);
			rda = service.getPort(RemoteDistributedAppointmentFrontend.class);
			System.out.println("Server Response: " + rda.serverResponse());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public boolean isAuthenticated(String userId) {
		boolean isValid = false;
		try {
			System.out.println("Authenticating " + role + "ID...");
			isValid = rda.authenticateUser(userId, role);
			if (isValid) {
				System.out.println("Server Response: You are successfully authenticated!");
				System.out.println("Logged in as: " + userId);
				serverCode = op.serverName.toUpperCase().split(" ")[1].replace("(", "").replace(")", "");
				userID = userId;
				this.setupLogs();
				authenticated(isValid);
			} else {
				System.out.println("Server Response: Access Denied! " + role + "ID " + userId + " not found!");
				Operations.userId = null;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return isValid;
	}

	@Override
	public void getPath() {
		path = System.getProperty("user.dir");
		if (path.contains("/bin")) {
			path = path.split("/bin")[0];
		}
		if (path.contains("/src")) {
			path = path.split("/src")[0];
		}
	}

	@Override
	public void generateDirectory() {
		File f1 = new File(path + "/Generated Files/Client Logs");
		if (!f1.exists()) {
			f1.mkdir();
		}
	}

	@Override
	public void setupLogs() {
		FileHandler handler = null;
		logger = Logger.getLogger(role + " Client");
		logger.setUseParentHandlers(false);
		try {
			handler = new FileHandler(path + "/Generated Files/Client Logs/" + userID + ".txt", true);
		} catch (SecurityException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		logger.addHandler(handler);
		SimpleFormatter formatter = new SimpleFormatter();
		handler.setFormatter(formatter);
	}

	@Override
	public void generateNewID() {
		String userID = null;
		try {
			userID = rda.generateId(role);
		} catch (Exception e) {
			System.out.println(e);
		}
		this.userID = userID;
		System.out.println("Server Response: You are successfully authenticated!");
		System.out.println("Logged in as: " + userID);
		serverCode = op.serverName.toUpperCase().split(" ")[1].replace("(", "").replace(")", "");
		this.setupLogs();
		authenticated(true);

	}

	@Override
	public void authenticated(boolean isValid) {
		logger.info("Server Response: You are connected to the " + op.serverName + " server!");
		logger.info("Server Response: You are successfully authenticated!");
		if (role.equals("Admin")) {
			logger.info("Logged in as Admin with Admin ID: " + userID);
			displayMenuForAdmin();

		} else {
			logger.info("Logged in as Patient with Patient ID: " + userID);
			displayMenuForPatient();
		}
	}

	@Override
	public void displayMenuForAdmin() {
		String temp;
		while (true) {
			int option = this.adminMenu();
			switch (option) {
			case 1:
				logger.info("Add Appointment Slot selected");
				this.addAppointment();
				this.changeOption(null);
				break;
			case 2:
				logger.info("View Appointment Slots selected");
				this.viewCurrentApointmentSlots();
				this.changeOption(null);
				break;
			case 3:
				logger.info("Remove Appointment selected");
				this.removeAppointment();
				this.changeOption(null);
				break;
			case 4:
				logger.info("View Appointment Types selected");
				this.displayAppointmentTypes();
				this.changeOption(null);
				break;
			case 5:
				logger.info("List Availibity (from all servers) selected");
				this.listAvailability();
				this.changeOption(null);
				break;
			case 6:
				logger.info("Book Appointment selected");
				op.role = "Patient";
				op.haveUserId(userID);
				temp = userID;
				this.swapID(Operations.userId);
				role = "Patient";
				this.bookAppointment();
				this.changeOptionP(null);
				this.swapID(temp);
				op.role = "Admin";
				role = "Admin";
				break;
			case 7:
				logger.info("View Appointment Schedule selected");
				op.role = "Patient";
				op.haveUserId(userID);
				temp = userID;
				this.swapID(Operations.userId);
				role = "Patient";
				this.viewAppointmentSchedule();
				this.changeOptionP(null);
				this.swapID(temp);
				op.role = "Admin";
				role = "Admin";
				break;
			case 8:
				logger.info("Cancel Appointment selected");
				op.role = "Patient";
				op.haveUserId(userID);
				temp = userID;
				this.swapID(Operations.userId);
				role = "Patient";
				this.cancelAppointment();
				this.changeOptionP(null);
				this.swapID(temp);
				;
				op.role = "Admin";
				role = "Admin";
				break;
			case 9:
				logger.info("Swap Appointment selected");
				op.role = "Patient";
				op.haveUserId(userID);
				temp = userID;
				this.swapID(Operations.userId);
				role = "Patient";
				this.swapAppointment();
				this.changeOptionP(null);
				this.swapID(temp);
				op.role = "Admin";
				role = "Admin";
				break;
			}
		}
	}

	@Override
	public void displayMenuForPatient() {
		while (true) {
			int option = this.patientMenu();
			switch (option) {
			case 1:
				logger.info("Book Appointment selected");
				this.bookAppointment();
				this.changeOptionP(null);
				break;
			case 2:
				logger.info("View Appointment Schedule selected");
				this.viewAppointmentSchedule();
				this.changeOptionP(null);
				break;
			case 3:
				logger.info("Cancel Appointment selected");
				this.cancelAppointment();
				this.changeOptionP(null);
				break;
			case 4:
				logger.info("Swap Appointment selected");
				this.swapAppointment();
				this.changeOptionP(null);
				break;

			}
		}
	}

	@Override
	public int adminMenu() {
		System.out.println("Welcome Admin to the " + op.serverName + " Hospital Server");
		System.out.println(" ");
		System.out.println("=====================================================");
		System.out.println("|                      Main Menu                    |");
		System.out.println("|---------------------------------------------------|");
		System.out.println("|  1) Add Appointment Slots                         |");
		System.out.println("|  2) View Appointment Slots (Current Server)       |");
		System.out.println("|  3) Delete/Remove Appointment Slots               |");
		System.out.println("|  4) View Appointment Types                        |");
		System.out.println("|  5) List All Hospital Servers Slots Availability  |");
		System.out.println("|  6) Book Appointments                             |");
		System.out.println("|  7) View/Get Booked Appointments Schedule         |");
		System.out.println("|  8) Cancel Appointments                           |");
		System.out.println("|  9) Swap Appointments                             |");
		System.out.println("|                                                   |");
		System.out.println("=====================================================");
		System.out.println("Please select the option:");
		int option = -1;
		logger.info("Menu with list of operations displayed");
		while (true) {
			try {
				option = Integer.parseInt(op.getUserInput());
				if (option != 1 && option != 2 && option != 3 && option != 4 && option != 5 && option != 6
						&& option != 7 && option != 8 && option != 9) {
					System.out.println("Invalid option! Please re-enter!");
					logger.warning("Invalid option! Please re-enter!");
					continue;
				}
				return option;
			} catch (NumberFormatException e) {
				System.out.println("Please enter only number values!");
				logger.warning("Please enter only number values!");
				continue;
			}
		}

	}

	@Override
	public int patientMenu() {
		System.out.println("Welcome Patient to the " + op.serverName + " Hospital Server");
		System.out.println(" ");
		System.out.println("=====================================================");
		System.out.println("|                      Main Menu                    |");
		System.out.println("|---------------------------------------------------|");
		System.out.println("|  1) Book Appointments                             |");
		System.out.println("|  2) View/Get Booked Appointments Schedule         |");
		System.out.println("|  3) Cancel Appointments                           |");
		System.out.println("|  4) Swap Appointments                             |");
		System.out.println("|                                                   |");
		System.out.println("=====================================================");
		System.out.println("Please select the option:");
		int option = -1;
		logger.info("Menu with list of operations displayed");
		while (true) {
			try {
				option = Integer.parseInt(op.getUserInput());
				if (option != 1 && option != 2 && option != 3 && option != 4) {
					System.out.println("Invalid option! Please re-enter!");
					logger.warning("Invalid option! Please re-enter!");
					continue;
				}
				return option;
			} catch (NumberFormatException e) {
				System.out.println("Please enter only number values!");
				logger.warning("Please enter only number values!");
				continue;
			}
		}

	}

	@Override
	public void displayAppointmentTypes() {
		try {
			System.out.println("");
			System.out.println("Appointment Types");
			System.out.println("---------------------");
			if (appointmentTypes == null) {
				appointmentTypes = rda.getAppointmentTypes();
			}
			for (int i = 0; i < appointmentTypes.length; i++) {
				System.out.println(i + 1 + ") " + appointmentTypes[i]);
			}
			System.out.println("");
			logger.info("Server Response: Appointment Types displayed");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.severe(e.getMessage());
		}
	}

	@Override
	public void changeOption(String operation) {
		System.out.println("Press 0 to go back");
		int option = -1;
		while (true) {
			try {
				option = Integer.parseInt(op.getUserInput());
				if (operation == null) {
					if (option != 0) {
						op.customErrorMessage("Please enter correct value!");
						logger.warning("Please enter correct value!");
						continue;
					}
					logger.info("Back to Main Menu");
					break;
				}
				if (option != 0 && option != 1 && option != 2 && option != 3) {
					op.customErrorMessage("Please enter correct value!");
					logger.warning("Please enter correct value!");
					continue;
				}
				if (option == 0) {
					logger.info("Back to Main Menu");
					this.displayMenuForAdmin();
					break;
				}
				if (operation.equals("ADD")) {
					this.displayAddAppointmentSubMenu(option);
					break;
				}
				if (operation.equals("LISTCURRENT")) {
					this.displayListCurrentAppointmentSubMenu(option);
					break;
				}
				if (operation.equals("DELETE")) {
					this.removeAppointmentSubMenu(option);
					break;
				}
				if (operation.equals("LIST")) {
					this.displayListAppointmentSubMenu(option);
					break;
				}

			} catch (NumberFormatException e) {
				op.customErrorMessage("Please enter numeric values only!");
			}
		}
	}

	@Override
	public void changeOptionP(String operation) {
		System.out.println("Press 0 to go back");
		int option = -1;
		while (true) {
			try {
				option = Integer.parseInt(op.getUserInput());
				if (operation == null) {
					if (option != 0) {
						op.customErrorMessage("Please enter correct value!");
						logger.warning("Please enter correct value!");
						continue;
					}
					logger.info("Back to Main Menu");
					break;
				}
				if (option != 0 && option != 1 && option != 2 && option != 3) {
					op.customErrorMessage("Please enter correct value!");
					logger.warning("Please enter correct value!");
					continue;
				}
				if (option == 0) {
					logger.info("Back to Main Menu");
					this.displayMenuForPatient();
					break;

				}
				if (operation.equals("BOOK")) {
					this.displayBookAppointmentSubMenu(option);
					break;
				}
				if (operation.equals("CANCEL")) {
					this.displayCancelAppointmentSubMenu(option);
					break;
				}
				if (operation.equals("SWAP")) {
					this.displaySwapAppointmentSubMenu(option);
					break;
				}

			} catch (NumberFormatException e) {
				op.customErrorMessage("Please enter numeric values only!");
				logger.warning("Please enter numeric values only!");
			}
		}
	}

	@Override
	public void addAppointment() {
		System.out.println("");
		System.out.println("Add Appointment Slot \n(Current Server)");
		System.out.println("---------------------");
		System.out.println(
				"Please select the Appointment Type for " + "which you " + "want to create slot (Current Server):");
		this.displayAppointmentTypes();
		logger.info("Server Response: Appointment Types displayed to be selected for" + " adding an appointment");
		this.changeOption("ADD");

	}

	@Override
	public void displayAddAppointmentSubMenu(int option) {
		logger.info("Appointment Type '" + appointmentTypes[option - 1] + "' selected");
		this.getAppointmentID(appointmentTypes[option - 1]);
	}

	@Override
	public void getAppointmentID(String appointmentType) {
		System.out.println(" ");
		System.out.println("Appointment Type: " + appointmentType);
		System.out.println("Please enter Appointment ID!");
		System.out.println("Appointment ID should be in this format:");
		System.out.println("XXX(CityCode)X(TimeSlot)XXXXXX(Date:DDMMYY)");
		boolean isValid = false;
		String appointmentID = null;
		while (!isValid) {
			appointmentID = op.getUserInput().toUpperCase();
			logger.info("Appointment ID '" + appointmentID + "' entered");
			isValid = this.validateAppointmentID(appointmentID);
		}
		if (isValid) {
			this.getAppointmentDetails(appointmentType, appointmentID);
		}

	}

	@Override
	public boolean validateAppointmentID(String appointmentID) {
		List<String> timeSlots = null;
		try {
			timeSlots = Arrays.asList(rda.getTimeSlots());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.severe(e.getMessage());
		}
		boolean isValid = false;
		if (appointmentID.length() != 10) {
			op.customErrorMessage("Length of the Appointment ID should " + "not be less or greater than 10!");
			op.customErrorMessage("Please re-enter!");
			logger.warning("Length of the Appointment ID should not be less or greater than 10! Please re-enter!");
		} else if (!appointmentID.substring(0, 3).equals(serverCode) && !role.toUpperCase().equals("PATIENT")) {
			op.customErrorMessage("Appointment ID " + appointmentID + " contains invalid city code, "
					+ "which doesn't match current city server! " + "Please re-enter!");
			logger.warning("Appointment ID " + appointmentID + " contains invalid city code, "
					+ "which doesn't match current city server! " + "Please re-enter!");
		} else if (!op.citiesPrefixes.contains(appointmentID.substring(0, 3))
				&& role.toUpperCase().equals("PATIENT")) {
			op.customErrorMessage("Appointment ID " + appointmentID + " contains invalid city code, "
					+ "which doesn't match cities servers! " + "Please re-enter!");
			logger.warning("Appointment ID " + appointmentID + " contains invalid city code, "
					+ "which doesn't match cities servers! " + "Please re-enter!");
		} else if (!timeSlots.contains(appointmentID.substring(3, 4))) {
			op.customErrorMessage(
					"Appointment ID " + appointmentID + " contains invalid " + "time slot! Please re-enter!");
			logger.warning("Appointment ID " + appointmentID + " contains invalid " + "time slot! Please re-enter!");
		} else {
			try {
				Integer.parseInt(appointmentID.substring(4, 10));
			} catch (NumberFormatException e) {
				op.customErrorMessage("Appointment ID should contain proper date in numeric " + "format (DDMMYY)!");
				logger.warning("Appointment ID should contain proper date in numeric " + "format (DDMMYY)!");
				return isValid;
			}
			isValid = true;
		}
		return isValid;
	}

	@Override
	public void getAppointmentDetails(String appointmentType, String appointmentID) {

		String appointmentDescription = this.getAppointmentDescription();
		int appointmentCapacity = 0;
		if (appointmentDescription != null) {
			appointmentCapacity = this.getAppointmentCapacity();
			if (appointmentCapacity != 0) {
				String serverResponse = null;
				try {
					serverResponse = rda.addAppointment(appointmentID.toUpperCase(), appointmentType,
							appointmentDescription, appointmentCapacity);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				System.out.println("Server Response: " + serverResponse);
				logger.info("Server Response: " + serverResponse);
				System.out.println(" ");
			}
		}

	}

	@Override
	public String getAppointmentDescription() {
		System.out.println("Please enter description for the time slot!");
		String description = null;
		while (true) {
			description = op.getUserInput();
			logger.info("Appointment Description '" + description + "' entered");
			if (description.length() == 0) {
				op.customErrorMessage("Description can't be empty! Please" + " re-enter!");
				logger.warning("Description can't be empty! Please" + " re-enter!");
				continue;
			}
			if (description.length() < 5) {
				op.customErrorMessage("Description can't be less than 5! Please" + " re-enter!");
				logger.warning("Description can't be less than 5! Please" + " re-enter!");
				continue;
			}
			break;
		}
		return description;
	}

	@Override
	public int getAppointmentCapacity() {
		System.out.println("Please enter capacity for the time slot!");
		int capacity = 0;
		while (capacity <= 0) {
			logger.info("Appointment Description '" + capacity + "' entered");
			try {
				capacity = Integer.parseInt(op.getUserInput());
			} catch (NumberFormatException e) {
				op.customErrorMessage("Please enter only numeric values!");
				logger.warning("Please enter only numeric values!");
				continue;
			}
			if (capacity <= 0) {
				op.customErrorMessage("Capacity can't be less than and " + "equals to 0! Please re-enter!");
				logger.warning("Capacity can't be less than and " + "equals to 0! Please re-enter!");
			}
		}
		return capacity;
	}

	@Override
	public void viewCurrentApointmentSlots() {
		System.out.println("");
		System.out.println("View Appointment Availability Slots (Current Server)");
		System.out.println("-------------------------------------------------");
		System.out.println("Please select the Appointment Type for which you "
				+ "want to list the availability \nslots (current)");
		this.displayAppointmentTypes();
		logger.info("Server Response: Appointment Types displayed to be selected for"
				+ " viewing current server appointment slots");
		this.changeOption("LISTCURRENT");
	}

	@Override
	public void displayListCurrentAppointmentSubMenu(int option) {
		logger.info("Appointment Type '" + appointmentTypes[option - 1] + "' selected");
		this.getAppointmentSlotsFromCurrentServerAndList(appointmentTypes[option - 1]);

	}

	@Override
	public void getAppointmentSlotsFromCurrentServerAndList(String appointmentType) {
		HashMap<String, String> appointments = null;
		System.out.println("");
		System.out.println("Fetching... please wait!");
		System.out.println("");
		try {
			appointments = rda.viewAppointment(appointmentType).getData();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.severe(e.getMessage());

		}
		System.out.println("Availability Slots of " + appointmentType + " from Current Server");
		System.out.println("-----------------------------------");
		if (appointments.size() == 0) {
			System.out.println("Sorry! No Slots Found!");
			logger.warning("Server Response: Sorry! No Slots Found for Appointment Type '" + appointmentType + "'!");
		} else {
			System.out.println(appointmentType + "-");
			System.out.println(op.serverName + " (Current)");
			// appointments.entrySet().stream().forEach(a -> System.out.print(a.getKey() + "
			// " + a.getValue() + ", "));
			appointments.entrySet().stream().forEach(a -> System.out.print(a.getKey() + " " + a.getValue() + ", "));
			logger.info("Server Response: Availability Slots of " + appointmentType + " from current server displayed");
			System.out.println("");
			System.out.println("");
		}

	}

	@Override
	public void removeAppointment() {
		System.out.println("");
		System.out.println("Delete/Remove Appointment Slots (Current Server)");
		System.out.println("-------------------------------------------------");
		System.out.println("Please select the Appointment Type for which you "
				+ "want to delete/remove the availability \nslots (Current Server): ");
		this.displayAppointmentTypes();
		logger.info(
				"Server Response: Appointment Types displayed to be selected for" + " deleting/removing appointment");
		System.out.println("");
		this.changeOption("DELETE");
	}

	@Override
	public void removeAppointmentSubMenu(int option) {
		logger.info("Appointment Type '" + appointmentTypes[option - 1] + "' selected");
		this.getAppointmentIDToDelete(appointmentTypes[option - 1]);
	}

	@Override
	public void getAppointmentIDToDelete(String appointmentType) {
		this.getAppointmentSlotsFromCurrentServerAndList(appointmentType);
		System.out.println("");
		System.out.println("Please enter Appointment ID to delete:");
		String appointmentID;
		while (true) {
			appointmentID = op.getUserInput().toUpperCase();
			logger.info("Appointment  ID '" + appointmentID + "' entered");
			boolean isValid = this.validateAppointmentID(appointmentID);
			if (isValid) {
				break;
			}
		}
		String serverResponse = null;
		try {
			serverResponse = rda.removeAppointment(appointmentID, appointmentType);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.severe(e.getMessage());
		}
		System.out.println("Server Response: " + serverResponse);
		logger.info("Server Response: " + serverResponse);

	}

	@Override
	public void listAvailability() {
		System.out.println("");
		System.out.println("List Appointment Availability Slots (All Servers)");
		System.out.println("-------------------------------------------------");
		System.out.println("Please select the Appointment Type for which you "
				+ "want to list the availability \nfrom all servers:");
		this.displayAppointmentTypes();
		logger.info("Server Response: Appointment Types displayed to be selected for"
				+ " viewing/listing all servers appointment slots");
		this.changeOption("LIST");
	}

	@Override
	public void displayListAppointmentSubMenu(int option) {
		logger.info("Appointment Type '" + appointmentTypes[option - 1] + "' selected");
		this.getAppointmentSlotsFromAllServersAndList(appointmentTypes[option - 1]);
	}

	@Override
	public void getAppointmentSlotsFromAllServersAndList(String appointmentType) {
		HashMap<String, String> appointments = null;
		System.out.println("");
		System.out.println("Fetching... please wait!");
		System.out.println("");
		try {
			appointments = rda.listAppointmentAvailability(appointmentType).getData();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.severe(e.getMessage());
		}
		System.out.println("Availability Slots of " + appointmentType + " from All Servers");
		System.out.println("-----------------------------------");
		if (appointments.size() == 0) {
			System.out.println("Sorry! No Slots Found!");
			logger.warning("Server Response: Sorry! No Slots Found for Appointment Type '" + appointmentType + "'!");
		} else {
			System.out.println(appointmentType + "-");
			System.out.println(op.serverName + " (Current)");

			appointments.entrySet().stream().filter(e -> e.getKey().trim().substring(0, 3).equals(serverCode))
					.forEach(a -> System.out.print(a.getKey().trim() + " " + a.getValue() + ", "));

			List<String> otherServers = op.cities.stream().filter(e -> !e.equals(op.serverName))
					.collect(Collectors.toList());
			System.out.println("");
			System.out.println("");
			System.out.println(otherServers.get(0));
			appointments.entrySet().stream()
					.filter(e -> otherServers.get(0).contains(e.getKey().trim().substring(0, 3)))
					.forEach(a -> System.out.print(a.getKey().trim() + " " + a.getValue() + ", "));
			System.out.println("");
			System.out.println("");
			System.out.println(otherServers.get(1));
			appointments.entrySet().stream()
					.filter(e -> otherServers.get(1).contains(e.getKey().trim().substring(0, 3)))
					.forEach(a -> System.out.print(a.getKey().trim() + " " + a.getValue() + ", "));
			logger.info("Server Response: Availability Slots of " + appointmentType + " from all servers displayed");
			System.out.println("");
			System.out.println("");

		}
	}

	@Override
	public void bookAppointment() {
		System.out.println("");
		System.out.println("Book Appointment Slots ");
		System.out.println("---------------------");
		System.out.println("Please select the Appointment Type for " + "which you " + "want to book appointment:");
		this.displayAppointmentTypes();
		logger.info("Server Response: Appointment Types displayed to be selected for" + " booking an appointment");
		this.changeOptionP("BOOK");
	}

	@Override
	public void displayBookAppointmentSubMenu(int option) {
		logger.info("Appointment Type '" + appointmentTypes[option - 1] + "' selected");
		this.getAppointmentSlotsForBooking(appointmentTypes[option - 1]);
	}

	@Override
	public void getAppointmentSlotsForBooking(String appointmentType) {
		this.getAppointmentSlotsFromAllServersAndList(appointmentType);
		System.out.println("Please enter the Appointment ID to book:");
		System.out.println(
				"Note: You can book any many appointments as" + " you want in your own city server, \nBut you can only "
						+ "book at most 3 appointments in other cities server!");
		boolean isValid = false;
		String appointmentID = null;
		while (!isValid) {
			appointmentID = op.getUserInput().toUpperCase();
			logger.info("Appointment  ID '" + appointmentID + "' entered");
			isValid = this.validateAppointmentID(appointmentID);
		}
		if (isValid) {
			String serverResponse = null;
			System.out.println("Please wait...");
			System.out.println("");
			try {
				serverResponse = rda.bookAppointment(userID, appointmentID, appointmentType);

			} catch (Exception e) {
				System.out.println(e.getMessage());
				logger.severe(e.getMessage());
			}
			System.out.println("Server Response: " + serverResponse);
			logger.info("Server Response: " + serverResponse);
			System.out.println("");
		}
	}

	@Override
	public void viewAppointmentSchedule() {
		System.out.println("");
		System.out.println("View Appointment Schedule (Bookings) ");
		System.out.println("---------------------");
		System.out.println("Your Patient ID: " + userID);
		System.out.println(" ");
		System.out.println("Fetching... Please wait!");

		List<String> schedule = null;
		try {
			schedule = Arrays.asList(rda.getAppointmentSchedule(userID));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.severe(e.getMessage());
		}
		System.out.print("Appointment Type   ");
		System.out.print("Appointment ID     ");
		System.out.println(" ");
		System.out.println(" ");
		if (!schedule.isEmpty()) {
			schedule.stream().forEach(e -> {
				String[] temp = e.substring(1, e.length() - 1).split(", ");
				System.out.print(temp[1].split("=")[1]);
				System.out.print("       ");
				System.out.print(temp[0].split("=")[1]);
				System.out.println("");
			});

			logger.info("Server Response: Appointment Schedule displayed for Patient ID '" + userID + "'!");
		} else {
			System.out.print("Sorry! No schedule found!");
			logger.warning("Server Response: Sorry! No schedule found for Patient ID '" + userID + "'!");
			System.out.println(" ");
			System.out.println(" ");
		}
		System.out.println(" ");
	}

	@Override
	public void cancelAppointment() {
		System.out.println("");
		System.out.println("Cancel Appointment");
		System.out.println("---------------------");
		this.viewAppointmentSchedule();
		logger.info("Patient appointment schedule displayed for reference");
		System.out.println("Please select the Appointment Type for " + "which you " + "want to cancel appointment:");
		this.displayAppointmentTypes();
		logger.info("Server Response: Appointment Types displayed to be selected for" + " cancelling appointments");
		this.changeOptionP("CANCEL");
	}

	@Override
	public void displayCancelAppointmentSubMenu(int option) {
		logger.info("Appointment Type '" + appointmentTypes[option - 1] + "' selected");
		this.getAppointmentIDToCancel(appointmentTypes[option - 1]);
	}

	@Override
	public void getAppointmentIDToCancel(String appointmentType) {
		System.out.println(" ");
		System.out.println("Your Patient ID: " + userID);
		System.out.println("Appointment Type: " + appointmentType);
		System.out.println("Please enter Appointment ID!");
		boolean isValid = false;
		String appointmentID = null;
		while (!isValid) {
			appointmentID = op.getUserInput().toUpperCase();
			logger.info("Appointment  ID '" + appointmentID + "' entered");
			isValid = this.validateAppointmentID(appointmentID);
		}
		if (isValid) {
			String serverResponse = null;
			System.out.println("Please wait...");
			System.out.println("");
			try {
				serverResponse = rda.cancelAppointment(userID, appointmentID, appointmentType);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				logger.severe(e.getMessage());
			}

			System.out.println("Server Response: " + serverResponse);
			logger.info("Server Response: " + serverResponse);
			System.out.println(" ");

		}

	}

	@Override
	public void swapAppointment() {
		System.out.println("");
		System.out.println("Swap Appointment");
		System.out.println("---------------------");
		this.viewAppointmentSchedule();
		logger.info("Patient appointment schedule displayed for reference");
		System.out.println(
				"Please select the current Appointment Type for " + "which you " + "want to swap Appointment ID:");
		this.displayAppointmentTypes();
		logger.info("Server Response: Appointment Types displayed to be selected (old Appointment Type) for"
				+ " swapping appointments");
		this.changeOptionP("SWAP");
	}

	@Override
	public void displaySwapAppointmentSubMenu(int option) {
		logger.info("Old Appointment Type '" + appointmentTypes[option - 1] + "' selected");
		this.getOldAppointmentIDForSwapping(appointmentTypes[option - 1]);
	}

	@Override
	public void getOldAppointmentIDForSwapping(String oldAppointmentType) {
		System.out.println("Please enter the old Appointment ID:");
		boolean isValid = false;
		String oldAppointmentID = null;
		while (!isValid) {
			oldAppointmentID = op.getUserInput().toUpperCase();
			logger.info("Old Appointment  ID '" + oldAppointmentID + "' entered");
			isValid = this.validateAppointmentID(oldAppointmentID);
		}
		if (isValid) {
			this.getNewAppointmentTypeForSwapping(oldAppointmentID, oldAppointmentType);
		}
	}

	@Override
	public void getNewAppointmentTypeForSwapping(String oldAppointmentID, String oldAppointmentType) {
		System.out
				.println("Please select the new Appointment Type for " + "which you " + "want to swap Appointment ID:");
		this.displayAppointmentTypes();
		logger.info("Server Response: Appointment Types displayed to be selected (new Appointment Type) for"
				+ " swapping appointments");
		int option = -1;
		while (true) {
			try {
				option = Integer.parseInt(op.getUserInput());
				if (option != 1 && option != 2 && option != 3) {
					op.customErrorMessage("Please enter correct value!");
					logger.warning("Please enter correct value!");
					continue;
				}
				break;
			} catch (NumberFormatException e) {
				op.customErrorMessage("Please enter numeric values only!");
				logger.warning("Please enter numeric values only!");
			}
		}

		String newAppointmentType = appointmentTypes[option - 1];
		logger.info("New Appointment Type '" + newAppointmentType + "' selected");
		this.getNewAppointmentTypeForSwapping(oldAppointmentID, oldAppointmentType, newAppointmentType);

	}

	@Override
	public void getNewAppointmentTypeForSwapping(String oldAppointmentID, String oldAppointmentType,
			String newAppointmentType) {
		System.out.println("Please enter New Appointment ID!");
		boolean isValid = false;
		String newAppointmentID = null;
		while (!isValid) {
			newAppointmentID = op.getUserInput().toUpperCase();
			logger.info("New Appointment  ID '" + newAppointmentID + "' entered");
			isValid = this.validateAppointmentID(newAppointmentID);
		}
		if (isValid) {
			String serverResponse = null;
			System.out.println("Please wait...");
			System.out.println("");
			try {
				serverResponse = rda.swapAppointment(userID, oldAppointmentID, oldAppointmentType, newAppointmentID,
						newAppointmentType);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				logger.severe(e.getMessage());
			}

			System.out.println("Server Response: " + serverResponse);
			logger.info("Server Response: " + serverResponse);
			System.out.println(" ");

		}
	}

	@Override
	public void swapID(String id) {
		this.userID = id;
	}

}
