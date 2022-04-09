package DAMS.Replicas.Replica4.Binding;

import DAMS.Replicas.Replica4.Interfaces.ServerBinding;
import DAMS.Replicas.Replica4.RemoteDistributedAppointment.RemoteDistributedAppointment;
import DAMS.Replicas.Replica4.UDP.IPCReplyToFE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RemoteDistributedAppointmentBinding implements ServerBinding {
	public String path;
	public RemoteDistributedAppointment rda;
	String serverName;
	Logger logger;
	
	public RemoteDistributedAppointmentBinding() {
		this.getPath();
	}

	@Override
	public RemoteDistributedAppointment binding(int port, String serverName) {
		try {
			this.serverName = serverName;
			this.generateDirectory();
			this.setupLogs();
			rda = new RemoteDistributedAppointment(serverName,path,logger);
			System.out.println(serverName + " server is running on port " + port);
			DatagramSocket datagramSocket = null;
			try {
				datagramSocket = new DatagramSocket(port);
			} catch (SocketException e) {
				System.out.println(e.getMessage());
			}
			new IPCReplyToFE(datagramSocket, serverName, port, rda);
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return rda;
	}

	@Override
	public void getPath(){
		path  = System.getProperty("user.dir");
		if(path.contains("/bin")) {
			path = path.split("/bin")[0];
		}
		if(path.contains("/src")) {
			path = path.split("/src")[0];
		}
	}
	@Override
	public void generateDirectory() {
		File f = new File(path+"/Generated Files");
		if(!f.exists()) {
			f.mkdir();
		}
		File f2 = new File(path+"/Generated Files/Database");
		if(!f2.exists()) {
			f2.mkdir();
		}
		File f3 = new File(path+"/Generated Files/Database/Admins.properties");
		if(!f3.exists()) {
			this.generateAdmins();
		}
		File f4 = new File(path+"/Generated Files/Database/Patients.properties");
		if(!f4.exists()) {
			this.generatePatients();
		}
		File f5 = new File(path+"/Generated Files/Server Logs");
		if(!f5.exists()) {
			f5.mkdir();
		}
		
	}
	
	@Override
	public void generateAdmins() {
		HashMap<String,String> admins = new HashMap<String,String>();
		List<String> mtlAdmins = Arrays.asList("MTLA2345","MTLA2046");
		List<String> queAdmins = Arrays.asList("QUEA2345","QUEA2046");
		List<String> sheAdmins = Arrays.asList("SHEA2345","SHEA2046");

		admins.put("MTL", mtlAdmins.toString());
		admins.put("QUE", queAdmins.toString());
		admins.put("SHE", sheAdmins.toString());
		try {
			BufferedWriter bW = new BufferedWriter(
					new FileWriter(path + "/Generated Files/Database/Admins.properties"));
			Properties prop = new Properties();
			for (Map.Entry<String,String> admin : admins.entrySet()) {
			    prop.put(admin.getKey(), admin.getValue());
			}
			prop.store(bW,null);
			bW.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	@Override
	public void generatePatients() {
		HashMap<String,String> patients = new HashMap<String,String>();
		List<String> mtlPatients = Arrays.asList("MTLP2345","MTLP2046");
		List<String> quePatients = Arrays.asList("QUEP2345","QUEP2046");
		List<String> shePatients = Arrays.asList("SHEP2345","SHEP2046");

		patients.put("MTL", mtlPatients.toString());
		patients.put("QUE", quePatients.toString());
		patients.put("SHE", shePatients.toString());
		try {
			BufferedWriter bW = new BufferedWriter(
					new FileWriter(path + "/Generated Files/Database/Patients.properties"));
			Properties prop = new Properties();
			for (Map.Entry<String,String> patient : patients.entrySet()) {
			    prop.put(patient.getKey(), patient.getValue());
			}
			prop.store(bW,null);
			bW.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	@Override
	public void setupLogs() {
		FileHandler handler = null;
		logger = Logger.getLogger(serverName + " Server");
		 logger.setUseParentHandlers(false);
		try {
			handler = new FileHandler(path+"/Generated Files/Server Logs/"
						+ serverName +".txt",true);
		} catch (SecurityException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		logger.addHandler(handler);
	    SimpleFormatter formatter = new SimpleFormatter();  
	    handler.setFormatter(formatter); 
	}
	
	
}
