package DAMS.Notification;

import java.io.Serializable;
import java.util.List;

public class Notification implements Serializable {

    String failureType;
    List<Integer> failedReplicas;

    public Notification(String failureType, List<Integer> failedReplicas) {
        this.failureType = failureType;
        this.failedReplicas = failedReplicas;
    }

    public String getFailureType() {
        return failureType;
    }

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    public List<Integer> getFailedReplicas() {
        return failedReplicas;
    }

    public void setFailedReplicas(List<Integer> failedReplicas) {
        this.failedReplicas = failedReplicas;
    }
}
