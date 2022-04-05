package DAMS.Replicas.Replica2.server;

import DAMS.Replicas.Replica2.server.domain.*;
import DAMS.Replicas.Replica2.server.exception.CustomException;
import DAMS.Replicas.Replica2.server.util.ConfigUtil;
import DAMS.Replicas.Replica2.server.util.FileUtil;
import DAMS.ResponseWrapper.ResponseWrapper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static DAMS.Replicas.Replica2.server.config.DamsConfig.SERVER_HOST;
import static DAMS.Replicas.Replica2.server.domain.LogLevel.ERROR;
import static DAMS.Replicas.Replica2.server.domain.LogLevel.INFO;
import static DAMS.Replicas.Replica2.server.domain.UserType.ADMIN;
import static DAMS.Replicas.Replica2.server.domain.UserType.PATIENT;
import static DAMS.Replicas.Replica2.server.exception.CustomErrorType.*;
import static DAMS.Replicas.Replica2.server.util.CommonUtil.*;
import static DAMS.Replicas.Replica2.server.util.LoggingUtil.log;
import static DAMS.Replicas.Replica2.server.util.ServerUtil.getPortByCityType;

public class AppointmentBookingImpl implements AppointmentBooking {

    private HashMap<AppointmentType, HashMap<String, Appointment>> appointments = new HashMap<>(); //HashMap<AppointmentType, HashMap<AppointmentId, Appointment>>
    private HashMap<UserType, HashMap<String, User>> users = new HashMap<>(); //HashMap<UserType, HashMap<UserId, User>>
    private CityType cityType;

    public AppointmentBookingImpl() {
    }

    public AppointmentBookingImpl(CityType cityType) {
        super();
        this.cityType = cityType;
        initUsers();
        initAppointments();
        //initUserAppointments(); //TODO: enable to initialize booking appointments for user
    }

    private void initUsers() {
        try {
            //Load user data into HashMap<UserType, HashMap<UserID, User>>
            //to put into HashMap
            users.putAll(FileUtil.readUsersFromFile(cityType).stream()
                    .collect(Collectors.groupingBy(User::getUserType)) //Map<UserType, List<User>>
                    .entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> e.getValue().stream()
                                    .collect(Collectors.toMap(User::getId, Function.identity(), (prev, next) -> next, HashMap::new))))); //put into user hashmap

            log(cityType, "Initialize users", INFO, String.format("[City: %s] Successfully initialized users", cityType.name()));
        } catch (IOException e) {
            log(cityType, "Initialize users", ERROR, String.format("[City: %s] Failed to initialize users", cityType.name()), e);
        }
    }

    private void initAppointments() {
        try {
            //Load appointment data into HashMap<AppointmentType, HashMap<AppointmentID, Appointment>>
            //to put into HashMap
            appointments.putAll(FileUtil.readAppointmentsFromFile(cityType).stream()
                    .collect(Collectors.groupingBy(Appointment::getAppointmentType)) //Map<UserType, List<User>>
                    .entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> e.getValue().stream()
                                    .collect(Collectors.toMap(Appointment::getId, Function.identity(), (prev, next) -> next, HashMap::new))))); //put into appointment hashmap

            log(cityType, "Initialize appointments", INFO, String.format("[City: %s] Successfully initialized appointments", cityType.name()));
        } catch (IOException | ParseException e) {
            log(cityType, "Initialize appointments", ERROR, String.format("[City: %s] Failed to initialize appointments", cityType.name()), e);
        }
    }

    private void initUserAppointments() {
        try {
            FileUtil.readUserAppointmentsFromFile(cityType).forEach((userId, value) -> {
                //For home city, assign user appointment to a valid user, will not create new user if doesn't exist
                if (getCityCodeFromId(userId).equals(cityType)) {
                    if (users.containsKey(PATIENT) && users.get(PATIENT).containsKey(userId)) {
                        HashSet<UserAppointment> userAppointments = new HashSet<>();
                        value.entrySet()
                                .forEach(appointmentEntry -> {
                                    String appointmentID = appointmentEntry.getKey();
                                    AppointmentType appointmentType = appointmentEntry.getValue();

                                    //Get valid appointment
                                    if (appointments.containsKey(appointmentType) && appointments.get(appointmentType).containsKey(appointmentID)) {
                                        Appointment validApp = appointments.get(appointmentType).get(appointmentID);
                                        User validUser = users.get(PATIENT).get(userId);
                                        UserAppointment newUserAppointment = new UserAppointment(validApp, validUser);
                                        userAppointments.add(newUserAppointment);
                                    }
                                });
                        //set user appointments
                        users.get(PATIENT).get(userId).setAppointments(userAppointments);
                    } else {
                        log(cityType, "Initialize user appointments", ERROR, String.format("[User ID: %s] Unknown user ID, failed to assign user appointment", userId));
                    }
                } else { //for user data from other cities, create user if it does not exist
                    HashSet<UserAppointment> userAppointments = new HashSet<>();

                    User patient = new User(userId, "Patient " + userId, PATIENT, getCityCodeFromId(userId));
                    if (!users.containsKey(PATIENT)) {
                        users.put(PATIENT, new HashMap<>(Map.of(userId, patient)));
                    } else if (!users.get(PATIENT).containsKey(userId)) {
                        users.get(PATIENT).put(userId, patient);
                    } else { //has existing patient from another city
                        users.get(PATIENT).get(userId).setAppointments(userAppointments);
                    }

                    value.entrySet()
                            .forEach(appointmentEntry -> {
                                String appointmentID = appointmentEntry.getKey();
                                AppointmentType appointmentType = appointmentEntry.getValue();

                                //Get valid appointment
                                if (appointments.containsKey(appointmentType) && appointments.get(appointmentType).containsKey(appointmentID)) {
                                    Appointment validApp = appointments.get(appointmentType).get(appointmentID);
                                    User validUser = users.get(PATIENT).get(userId);
                                    UserAppointment newUserAppointment = new UserAppointment(validApp, validUser);
                                    userAppointments.add(newUserAppointment);
                                }
                            });
                    //set user appointments
                    users.get(PATIENT).get(userId).setAppointments(userAppointments);
                }
            });
            log(cityType, "Initialize user appointments", INFO, String.format("[City: %s] Successfully initialized user appointments", cityType.name()));
        } catch (IOException e) {
            log(cityType, "Initialize user appointments", ERROR, String.format("[City: %s] Failed to initialize user appointments", cityType.name()), e);
        }
    }

