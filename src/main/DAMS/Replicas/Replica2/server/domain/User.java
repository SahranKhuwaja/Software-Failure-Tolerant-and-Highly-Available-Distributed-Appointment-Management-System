package DAMS.Replicas.Replica2.server.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {
    private String id;
    private String name;
    private UserType userType;
    private CityType cityType;
    private HashSet<UserAppointment> appointments = new HashSet<>();

    public User() {
    }

    public User(String id, String name, UserType userType, CityType cityType) {
        this.id = id;
        this.name = name;
        this.userType = userType;
        this.cityType = cityType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserType getUserType() {
        return userType;
    }

    public void userType(UserType userType) {
        this.userType = userType;
    }

    public CityType getCityType() {
        return cityType;
    }

    public void setCityType(CityType cityType) {
        this.cityType = cityType;
    }

    public Set<UserAppointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(HashSet<UserAppointment> appointments) {
        this.appointments = appointments;
    }
}
