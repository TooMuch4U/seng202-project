package seng202.group10.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import seng202.group10.controller.filters.AirlineFilters;
import seng202.group10.controller.filters.AirportFilters;
import seng202.group10.model.Airline;
import seng202.group10.model.Airport;
import seng202.group10.model.IncompatibleFileException;

import java.io.IOException;
import java.util.ArrayList;

public class AirportTabController {

    @FXML
    public TableView airportTable;
    @FXML
    private TableColumn cityCol;
    @FXML
    private TableColumn latCol;
    @FXML
    private TableColumn lngCol;
    @FXML
    private TableColumn altCol;
    @FXML
    private TableColumn tzCol;
    @FXML
    private TableColumn distCol;
    @FXML
    private TableColumn tzdbCol;
    @FXML
    private TableColumn nameCol;
    @FXML
    private TableColumn iataCol;
    @FXML
    private TableColumn icaoCol;
    @FXML
    private TableColumn countryCol;
    @FXML
    private ViewController mainController;
    @FXML
    private TextField nameFilterField;
    @FXML
    private TextField cityFilterField;
    @FXML
    private TextField countryFilterField;



    public void updateTable(ArrayList<Airport> data) {
        airportTable.setEditable(true);
        nameCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("name"));
        cityCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("city"));
        latCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("latitude"));
        lngCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("longitude"));
        altCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("altitude"));
        tzCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("timezone"));
        distCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("dstType"));
        tzdbCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("tzDatabase"));
        iataCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("iata"));
        icaoCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("icao"));
        countryCol.setCellValueFactory(new PropertyValueFactory<Airline, String>("country"));
        airportTable.setItems(FXCollections.observableList(data));
    }

    public void injectMainController(ViewController controller) {
        this.mainController = controller;
    }

    public void importAirports() {
        // Pick file
        String filepath = mainController.showFileExplorer();

        // Check if a file was chosen
        if (filepath != null) {

            // Import file
            AirportController controller = mainController.controllerFacade.getAirportController();
            try {
                controller.importAirports(filepath);
            } catch (IncompatibleFileException | IOException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("File Not Compatible");
                errorAlert.showAndWait();
            }

            // Update table
            ArrayList<Airport> data = controller.getAirports();
            updateTable(data);
        }
    }

    public void applyAirportFilters() {
        AirportFilters filter = new AirportFilters();
        AirportController airports = new AirportController();
        ArrayList<Airport> data = new ArrayList<Airport>();
        data = filter.filterByAll(airports.getAirports(), nameFilterField.getText(), cityFilterField.getText(), countryFilterField.getText());
        updateTable(data);
    }
}
