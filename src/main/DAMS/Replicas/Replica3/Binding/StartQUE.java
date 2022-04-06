package DAMS.Replicas.Replica3.Binding;

import DAMS.Replicas.Replica3.Server.QuebecImpl;

import java.net.DatagramSocket;
import java.net.SocketException;

public class StartQUE {

    public static void main(String[] args){
        String serverName = "Quebec (QUE)";
        int port  = 6482;
        QuebecImpl QUE = new QuebecImpl();
        try {
            DatagramSocket datagramSocket = null;
            try {
                datagramSocket = new DatagramSocket(port);
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            }

            new QUEReplyToFE(datagramSocket, serverName, port, QUE);



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
