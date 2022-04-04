package DAMS.Replicas.Replica2.server.domain;

import java.io.Serializable;

public class Response implements Serializable {
    boolean success;
    String methodName;
    String methodDescription;
    String message;

    public Response() {
    }

    public Response(String methodName, String methodDescription, boolean success, String message) {
        this.methodName = methodName;
        this.methodDescription = methodDescription;
        this.success = success;
        this.message = message;
    }

    public String methodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String methodDescription() {
        return methodDescription;
    }

    public void setMethodDescription(String methodDescription) {
        this.methodDescription = methodDescription;
    }

    public boolean success() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String message() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
