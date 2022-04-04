package DAMS.Replicas.Replica2;

import DAMS.Replicas.Replica2.server.AppointmentBooking;
import DAMS.Replicas.Replica2.server.domain.AppointmentType;
import DAMS.Replicas.Replica2.server.domain.CityType;
import DAMS.Replicas.Replica2.server.domain.UDPActionType;
import DAMS.Replicas.Replica2.server.domain.User;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

import static DAMS.Replicas.Replica2.server.domain.LogLevel.ERROR;
import static DAMS.Replicas.Replica2.server.domain.LogLevel.INFO;
import static DAMS.Replicas.Replica2.server.util.CommonUtil.convertFromBytes;
import static DAMS.Replicas.Replica2.server.util.CommonUtil.convertToBytes;
import static DAMS.Replicas.Replica2.server.util.LoggingUtil.log;

class UDPThread implements Runnable {
    CityType cityType;
    int port;
    AppointmentBooking appointmentBookingService;

    UDPThread(CityType cityType, int port, AppointmentBooking appointmentBookingService) {
        this.cityType = cityType;
        this.port = port;
        this.appointmentBookingService = appointmentBookingService;
    }

    @Override
    public void run() {
        try (DatagramSocket aSocket = new DatagramSocket(port)) {
            byte[] buffer = new byte[65508];
            boolean isRunning = true;
            while (isRunning) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(packet);

                Map<UDPActionType, Object> request = (Map<UDPActionType, Object>) convertFromBytes(packet.getData());
                Object response;

                AppointmentType appointmentType;
                HashMap<String, Object> requestData;
                switch (request.keySet().stream().findFirst().get()) {
                    case LIST_APPOINTMENT_AVAILABILITY:
                        log(cityType, "Received UDP request", INFO, String.format("Received request action [%s]", UDPActionType.LIST_APPOINTMENT_AVAILABILITY.getDescription()));
                        appointmentType = AppointmentType.valueOf((String) request.get(UDPActionType.LIST_APPOINTMENT_AVAILABILITY));
                        response = appointmentBookingService.getAppointmentsByType(appointmentType);
                        break;
                    case VALIDATE_BOOK_APPOINTMENT:
                        log(cityType, "Received UDP request", INFO, String.format("Received request action [%s]", UDPActionType.VALIDATE_BOOK_APPOINTMENT.getDescription()));
                        requestData = (HashMap<String, Object>) request.get(UDPActionType.VALIDATE_BOOK_APPOINTMENT);
                        appointmentType = AppointmentType.valueOf((String) requestData.get("appointmentType"));
                        response = appointmentBookingService.validateBookAppointment(
                                (String) requestData.get("patientID"), (String) requestData.get("appointmentID"), appointmentType);
                        break;
                    case BOOK_APPOINTMENT:
                        log(cityType, "Received UDP request", INFO, String.format("Received request action [%s]", UDPActionType.BOOK_APPOINTMENT.getDescription()));
                        requestData = (HashMap<String, Object>) request.get(UDPActionType.BOOK_APPOINTMENT);
                        appointmentType = AppointmentType.valueOf((String) requestData.get("appointmentType"));
                        response = appointmentBookingService.bookAppointment((User) requestData.get("requester"), (String) requestData.get("patientID"),
                                (String) requestData.get("appointmentID"), appointmentType);
                        break;
                    case VALIDATE_CANCEL_APPOINTMENT:
                        log(cityType, "Received UDP request", INFO, String.format("Received request action [%s]", UDPActionType.VALIDATE_CANCEL_APPOINTMENT.getDescription()));
                        requestData = (HashMap<String, Object>) request.get(UDPActionType.VALIDATE_CANCEL_APPOINTMENT);
                        response = appointmentBookingService.validateCancelAppointment((User) requestData.get("requester"), (String) requestData.get("patientID"),
                                (String) requestData.get("appointmentID"));
                        break;
                    case CANCEL_APPOINTMENT:
                        log(cityType, "Received UDP request", INFO, String.format("Received request action [%s]", UDPActionType.CANCEL_APPOINTMENT.getDescription()));
                        requestData = (HashMap<String, Object>) request.get(UDPActionType.CANCEL_APPOINTMENT);
                        response = appointmentBookingService.cancelAppointment((User) requestData.get("requester"), (String) requestData.get("patientID"),
                                (String) requestData.get("appointmentID"));
                        break;
                    case GET_APPOINTMENT_SCHEDULE:
                        log(cityType, "Received UDP request", INFO, String.format("Received request action [%s]", UDPActionType.GET_APPOINTMENT_SCHEDULE.getDescription()));
                        String patientID = (String) request.get(UDPActionType.GET_APPOINTMENT_SCHEDULE);
                        response = appointmentBookingService.getPatientAppointments(patientID);
                        break;
                    default:
                        log(cityType, "Received UDP request", ERROR, String.format("Unknown request action provided"));
                        continue;
                }

                byte[] responseDataBytes = convertToBytes(response);
                DatagramPacket reply = new DatagramPacket(responseDataBytes, responseDataBytes.length, packet.getAddress(), packet.getPort());
                aSocket.send(reply);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}