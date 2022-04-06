package DAMS.Replicas.Replica2.server.domain;

import java.util.Arrays;

public enum AppointmentType {
    PHYSICIAN("Physician"),
    SURGEON("Surgeon"),
    DENTAL("Dental");

    private String description;

    AppointmentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static AppointmentType getAppointmentTypeFromDescription(String description) {
        return Arrays.stream(AppointmentType.values())
                .filter(appointmentType -> appointmentType.getDescription().equals(description))
                .findFirst()
                .orElse(null);
    }
}
