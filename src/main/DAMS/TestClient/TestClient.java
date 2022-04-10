package DAMS.TestClient;

import DAMS.Clients.Operations.Operations;
import DAMS.Clients.WebService.WebServiceClientOperations;
import DAMS.Frontend.Interfaces.RemoteDistributedAppointmentFrontend;

import java.util.Scanner;

public class TestClient {

    public static void main(String[] args) {
        Operations dT = new Operations("Admin");
        String serverName = dT.selectedCity(1); //MTL
        WebServiceClientOperations op = new WebServiceClientOperations("Admin", dT);
        op.lookup(dT.getIP(), dT.getPort(), serverName);

        RemoteDistributedAppointmentFrontend rda = op.authenticateForTestAdmin(serverName);

        //Start test cases
        System.out.println("\n\nPress ENTER to start tests...");
        try (Scanner kbd = new Scanner(System.in)) {
            kbd.nextLine();

            TestDetail testDetail = new TestDetail();
            assertTrue(testDetail.init("Appointment types has a count of 3"), 3, rda.getAppointmentTypes().length);
            assertTrue(testDetail.init("Number of Dental appointments is 9"), 9, rda.listAppointmentAvailability("Dental").getData().size());
            assertTrue(testDetail.init("Successfully add new appointment"), true, rda.addAppointment("MTLE060422", "Dental", "Dental", 2).contains("success"));
            assertTrue(testDetail.init("Number of Dental appointments is 10"), 10, rda.listAppointmentAvailability("Dental").getData().size());
            assertFalse(testDetail.init("Fail to add appointment for another city"), true, rda.addAppointment("SHEE060422", "Dental", "Dental", 2).contains("success"));

            //Process crash simulation
            System.out.println("\n\nStop one server to simulate process crash. Press ENTER when ready...");
            kbd.nextLine();

            String[] appointmentSchedule1 = rda.getAppointmentSchedule("MTLP2046");
            assertTrue(testDetail.init("Patient MTL2046 has no appointments"), true, appointmentSchedule1.length == 0 || (appointmentSchedule1.length == 2 && appointmentSchedule1[0].equals("No") && appointmentSchedule1[1].equals("Replica3")));
            assertTrue(testDetail.init("Successfully booked Dental appointment ID MTLE100222 for Patient MTL2046"), true, rda.bookAppointment("MTLP2046", "MTLE100222", "Dental").contains("success"));
            String[] appointmentSchedule2 = rda.getAppointmentSchedule("MTLP2046");
            assertTrue(testDetail.init("Patient MTL2046 has 1 appointment"), 1, appointmentSchedule2.length == 2 || (appointmentSchedule2.length == 2 && appointmentSchedule2[0].equals("Yes") && appointmentSchedule2[1].equals("Replica3")));
            assertFalse(testDetail.init("Cannot book same Dental appointment ID MTLE100222 for Patient MTL2046"), true, rda.bookAppointment("MTLP2046", "MTLE100222", "Dental").contains("success"));

            //Software failure simulation
            System.out.println("\n\nRestart server with simulated software failure. Press ENTER when ready...");
            kbd.nextLine();

            assertTrue(testDetail.init("Successfully add new appointment"), true, rda.addAppointment("MTLE070422", "Dental", "Dental", 2).contains("success"));

            System.out.println("\n\n----------------------------------------------\n" +
                    "Completed " + testDetail.getCount() + " tests!\n" +
                    "Passed count:" + testDetail.getPassedCount() + "\n" +
                    "Failed count:" + testDetail.getFailedCount());
        }
    }

    public static boolean assertTrue(TestDetail testDetail, Object expected, Object actual) {
        boolean isTrue = expected.equals(actual);
        System.out.println(testDetail.getCount() + ") " + (isTrue ? "PASSED - " : "FAILED - ") + testDetail.getDescription());
        if (isTrue)
            testDetail.setPassedCount(testDetail.getPassedCount() + 1);
        else
            testDetail.setFailedCount(testDetail.getFailedCount() + 1);
        return isTrue;
    }

    public static boolean assertFalse(TestDetail testDetail, Object expected, Object actual) {
        boolean isFalse = !expected.equals(actual);
        System.out.println(testDetail.getCount() + ") " + (isFalse ? "PASSED - " : "FAILED - ") + testDetail.getDescription());
        if (isFalse)
            testDetail.setPassedCount(testDetail.getPassedCount() + 1);
        else
            testDetail.setFailedCount(testDetail.getFailedCount() + 1);
        return isFalse;
    }
}
