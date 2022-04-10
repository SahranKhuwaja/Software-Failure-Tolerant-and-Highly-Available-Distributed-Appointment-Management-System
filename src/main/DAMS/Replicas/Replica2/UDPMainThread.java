package DAMS.Replicas.Replica2;

import DAMS.Replicas.Replica2.server.AppointmentBooking;
import DAMS.Replicas.Replica2.server.domain.AppointmentType;
import DAMS.Replicas.Replica2.server.domain.CityType;
import DAMS.Replicas.Replica2.server.util.ConfigUtil;
import DAMS.Request.Request;
import DAMS.Response.Response;
import DAMS.ResponseWrapper.ResponseWrapper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import static DAMS.Replicas.Replica2.server.config.DamsConfig.*;
import static DAMS.Replicas.Replica2.server.domain.LogLevel.INFO;
import static DAMS.Replicas.Replica2.server.util.CommonUtil.convertFromBytes;
import static DAMS.Replicas.Replica2.server.util.CommonUtil.convertToBytes;
import static DAMS.Replicas.Replica2.server.util.LoggingUtil.log;

public class UDPMainThread implements Runnable {
    private final int REPLICA_NO = 2;
    private AppointmentBooking mtlService;
    private AppointmentBooking queService;
    private AppointmentBooking sheService;

    UDPMainThread(AppointmentBooking mtlService, AppointmentBooking queService, AppointmentBooking sheService) {
        this.mtlService = mtlService;
        this.queService = queService;
        this.sheService = sheService;
    }

    @Override
    public void run() {
        try {
            int port = Integer.parseInt(ConfigUtil.getPropValue(MAIN_UDP_SERVER_PORT));
            System.out.println("Main server started at " + port);
            try (DatagramSocket aSocket = new DatagramSocket(port)) {
                byte[] buffer = new byte[65508];
                boolean isRunning = true;
                while (isRunning) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(packet);

                    Request request = (Request) convertFromBytes(packet.getData());
                    AppointmentBooking targetServer;
                    switch (CityType.valueOf(request.getServerCode())) {
                        case MTL:
                            targetServer = mtlService;
                            break;
                        case QUE:
                            targetServer = queService;
                            break;
                        case SHE:
                            targetServer = sheService;
                            break;
                        default:
                            System.out.println("Unknown server code received");
                            return;
                    }
                    log(CityType.valueOf(request.getServerCode()), "Received UDP message", INFO, request.getOperation());

                    //Main method to process request in target server
                    Response response = process(targetServer, request);

                    //TODO: uncomment this line for software failure simulation
                    //response.setSuccess(false);

                    response.setReplica(REPLICA_NO);

                    //Send response to Frontend
                    byte[] responseDataBytes = convertToBytes(response);
                    InetSocketAddress ip = new InetSocketAddress(request.getFE_IP(), request.getFE_PORT());
                    //InetSocketAddress ip = new InetSocketAddress(ConfigUtil.getPropValue(FRONTEND_UDP_SERVER_IP), Integer.parseInt(ConfigUtil.getPropValue(FRONTEND_UDP_SERVER_PORT)));
                    DatagramPacket reply = new DatagramPacket(responseDataBytes, responseDataBytes.length, ip);

                    log(CityType.valueOf(request.getServerCode()), "Replying UDP message", INFO, request.getOperation());
                    aSocket.send(reply);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response process(AppointmentBooking targetServer, Request request) {
        String message;
        ResponseWrapper responseWrapper;
        Response response = null;

        switch (request.getOperation()) {
            case "AddAppointment":
                message = targetServer.addAppointment(request.getAppointmentID(), AppointmentType.getAppointmentTypeFromDescription(request.getAppointmentType()), request.getCapacity());
                response = new Response(request.getOperation(), request.getOperation(), message.toLowerCase().contains("success"), message);
                break;
            case "RemoveAppointment":
                message = targetServer.removeAppointment(request.getAppointmentID(), AppointmentType.getAppointmentTypeFromDescription(request.getAppointmentType()), request.getUserID());
                response = new Response(request.getOperation(), request.getOperation(), message.toLowerCase().contains("success"), message);
                break;
            case "ListAppointmentAvailability":
                responseWrapper = targetServer.listAppointmentAvailability(AppointmentType.getAppointmentTypeFromDescription(request.getAppointmentType()));
                responseWrapper.setReplica(REPLICA_NO);
                response = new Response(request.getOperation(), request.getOperation(), responseWrapper.getData().isEmpty(), responseWrapper);
                break;
            case "BookAppointment":
                message = targetServer.bookAppointment(request.getUserID(), request.getPatientID(), request.getAppointmentID(), AppointmentType.getAppointmentTypeFromDescription(request.getAppointmentType()));
                response = new Response(request.getOperation(), request.getOperation(), message.toLowerCase().contains("success"), message);
                break;
            case "GetAppointmentSchedule":
                String[] appointments = targetServer.getAppointmentSchedule(request.getPatientID());
                response = new Response(request.getOperation(), request.getOperation(), appointments.length > 0, appointments);
                break;
            case "CancelAppointment":
                message = targetServer.cancelAppointment(request.getUserID(), request.getPatientID(), request.getAppointmentID());
                response = new Response(request.getOperation(), request.getOperation(), message.toLowerCase().contains("success"), message);
                break;
            case "SwapAppointment":
                message = targetServer.swapAppointment(request.getUserID(), request.getPatientID(),
                        request.getOldAppointmentID(), AppointmentType.getAppointmentTypeFromDescription(request.getOldAppointmentType()),
                        request.getAppointmentID(), AppointmentType.getAppointmentTypeFromDescription(request.getAppointmentType()));
                response = new Response(request.getOperation(), request.getOperation(), message.toLowerCase().contains("success"), message);
                break;
            case "GetAppointmentTypes":
                String[] appointmentTypes = targetServer.getAppointmentTypes();
                response = new Response(request.getOperation(), request.getOperation(), appointmentTypes.length > 0, appointmentTypes);
                break;
            case "GetTimeSlots":
                String[] timeSlots = targetServer.getTimeSlots();
                response = new Response(request.getOperation(), request.getOperation(), timeSlots.length > 0, timeSlots);
                break;
            default:
                System.out.println("Unknown request operation received");
                break;
        }
        return response;
    }
}
