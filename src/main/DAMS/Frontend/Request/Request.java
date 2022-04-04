package DAMS.Frontend.Request;

import java.io.Serializable;

public class Request implements Serializable {
    String serverCode;
    String userID;
    String operation;
    String patientID;
    String appointmentID;
    String appointmentType;
    String appointmentDescription;
    int capacity;
    String operationType;
    String performedBy;
    String oldAppointmentID;
    String oldAppointmentType;
    Integer sequenceNumber;

    public Request(String serverCode, String userID, String operation) {
        this.serverCode = serverCode;
        this.userID = userID;
        this.operation = operation;
    }

    public Request(String serverCode, String userID, String operation, String appointmentID, String appointmentType, String appointmentDescription, int capacity) {
        this.serverCode = serverCode;
        this.userID = userID;
        this.operation = operation;
        this.appointmentID = appointmentID;
        this.appointmentType = appointmentType;
        this.appointmentDescription = appointmentDescription;
        this.capacity = capacity;
    }

    public Request(String serverCode, String userID, String operation, String patientID, String appointmentType, String operationType, String performedBy) {
        this.serverCode = serverCode;
        this.userID = userID;
        this.operation = operation;
        this.patientID = patientID;
        this.appointmentType = appointmentType;
        this.operationType = operationType;
        this.performedBy = performedBy;
    }

    public Request(String serverCode, String userID, String operation, String appointmentType) {
        this.serverCode = serverCode;
        this.userID = userID;
        this.operation = operation;
        this.appointmentType = appointmentType;
    }

    public Request(String serverCode, String userID, String operation, String appointmentID, String appointmentType) {
        this.serverCode = serverCode;
        this.userID = userID;
        this.operation = operation;
        this.appointmentID = appointmentID;
        this.appointmentType = appointmentType;
    }

    public Request(String serverCode, String userID, String operation, String patientID, String appointmentID, String appointmentType) {
        this.serverCode = serverCode;
        this.userID = userID;
        this.operation = operation;
        this.patientID = patientID;
        this.appointmentID = appointmentID;
        this.appointmentType = appointmentType;
    }

    public Request(String serverCode, String userID, String operation, String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) {
        this.serverCode = serverCode;
        this.userID = userID;
        this.operation = operation;
        this.patientID = patientID;
        this.oldAppointmentID = oldAppointmentID;
        this.oldAppointmentType = oldAppointmentType;
        this.appointmentID = newAppointmentID;
        this.appointmentType = newAppointmentType;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getserverCode() {
        return serverCode;
    }

    public void setserverCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
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

    public String getAppointmentDescription() {
        return appointmentDescription;
    }

    public void setAppointmentDescription(String appointmentDescription) {
        this.appointmentDescription = appointmentDescription;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getOldAppointmentID() {
        return oldAppointmentID;
    }

    public void setOldAppointmentID(String oldAppointmentID) {
        this.oldAppointmentID = oldAppointmentID;
    }

    public String getOldAppointmentType() {
        return oldAppointmentType;
    }

    public void setOldAppointmentType(String oldAppointmentType) {
        this.oldAppointmentType = oldAppointmentType;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
