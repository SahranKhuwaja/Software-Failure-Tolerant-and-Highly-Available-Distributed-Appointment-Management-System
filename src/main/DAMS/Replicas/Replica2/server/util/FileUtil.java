package DAMS.Replicas.Replica2.server.util;

import DAMS.Replicas.Replica2.server.domain.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;

public final class FileUtil {

    public static List<User> readUsersFromFile(CityType cityType) throws IOException {
        String fileName = cityType + "ServerUserData.csv";

        if (Objects.isNull(FileUtil.class.getClassLoader().getResource(fileName))) {
            System.out.println(String.format("[%s] No resource file found for users", cityType.name()));
            return Collections.emptyList();
        }

        InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

        List<User> users = new ArrayList<>();
        String line;
        int lines = 0;
        while ((line = bufferedReader.readLine()) != null) {
            lines++;
            String[] columns = line.split(",");
            if (lines != 1) {
                String id = columns[0];
                String name = columns[1];
                User user = new User(id, name, UserType.valueOf(columns[2]), cityType);
                users.add(user);
            }
        }

        return users;
    }

    public static List<Appointment> readAppointmentsFromFile(CityType cityType) throws IOException, ParseException {
        String fileName = cityType+ "ServerAppointmentData.csv";

        if (Objects.isNull(FileUtil.class.getClassLoader().getResource(fileName))) {
            System.out.println(String.format("[%s] No resource file found for appointments", cityType.name()));
            return Collections.emptyList();
        }

        InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

        List<Appointment> appointments = new ArrayList<>();
        String line;
        int lines = 0;
        while ((line = bufferedReader.readLine()) != null) {
            lines++;
            String[] columns = line.split(",");
            if (lines != 1) {
                Appointment appointment = new Appointment(columns[0], AppointmentType.valueOf(columns[1]), Integer.parseInt(columns[2]));
                appointments.add(appointment);
            }
        }

        return appointments;
    }

    public static Map<String, Map<String, AppointmentType>> readUserAppointmentsFromFile(CityType cityType) throws IOException {
        String fileName = cityType + "ServerUserAppointmentsData.csv";

        if (Objects.isNull(FileUtil.class.getClassLoader().getResource(fileName))) {
            System.out.println(String.format("[%s] No resource file found for user appointments", cityType.name()));
            return Collections.emptyMap();
        }

        InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

        Map<String, Map<String, AppointmentType>> userAppointments = new HashMap<>();
        String line;
        int lines = 0;
        while ((line = bufferedReader.readLine()) != null) {
            lines++;
            String[] columns = line.split(",");
            if (lines != 1) {
                if (userAppointments.containsKey(columns[0])) {
                    userAppointments.get(columns[0]).put(columns[1], AppointmentType.valueOf(columns[2]));
                } else { //add new user
                    userAppointments.put(columns[0], new HashMap<>(Map.of(columns[1], AppointmentType.valueOf(columns[2]))));
                }
            }
        }
        return userAppointments;
    }
}
