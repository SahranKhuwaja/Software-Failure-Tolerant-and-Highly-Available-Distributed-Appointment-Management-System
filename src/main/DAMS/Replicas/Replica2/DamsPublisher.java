package DAMS.Replicas.Replica2;

import DAMS.Replicas.Replica2.server.AppointmentBooking;
import DAMS.Replicas.Replica2.server.AppointmentBookingImpl;
import DAMS.Replicas.Replica2.server.domain.CityType;

import static DAMS.Replicas.Replica2.server.util.ServerUtil.getPortByCityType;

public class DamsPublisher {

    private static AppointmentBooking mtlService;
    private static AppointmentBooking queService;
    private static AppointmentBooking sheService;

    public static void main(String[] args) {

        startServer(CityType.MTL);
        startServer(CityType.QUE);
        startServer(CityType.SHE);

        Thread udpThread = new Thread(new UDPMainThread(mtlService, queService, sheService));
        udpThread.start();
    }

    private static void startServer(CityType cityType) {
        try {
            initServer(cityType);
        } catch(Exception ex) {
            System.out.println(String.format("Error while starting %s server", cityType.getDescription()));
        }
    }

    private static void initServer(CityType cityType) throws Exception {
        AppointmentBooking appointmentBookingService = new AppointmentBookingImpl(cityType);
        switch(cityType) {
            case MTL:
                mtlService = appointmentBookingService;
                break;
            case QUE:
                queService = appointmentBookingService;
                break;
            case SHE:
                sheService = appointmentBookingService;
                break;
        }
        //Listen to UDP requests
        Integer port = getPortByCityType(cityType);
        Thread tcpThread = new Thread(new UDPThread(cityType, port, appointmentBookingService));
        tcpThread.start();
        System.out.println(String.format("Server started - %s (Port %s)", cityType.getDescription(), port));
    }
}
