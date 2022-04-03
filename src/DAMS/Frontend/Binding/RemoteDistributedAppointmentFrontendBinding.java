package DAMS.Frontend.Binding;

import DAMS.Frontend.Interfaces.FrontendBinding;
import DAMS.Frontend.WebService.RemoteDistributedAppointmentFrontendWebService;

import javax.xml.ws.Endpoint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RemoteDistributedAppointmentFrontendBinding implements FrontendBinding {
    RemoteDistributedAppointmentFrontendWebService rda;
    String path;

    public RemoteDistributedAppointmentFrontendBinding(){
        this.getPath();
    }

    @Override
    public RemoteDistributedAppointmentFrontendWebService binding(String url, int port) {
        try {
            this.generateDirectory();
            rda = new RemoteDistributedAppointmentFrontendWebService(path);
            Endpoint endpoint = Endpoint.create(rda);
            endpoint.publish(url);
            System.out.println("Remote Distributed Appointment Frontend is running on port " + port);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return rda;
    }

    @Override
    public  void getPath(){
        path  = System.getProperty("user.dir");
        if(path.contains("/out")) {
            path = path.split("/out")[0];
        }
        if(path.contains("/bin")) {
            path = path.split("/bin")[0];
        }
        if(path.contains("/src")) {
            path = path.split("/src")[0];
        }
    }

    @Override
    public  void generateDirectory() {
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
    public  void generateAdmins() {
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
    public  void generatePatients() {
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
}
