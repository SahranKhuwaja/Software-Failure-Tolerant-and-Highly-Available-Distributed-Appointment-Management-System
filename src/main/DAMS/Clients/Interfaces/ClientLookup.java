package DAMS.Clients.Interfaces;


public interface ClientLookup {
	public void lookup(String ip, int port, String serverName);
	public boolean isAuthenticated(String userId);
	public void generateNewID();
	public void getPath();
	public void generateDirectory();
	public void setupLogs();
	public void authenticated(boolean isValid);
	public void displayMenuForAdmin();
	public void displayMenuForPatient();
	public int adminMenu();
	public void displayAppointmentTypes();
	public void addAppointment();
	public void changeOption(String operation);
	public void changeOptionP(String operation);
	public void displayAddAppointmentSubMenu(int option); 
	public void getAppointmentID(String appointmentType);
	public boolean validateAppointmentID(String appointmentID);
	public void getAppointmentDetails(String appointmentType,String appointmentID);
	public String getAppointmentDescription();
	public int getAppointmentCapacity();
	public void viewCurrentApointmentSlots();
	public void displayListCurrentAppointmentSubMenu(int option);
	public void getAppointmentSlotsFromCurrentServerAndList (String appointmentType);
	public void removeAppointment();
	public void removeAppointmentSubMenu(int option);
	public void getAppointmentIDToDelete(String appointmentType);
	public void listAvailability();
	public void displayListAppointmentSubMenu(int option);
	public void getAppointmentSlotsFromAllServersAndList(String appointmentType);
	public int patientMenu();
	public void bookAppointment();
	public void displayBookAppointmentSubMenu(int option);
	public void getAppointmentSlotsForBooking(String appointmentType);
	public void viewAppointmentSchedule();
	public void cancelAppointment();
	public void displayCancelAppointmentSubMenu(int option);
	public void getAppointmentIDToCancel(String appointmentType);
	public void swapAppointment();
	public void displaySwapAppointmentSubMenu(int option);
	public void getOldAppointmentIDForSwapping(String oldAppointmentType);
	public void getNewAppointmentTypeForSwapping(String oldAppointmentID, String oldAppointmentType);
	public void getNewAppointmentTypeForSwapping(String oldAppointmentID, String oldAppointmentType,
			String newAppointmentType);
	public void swapID(String id);
}