//    @Override
//    public User login(UserType userType, String userId) throws CustomException {
//        String methodDescription = "Login";
//        String errorString = "Failed to login";
//
//        try {
//            if (!users.containsKey(userType) || Objects.isNull(users.get(userType).get(userId))) {
//                String message = String.format("[User Type: %s; User ID: %s] Unable to find user", userType.getDescription(), userId);
//                log(cityType, methodDescription, INFO, message);
//                throw new CustomException(ERROR_USER_NOT_FOUND);
//            }
//            User loggedInUser = users.get(userType).get(userId);
//            String message = String.format("[User Type: %s; User ID: %s] Successfully logged in user", userType.getDescription(), userId);
//            log(cityType, methodDescription, INFO, message);
//            return loggedInUser;
//        } catch (Exception e) {
//            String message = String.format("[User Type: %s; User ID: %s] %s :: An unknown error occurred", userType.getDescription(), userId, errorString);
//            log(cityType, methodDescription, ERROR, message, e);
//            throw new CustomException(ERROR_USER_NOT_FOUND);
//        }
//    }

    @Override
    public String addAppointment(String appointmentID, AppointmentType appointmentType, int capacity) {
        String methodName = "addAppointment";
        String methodDescription = "Add appointment";
        String errorString = "Failed to add appointment";

        try {
            if (!getCityCodeFromId(appointmentID).equals(cityType)) {
                String message = String.format("[Appointment ID: %s; Type: %s; Capacity: %s] %s :: Unable to create appointment for another city", appointmentID, appointmentType.getDescription(), capacity, errorString);
                log(cityType, methodDescription, ERROR, message);
                return "Appointment ID " + appointmentID + " contains invalid city code, which doesn't match"
                        + "current city server!";
            }

            if (appointments.containsKey(appointmentType) && appointments.get(appointmentType).containsKey(appointmentID)) {
                String message = String.format("[Appointment ID: %s; Type: %s; Capacity: %s] %s :: Appointment already exists", appointmentID, appointmentType.getDescription(), capacity, errorString);
                log(cityType, methodDescription, ERROR, message);
                return "Appointment Slot with this Appointment ID " + appointmentID + " already exists! \n"
                        + "Appointment ID should be unique!";
            }

            //Appointment type already exists
            if (appointments.containsKey(appointmentType)) {
                appointments.get(appointmentType).put(appointmentID, buildAppointment(appointmentID, appointmentType, capacity));
            } else {
                appointments.put(appointmentType, new HashMap<>(Map.of(appointmentID, buildAppointment(appointmentID, appointmentType, capacity))));
            }

            String message = String.format("[Appointment ID: %s; Type: %s; Capacity: %s] Successfully added appointment", appointmentID, appointmentType.getDescription(), capacity);
            log(cityType, methodDescription, INFO, message);
            return "Appointment Slot with Appointment ID " + appointmentID + " has" + " been created successfully!";
        } catch (Exception ex) {
            String message = String.format("[Appointment ID: %s; Type: %s; Capacity: %s] %s :: An unknown error has occurred", appointmentID, appointmentType.getDescription(), capacity, errorString);
            log(cityType, methodDescription, ERROR, message, ex);
            return message;
        }
    }

    @Override
    public String removeAppointment(String appointmentID, AppointmentType appointmentType, String requesterUserId) {
        String methodName = "removeAppointment";
        String methodDescription = "Remove appointment";
        String errorString = "Failed to remove appointment";

        try {
            User requester = getUserById(requesterUserId);

            if (Objects.isNull(requester)) {
                String message = String.format("[Appointment ID: %s; Type: %s; Requester ID: %s] %s :: Unknown User ID provided", appointmentID, appointmentType.getDescription(), requesterUserId, errorString);
                log(cityType, methodDescription, ERROR, message);
                return message;
            }

            if (!getCityCodeFromId(appointmentID).equals(cityType)) {
                String message = String.format("[Appointment ID: %s; Type: %s] %s :: Unable to remove appointment for another city", appointmentID, appointmentType.getDescription(), errorString);
                log(cityType, methodDescription, ERROR, message);
                return "Appointment ID " + appointmentID + " contains invalid city code, "
                        + "which doesn't match current city server! " + "Please re-enter!";
            }

            if (!appointments.containsKey(appointmentType) || !appointments.get(appointmentType).containsKey(appointmentID)) {
                String message = String.format("[Appointment ID: %s; Type: %s] %s :: No appointment found for removal", appointmentID, appointmentType.getDescription(), errorString);
                log(cityType, methodDescription, ERROR, message);
                return "Appointment Slot with this Appointment ID " + appointmentID + " doesn't exists!";
            }

            Appointment appointmentToRemove = appointments.get(appointmentType).get(appointmentID);
            if (!Objects.isNull(appointmentToRemove)) {
                users.get(PATIENT).entrySet().forEach(entry -> {
                    User patient = entry.getValue();
                    Appointment nextAppointment = getNextAvailableAppointment(appointmentToRemove);
                    //Rebook patient for next appointment (if any)
                    if (patient.getAppointments().stream()
                            .anyMatch(app -> app.getAppointment().getId().equals(appointmentID))) {
                        while (!Objects.isNull(nextAppointment)) {
                            if (hasCapacity(nextAppointment)) {
                                String message = String.format("[Appointment ID: %s; Type: %s] Rebooking for patient %s for next available %s slot %s", appointmentID, appointmentType.getDescription(), patient.getId(), appointmentType.getDescription(), nextAppointment.getId());
                                log(cityType, methodDescription, INFO, message);
                                bookAppointmentForPatient(patient, nextAppointment, requester);
                                break;
                            } else {
                                nextAppointment = getNextAvailableAppointment(nextAppointment);
                            }
                        }
                        if (Objects.isNull(nextAppointment)) {
                            String message = String.format("[Appointment ID: %s; Type: %s] No next available %s slots for patient %s", appointmentID, appointmentType.getDescription(), appointmentType.getDescription(), patient.getId());
                            log(cityType, methodDescription, INFO, message);
                        }
                    }
                    cancelAppointmentForPatient(patient, appointmentID);
                });
            }
            //Delete appointment
            appointments.get(appointmentType).remove(appointmentID);
            String message = String.format("[Appointment ID: %s; Type: %s] Successfully removed appointment!", appointmentID, appointmentType.getDescription());
            log(cityType, methodDescription, INFO, message);
            return "Appointment Slot with this Appointment ID " + appointmentID + " has been \ndeleted/removed "
                    + "successfully!";
        } catch (Exception ex) {
            String message = String.format("[Appointment ID: %s; Type: %s] %s :: An error has occurred while trying to remove appointment", appointmentID, appointmentType.getDescription(), errorString);
            log(cityType, methodDescription, ERROR, message, ex);
            return message;
        }
    }

    @Override
    public ResponseWrapper listAppointmentAvailability(AppointmentType appointmentType) {
        String methodName = "listAppointmentAvailability";
        String methodDescription = "List appointment availability";
        String errorString = "Failed to list appointment availability";
        try (DatagramSocket aSocket = new DatagramSocket()) {
            HashMap<String, String> results = new HashMap<>();

            //Put current server's appointments into results
            if (!Objects.isNull(appointments.get(appointmentType))) {
                appointments.get(appointmentType)
                        .entrySet()
                        .forEach(entry -> results.put(entry.getKey(), String.valueOf(entry.getValue().getCapacity())));
            }

            Map<UDPActionType, String> message = new HashMap<>();
            message.put(UDPActionType.LIST_APPOINTMENT_AVAILABILITY, appointmentType.name());
            byte[] messageBytes = convertToBytes(message);
            InetAddress aHost = InetAddress.getByName(ConfigUtil.getPropValue(SERVER_HOST));

            for (CityType city : CityType.values()) {
                // Send request to other cities
                if (!city.equals(cityType)) {
                    DatagramPacket request = new DatagramPacket(messageBytes, messageBytes.length, aHost, getPortByCityType(city));
                    aSocket.send(request);

                    byte[] buffer = new byte[65508];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(reply);
                    HashMap<String, Appointment> data = (HashMap<String, Appointment>) convertFromBytes(reply.getData());

                    data.entrySet().stream().forEach(entry -> results.put(entry.getKey(), String.valueOf(entry.getValue().getCapacity())));
                }
            }
            log(cityType, methodDescription, INFO, String.format("[Appointment Type: %s] Successfully listed appointment availability!", appointmentType.getDescription()));
            return new ResponseWrapper(results);
        } catch (Exception e) {
            String message = String.format("[Appointment Type: %s] %s :: An unknown error has occurred", appointmentType.getDescription(), errorString);
            log(cityType, methodDescription, ERROR, message, e);
            return new ResponseWrapper(new HashMap<>());
        }
    }

    @Override
    public String bookAppointment(String requesterUserId, String patientID, String appointmentID, AppointmentType appointmentType) {
        String methodName = "bookAppointment";
        String methodDescription = "Book appointment";
        String errorString = "Failed to book appointment";

        CityType destinationCity = getCityCodeFromId(appointmentID);
        boolean isPatientCity = getCityCodeFromId(patientID).equals(cityType);
        boolean isDestinationCity = destinationCity.equals(cityType);

        Response isValidationSuccess = doValidateBookAppointment(patientID, appointmentID, appointmentType);

        //Failed validation
        if (!isValidationSuccess.success()) {
            return isValidationSuccess.message();
        }

        try {
            User requester = getUserById(requesterUserId);
            if (Objects.isNull(requester)) {
                String message = String.format("[Requester ID: %s; Patient ID: %s; Appointment ID: %s; Type: %s] Unknown User ID provided", requesterUserId, patientID, appointmentID, appointmentType.getDescription());
                log(cityType, methodDescription, ERROR, message);
                return message;
            }

            //If appointment is for another server, send UDP request to book
            if (!isDestinationCity) {
                Response response = bookAppointmentInAnotherCity(methodName, methodDescription, errorString, requester, patientID, appointmentID, appointmentType, destinationCity);
                return response.message();
            }

            boolean isValidUser = users.containsKey(PATIENT) && users.get(PATIENT).containsKey(patientID);

            if (!isPatientCity && !isValidUser) { //if not patient city, create patient if it doesn't exist
                HashMap<String, User> currentUsers = users.get(PATIENT);
                User newUser = buildUser(patientID);
                currentUsers.put(patientID, newUser);
                users.put(PATIENT, currentUsers);
            }
            Appointment appointment = appointments.get(appointmentType).get(appointmentID);
            User patient = users.get(PATIENT).get(patientID);

            bookAppointmentForPatient(patient, appointment, requester);

            String message = String.format("[Requester ID: %s; Patient ID: %s; Appointment ID: %s; Type: %s] Successfully booked appointment!", requester.getId(), patientID, appointmentID, appointmentType.getDescription());
            log(cityType, methodDescription, INFO, message);
            return String.format("Appointment ID %s in the Appointment Type %s has been booked successfully!", appointmentID, appointmentType);

        } catch (Exception ex) {
            String message = String.format("[Requester ID: %s; Patient ID: %s; Appointment ID: %s; Type: %s] %s :: An error occurred while trying to book appointment", requesterUserId, patientID, appointmentID, appointmentType.getDescription(), errorString);
            log(cityType, methodDescription, ERROR, message, ex);
            return ex.getMessage();
        }
    }

    @Override
    public String cancelAppointment(String requesterUserId, String patientID, String appointmentID) {
        String methodName = "cancelAppointment";
        String methodDescription = "Cancel appointment";
        String errorString = "Failed to cancel appointment";

        CityType destinationCity = getCityCodeFromId(appointmentID);
        boolean isDestinationCity = destinationCity.equals(cityType);

        User requester = getUserById(requesterUserId);
        if (Objects.isNull(requester)) {
            String message = String.format("[Requester ID: %s Patient ID: %s; Appointment ID: %s] :: Unknown User ID provided", requesterUserId, patientID, appointmentID);
            log(cityType, methodDescription, ERROR, message);
            return message;
        }

        Response isValidationSuccess = doValidateCancelAppointment(requester, patientID, appointmentID);

        //Failed validation
        if (!isValidationSuccess.success()) {
            return isValidationSuccess.message();
        }

        try {
            //If appointment cancellation is for another server, send UDP request
            if (!isDestinationCity) {
                Response response = cancelAppointmentInAnotherCity(methodName, methodDescription, errorString, requester, patientID, appointmentID, destinationCity);
                return response.message();
            }

            //Cancel appointment
            User patient = users.get(PATIENT).get(patientID);
            Set<UserAppointment> bookedAppointments = patient.getAppointments();
            UserAppointment foundAppointment = bookedAppointments.stream()
                    .filter(appointment -> appointment.getAppointment().getId().equals(appointmentID))
                    .findFirst()
                    .orElse(null);

            cancelAppointmentForPatient(patient, foundAppointment.getAppointment().getId());

            //Increase capacity of appointment
            for (HashMap<String, Appointment> appointmentsByType : appointments.values()) {
                if (appointmentsByType.get(appointmentID) != null) {
                    appointmentsByType.get(appointmentID).setCapacity(appointmentsByType.get(appointmentID).getCapacity() + 1);
                    break;
                }
            }
            String message = String.format("[Requester ID: %s Patient ID: %s; Appointment ID: %s] :: Successfully cancelled appointment!", requester.getId(), patientID, appointmentID);
            log(cityType, methodDescription, INFO, message);
            return "Booking appointment schedule of the Patient ID " + patientID + " for the Appointment ID "
                    + appointmentID + " in the Appointment Type " + foundAppointment.getAppointment().getAppointmentType().getDescription() + " has been "
                    + " cancelled successfully!";

        } catch (Exception ex) {
            String message = String.format("[Requester ID: %s Patient ID: %s; Appointment ID: %s] %s :: An error occurred while trying to cancel appointment", requester.getId(), patientID, appointmentID, errorString);
            log(cityType, methodDescription, ERROR, message, ex);
            return ex.getMessage();
        }
    }

    @Override
    public String[] getAppointmentSchedule(String patientID) {
        String methodName = "getAppointmentSchedule";
        String methodDescription = "Get appointment schedule";
        String errorString = "Failed to get appointment schedule";
        try (DatagramSocket aSocket = new DatagramSocket()) {
            if (!users.containsKey(PATIENT) || !users.get(PATIENT).containsKey(patientID)) {
                String message = String.format("[Patient ID: %s] %s :: Invalid patient ID provided", errorString, patientID);
                log(cityType, methodDescription, ERROR, message);
            }

            User patient = users.get(PATIENT).get(patientID);
            HashSet<UserAppointment> results = new HashSet<>(patient.getAppointments());

            Map<UDPActionType, String> requestMessage = new HashMap<>();
            requestMessage.put(UDPActionType.GET_APPOINTMENT_SCHEDULE, patientID);
            byte[] messageBytes = convertToBytes(requestMessage);
            InetAddress aHost = InetAddress.getByName(ConfigUtil.getPropValue(SERVER_HOST));

            for (CityType city : CityType.values()) {
                // Send request to other cities
                if (!city.equals(cityType)) {
                    DatagramPacket request = new DatagramPacket(messageBytes, messageBytes.length, aHost, getPortByCityType(city));
                    aSocket.send(request);

                    byte[] buffer = new byte[65508];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(reply);
                    results.addAll((HashSet<UserAppointment>) convertFromBytes(reply.getData()));
                }
            }

            String message = String.format("[Patient ID: %s] Successfully retrieved appointment schedule", patientID);
            log(cityType, methodDescription, INFO, message);

            LinkedHashSet<String> set = results.stream()
                    .sorted(Comparator
                            .comparing(UserAppointment::getAppointment,
                                    Comparator.comparing(Appointment::getCityType).thenComparing(Appointment::getDate))) //Sort by city then date
                    .map(ua -> "[appointmentID=" + ua.getAppointment().getId() + ", appointmentType=" + ua.getAppointment().getAppointmentType())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            return set.toArray(String[]::new);
        } catch (Exception e) {
            String message = String.format("[Patient ID: %s] %s :: An unknown error occured", errorString, patientID);
            log(cityType, methodDescription, ERROR, message, e);
            return new String[0];
        }
    }

    @Override
    public String swapAppointment(String requesterUserId, String patientID, String oldAppointmentID, AppointmentType oldAppointmentType, String newAppointmentID, AppointmentType newAppointmentType) {
        String methodName = "swapAppointment";
        String methodDescription = "Swap appointment";
        String errorString = "Failed to swap appointment";

        try {
            User requester = getUserById(requesterUserId);
            if (Objects.isNull(requester)) {
                String message = String.format("[Requester ID: %s; Patient ID: %s; Old Appointment: %s (%s); New Appointment: %s (%s)] Unknown User ID provided", requesterUserId, patientID, oldAppointmentID, oldAppointmentType.getDescription(), newAppointmentID, newAppointmentType.getDescription());
                log(cityType, methodDescription, ERROR, message);
                return message;
            }

            Response isCancelValidationSuccess = doValidateCancelAppointment(requester, patientID, oldAppointmentID);
            Response isBookValidationSuccess = doValidateBookAppointment(patientID, newAppointmentID, newAppointmentType);

            if (isBookValidationSuccess.success() && isCancelValidationSuccess.success()) {
                cancelAppointment(requesterUserId, patientID, oldAppointmentID);
                bookAppointment(requesterUserId, patientID, newAppointmentID, newAppointmentType);

                String message = String.format("[Requester ID: %s; Patient ID: %s; Old Appointment: %s (%s); New Appointment: %s (%s)] Successfully swapped appointment!", requester.getId(), patientID, oldAppointmentID, oldAppointmentType.getDescription(), newAppointmentID, newAppointmentType.getDescription());
                log(cityType, methodDescription, INFO, message);
                return "Old Appointment ID " + oldAppointmentID + " in the old Appointment Type\n"
                        + oldAppointmentType + " has been swapped  with new Appointment ID " + newAppointmentID
                        + " in the new \nAppointment Type " + newAppointmentType + " successfully!";
            } else {
                String errorMessage = isCancelValidationSuccess.success() ? isBookValidationSuccess.message() : isCancelValidationSuccess.message();
                //String message = String.format("[Requester ID: %s; Patient ID: %s; Old Appointment: %s (%s); New Appointment: %s (%s)] %s :: %s", requester.id(), patientID, oldAppointmentID, oldAppointmentType.getDescription(), newAppointmentID, newAppointmentType.getDescription(), errorString, errorMessage);
                return errorMessage;
            }

        } catch (Exception ex) {
            String message = String.format("[Requester ID: %s; Patient ID: %s; Old Appointment: %s (%s); New Appointment: %s (%s)] %s :: An error occurred while trying to swap appointment", requesterUserId, patientID, oldAppointmentID, oldAppointmentType.getDescription(), newAppointmentID, newAppointmentType.getDescription(), errorString);
            log(cityType, methodDescription, ERROR, message, ex);
            return ex.getMessage();

        }
    }

    @Override
    public HashMap<String, Appointment> getAppointmentsByType(AppointmentType appointmentType) {
        String methodName = "getAppointmentsByType";
        String methodDescription = "Get appointments by type";
        String errorString = "Failed to get appointments by type";
        try {
            return this.appointments.containsKey(appointmentType) ? this.appointments.get(appointmentType) : new HashMap<>();
        } catch (Exception ex) {
            String message = String.format("[Appointment Type: %s] %s :: An error occurred while trying to get appointments by type", appointmentType, errorString);
            log(cityType, methodDescription, ERROR, message, ex);
            return new HashMap<>();
        }
    }

    @Override
    public HashSet<UserAppointment> getPatientAppointments(String patientID) {
        String methodName = "getPatientAppointments";
        String methodDescription = "Get patient appointments";
        String errorString = "Failed to get patient appointments";
        try {
            if (!users.containsKey(PATIENT) || !users.get(PATIENT).containsKey(patientID)) {
                return new HashSet<>();
            }
            User patient = users.get(PATIENT).get(patientID);
            return new HashSet<>(patient.getAppointments());
        } catch (Exception ex) {
            String message = String.format("[Appointment Type: %s] %s :: An error occurred while trying to get patient appointments", patientID, errorString);
            log(cityType, methodDescription, ERROR, message, ex);
            return new HashSet<>();
        }
    }

    @Override
    public Response validateBookAppointment(String patientID, String appointmentID, AppointmentType appointmentType) {
        String methodName = "validateBookAppointment";
        String methodDescription = "Validate cancel appointment";
        String errorString = "Failed to validate book appointment";

        try {
            boolean isPatientCity = getCityCodeFromId(patientID).equals(cityType);
            boolean isValidUser = users.containsKey(PATIENT) && users.get(PATIENT).containsKey(patientID);

            if (isPatientCity) {
                if (!isValidUser) {
                    throw new CustomException(ERROR_INVALID_PATIENT);
                }
            }
            User patient = users.get(PATIENT).get(patientID);

            //Appointment ID is not valid
            if (!(appointments.containsKey(appointmentType) && appointments.get(appointmentType).containsKey(appointmentID))) {
                throw new Exception(String.format(ERROR_INVALID_APPOINTMENT_ID.getDescription(), appointmentID, appointmentType));
            }

            //Appointment ID is valid
            Appointment appointment = appointments.get(appointmentType).get(appointmentID);

            //Appointment has no more available slots
            if (!hasCapacity(appointment)) {
                throw new Exception(String.format(ERROR_UNAVAILABLE_APPOINTMENT_SLOT.getDescription(), appointmentID, appointmentType));
            }

            //Checks if user has existing appointments booked and do validations
            if (patient != null && !patient.getAppointments().isEmpty()) {
                LocalDate appointmentDate = getDateFromAppointmentId(appointmentID);

                //User cannot book appointment with same ID and type
                UserAppointment existingAppointment = patient.getAppointments().stream()
                        .filter(bookedAppointment ->
                                bookedAppointment.getAppointment().getId().equals(appointmentID) &&
                                        bookedAppointment.getAppointment().getAppointmentType().equals(appointmentType))
                        .findFirst()
                        .orElse(null);
                if (existingAppointment != null) {
                    throw new Exception(String.format(ERROR_SAME_APPOINTMENT_BOOKED.getDescription(), appointmentID, appointmentType));
                }

                //User cannot have same appointment type in the same day
                UserAppointment existingAppointmentType = patient.getAppointments().stream()
                        .filter(bookedAppointment -> isSameAppointmentTypeOnSameDay(bookedAppointment, appointmentDate, appointmentType))
                        .findFirst()
                        .orElse(null);
                if (existingAppointmentType != null) {
                    throw new CustomException(ERROR_SAME_APPOINTMENT_TYPE_ON_SAME_DAY_BOOKED);
                }
            }
            String message = String.format("[Patient ID: %s; Appointment ID: %s; Type: %s] Successfully validated appointment!", patientID, appointmentID, appointmentType.getDescription());
            log(cityType, methodDescription, INFO, message);
            return new Response(methodName, methodDescription, true, message);
        } catch (Exception ex) {
            //String message = String.format("[Patient ID: %s; Appointment ID: %s; Type: %s] %s :: An error occurred while trying to validate book appointment", patientID, appointmentID, appointmentType.getDescription(), errorString);
            log(cityType, methodDescription, ERROR, ex.getMessage(), ex);
            return new Response(methodName, methodDescription, false, ex.getMessage());
        }
    }

    @Override
    public Response validateCancelAppointment(User requester, String patientID, String appointmentID) {
        String methodName = "validateCancelAppointment";
        String methodDescription = "Validate cancel appointment";
        String errorString = "Failed to validate cancel appointment";

        try {
            if (!users.containsKey(PATIENT) || !users.get(PATIENT).containsKey(patientID)) {
                throw new CustomException(ERROR_INVALID_PATIENT);
            }

            User patient = users.get(PATIENT).get(patientID);

            if (patient.getAppointments().isEmpty()) {
                throw new Exception(String.format(ERROR_USER_INVALID_APPOINTMENT_ID.getDescription(), patientID));
            }
            Set<UserAppointment> bookedAppointments = patient.getAppointments();
            UserAppointment foundAppointment = bookedAppointments.stream()
                    .filter(app -> app.getAppointment().getId().equals(appointmentID))
                    .findFirst()
                    .orElse(null);

            //No valid appointment found booked by user
            if (Objects.isNull(foundAppointment)) {
                throw new Exception(String.format(ERROR_USER_INVALID_APPOINTMENT_ID.getDescription(), patientID));
            }

            //Requester user ID must be same as user who booked appointment
            if (!foundAppointment.getCreatedBy().getId().equals(requester.getId())) {
                throw new Exception(ERROR_CANCEL_APPOINTMENT_USER_MISMATCH.getDescription());
            }

            String message = String.format("[Requester ID: %s; Patient ID: %s; Appointment ID: %s] Successfully validated appointment!", requester.getId(), patientID, appointmentID);
            log(cityType, methodDescription, INFO, message);
            return new Response(methodName, methodDescription, true, message);

        } catch (Exception ex) {
            String message = String.format("[Requester ID: %s; Patient ID: %s; Appointment ID: %s] %s :: An error occurred while trying to validate cancel appointment", requester.getId(), patientID, appointmentID, errorString);
            if (ex.getClass().equals(CustomException.class)) {
                message = String.format("[Requester ID: %s; Patient ID: %s; Appointment ID: %s] %s :: %s", requester.getId(), patientID, appointmentID, errorString, ex.getMessage());
            }
            log(cityType, methodDescription, ERROR, message, ex);
            return new Response(methodName, methodDescription, false, message);
        }
    }

    @Override
    public String[] getAppointmentTypes() {
        return Arrays.stream(AppointmentType.values())
                .map(AppointmentType::getDescription)
                .toArray(String[]::new);
    }

    @Override
    public String[] getTimeSlots() {
        return Arrays.stream(TimeSlotType.values())
                .map(TimeSlotType::getCode)
                .toArray(String[]::new);
    }

    private Response doValidateBookAppointment(String patientID, String appointmentID, AppointmentType appointmentType) {
        String methodName = "validateBookAppointment";
        String methodDescription = "Validate book appointment";
        String errorString = "Failed to validate book appointment";

        CityType destinationCity = getCityCodeFromId(appointmentID);
        boolean isDestinationCity = destinationCity.equals(cityType);

        try {
            if (isDestinationCity) {
                return validateBookAppointment(patientID, appointmentID, appointmentType);
            } else { //If appointment is not destination city, retrieve all appointments and validate that only can book 3 appointments in a city
                Set<UserAppointment> otherCityAppointments = new HashSet<>();

                try (DatagramSocket aSocket = new DatagramSocket()) {
                    Map<UDPActionType, String> requestMessage = new HashMap<>();
                    requestMessage.put(UDPActionType.GET_APPOINTMENT_SCHEDULE, patientID);
                    byte[] messageBytes = convertToBytes(requestMessage);
                    InetAddress aHost = InetAddress.getByName(ConfigUtil.getPropValue(SERVER_HOST));

                    for (CityType city : CityType.values()) {
                        // Send request to other cities
                        if (!city.equals(cityType)) {
                            DatagramPacket request = new DatagramPacket(messageBytes, messageBytes.length, aHost, getPortByCityType(city));
                            aSocket.send(request);

                            byte[] buffer = new byte[65508];
                            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                            aSocket.receive(reply);
                            HashSet<UserAppointment> response = (HashSet<UserAppointment>) convertFromBytes(reply.getData());
                            otherCityAppointments.addAll(response);
                        }
                    }

                    long numberOfAppointmentsThisWeek = otherCityAppointments.stream()
                            .filter(bookedAppointment -> isWithinWeek(appointmentID, bookedAppointment.getAppointment().getDate()))
                            .count();
                    if (numberOfAppointmentsThisWeek >= 3) {
                        throw new CustomException(ERROR_EXCEED_3_APPOINTMENTS_IN_OTHER_CITIES);
                    }

                    return validateBookAppointmentInAnotherCity(methodName, methodDescription, errorString, patientID, appointmentID, appointmentType, destinationCity);
                }
            }

        } catch (Exception ex) {
            String message = String.format("[Patient ID: %s; Appointment ID: %s; Type: %s] %s :: An error occurred while trying to validate book appointment", patientID, appointmentID, appointmentType.getDescription(), errorString);
            if (ex.getClass().equals(CustomException.class)) {
                message = String.format("[Patient ID: %s; Appointment ID: %s; Type: %s] %s :: %s", patientID, appointmentID, appointmentType.getDescription(), errorString, ex.getMessage());
            }
            log(cityType, methodDescription, ERROR, message, ex);
            return new Response(methodName, methodDescription, false, message);
        }
    }

    private Response doValidateCancelAppointment(User requester, String patientID, String appointmentID) {
        String methodName = "validateCancelAppointment";
        String methodDescription = "Validate cancel appointment";
        String errorString = "Failed to validate cancel appointment";
        try {
            CityType destinationCity = getCityCodeFromId(appointmentID);
            boolean isDestinationCity = destinationCity.equals(cityType);

            if (isDestinationCity) {
                return validateCancelAppointment(requester, patientID, appointmentID);
            } else {
                return validateCancelAppointmentInAnotherCity(methodName, methodDescription, errorString, requester, patientID, appointmentID, destinationCity);
            }
        } catch (Exception ex) {
            String message = String.format("[RequesterID: %s; Patient ID: %s; Appointment ID: %s] %s :: An error occurred while trying to validate book appointment", requester.getId(), patientID, appointmentID, errorString);
            log(cityType, methodDescription, ERROR, message, ex);
            return new Response(methodName, methodDescription, false, message);
        }
    }

    private Response validateBookAppointmentInAnotherCity(String methodName, String methodDescription, String errorString,
                                                          String patientID, String appointmentID, AppointmentType appointmentType, CityType destinationCity) {

        try (DatagramSocket aSocket = new DatagramSocket()) {
            Map<UDPActionType, Object> requestMessage = new HashMap<>();
            requestMessage.put(UDPActionType.VALIDATE_BOOK_APPOINTMENT, new HashMap<String, Object>(Map.of(
                    "patientID", patientID,
                    "appointmentID", appointmentID,
                    "appointmentType", appointmentType.name()
            )));
            byte[] messageBytes = convertToBytes(requestMessage);
            InetAddress aHost = InetAddress.getByName(ConfigUtil.getPropValue(SERVER_HOST));

            DatagramPacket request = new DatagramPacket(messageBytes, messageBytes.length, aHost, getPortByCityType(destinationCity));
            aSocket.send(request);

            byte[] buffer = new byte[65508];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            Response serverResponse = (Response) convertFromBytes(reply.getData());

            if (serverResponse.success()) {
                log(cityType, methodDescription, INFO, serverResponse.message());
            } else {
                log(cityType, methodDescription, ERROR, serverResponse.message());
            }

            return serverResponse;
        } catch (Exception e) {
            String message = String.format("[Patient ID: %s; Appointment ID: %s; Type: %s] %s :: Failed to validate appointment in %s", patientID, appointmentID, appointmentType.getDescription(), errorString, destinationCity);
            log(cityType, methodDescription, ERROR, message, e);
            return new Response(methodName, methodDescription, false, message);
        }
    }

    private Response validateCancelAppointmentInAnotherCity(String methodName, String methodDescription, String errorString,
                                                            User requester, String patientID, String appointmentID, CityType destinationCity) {
        try (DatagramSocket aSocket = new DatagramSocket()) {
            Map<UDPActionType, Object> requestMessage = new HashMap<>();
            requestMessage.put(UDPActionType.VALIDATE_CANCEL_APPOINTMENT, new HashMap<>(Map.of(
                    "requester", requester,
                    "patientID", patientID,
                    "appointmentID", appointmentID
            )));
            byte[] messageBytes = convertToBytes(requestMessage);
            InetAddress aHost = InetAddress.getByName(ConfigUtil.getPropValue(SERVER_HOST));

            DatagramPacket request = new DatagramPacket(messageBytes, messageBytes.length, aHost, getPortByCityType(destinationCity));
            aSocket.send(request);

            byte[] buffer = new byte[65508];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            Response serverResponse = (Response) convertFromBytes(reply.getData());

            if (serverResponse.success()) {
                log(cityType, methodDescription, INFO, serverResponse.message());
            } else {
                log(cityType, methodDescription, ERROR, serverResponse.message());
            }

            return serverResponse;
        } catch (Exception e) {
            String message = String.format("[Requester ID: %s; Patient ID: %s; Appointment ID: %s] %s :: Failed to validate cancel appointment in %s", requester.getId(), patientID, appointmentID, errorString, destinationCity);
            log(cityType, methodDescription, ERROR, message, e);
            return new Response(methodName, methodDescription, false, message);
        }
    }

    private Response bookAppointmentInAnotherCity(String methodName, String methodDescription, String errorString,
                                                  User requester, String patientID, String appointmentID, AppointmentType appointmentType,
                                                  CityType destinationCity) {
        try (DatagramSocket aSocket = new DatagramSocket()) {
            Map<UDPActionType, Object> requestMessage = new HashMap<>();
            requestMessage.put(UDPActionType.BOOK_APPOINTMENT, new HashMap<String, Object>(Map.of(
                    "requesterID", requester.getId(),
                    "patientID", patientID,
                    "appointmentID", appointmentID,
                    "appointmentType", appointmentType.name()
            )));
            byte[] messageBytes = convertToBytes(requestMessage);
            InetAddress aHost = InetAddress.getByName(ConfigUtil.getPropValue(SERVER_HOST));

            DatagramPacket request = new DatagramPacket(messageBytes, messageBytes.length, aHost, getPortByCityType(destinationCity));
            aSocket.send(request);

            byte[] buffer = new byte[65508];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            Response serverResponse = (Response) convertFromBytes(reply.getData());

            if (serverResponse.success()) {
                log(cityType, methodDescription, INFO, serverResponse.message());
            } else {
                log(cityType, methodDescription, ERROR, serverResponse.message());
            }

            return serverResponse;
        } catch (Exception e) {
            String message = String.format("[Requester ID: %s; Patient ID: %s; Appointment ID: %s; Type: %s] %s :: Failed to book appointment in %s", requester.getId(), patientID, appointmentID, appointmentType.getDescription(), errorString, destinationCity);
            log(cityType, methodDescription, ERROR, message, e);
            return new Response(methodName, methodDescription, false, message);
        }
    }

    private Response cancelAppointmentInAnotherCity(String methodName, String methodDescription, String errorString,
                                                    User requester, String patientID, String appointmentID, CityType destinationCity) {
        try (DatagramSocket aSocket = new DatagramSocket()) {
            Map<UDPActionType, Object> requestMessage = new HashMap<>();
            requestMessage.put(UDPActionType.CANCEL_APPOINTMENT, new HashMap<>(Map.of(
                    "requesterID", requester.getId(),
                    "patientID", patientID,
                    "appointmentID", appointmentID
            )));
            byte[] messageBytes = convertToBytes(requestMessage);
            InetAddress aHost = InetAddress.getByName(ConfigUtil.getPropValue(SERVER_HOST));

            DatagramPacket request = new DatagramPacket(messageBytes, messageBytes.length, aHost, getPortByCityType(destinationCity));
            aSocket.send(request);

            byte[] buffer = new byte[65508];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            Response serverResponse = (Response) convertFromBytes(reply.getData());

            if (serverResponse.success()) {
                log(cityType, methodDescription, INFO, serverResponse.message());
            } else {
                log(cityType, methodDescription, ERROR, serverResponse.message());
            }

            return serverResponse;
        } catch (Exception e) {
            String message = String.format("[Requester ID: %s; Patient ID: %s; Appointment ID: %s] %s :: Failed to cancel appointment in %s", requester.getId(), patientID, appointmentID, errorString, destinationCity);
            log(cityType, methodDescription, ERROR, message, e);
            return new Response(methodName, methodDescription, false, message);
        }
    }

    private Appointment buildAppointment(String id, AppointmentType appointmentType, int capacity) {
        return new Appointment(id, appointmentType, capacity);
    }

    private User buildUser(String id) {
        return new User(id, "Patient " + id, PATIENT, getCityCodeFromId(id));
    }

    private void bookAppointmentForPatient(User patient, Appointment appointment, User requester) {
        patient.getAppointments().add(new UserAppointment(appointment, requester));
        appointment.setCapacity(appointment.getCapacity() - 1);
    }

    private void cancelAppointmentForPatient(User patient, String appointmentID) {
        patient.getAppointments()
                .removeIf(userApp -> userApp.getAppointment().getId().equals(appointmentID));
    }

    private boolean hasCapacity(Appointment appointment) {
        return appointment.getCapacity() > 0;
    }

    private boolean isSameAppointmentTypeOnSameDay(UserAppointment userAppointment, LocalDate appointmentDate, AppointmentType appointmentType) {
        LocalDate bookedDate = userAppointment.getAppointment().getDate();
        return bookedDate.equals(appointmentDate) && userAppointment.getAppointment().getAppointmentType().equals(appointmentType);
    }

    private boolean isWithinWeek(String appointmentID, LocalDate date) {
        LocalDate thisWeeksMonday = getDateFromAppointmentId(appointmentID).with(DayOfWeek.MONDAY);
        LocalDate thisWeeksSunday = getDateFromAppointmentId(appointmentID).with(DayOfWeek.SUNDAY);
        return date.isAfter(thisWeeksMonday.minusDays(1)) && date.isBefore(thisWeeksSunday.plusDays(1));
    }

    private Appointment getNextAvailableAppointment(Appointment currentAppointment) {
        AppointmentType appointmentType = currentAppointment.getAppointmentType();
        if (appointments.containsKey(appointmentType)) {
            Set<Appointment> sortedAppointments = appointments.get(appointmentType).values().stream()
                    .sorted(Comparator.comparing(Appointment::getDate).thenComparing(Appointment::getTimeSlotType))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            return sortedAppointments.stream()
                    .filter(appointment -> appointment.getCapacity() > 0)
                    .filter(appointment ->
                            (appointment.getDate().equals(currentAppointment.getDate()) && appointment.getTimeSlotType().compareTo(currentAppointment.getTimeSlotType()) > 0) || appointment.getDate().isAfter(currentAppointment.getDate()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private User getUserById(String userId) {
        if (users.containsKey(PATIENT) && users.get(PATIENT).containsKey(userId)) {
            return users.get(PATIENT).get(userId);
        } else if (users.containsKey(ADMIN) && users.get(ADMIN).containsKey(userId)) {
            return users.get(ADMIN).get(userId);
        } else {
            log(cityType, "Get user by ID", ERROR, "Unknown user ID" + userId);
            return null;
        }
    }
}