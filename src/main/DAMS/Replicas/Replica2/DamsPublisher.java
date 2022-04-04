package DAMS.Replicas.Replica2;

import DAMS.Replicas.Replica2.server.AppointmentBooking;
import DAMS.Replicas.Replica2.server.AppointmentBookingImpl;
import DAMS.Replicas.Replica2.server.domain.CityType;

import static DAMS.Replicas.Replica2.server.util.ServerUtil.getPortByCityType;

public class DamsPublisher {

    public static void main(String[] args) {
        Thread udpThread = new Thread(new UDPMainThread());
        udpThread.start();

        startServer(CityType.MTL);
        startServer(CityType.QUE);
        startServer(CityType.SHE);
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
        //Listen to UDP requests
        Thread tcpThread = new Thread(new UDPThread(cityType, getPortByCityType(cityType), appointmentBookingService));
        tcpThread.start();
        System.out.println(String.format("Server started - %s", cityType.getDescription()));
    }
}
