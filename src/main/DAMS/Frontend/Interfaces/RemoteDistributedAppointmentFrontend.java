package DAMS.Frontend.Interfaces;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import DAMS.Frontend.ResponseWrapper.ResponseWrapper;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface RemoteDistributedAppointmentFrontend {

        //Other Functions
        @WebMethod
        public String generateId(String role);
        @WebMethod
        public String serverResponse();
        @WebMethod
        public boolean authenticateUser(String userID, String role);

        //Main Core Functions
        @WebMethod
        public String[] getAppointmentTypes();
        @WebMethod
        public String[] getTimeSlots();
        @WebMethod
        public String addAppointment(String appointmentID, String appointmentType,
                                     String appointmentDescription, int capacity);
        //ResponseWrapper is used for sending HashMaps
        @WebMethod
        public ResponseWrapper viewAppointment(String appointmentType);
        @WebMethod
        public String removeAppointment (String appointmentID, String appointmentType);
        //ResponseWrapper is used for sending HashMaps
        @WebMethod
        public ResponseWrapper listAppointmentAvailability(String appointmentType);
        @WebMethod
        public String bookAppointment (String patientID, String appointmentID,
                                       String appointmentType);
        @WebMethod
        public String[] getAppointmentSchedule (String patientID);
        @WebMethod
        public String cancelAppointment (String patientID, String appointmentID,
                                         String appointmentType);
        @WebMethod
        public String swapAppointment (String patientID, String oldAppointmentID,
                                       String oldAppointmentType, String newAppointmentID,
                                       String newAppointmentType);


}
