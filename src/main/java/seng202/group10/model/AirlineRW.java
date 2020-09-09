package seng202.group10.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author Mitchell
 */
public class AirlineRW extends RWStream {
    public AirlineRW(String inFile) {
        super(inFile, "airline.csv");
    }

    public AirlineRW() {
        super("airline.csv");
    }

    public ArrayList<Airline> readAirlines() throws IOException, IncompatibleFileException {

        ArrayList<Airline> airlineList = new ArrayList<>();

        // Initialise file reader and string row variable
        BufferedReader csvReader = new BufferedReader(new FileReader(super.getInFilename()));

        // Parse each line
        CSVParser parser = CSVParser.parse(csvReader, CSVFormat.EXCEL);
        for (CSVRecord csvRecord : parser) {
            try {

                // Get corresponding values from the csv record
                String name = csvRecord.get(1);
                String alias = csvRecord.get(2);
                String iata = csvRecord.get(3);
                String icao = csvRecord.get(4);
                String callsign = csvRecord.get(5);
                String country = csvRecord.get(6);

                // Create airline and add to model
                Airline airline = new Airline(name, alias, iata, icao, callsign, country);
                airlineList.add(airline);
            } catch(Exception e) {
                throw new IncompatibleFileException();
            }
        }

        // Close reader
        csvReader.close();
        return airlineList;
    }

    public void writeAirlines(ArrayList<Airline> airlines) {
        ArrayList<ArrayList<String>> airlineStrings = new ArrayList<ArrayList<String>>();

        for (Airline airline: airlines) {
            airlineStrings.add(
                    new ArrayList<String>(Arrays.asList(
                            airline.getName(),
                            airline.getAlias(),
                            airline.getIata(),
                            airline.getIcao(),
                            airline.getCallsign(),
                            airline.getCountry()
                    )));
        }
        writeAll(airlineStrings);
    }

    public ArrayList<Airline> readDatabaseAirlines() {
        ResultSet results = databaseConnection.executeQuery("SELECT * FROM airlines");

        ArrayList<Airline> output = new ArrayList<Airline>();

        try {
            while (results.next()) {
                output.add(new Airline(
                        results.getString("name"),
                        results.getString("alias"),
                        results.getString("iata"),
                        results.getString("icao"),
                        results.getString("callsign"),
                        results.getString("country")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return output;
    }

    public void writeDatabaseAirline(Airline airline) {
        String statement = String.format("INSERT INTO airlines" +
                "VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", airline.getName(), airline.getAlias(), airline.getIata(), airline.getIcao(), airline.getCallsign(), airline.getCountry());
    }
}