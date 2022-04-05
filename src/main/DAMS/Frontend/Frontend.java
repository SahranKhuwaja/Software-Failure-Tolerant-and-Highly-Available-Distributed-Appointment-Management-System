package DAMS.Frontend;

import DAMS.Frontend.Binding.RemoteDistributedAppointmentFrontendBinding;
import DAMS.Frontend.WebService.RemoteDistributedAppointmentFrontendWebService;


public class Frontend {

    public static final String HOST_IP = "192.168.2.12";
    public static final int PORT = 6800;

    public static void main(String[] args){
        initServer();
    }

    public static void initServer(){
        RemoteDistributedAppointmentFrontendBinding op = new
                RemoteDistributedAppointmentFrontendBinding();
        String url = "http://" + HOST_IP + ":" + PORT + "/RemoteDistributedAppointmentFrontend";
        RemoteDistributedAppointmentFrontendWebService rda = op.binding(url, PORT);
    }
}
