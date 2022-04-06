package DAMS.Replicas.Replica3.Binding;


import DAMS.Replicas.Replica3.Server.MontrealImpl;

import java.net.DatagramSocket;
import java.net.SocketException;

public class StartMTL {


    public static void main(String[] args){
        String serverName = "Montreal (MTL)";
        int port  = 6481;
        MontrealImpl MTL = new MontrealImpl();
        try {
            DatagramSocket datagramSocket = null;
            try {
                datagramSocket = new DatagramSocket(port);
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            }

            new MTLReplyToFE(datagramSocket, serverName, port, MTL);



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

}
