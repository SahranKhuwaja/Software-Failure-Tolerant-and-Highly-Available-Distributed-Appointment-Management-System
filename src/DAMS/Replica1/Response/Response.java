package DAMS.Replica1.Response;

import java.io.Serializable;

public class Response implements Serializable {
    String description;
    String message;
    boolean success;

    public Response(String description, String message, boolean success) {
        this.description = description;
        this.message = message;
        this.success = success;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
