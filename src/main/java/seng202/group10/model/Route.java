package seng202.group10.model;

import java.util.ArrayList;

public class Route {
    private String airlineCode;
    private int airlineId;
    private String sourceAirportCode;
    private Airport sourceAirport;
    private String destinationAirportCode;
    private Airport destinationAirport;
    private int stops;
    private ArrayList<String> equipment;

    public Route(String airlineCode, int airlineId, String sourceAirportCode, Airport sourceAirport, String destinationAirportCode, Airport destinationAirport, int stops, ArrayList<String> equipment) {
        this.airlineCode = airlineCode;
        this.airlineId = airlineId;
        this.sourceAirportCode = sourceAirportCode;
        this.sourceAirport = sourceAirport;
        this.destinationAirportCode = destinationAirportCode;
        this.destinationAirport = destinationAirport;
        this.stops = stops;
        this.equipment = equipment;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public String getSourceAirportCode() {
        return sourceAirportCode;
    }

    public Airport getSourceAirport() {
        return sourceAirport;
    }

    public String getDestinationAirportCode() {
        return destinationAirportCode;
    }

    public Airport getDestinationAirport() {
        return destinationAirport;
    }

    public int getStops() {
        return stops;
    }

    public ArrayList<String> getEquipment() {
        return equipment;
    }
}