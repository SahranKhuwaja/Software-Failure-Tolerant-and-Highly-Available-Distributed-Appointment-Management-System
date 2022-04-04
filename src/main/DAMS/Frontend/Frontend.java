package DAMS.Frontend;

import DAMS.Frontend.Binding.RemoteDistributedAppointmentFrontendBinding;
import DAMS.Frontend.WebService.RemoteDistributedAppointmentFrontendWebService;


public class Frontend {

    public static void main(String[] args){
        initServer();
    }

    public static void initServer(){
        RemoteDistributedAppointmentFrontendBinding op = new
                RemoteDistributedAppointmentFrontendBinding();
        String ip = "172.30.84.19";
        int port = 6800;
        String url = "http://" + ip + ":" + port + "/RemoteDistributedAppointmentFrontend";
        RemoteDistributedAppointmentFrontendWebService rda = op.binding(url, port);
    }
}
