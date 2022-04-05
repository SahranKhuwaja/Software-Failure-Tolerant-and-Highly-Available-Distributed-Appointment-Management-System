package DAMS.Replicas.Replica2;

import DAMS.Request.Request;
import DAMS.Replicas.Replica2.server.domain.CityType;
import DAMS.Replicas.Replica2.server.util.ConfigUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static DAMS.Replicas.Replica2.server.config.DamsConfig.MAIN_UDP_SERVER_PORT;
import static DAMS.Replicas.Replica2.server.util.CommonUtil.convertFromBytes;
import static DAMS.Replicas.Replica2.server.util.ServerUtil.getPortByCityType;

public class UDPMainThread implements Runnable {

    @Override
    public void run() {
        try {
            int port = Integer.parseInt(ConfigUtil.getPropValue(MAIN_UDP_SERVER_PORT));
            try (DatagramSocket aSocket = new DatagramSocket(port)) {

                byte[] buffer = new byte[65508];
                boolean isRunning = true;
                while (isRunning) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(packet);

                    Request request = (Request) convertFromBytes(packet.getData());
                    Integer targetCityPort = getPortByCityType(CityType.valueOf(request.getServerCode()));

                    switch (request.getOperation()) {
                        case "AddAppointment":
                            break;
                        case "RemoveAppointment":
                            break;
                        case "ListAppointmentAvailability":
                            break;
                        case "BookAppointment":
                            break;
                        case "GetAppointmentSchedule":
                            break;
                        case "CancelAppointment":
                            break;
                        case "SwapAppointment":
                            break;
                        default:
                            System.out.println("Unknown request operation received");
                            break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
