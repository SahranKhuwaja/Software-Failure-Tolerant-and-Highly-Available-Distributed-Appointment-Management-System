package DAMS.Replicas.Replica2.server.util;

import DAMS.Replicas.Replica2.server.domain.CityType;
import DAMS.Replicas.Replica2.server.domain.LogLevel;
import DAMS.Replicas.Replica2.server.domain.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static DAMS.Replicas.Replica2.server.config.DamsConfig.LOG_FOLDER;

public final class LoggingUtil {

    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final String filePathString = "%s/%s.log";

    private LoggingUtil() {
        throw new IllegalStateException("Utility class");
    }

    //For clients
    public static void log(User user, String methodDescription, LogLevel logLevel, String message) {
        try {
            String filePath = String.format(filePathString, ConfigUtil.getPropValue(LOG_FOLDER), getClientFileName(user));
            log(filePath, methodDescription, logLevel, message, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void log(User user, String methodDescription, LogLevel logLevel, String message, Exception exception) {
        try {
            String filePath = String.format(filePathString, ConfigUtil.getPropValue(LOG_FOLDER), getClientFileName(user));
            log(filePath, methodDescription, logLevel, message, exception);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //For servers
    public static void log(CityType cityType, String methodDescription, LogLevel logLevel, String message) {
        try {
            String filePath = String.format(filePathString, ConfigUtil.getPropValue(LOG_FOLDER), getServerFileName(cityType));
            log(filePath, methodDescription, logLevel, message, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void log(CityType cityType, String methodDescription, LogLevel logLevel, String message, Exception exception) {
        try {
            String filePath = String.format(filePathString, ConfigUtil.getPropValue(LOG_FOLDER), getServerFileName(cityType));
            log(filePath, methodDescription, logLevel, message, exception);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getClientFileName(User user) {
        return "User_" + user.id();
    }

    private static String getServerFileName(CityType cityType) {
        return "Server_" + cityType;
    }

    private static void log(String filePath, String methodDescription, LogLevel logLevel, String message, Exception exception) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             BufferedWriter writer = new BufferedWriter(fileWriter)
        ) {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            String formattedMessage = String.format("%s\t%s --- %s : %s", LocalDateTime.now().format(formatter), logLevel, methodDescription, message);
            writer.append(formattedMessage);

            //Print stacktrace if any
            if (logLevel.equals(LogLevel.ERROR) && !Objects.isNull(exception)) {
                writer.newLine();
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                String stackTrace = sw.toString(); // stack trace as a string
                writer.append(stackTrace);
                sw.close();
            }
            writer.newLine();
        }
    }

}
