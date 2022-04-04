package DAMS.Replicas.Replica2.server.domain;

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
}
