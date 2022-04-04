package DAMS.Replicas.Replica2.server.domain;

import java.util.HashMap;

public class MapResponse <T> extends Response {

    HashMap<String, T> data;

    public MapResponse() {}

    public MapResponse(String methodName, String methodDescription, boolean success, HashMap<String, T> data) {
        this.methodName = methodName;
        this.methodDescription = methodDescription;
        this.success = success;
        this.data = data;
    }

    public MapResponse(String methodName, String methodDescription, boolean isSuccess, String message) {
        super(methodName, methodDescription, isSuccess, message);
    }

    public HashMap<String, T> data() {
        return data;
    }

    public void setData(HashMap<String, T> data) {
        this.data = data;
    }

}
