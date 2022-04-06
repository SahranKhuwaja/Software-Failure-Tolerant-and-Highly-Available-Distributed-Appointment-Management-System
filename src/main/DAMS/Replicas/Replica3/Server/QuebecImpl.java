package DAMS.Replicas.Replica3.Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class QuebecImpl implements HospitalInterface {



    HashMap<String, HashMap<String,Integer>> QUEA =new HashMap<String, HashMap<String,Integer>>();
    HashMap<String,ArrayList<String>> QUEP =new HashMap<String,ArrayList<String>>();

    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date d = new Date();



    public QuebecImpl(){
        super();
        UDPListener listener = new UDPListener();

        HashMap<String,Integer> t1=new HashMap<String,Integer>();
        t1.put("QUEA200222",3);
        QUEA.put("P",t1);

        HashMap<String,Integer> t2=new HashMap<String,Integer>();
        t2.put("QUEA210222",3);
        QUEA.put("S",t2);

        HashMap<String,Integer> t3=new HashMap<String,Integer>();
        t3.put("QUEA220222",3);
        QUEA.put("D",t3);

        ArrayList<String> al1 = new ArrayList<String>();
        al1.add("QUEA200222 P");
        al1.add("QUEA220222 D");
        al1.add("SHEA220222 D");
        al1.add("SHEA200222 P");
        QUEP.put("QUEP1234", al1);

        ArrayList<String> al2 = new ArrayList<String>();
        al2.add("QUEA200222 P");
        QUEP.put("MTLP1234", al2);

        ArrayList<String> al3 = new ArrayList<String>();
        al3.add("QUEA200222 P");
        QUEP.put("SHEP1234", al3);

        listener.start();

    }

    public static BufferedWriter logWriter() {

        File myObj = null;
        FileWriter fw = null;
        BufferedWriter output = null;
        try {

            myObj = new File("DAMS/Replicas/Replica3/logs/QUEServerLogs.txt");

            if (myObj.createNewFile()) {
                System.out.println("Log File created: " + myObj.getName());
            }

            fw = new FileWriter("DAMS/Replicas/Replica3/logs/QUEServerLogs.txt", true);

            output = new BufferedWriter(fw);

        }catch(Exception e) {
            System.out.println("Error while creating Logs");
        }

        return output;

    }

    public String[] getAppointmentTypes(){
        String[] s = {"Physician", "Surgeon", "Dental"};
        return s;
    }
    public String[] getTimeSlots(){
        String[] s = {"M", "A", "E"};
        return s;
    }

    public String localCheckAvailability(String appointmentID, String appointmentType) {

        boolean has_type=QUEA.containsKey(appointmentType);
        boolean has_id=QUEA.get(appointmentType).containsKey(appointmentID);
        int c=0;
        if(has_id && has_type) {
            c = QUEA.get(appointmentType).get(appointmentID);
        }

        if(has_type && has_id && c>0) {
            return "Yes";
        }else {
            return "No";
        }



    }




    public String checkAvailability(String appointmentID, String appointmentType) {

        String city=appointmentID.substring(0, 3);
        String msg = null;

        try {

            switch(city) {
                case "MTL": {
                    //output.append(df.format(d)+" Check Montreal Server Appointment "+System.lineSeparator());
                    msg = InterServerUDPSender.checkAvailability(4141, appointmentID, appointmentType);
                }break;
                case "QUE":{
                    //output.append(df.format(d)+" Quebec Server called for check Appointment"+System.lineSeparator());

                    msg  = localCheckAvailability(appointmentID, appointmentType);

                }break;
                case "SHE":{
                    //output.append(df.format(d)+" Sherbrooke Server called for check Appointment"+System.lineSeparator());
                    msg = InterServerUDPSender.checkAvailability(2121, appointmentID, appointmentType);
                }
            }
        }catch(Exception e) {

            msg = "err";
        }

        return msg;

    }


    @Override
    public synchronized String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType,
                                               String newAppointmentID, String newAppointmentType) {

        ArrayList<String> lst = new ArrayList<String>();
        String oldPappointmentID =  oldAppointmentID+" "+oldAppointmentType;
        boolean check1 = false;
        boolean check2 = false;

        String city=newAppointmentID.substring(0, 3);
        ArrayList<String> app2 = new ArrayList<String>();

        // Check
        if(QUEP.get(patientID)!=null) {
            lst = QUEP.get(patientID);
            if(lst.contains(oldPappointmentID)) {
                check1 = true;
            }
        }

        String reply = checkAvailability(newAppointmentID, newAppointmentType);
        if(reply.equals("Yes")) {
            check2 = true;
        }

        if(!(check1 && check2)) {
            return "No";
        }

        String msg1 = null;
        String msg2 = null;



        msg1 = cancelAppointment(patientID, oldAppointmentID, oldAppointmentType );



        try {

            switch(city) {
                case "MTL": {



                    msg2 = InterServerUDPSender.bookAppointment(4141, patientID, newAppointmentID, newAppointmentType);



                    if(msg2.equals("Yes")) {

                        app2 = QUEP.get(patientID);
                        app2.add(newAppointmentID+" "+newAppointmentType);
                        QUEP.put(patientID, app2);
                    }else {
                        //output.append(df.format(d)+" Cannot Book an Appointment "+System.lineSeparator());
                    }




                }break;
                case "QUE":{msg2  = localBook(patientID, newAppointmentID, newAppointmentType);}break;
                case "SHE":{

                    msg2  = InterServerUDPSender.bookAppointment(2121,patientID, newAppointmentID, newAppointmentType);

                    if(msg2.equals("Yes")) {
                        //output.append(df.format(d)+" Appointment Booked Successfully "+System.lineSeparator());
                        app2 = QUEP.get(patientID);
                        app2.add(newAppointmentID+" "+newAppointmentType);
                        QUEP.put(patientID, app2);
                    }else {
                        //output.append(df.format(d)+" Cannot Book an Appointment "+System.lineSeparator());
                    }


                }
            }
        }catch (Exception e) {
            msg2 = "err";
        }


        if(msg1.equals("Yes") && msg2.equals("Yes")) {
            return "Yes";
        }else {
            return "No";
        }

    }




    @Override
    public synchronized String addAppointment(String appointmentID, String appointmentType, Integer capacity) {
        BufferedWriter output = logWriter();

        try {

            output.append(df.format(d)+"Request for adding Appointmetn Received at Quebec Server"+System.lineSeparator());
            output.append(df.format(d)+" Data From Client"+" appointmentID "+appointmentID+" appointmentType "+appointmentType+"Capacity"+capacity+System.lineSeparator());


            boolean has_type=QUEA.containsKey(appointmentType);
            boolean has_id=QUEA.get(appointmentType).containsKey(appointmentID);
            if(has_type && has_id) {
                output.append(df.format(d)+" Appointment Already Exist "+System.lineSeparator());
                output.append(System.lineSeparator());
                output.close();
                return "No";
            }

            QUEA.get(appointmentType).put(appointmentID, capacity);
            System.out.println(QUEA);
            output.append(df.format(d)+" Appointment Added Successfully "+System.lineSeparator());
            output.append(System.lineSeparator());
            output.close();
            return "Yes";


        }catch(Exception e) {
            try {
                output.append(df.format(d)+" Some error occured"+System.lineSeparator());
                output.append(System.lineSeparator());
                output.close();
            } catch (IOException e1) {

                System.out.println("error while dealing with logs");
            }
            return "err";
        }


    }



    @Override
    public synchronized String removeAppointment(String appointmentID, String appointmentType) {
        BufferedWriter output = logWriter();
        try {
            output.append(df.format(d)+"Request for Remove Appointment Received at Quebec Server"+System.lineSeparator());
            output.append(df.format(d)+" Data From Client"+" appointmentID "+appointmentID+" appointmentType "+appointmentType+System.lineSeparator());

            if(QUEA.get(appointmentType).get(appointmentID) != null){
                QUEA.get(appointmentType).remove(appointmentID);
                output.append(df.format(d)+" Appointment removed Successfully "+System.lineSeparator());
                output.append(System.lineSeparator());
                output.close();
                String PappointmentId = appointmentID+" "+appointmentType;
                Set<String> keys = QUEP.keySet();
                for(String s : keys) {
                    if(QUEP.get(s).contains(PappointmentId)) {
                        String msg = remove(s,PappointmentId);
                    }
                }
                return "Yes";
            }
            output.append(df.format(d)+"No Appointment to Remove"+System.lineSeparator());
            output.append(System.lineSeparator());
            output.close();
            return "No";


        }catch(Exception e) {
            try {
                output.append(df.format(d)+"Some Error Occured"+System.lineSeparator());
                output.append(System.lineSeparator());
                output.close();
            }catch(Exception e1) {
                System.out.println("error while dealing with logs");
            }
            return "err";
        }

    }

    public String remove(String patientId, String appointmentId ) {

        String city=patientId.substring(0, 3);
        String msg = "No";

        try {

            switch(city) {
                case "MTL": {

                    msg = InterServerUDPSender.remove(4141, patientId, appointmentId);
                    QUEP.get(patientId).remove(appointmentId);


                }break;
                case "QUE":{
                    QUEP.get(patientId).remove(appointmentId);
                    msg= "Yes";

                }break;
                case "SHE":{
                    msg = InterServerUDPSender.remove(2121, patientId, appointmentId);
                    QUEP.get(patientId).remove(appointmentId);

                }
            }
        }catch (Exception e) {
            System.out.println(e);
        }

        return msg;

    }
    public String localListAvailability(String appointmentType) {
        String msg ="";
        HashMap<String, Integer> hm = new HashMap<String, Integer>();

        try {

            boolean has_type=QUEA.containsKey(appointmentType);
            if(has_type) {
                hm = QUEA.get(appointmentType);
                Set<String> keys = hm.keySet();
                int size = keys.size();
                if(size>0) {
                    for(String key: keys){
                        msg = msg+key+" "+hm.get(key)+" ";
                        System.out.println(key);
                    }
                    return msg;

                }else {
                    return null;
                }


            }else {
                return null;
            }

        }catch(Exception e) {
            return "err";
        }



    }
    @Override
    public String listAppointmentAvailability(String appointmentType) {
        String msg1 = null;
        String msg2 = null;
        String msg3 = null;
        String msg = "";

        BufferedWriter output = logWriter();

        try {
            output.append(df.format(d)+"Request for View Appointment Details Received at Quebec Server"+System.lineSeparator());
            output.append(df.format(d)+" Data From Client"+" appointmentType "+appointmentType+System.lineSeparator());
            msg1  = localListAvailability(appointmentType);
            output.append(df.format(d)+"Appointment Details from Quebec Server"+msg1+System.lineSeparator());

        } catch (IOException e) {
            msg = "err";
            System.out.println("error in Quebec in list availability");
        }



        try {
            output.append(df.format(d)+" Calling Montreal server from Quebec Server for Appointment Details "+System.lineSeparator());
            msg2  = InterServerUDPSender.listAppointment(4141, appointmentType);
            output.append(df.format(d)+"Appointment Details from Montreal Server"+msg2+System.lineSeparator());
        } catch (IOException e) {
            msg = "err";
            System.out.println("error while calling the Montreal from Quebec in list availability");
        }



        try {
            output.append(df.format(d)+" Calling Sherbrooke server from Quebec Server for Appointment Details "+System.lineSeparator());
            msg3  = InterServerUDPSender.listAppointment(2121, appointmentType);
            output.append(df.format(d)+"Appointment Details from Sherbrooke Server"+msg3+System.lineSeparator());
        } catch (IOException e) {
            msg = "err";
            System.out.println("error while calling the sheerbroke from Quebec in list availability");
        }




        if(msg1!=null && msg1!="err") {
            msg = msg+msg1;
        }

        if(msg2!=null && msg2!="err") {
            msg = msg+msg2;
        }

        if(msg3!=null && msg3!="err") {
            msg = msg+msg3;
        }



        if(msg1=="err" && msg2=="err" && msg3=="err") {
            return "err";
        }else if(msg=="") {
            return "No";
        }else {
            try {
                output.append(df.format(d)+"Appointment from all the servers"+msg+System.lineSeparator());
                output.append(System.lineSeparator());
                output.close();
            }catch(Exception e) {
                System.out.println("Error while dealing log writer");
            }
            return msg;
        }


    }


    public String localBook(String patientID, String  appointmentID, String appointmentType) {
        String msg = "err";
        ArrayList<String> l = new ArrayList<String>();
        String PappointmentID =  appointmentID+" "+appointmentType;
        int c= 0 ;

        try {
            boolean has_type=QUEA.containsKey(appointmentType);
            boolean has_id=QUEA.get(appointmentType).containsKey(appointmentID);
            if(has_id && has_type) {
                c = QUEA.get(appointmentType).get(appointmentID);
            }


            if(has_type && has_id && c>0) {

                if(QUEP.containsKey(patientID)) {
                    if(QUEP.get(patientID).contains(PappointmentID)) {
                        // Appointment Already exist
                        msg = "No";

                    }else {
                        l = QUEP.get(patientID);
                        l.add(PappointmentID);
                        QUEP.put(patientID,l);
                        c = c-1;
                        QUEA.get(appointmentType).put(appointmentID,c);
                        msg = "Yes";
                    }

                }else {
                    l.add(PappointmentID);
                    QUEP.put(patientID,l);
                    c = c-1;
                    QUEA.get(appointmentType).put(appointmentID,c);
                    msg = "Yes";
                }


            }else {
                //Appointment not Available

                msg ="No";
            }
        }catch(Exception e) {
            msg = "err";
        }

        return msg;
    }


    @Override
    public synchronized String bookAppointment(String patientID, String appointmentID, String appointmentType) {

        String city=appointmentID.substring(0, 3);
        String msg = "err";
        int count = 0;
        ArrayList<String> app = new ArrayList<String>();
        ArrayList<String> app2 = new ArrayList<String>();

        if(QUEP.containsKey(patientID)) {
            app = QUEP.get(patientID);
            for(String s : app) {
                char c = s.charAt(0);
                if(c!='Q') {
                    count++;
                }
            }
        }else{
            QUEP.put(patientID,app);
        }
        BufferedWriter output = logWriter();


        try {
            output.append(df.format(d)+" Request for Book Appointment Received at Quebec Server "+System.lineSeparator());
            output.append(df.format(d)+" Data From Client"+" Patient ID "+patientID+" AppointmentID "+appointmentID+" Appointment Type "+appointmentType+System.lineSeparator());


            switch(city) {
                case "MTL": {
                    output.append(df.format(d)+" Montreal Server called for Appointment Booking "+System.lineSeparator());


                    if(count>2) {
                        output.append(df.format(d)+" Cannot book more than 3 Appoinment in other cities. "+System.lineSeparator());
                        return "No";
                    }
                    msg = InterServerUDPSender.bookAppointment(4141, patientID, appointmentID, appointmentType);
                    if(msg.equals("Yes")) {
                        output.append(df.format(d)+" Appointment Booked Successfully "+System.lineSeparator());

                        app2 = QUEP.get(patientID);
                        app2.add(appointmentID+" "+appointmentType);
                        QUEP.put(patientID, app2);
                    }else {
                        output.append(df.format(d)+" Cannot Book an Appointment "+System.lineSeparator());
                    }

                }break;
                case "QUE":{
                    output.append(df.format(d)+" Book Appointment at Quebec Server "+System.lineSeparator());

                    msg  = localBook(patientID, appointmentID, appointmentType);

                    if(msg.equals("Yes")) {
                        output.append(df.format(d)+" Appointment Booked Successfully "+System.lineSeparator());
                    }else {
                        output.append(df.format(d)+" Cannot Book an Appointment "+System.lineSeparator());
                    }

                }break;
                case "SHE":{
                    output.append(df.format(d)+" Sherbrooke Server called for Appointment Booking "+System.lineSeparator());


                    if(count>2) {
                        output.append(df.format(d)+" Cannot book more than 3 Appoinment in other cities. "+System.lineSeparator());
                        return "No";
                    }
                    msg  = InterServerUDPSender.bookAppointment(2121, patientID, appointmentID, appointmentType);
                    if(msg.equals("Yes")) {
                        output.append(df.format(d)+" Appointment Booked Successfully "+System.lineSeparator());

                        app2 = QUEP.get(patientID);
                        app2.add(appointmentID+" "+appointmentType);
                        QUEP.put(patientID, app2);
                    }else {
                        output.append(df.format(d)+" Cannot Book an Appointment "+System.lineSeparator());
                    }

                }
            }
        }catch(Exception e) {
            msg = "err";
        }

        try {
            output.append(System.lineSeparator());
            output.close();
        }catch(Exception e1) {
            System.out.println("Error with logging files");
        }

        return msg;
    }

    @Override
    public String getAppointmentSchedule(String patientID) {
        String msg = "";

        ArrayList<String> l = new ArrayList<String>();
        BufferedWriter output = logWriter();

        try {
            output.append(df.format(d)+" Request for Get Appointment Schedule Received at Quebec Server "+System.lineSeparator());
            output.append(df.format(d)+" Data From Client"+" Patient ID "+patientID+System.lineSeparator());
            boolean has_type=QUEP.containsKey(patientID);
            if(has_type) {
                l = QUEP.get(patientID);
                int size = l.size();
                if(size>0) {

                    for(String a:l) {
                        msg = msg + a +" ";
                    }
                    output.append(df.format(d)+"Appointment Schedule"+msg+System.lineSeparator());
                    output.append(System.lineSeparator());
                    output.close();
                    return msg;
                }else {
                    output.append(df.format(d)+" No Appointments Found "+System.lineSeparator());
                    output.append(System.lineSeparator());
                    output.close();
                    msg = "No";
                }

            }else {
                output.append(df.format(d)+" No Appointments Found "+System.lineSeparator());
                output.append(System.lineSeparator());
                output.close();
                msg =  "No";
            }
        }catch(Exception e) {
            msg = "err";
        }
        return msg;
    }
    public String localCancel(String patientID, String appointmentID, String appointmentType) {

        String msg = "err";

        ArrayList<String> lst = new ArrayList<String>();
        String PappointmentID =  appointmentID+" "+appointmentType;
        try {
            if(QUEP.get(patientID)!=null) {
                lst = QUEP.get(patientID);
                if(lst.contains(PappointmentID)) {

                    lst.remove(PappointmentID);
                    QUEP.put(patientID,lst);

                    int count = QUEA.get(appointmentType).get(appointmentID);
                    count++;
                    QUEA.get(appointmentType).put(appointmentID, count);
                    msg = "Yes";

                }else {
                    msg = "No";
                }
            }else {
                msg = "No";
            }
        }catch(Exception e) {
            msg = "err";
        }


        return msg;
    }

    @Override
    public synchronized String cancelAppointment(String patientID, String appointmentID, String appointmentType) {

        String city=appointmentID.substring(0, 3);
        String PappointmentID =  appointmentID+" "+appointmentType;
        ArrayList<String> lst = new ArrayList<String>();
        String msg = null;
        BufferedWriter output = logWriter();

        try {

            output.append(df.format(d)+" Request for Cancel Appointment Received at Quebec Server "+System.lineSeparator());
            output.append(df.format(d)+" Data From Client"+" Patient ID "+patientID+" AppointmentID "+appointmentID+" Appointment Type "+appointmentType+System.lineSeparator());

            switch(city) {
                case "MTL": {
                    output.append(df.format(d)+" Montreal Server called for Appointment Cancellation "+System.lineSeparator());


                    msg = InterServerUDPSender.cancelAppointment(4141, patientID, appointmentID, appointmentType);
                    if(msg.equals("Yes")) {
                        output.append(df.format(d)+" Appointment Canceled at Montreal Server "+System.lineSeparator());

                        lst = QUEP.get(patientID);
                        lst.remove(PappointmentID);
                        QUEP.put(patientID,lst);
                    }else {
                        output.append(df.format(d)+" No Appointment to Cancel "+System.lineSeparator());
                    }


                }break;
                case "QUE":{
                    output.append(df.format(d)+" Cancel Quebec Server Appointment "+System.lineSeparator());

                    msg  = localCancel(patientID, appointmentID, appointmentType);
                    if(msg.equals("Yes")) {
                        output.append(df.format(d)+" Appointment Canceled "+System.lineSeparator());
                    }else {
                        output.append(df.format(d)+" No Appointment to Cancel "+System.lineSeparator());
                    }

                }break;
                case "SHE":{
                    output.append(df.format(d)+" Sherbrooke Server called for Appointment Cancellation "+System.lineSeparator());
                    msg  = InterServerUDPSender.cancelAppointment(2121, patientID, appointmentID, appointmentType);
                    if(msg.equals("Yes")) {
                        output.append(df.format(d)+" Appointment Canceled at Sherbrooke Server "+System.lineSeparator());
                        lst = QUEP.get(patientID);
                        lst.remove(PappointmentID);
                        QUEP.put(patientID,lst);
                    }else {
                        output.append(df.format(d)+" No Appointment to Cancel "+System.lineSeparator());

                    }

                }
            }
        }catch(Exception e) {

            msg = "err";
        }

        try {
            output.append(System.lineSeparator());
            output.close();
        }catch(Exception e1) {
            System.out.println("Error with logging files");
        }
        return msg;

    }



    class UDPListener extends Thread{

        public DatagramSocket serverSocket;


        public void run(){

            try {
                serverSocket = new DatagramSocket(3131);

                while (true) {

                    String msg = null;
                    byte[] inData = new byte[1024];
                    byte[] outData = new byte[1024];
                    String[] dataPart;
                    DatagramPacket inPacket = new DatagramPacket(inData, inData.length);

                    serverSocket.receive(inPacket);
                    String receivedData =new String(inPacket.getData());
                    receivedData = receivedData.trim();
                    BufferedWriter output = logWriter();

                    try {
                        output.append(df.format(d)+" Inter Server Request Received at Quebec Server "+System.lineSeparator());
                        output.append(df.format(d)+" Data From other server "+receivedData+System.lineSeparator());

                        if (receivedData.contains(",")) {
                            dataPart = receivedData.split(",");


                            if(dataPart[0].equals("book app")) {
                                output.append(df.format(d)+" Book Appointment Request "+System.lineSeparator());

                                String patId = dataPart[1];
                                String appId = dataPart[2];
                                String appType = dataPart[3];
                                msg = localBook(patId, appId, appType);

                            }else if(dataPart[0].equals("cancel app")) {
                                output.append(df.format(d)+" Cancel Appointment Request "+System.lineSeparator());

                                String patId = dataPart[1];
                                String appId = dataPart[2];
                                String appType = dataPart[3];
                                msg = localCancel(patId, appId, appType);

                            }else if(dataPart[0].equals("check")) {
                                output.append(df.format(d)+" Check Availability "+System.lineSeparator());

                                String appId = dataPart[1];
                                String appType = dataPart[2];
                                msg = localCheckAvailability(appId, appType);

                            }else if(dataPart[0].equals("type app")) {
                                output.append(df.format(d)+" List Availability Request "+System.lineSeparator());

                                String appType = dataPart[1];
                                msg = localListAvailability(appType);

                            }else if(dataPart[0].equals("remove")) {
                                output.append(df.format(d)+" List Availability Request "+System.lineSeparator());

                                String appType = dataPart[2];
                                String patientId = dataPart[1];
                                msg = remove(patientId, appType);

                            }else{
                                msg = "err";
                            }
                        }else {
                            msg = "err";
                        }




                        output.append(df.format(d)+" Sent Response back to server "+msg+System.lineSeparator());

                        InetAddress IPAddress = inPacket.getAddress();
                        int port = inPacket.getPort();
                        outData = msg.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(outData, outData.length, IPAddress, port);
                        serverSocket.send(sendPacket);
                        output.append(System.lineSeparator());
                        output.close();




                    } catch (Exception e) {
                        System.out.println("Error from QUE server UDP innerloop"+ e);
                    }
                }

            } catch (Exception e) {
                System.out.println("Error from QUE server UDP"+ e);
            }


        }



    }

}
