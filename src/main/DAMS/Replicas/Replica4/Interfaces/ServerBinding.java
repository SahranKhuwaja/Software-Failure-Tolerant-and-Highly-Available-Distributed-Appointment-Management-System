package DAMS.Replicas.Replica4.Interfaces;


import DAMS.Replicas.Replica1.RemoteDistributedAppointment.RemoteDistributedAppointment;

public interface ServerBinding {
	public RemoteDistributedAppointment binding(int port, String serverName);
	public void getPath();
	public void generateDirectory();
	public void generateAdmins();
	public void generatePatients();
	public void setupLogs();
}
