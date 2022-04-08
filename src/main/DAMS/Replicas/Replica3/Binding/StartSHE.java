package DAMS.Replicas.Replica3.Binding;
import DAMS.Replicas.Replica3.Server.SherbrookeImpl;

import java.net.DatagramSocket;
import java.net.SocketException;

public class StartSHE {
    SherbrookeImpl SHE = new SherbrookeImpl();

    public static void main(String[] args){
        String serverName = "Sherbrooke (SHE)";
        int port  = 6823;
        SherbrookeImpl SHE = new SherbrookeImpl();
        try {
            DatagramSocket datagramSocket = null;
            try {
                datagramSocket = new DatagramSocket(port);
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            }

            new SHEReplyToFE(datagramSocket, serverName, port, SHE);



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

}
