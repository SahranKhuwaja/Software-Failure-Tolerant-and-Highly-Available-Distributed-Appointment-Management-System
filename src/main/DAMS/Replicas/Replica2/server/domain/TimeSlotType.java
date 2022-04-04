package DAMS.Replicas.Replica2.server.domain;

import java.util.Arrays;

public enum TimeSlotType {
    MORNING("Morning"),
    AFTERNOON("Afternoon"),
    EVENING("Evening");

    private String description;
    private String code;

    TimeSlotType(String description) {
        this.description = description;
        this.code = description.substring(0, 1);
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public static TimeSlotType getTimeSlotTypeFromCode(String code) {
        return Arrays.stream(TimeSlotType.values())
                .filter(timeSlotType -> timeSlotType.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

}
