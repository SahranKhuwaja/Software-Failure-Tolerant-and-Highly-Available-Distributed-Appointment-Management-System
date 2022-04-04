package DAMS.Replicas.Replica2.server.util;

import DAMS.Replicas.Replica2.server.domain.CityType;
import DAMS.Replicas.Replica2.server.domain.TimeSlotType;
import DAMS.Replicas.Replica2.server.domain.UserType;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class CommonUtil {
    private static final String FORMAT_APPOINTMENT_DATE = "ddMMyy";
    private static final String FORMAT_DATE_STRING = "dd MMM yyyy";

    private CommonUtil() {
        throw new IllegalStateException("Utility class");
    }

    //Generates user ID with city code, user type & 4-digit number
    //eg: MTLA0001: Montreal, Admin, 1 (4-digit number)
    public static String generateUserId(String cityCode, UserType userType, int patientNum) {
        String patientNumString = String.format("%04d", patientNum).substring(0, 4);
        return cityCode + userType.getCode() + patientNumString;
    }

    //Generates appointment ID with city code, timeslot type and date
    //eg: MTLE131122: Montreal, evening, 13/01/2022
    public static String generateAppointmentId(CityType cityType, TimeSlotType timeSlotType, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_APPOINTMENT_DATE);
        String formattedDate = formatter.format(date);
        return cityType + timeSlotType.getCode() + formattedDate;
    }

    public static CityType getCityCodeFromId(String id) {
        return CityType.valueOf(id.substring(0, 3));
    }

    public static TimeSlotType getTimeSlotTypeFromAppointmentId(String appointmentId) {
        return TimeSlotType.getTimeSlotTypeFromCode(appointmentId.substring(3, 4));
    }

    public static String getDateStringFromAppointmentId(String appointmentId) {
        return appointmentId.substring(4, 10);
    }

    public static LocalDate getDateFromAppointmentId(String appointmentId) {
        String dateString = getDateStringFromAppointmentId(appointmentId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_APPOINTMENT_DATE);
        return LocalDate.parse(dateString, formatter);
    }

    public static String getDateString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE_STRING);
        return formatter.format(date);
    }

    public static LocalDate getDateFromString(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_APPOINTMENT_DATE);
        return LocalDate.parse(dateString, formatter);
    }

    public static byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    public static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

}
