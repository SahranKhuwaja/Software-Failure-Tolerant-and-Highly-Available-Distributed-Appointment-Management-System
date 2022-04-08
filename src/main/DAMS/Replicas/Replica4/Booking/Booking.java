package DAMS.Replicas.Replica4.Booking;

import java.io.Serializable;

public class Booking implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String appointmentID;
	String appointmentType;
	public Booking(String appointmentID, String appointmentType){
		super();
		this.appointmentID = appointmentID;
		this.appointmentType = appointmentType;
	}
	public String getAppointmentID() {
		return appointmentID;
	}
	public void setAppointmentID(String appointmentID) {
		this.appointmentID = appointmentID;
	}
	public String getAppointmentType() {
		return appointmentType;
	}
	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
	}
	@Override
	public String toString() {
		return "appointmentID=" + appointmentID + ", "
				+ "appointmentType=" + appointmentType;
	}
	
	
	
}
