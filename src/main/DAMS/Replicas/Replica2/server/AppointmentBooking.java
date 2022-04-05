package DAMS.Replicas.Replica2.server;

import DAMS.Replicas.Replica2.server.domain.*;
import DAMS.Replicas.Replica2.server.exception.CustomException;
import DAMS.ResponseWrapper.ResponseWrapper;

import java.util.HashMap;
import java.util.HashSet;

public interface AppointmentBooking {

    //User login(UserType userType, String userID) throws CustomException;

    //Admin role functions
    String addAppointment(String appointmentID, AppointmentType appointmentType, int capacity);

    String removeAppointment(String appointmentID, AppointmentType appointmentType, String requesterUserId);

    ResponseWrapper listAppointmentAvailability(AppointmentType appointmentType);

    //Patient role functions
    String bookAppointment(String requesterUserId, String patientID, String appointmentID, AppointmentType appointmentType);

    String cancelAppointment(String requesterUserId, String patientID, String appointmentID);

    String[] getAppointmentSchedule(String patientID);

    String swapAppointment(String requesterUserId, String patientID, String oldAppointmentID, AppointmentType oldAppointmentType, String newAppointmentID, AppointmentType newAppointmentType);

    //UDP helper functions - not required to expose to client
    HashMap<String, Appointment> getAppointmentsByType(AppointmentType appointmentType);

    HashSet<UserAppointment> getPatientAppointments(String patientID);

    Response validateBookAppointment(String patientID, String appointmentID, AppointmentType appointmentType);

    Response validateCancelAppointment(User requester, String patientID, String appointmentID);

    //Extra functions
    String[] getAppointmentTypes();

    String[] getTimeSlots();

    //TODO: view appointment type

}
