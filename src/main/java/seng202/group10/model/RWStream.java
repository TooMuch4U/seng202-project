package seng202.group10.model;
import javafx.scene.control.Alert;

import java.io.*;

import java.util.ArrayList;
import java.util.Scanner;

import java.util.Arrays;

import com.google.common.base.Joiner;


/**
 * Super class for file readers
 * Reads in files, writes to files
 * Connects to database
 * @author Mitchell Freeman
 * @author Niko Tainui
 * @author Johnny Howe
 */
public class RWStream {
    private Scanner fileReader;
    private FileWriter fileWriter;
    private String inFilename;
    private String outFilename;
    public DatabaseConnection databaseConnection;
    private boolean makeFile;

    /**
     * Constructor for RWStream, sets in and out file to inputted filename.
     * Creates the out file, gets the database instance
     * @param filename - file to import
     * @param outFilename - out file
     * @param createFile - weather to create the outfile or not
     */
    public RWStream(String filename, String outFilename, Boolean createFile) {
        this.inFilename = filename;
        this.outFilename = outFilename;
        this.makeFile = createFile;
        if (createFile) {
            makeFile();
        }
        getDatabase();
    }

    /**
     * Constructor for RWStream, sets in and out file to inputted filename.
     * Creates the out file, gets the database instance
     * @param filename - file to import
     */
    public RWStream(String filename) {
        this(filename, filename, true);
    }

    /**
     * Constructor for RWStream, sets in and out to separate files.
     * Creates the out file, gets the database instance.
     * @param inFilename in file
     * @param outFilename out file
     */
    public RWStream(String inFilename, String outFilename) {
        this(inFilename, outFilename, true);
    }

    /**
     * Closes the database connection
     */
    public void closeDb() {
        databaseConnection.disconnect();
    }

    /**
     * Creates file at outFilename
     */
    private void makeFile() {
        try {
            File file = new File(outFilename);
            file.createNewFile();
        } catch(IOException error) {
            displayError("Unable to make file");
        }
    }

    /**
     * Sets the attribute databaseConnection to an instance of the database
     */
    private void getDatabase() {
        databaseConnection = DatabaseConnection.getInstance();
    }

    /**
     * Contents of file at inFilename
     * Each line is split by commas and put into an arraylist
     * Each line arraylist is then put into another arraylist which is returned
     * @return thing explained before
     */
    public ArrayList<ArrayList<String>> read() {
        try {
            fileReader = new Scanner(new File(inFilename));
        } catch (FileNotFoundException error) {
            displayError("File not found");
        }
        ArrayList<ArrayList<String>> lines = new ArrayList<>();
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            ArrayList<String> lineList = new ArrayList<>(Arrays.asList(line.split("[, ]+")));
            lines.add(lineList);
        }
        fileReader.close();
        return lines;
    }

    public String getInFilename() {
        return inFilename;
    }

    /**
     * Write a single line to the file outFilename (and newline char).
     * line is all values in data separated by commas.
     * @param data list of data to write on the line
     */
    public void writeSingle(ArrayList<String> data) {
        try {
            fileWriter = new FileWriter(outFilename);
            fileWriter.write(getDataString(data));
            fileWriter.close();
        } catch (IOException error) {
            displayError("Unable to write to file");
        }
    }

    /**
     * Write the data from dataArr to a file
     * Assumes file exists and is writable
     * @param dataArr list of lists of strings to write
     */
    public void writeLines(ArrayList<ArrayList<String>> dataArr) {
        try {
            fileWriter = new FileWriter(outFilename);
            for (ArrayList<String> data : dataArr) {
                fileWriter.write(getDataString(data));
            }
            fileWriter.close();
        } catch (IOException error) {
            displayError("Unable to write to file");
        }
    }

    /**
     *
     * @param data list of data to write on the line
     * @return string representation of data
     */
    public String getDataString(ArrayList<String> data) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < data.size() - 1; i++) {
            builder.append(data.get(i));
            builder.append(',');
        }
        builder.append(data.get(data.size() - 1));
        builder.append('\n');
        return builder.toString();
    }

    /**
     * Writes all the lines specified in data to the file at outFilename
     * each entry of data is another ArrayList of strings, that list is added to the file according
     *  to writeSingle.
     * @param data list of line lists
     */
    public void writeAll(ArrayList<ArrayList<String>> data) {
        File file = new File(outFilename);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch(IOException error) {
            error.printStackTrace();
        }
        writeLines(data);
    }

    public void displayError(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(message);
        errorAlert.showAndWait();
    }

    public void setOutFileName(String filename) {
        outFilename = filename;
    }
}
