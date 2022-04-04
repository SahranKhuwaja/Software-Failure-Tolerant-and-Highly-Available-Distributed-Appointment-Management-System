package DAMS.Replicas.Replica2.server.domain;


public enum UserType {
    ADMIN("Admin"),
    PATIENT("Patient");

    private String description;
    private String code;

    UserType(String description) {
        this.description = description;
        this.code = description.substring(0, 1);
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
