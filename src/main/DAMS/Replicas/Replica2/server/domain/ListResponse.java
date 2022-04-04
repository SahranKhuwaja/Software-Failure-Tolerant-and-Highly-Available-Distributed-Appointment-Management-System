package DAMS.Replicas.Replica2.server.domain;

import java.util.HashSet;
import java.util.Set;

public class ListResponse<T> extends Response {

    private Set<T> dataSet;

    public ListResponse() {
    }

    public ListResponse(String methodName, String methodDescription, boolean success, Set<T> dataSet) {
        this.methodName = methodName;
        this.methodDescription = methodDescription;
        this.success = success;
        this.dataSet = dataSet;
    }

    public ListResponse(String methodName, String methodDescription, boolean isSuccess, String message) {
        super(methodName, methodDescription, isSuccess, message);
    }

    public Set<T> dataSet() {
        return dataSet;
    }

    public void setDataSet(HashSet<T> dataSet) {
        this.dataSet = dataSet;
    }

}
