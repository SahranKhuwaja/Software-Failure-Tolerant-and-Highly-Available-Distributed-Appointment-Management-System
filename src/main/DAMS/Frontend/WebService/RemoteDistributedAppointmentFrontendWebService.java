package DAMS.Frontend.WebService;

import DAMS.Frontend.Interfaces.RemoteDistributedAppointmentFrontend;
import DAMS.Request.Request;
import DAMS.Frontend.UDP.IPCRequest;
import DAMS.Response.Response;
import DAMS.ResponseWrapper.ResponseWrapper;

import javax.jws.WebService;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@WebService(endpointInterface = "DAMS.Frontend.Interfaces.RemoteDistributedAppointmentFrontend")
public class RemoteDistributedAppointmentFrontendWebService implements RemoteDistributedAppointmentFrontend {

    String serverCode;
    String path;
    String userID;
    String role;
    IPCRequest ipcRequest;

    public RemoteDistributedAppointmentFrontendWebService(String path) {
        super();
        this.path = path;
        ipcRequest = new IPCRequest();
    }

    @Override
    public String generateId(String role) {
        int min = 1;
        int max = 9999;
        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
        return role.substring(0, 1) + random_int;
    }

    @Override
    public String serverResponse() {
        System.out.println("working");
//        Request request = new Request(serverCode, userID,"AddAppointment", "MTLE220222", "Dental", "Testing...", 3);
//        ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        return "testing";

    }

    @Override
    public boolean authenticateUser(String userID, String role) {
        boolean isValid = false;
        this.serverCode = userID.substring(0, 3);
        this.userID = userID;
        this.role = role;
        if (role.equals("Admin")) {
            isValid = this.authenticateAdmin(userID);
        } else {
            isValid = this.authenticatePatient(userID);
        }
        return isValid;
    }

    private boolean authenticateAdmin(String userId) {
        try {
            BufferedReader bR = new BufferedReader(
                    new FileReader(path + "/Generated Files/Database/Admins.properties"));
            Properties prop = new Properties();
            prop.load(bR);
            bR.close();
            List<String> admins = Arrays.asList(
                    prop.getProperty(serverCode).substring(1, prop.getProperty(serverCode).length() - 1).trim().split(", "));
            if (admins.contains(userId)) {
                return true;
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private boolean authenticatePatient(String userId) {
        try {
            BufferedReader bR = new BufferedReader(
                    new FileReader(path + "/Generated Files/Database/Patients.properties"));
            Properties prop = new Properties();
            prop.load(bR);
            bR.close();
            List<String> patients = Arrays.asList(
                    prop.getProperty(serverCode).substring(1, prop.getProperty(serverCode).length() - 1).trim().split(", "));
            if (patients.contains(userId)) {
                return true;
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;

    }

    @Override
    public String[] getAppointmentTypes() {
        Request request = new Request(serverCode, userID, "GetAppointmentTypes");
        Response response = ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        return response.getMessages();
    }

    @Override
    public String[] getTimeSlots() {

        Request request = new Request(serverCode, userID, "GetTimeSlots");
        Response response = ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        return response.getMessages();
    }

    @Override
    public String addAppointment(String appointmentID, String appointmentType, String appointmentDescription, int capacity) {

        Request request = new Request(serverCode, userID, "AddAppointment", appointmentID, appointmentType, appointmentDescription, capacity);
        Response response = ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        return response.message();
    }

    @Override
    public ResponseWrapper viewAppointment(String appointmentType) {
        try {
            Request request = new Request(serverCode, userID, "ViewAppointment", appointmentType);
            ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String removeAppointment(String appointmentID, String appointmentType) {
        try {
            Request request = new Request(serverCode, userID, "RemoveAppointment", appointmentID, appointmentType);
            ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public ResponseWrapper listAppointmentAvailability(String appointmentType) {
        Request request = new Request(serverCode, userID, "ListAppointmentAvailability", appointmentType);
        Response response = ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        return response.getResponseWrapper();
    }

    @Override
    public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
        Request request = new Request(serverCode, userID, "BookAppointment", patientID, appointmentID, appointmentType);
        Response response = ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        return response.message();
    }

    @Override
    public String[] getAppointmentSchedule(String patientID) {
        Request request = new Request(serverCode, userID, "GetAppointmentSchedule", patientID, "", "");
        System.out.println(patientID);
        Response response = ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        System.out.println(response.getMessages().length);
        for (String a : response.getMessages()) {
            System.out.println(a);
        }
        return response.getMessages();
    }

    @Override
    public String cancelAppointment(String patientID, String appointmentID, String appointmentType) {
        try {
            Request request = new Request(serverCode, userID, "CancelAppointment", patientID, appointmentID, appointmentType);
            ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) {
        try {
            Request request = new Request(serverCode, userID, "SwapAppointment", patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
            ipcRequest.sendRequestToSequencerAndGetReplyFromFE(request);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

}
