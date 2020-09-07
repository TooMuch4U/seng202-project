package seng202.group10.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import seng202.group10.controller.AirportTabController;
import seng202.group10.controller.ControllerFacade;
import seng202.group10.controller.ViewController;
import java.io.IOException;
import java.net.URL;


public class GUIApp extends Application {

    private Stage stage;
    private ControllerFacade controllerFacade;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.controllerFacade = new ControllerFacade();

        // Get the main gui controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = fxmlLoader.load();
        ViewController controller = fxmlLoader.getController();
        controller.setControllerFacade(this.controllerFacade);

        // Set the airport tab controller in the controller facade.
        fxmlLoader = new FXMLLoader(getClass().getResource("airportTab.fxml"));
        controller.controllerFacade.setAirportTabController(fxmlLoader.getController());

        primaryStage.setTitle("Recreational Flight Planner");
        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setScene(scene);
        primaryStage.show();

        WebView mapView = (WebView) scene.lookup("#mapWebView");
        WebEngine mapEngine = mapView.getEngine();

        URL url = this.getClass().getResource("GoogleMaps/index.html");
        mapEngine.load(url.toString());
    }

    public static void main(String[] args) {
        launch(args);

    }
}
