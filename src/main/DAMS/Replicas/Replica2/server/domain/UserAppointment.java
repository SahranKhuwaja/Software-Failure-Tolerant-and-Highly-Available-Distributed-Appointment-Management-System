package DAMS.Replicas.Replica2.server.domain;

import java.io.Serializable;

public class UserAppointment implements Serializable {

    private Appointment appointment;
    private User createdBy;

    public UserAppointment() {
    }

    public UserAppointment(Appointment appointment, User createdBy) {
        this.appointment = appointment;
        this.createdBy = createdBy;
    }

    public Appointment appointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public User createdBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
