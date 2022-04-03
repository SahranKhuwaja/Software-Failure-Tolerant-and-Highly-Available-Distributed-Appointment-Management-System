package DAMS.Frontend.WebService;

import DAMS.Frontend.Interfaces.RemoteDistributedAppointmentFrontend;
import DAMS.Frontend.Request.Request;
import DAMS.Frontend.ResponseWrapper.ResponseWrapper;
import DAMS.Frontend.UDP.IPCRequest;

import javax.jws.WebService;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

    public RemoteDistributedAppointmentFrontendWebService(String path){
        super();
        this.path = path;
        ipcRequest = new IPCRequest();
    }

    public RemoteDistributedAppointmentFrontendWebService(){

    }

    @Override
    public String generateId(String role) {
        return role;
    }

    @Override
    public String serverResponse() {
        System.out.println("working");
        return "testing";

    }

    @Override
    public boolean authenticateUser(String userID, String role) {
        boolean isValid = false;
        this.serverCode = userID.substring(0,3);
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
                    prop.getProperty(serverCode).substring(1,prop.getProperty(serverCode).length()-1).trim().split(", "));
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
                    prop.getProperty(serverCode).substring(1,prop.getProperty(serverCode).length()-1).trim().split(", "));
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
        try {
            Request request = new Request("GetAppointmentTypes");
            ipcRequest.sendRequestToSequencer(request);
        }catch (IOException e){
            System.out.println(e);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;

    }

    @Override
    public String[] getTimeSlots() {
        try {
            Request request = new Request("GetTimeSlots");
            ipcRequest.sendRequestToSequencer(request);
        }catch (IOException e){
            System.out.println(e);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String addAppointment(String appointmentID, String appointmentType, String appointmentDescription, int capacity) {
        try {
            Request request = new Request("AddAppointment", appointmentID, appointmentType, appointmentDescription, capacity);
            ipcRequest.sendRequestToSequencer(request);
        }catch (IOException e){
            System.out.println(e);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public ResponseWrapper viewAppointment(String appointmentType) {
        try {
            Request request = new Request("ViewAppointment", appointmentType);
            ipcRequest.sendRequestToSequencer(request);
        }catch (IOException e){
            System.out.println(e);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String removeAppointment(String appointmentID, String appointmentType) {
        try {
            Request request = new Request("RemoveAppointment", appointmentID, appointmentType);
            ipcRequest.sendRequestToSequencer(request);
        }catch (IOException e){
            System.out.println(e);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public ResponseWrapper listAppointmentAvailability(String appointmentType) {
        try {
            Request request = new Request("ListAppointmentAvailability", appointmentType);
            ipcRequest.sendRequestToSequencer(request);
        }catch (IOException e){
            System.out.println(e);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
        try {
            Request request = new Request("BookAppointment", patientID, appointmentID, appointmentType);
            ipcRequest.sendRequestToSequencer(request);
        }catch (IOException e){
            System.out.println(e);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String[] getAppointmentSchedule(String patientID) {
        try {
            Request request = new Request("GetAppointmentSchedule", patientID, null, "PatientOperation", role);
            ipcRequest.sendRequestToSequencer(request);
        }catch (IOException e){
            System.out.println(e);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String cancelAppointment(String patientID, String appointmentID, String appointmentType) {
        try {
            Request request = new Request("CancelAppointment", patientID, appointmentID, appointmentType);
            ipcRequest.sendRequestToSequencer(request);
        }catch (IOException e){
            System.out.println(e);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) {
        try {
            Request request = new Request("SwapAppointment", patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
            ipcRequest.sendRequestToSequencer(request);
        }catch (IOException e){
            System.out.println(e);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }



}
