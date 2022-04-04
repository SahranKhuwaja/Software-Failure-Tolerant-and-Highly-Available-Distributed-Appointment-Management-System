package DAMS.Replicas.Replica2.server;

import DAMS.Replicas.Replica2.server.domain.*;
import DAMS.Replicas.Replica2.server.exception.CustomException;

import java.util.HashMap;
import java.util.HashSet;

public interface AppointmentBooking {

    User login(UserType userType, String userID) throws CustomException;

    //Admin role functions
    Response addAppointment(String appointmentID, AppointmentType appointmentType, int capacity);

    Response removeAppointment(String appointmentID, AppointmentType appointmentType, User requester);

    MapResponse<Integer> listAppointmentAvailability(AppointmentType appointmentType);

    //Patient role functions
    Response bookAppointment(User requester, String patientID, String appointmentID, AppointmentType appointmentType);

    Response cancelAppointment(User requester, String patientID, String appointmentID);

    ListResponse<UserAppointment> getAppointmentSchedule(String patientID);

    Response swapAppointment(User requester, String patientID, String oldAppointmentID, AppointmentType oldAppointmentType, String newAppointmentID, AppointmentType newAppointmentType);

    //UDP helper functions - not required to expose to client
    HashMap<String, Appointment> getAppointmentsByType(AppointmentType appointmentType);

    HashSet<UserAppointment> getPatientAppointments(String patientID);

    Response validateBookAppointment(String patientID, String appointmentID, AppointmentType appointmentType);

    Response validateCancelAppointment(User requester, String patientID, String appointmentID);

    //Extra functions
    String[] getAppointmentTypes();

    String[] getTimeSlots();


}
