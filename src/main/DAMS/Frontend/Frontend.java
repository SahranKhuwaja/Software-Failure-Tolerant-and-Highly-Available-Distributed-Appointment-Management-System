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
        String ip = "172.20.10.2";
        int port = 6800;
        String url = "http://" + ip + ":" + port + "/RemoteDistributedAppointmentFrontend";
        RemoteDistributedAppointmentFrontendWebService rda = op.binding(url, port);
    }
}
