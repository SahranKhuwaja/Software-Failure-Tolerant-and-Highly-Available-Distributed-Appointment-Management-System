package DAMS.Replicas.Replica2.server.util;

import DAMS.Replicas.Replica2.server.domain.CityType;

import static DAMS.Replicas.Replica2.server.config.DamsConfig.*;

public final class ServerUtil {

    //Extract service name by ID (either appointmentId / patientId)
    public static Integer getPortById(String id) throws Exception {
        switch (CityType.valueOf(id.substring(0, 3))) {
            case MTL:
                return Integer.parseInt(ConfigUtil.getPropValue(MTL_UDP_SERVER_PORT));
            case QUE:
                return Integer.parseInt(ConfigUtil.getPropValue(QUE_UDP_SERVER_PORT));
            case SHE:
                return Integer.parseInt(ConfigUtil.getPropValue(SHE_UDP_SERVER_PORT));
            default:
                throw new Exception("Unable to retrieve valid port number by ID :: " + id);
        }
    }

    public static Integer getPortByCityType(CityType cityType) throws Exception {
        switch (cityType) {
            case MTL:
                return Integer.parseInt(ConfigUtil.getPropValue(MTL_UDP_SERVER_PORT));
            case QUE:
                return Integer.parseInt(ConfigUtil.getPropValue(QUE_UDP_SERVER_PORT));
            case SHE:
                return Integer.parseInt(ConfigUtil.getPropValue(SHE_UDP_SERVER_PORT));
            default:
                throw new Exception("Unable to retrieve valid port number by city type :: " + cityType);
        }
    }

}
