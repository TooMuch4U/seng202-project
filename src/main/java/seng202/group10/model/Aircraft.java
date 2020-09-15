package seng202.group10.model;

/**
 * @Author Tom Rizzi
 * @Author Niko Tainui
 */
public class Aircraft {
    // means travel agent code
    private String iata;
    // name of plane
    private String name;
    // icao stands for aircraft type code
    private String icao;
    // range of aircraft
    private double range;

    public Aircraft(String iata, String name, String icao, double range) {
        this.iata = iata;
        this.name = name;
        this.icao = icao;
        this.range = range;
    }

    public String getIata() {
        return iata;
    }

    public String getName() {
        return name;
    }

    public String getIcao() {
        return icao;
    }

    public double getRange() {
        return range;
    }
}
