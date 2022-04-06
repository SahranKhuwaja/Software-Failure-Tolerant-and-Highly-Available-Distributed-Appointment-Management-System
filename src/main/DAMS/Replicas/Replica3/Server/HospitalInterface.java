package DAMS.Replicas.Replica3.Server;


import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

public interface HospitalInterface {

    String swapAppointment (String patientID,String oldAppointmentID,String oldAppointmentType,String newAppointmentID,String newAppointmentType);

    String addAppointment (String appointmentID, String appointmentType, Integer capacity);

    String removeAppointment (String appointmentID, String appointmentType);

    String listAppointmentAvailability (String appointmentType);

    String bookAppointment (String patientID, String appointmentID, String appointmentType);

    String getAppointmentSchedule (String patientID);

    String cancelAppointment (String patientID, String appointmentID, String appointmentType);

}
