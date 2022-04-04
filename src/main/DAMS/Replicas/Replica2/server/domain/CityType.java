package DAMS.Replicas.Replica2.server.domain;

public enum CityType {
    MTL("Montreal"),
    QUE("Quebec"),
    SHE("Sherbrooke");

    private String description;

    CityType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
