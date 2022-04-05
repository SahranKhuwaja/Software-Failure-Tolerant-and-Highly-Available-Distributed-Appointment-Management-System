package DAMS.Response;

import DAMS.ResponseWrapper.ResponseWrapper;

import java.io.Serializable;

public class Response implements Serializable {
    int replica;
    boolean success;
    String methodName;
    String methodDescription;
    String message;
    String[] messages;
    ResponseWrapper responseWrapper;

    public Response() {
    }

    public Response(String methodName, String methodDescription, boolean success, String message) {
        this.methodName = methodName;
        this.methodDescription = methodDescription;
        this.success = success;
        this.message = message;
    }

    public Response(String methodName, String methodDescription, boolean success, String[] messages) {
        this.methodName = methodName;
        this.methodDescription = methodDescription;
        this.success = success;
        this.messages = messages;
    }

    public Response(String methodName, String methodDescription, boolean success, ResponseWrapper responseWrapper) {
        this.methodName = methodName;
        this.methodDescription = methodDescription;
        this.success = success;
        this.responseWrapper = responseWrapper;
    }

    public int getReplica() {
        return replica;
    }

    public void setReplica(int replica) {
        this.replica = replica;
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

    public String[] getMessages() { return messages; }

    public void setMessage(String[] messages) {
        this.messages = messages;
    }

    public ResponseWrapper getResponseWrapper() {
        return responseWrapper;
    }

    public void setResponseWrapper(ResponseWrapper responseWrapper) {
        this.responseWrapper = responseWrapper;
    }
}
