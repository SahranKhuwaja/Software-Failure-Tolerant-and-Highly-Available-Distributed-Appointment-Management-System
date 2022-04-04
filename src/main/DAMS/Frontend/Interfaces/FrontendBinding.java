package DAMS.Frontend.Interfaces;

import DAMS.Frontend.WebService.RemoteDistributedAppointmentFrontendWebService;

public interface FrontendBinding {

    public RemoteDistributedAppointmentFrontendWebService binding(String url, int port);
    public void getPath();
    public  void generateDirectory();
    public  void generateAdmins();
    public  void generatePatients();

}
