package DAMS.Replicas.Replica2.server.exception;

public class CustomException extends Exception {

    public CustomException(CustomErrorType errorType) {
        super(errorType.getDescription());
    }

    private static String getDescription(String description) {
        return description;
    }

}
