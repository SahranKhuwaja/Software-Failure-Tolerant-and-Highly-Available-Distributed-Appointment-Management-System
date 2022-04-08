package DAMS.Replicas.Replica4.Interfaces;


import DAMS.ResponseWrapper.ResponseWrapper;




public interface DistributedAppointment {
	
	//Other Functions
	public String generateId(String role);
    public String serverResponse();
	public boolean authenticateUser(String userID, String role);
	
	//Main Core Functions
	public String[] getAppointmentTypes();
	public String[] getTimeSlots();
	public String addAppointment(String appointmentID, String appointmentType, 
			String appointmentDescription, int capacity); 
	//ResponseWrapper is used for sending HashMaps
	public ResponseWrapper viewAppointment(String appointmentType);

	public String removeAppointment (String appointmentID, String appointmentType);
	//ResponseWrapper is used for sending HashMaps
	public ResponseWrapper listAppointmentAvailability(String appointmentType);
	public String bookAppointment (String patientID, String appointmentID, 
			String appointmentType);
	public String[] getAppointmentSchedule (String patientID);
	public String cancelAppointment (String patientID, String appointmentID, 
			String appointmentType);
	public String swapAppointment (String patientID, String oldAppointmentID, 
			String oldAppointmentType, String newAppointmentID, 
				String newAppointmentType);
}