package DAMS.Address;

public class Address {

    String HOST_IP;
    int PORT;

    public Address(String HOST_IP, int PORT) {
        this.HOST_IP = HOST_IP;
        this.PORT = PORT;
    }

    public String getHOST_IP() {
        return HOST_IP;
    }

    public void setHOST_IP(String HOST_IP) {
        this.HOST_IP = HOST_IP;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }
}
