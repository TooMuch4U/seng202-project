package seng202.group10.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author Mitchell
 */
public class AirportRW extends RWStream {


    public AirportRW(String inFile) {
        super(inFile, "airport.csv");
    }

    public AirportRW() {
        super("airport.csv");
    }

    public ArrayList<Airport> readAirports() throws IOException, IncompatibleFileException {
        ArrayList<Airport> airports = new ArrayList<>();

        // Initialise file reader and string row variable
        BufferedReader csvReader = new BufferedReader(new FileReader(super.getInFilename()));

        // Parse each line
        CSVParser parser = CSVParser.parse(csvReader, CSVFormat.EXCEL);
        for (CSVRecord csvRecord : parser) {
            try {

                // Get corresponding values from the csv record
                String name = csvRecord.get(1);
                String city = csvRecord.get(2);
                String country = csvRecord.get(3);
                String iata = csvRecord.get(4);
                String icao = csvRecord.get(5);
                double latitude = Double.parseDouble(csvRecord.get(6));
                double longitude = Double.parseDouble(csvRecord.get(7));
                float altitude = Float.parseFloat(csvRecord.get(8));
                float timezone = Float.parseFloat(csvRecord.get(9));
                String dstType = csvRecord.get(10);
                String tzDatabase = csvRecord.get(11);

                // Create airline and add to model
                Airport airport = new Airport(name, city, country, iata, icao, latitude, longitude, altitude, timezone, dstType, tzDatabase);
                airports.add(airport);
            } catch (Exception e) {
                throw new IncompatibleFileException();
            }
        }

        // Close reader
        csvReader.close();
        return airports;
    }

    public void writeAirports(ArrayList<Airport> airports) {
        ArrayList<ArrayList<String>> airportStrings = new ArrayList<ArrayList<String>>();

        for (Airport airport: airports) {
            airportStrings.add(
                    new ArrayList<String>(Arrays.asList(
                            airport.getName(),
                            airport.getCity(),
                            airport.getCountry(),
                            airport.getIata(),
                            airport.getIcao(),
                            Double.toString(airport.getLatitude()),
                            Double.toString(airport.getLongitude()),
                            Float.toString(airport.getAltitude()),
                            Float.toString(airport.getTimezone()),
                            airport.getDstType(),
                            airport.getTzDatabase()
            )));
        }
        writeAll(airportStrings);
    }

    public ArrayList<Airport> readDatabaseAirports() {
        ResultSet results = databaseConnection.executeQuery("SELECT * FROM airports");

        ArrayList<Airport> output = new ArrayList<Airport>();

        try {
            while (results.next()) {
                output.add(new Airport(
                        results.getString("name"),
                        results.getString("city"),
                        results.getString("country"),
                        results.getString("iata"),
                        results.getString("icao"),
                        results.getDouble("latitude"),
                        results.getDouble("longitude"),
                        results.getFloat("altitude"),
                        results.getFloat("timezone"),
                        results.getString("dstType"),
                        results.getString("tzDatabase")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return output;
    }

    public void writeDatabaseAirports(ArrayList<Airport> airports) {
        databaseConnection.setAutoCommit(false);
        for (int i = 0; i < airports.size(); i++) {
            try {
                PreparedStatement pStatement = databaseConnection.getFormattedPreparedStatement(
                        "INSERT INTO airports (name, city, country, iata, icao, latitude, longitude, altitude, timezone, dstType, tzDatabase)",
                        11
                );
                Airport airport = airports.get(i);
                pStatement.setString(1, airport.getName());
                pStatement.setString(2, airport.getCity());
                pStatement.setString(3, airport.getCountry());
                pStatement.setString(4, airport.getIata());
                pStatement.setString(5, airport.getIcao());
                pStatement.setDouble(6, airport.getLatitude());
                pStatement.setDouble(7, airport.getLongitude());
                pStatement.setFloat(8, airport.getAltitude());
                pStatement.setFloat(9, airport.getTimezone());
                pStatement.setString(10, airport.getDstType());
                pStatement.setString(11, airport.getTzDatabase());

                pStatement.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        databaseConnection.commit();
        databaseConnection.setAutoCommit(true);
    }
}
