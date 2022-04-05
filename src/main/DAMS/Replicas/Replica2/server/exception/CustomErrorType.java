package DAMS.Replicas.Replica2.server.exception;

public enum CustomErrorType {
    ERROR_USER_NOT_FOUND("User is not found"),
    ERROR_INVALID_PATIENT("No valid patient found"),
    ERROR_EXCEED_3_APPOINTMENTS_IN_OTHER_CITIES("User already has 3 appointments in a week in other cities"),
    ERROR_INVALID_APPOINTMENT_ID("Appointment ID %s doesn't exists in the Appointment Type %s!"),
    ERROR_USER_INVALID_APPOINTMENT_ID("No booking appointment schedule found of the Patient ID %s"),
    ERROR_UNAVAILABLE_APPOINTMENT_SLOT("No empty slots available for Appointment ID %s in the Appointment Type %s! Book with another Appointment ID!"),
    ERROR_SAME_APPOINTMENT_BOOKED("Appointment ID %s in same Appointment Type %s already booked! Cannot book twice!"),
    ERROR_SAME_APPOINTMENT_TYPE_ON_SAME_DAY_BOOKED("User already has appointment of same type on the same day booked"),
    ERROR_USER_NO_APPOINTMENTS("User does not have any appointments"),
    ERROR_CANCEL_APPOINTMENT_USER_MISMATCH("User is not authorized to cancel appointment booked by another user");

    private String description;

    CustomErrorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
