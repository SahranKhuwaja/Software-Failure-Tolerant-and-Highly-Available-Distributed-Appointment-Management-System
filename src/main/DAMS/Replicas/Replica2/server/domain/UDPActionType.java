package DAMS.Replicas.Replica2.server.domain;

public enum UDPActionType {
    LIST_APPOINTMENT_AVAILABILITY("List appointment availability"),
    VALIDATE_BOOK_APPOINTMENT("Validate book appointment"),
    BOOK_APPOINTMENT("Book appointment"),
    VALIDATE_CANCEL_APPOINTMENT("Validate cancel appointment"),
    CANCEL_APPOINTMENT("Cancel appointment"),
    GET_APPOINTMENT_SCHEDULE("Get appointment schedule");

    private String description;

    UDPActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
