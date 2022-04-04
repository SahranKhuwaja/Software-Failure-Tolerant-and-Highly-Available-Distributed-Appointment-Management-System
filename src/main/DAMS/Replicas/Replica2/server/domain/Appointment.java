package DAMS.Replicas.Replica2.server.domain;

import java.io.Serializable;
import java.time.LocalDate;

import static DAMS.Replicas.Replica2.server.util.CommonUtil.*;

public class Appointment implements Serializable {
    private String id;
    private CityType cityType;
    private AppointmentType appointmentType;
    private TimeSlotType timeSlotType;
    private LocalDate date;
    private int capacity;

    public Appointment() {
    }

    public Appointment(String appointmentId, AppointmentType appointmentType, int capacity) {
        this.cityType = getCityCodeFromId(appointmentId);
        this.date = getDateFromAppointmentId(appointmentId);
        this.timeSlotType = getTimeSlotTypeFromAppointmentId(appointmentId);
        this.id = generateAppointmentId(cityType, timeSlotType, date);
        this.appointmentType = appointmentType;
        this.capacity = capacity;
    }

    public String id() {
        return id;
    }

    public void setId(CityType cityType, TimeSlotType timeSlotType, LocalDate date) {
        this.id = generateAppointmentId(cityType, timeSlotType, date);
    }

    public CityType cityType() {
        return cityType;
    }

    public void setCityType(CityType cityType) {
        this.cityType = cityType;
    }

    public AppointmentType apointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public TimeSlotType timeSlotType() {
        return timeSlotType;
    }

    public void setTimeSlotType(TimeSlotType timeSlotType) {
        this.timeSlotType = timeSlotType;
    }

    public LocalDate date() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int capacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

}
