package com.hotelapp;

import com.hotelapp.utils.DBInit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * MainApp - robust startup: logs exceptions and shows a user-friendly alert if startup fails.
 */
public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize DB (safe)
            DBInit.initDatabase();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());

            primaryStage.setTitle("Hotel Reservation System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            // Force front on macOS
            primaryStage.toFront();
            primaryStage.requestFocus();

        } catch (Exception ex) {
            // Print to console
            ex.printStackTrace();

            // Show error to user on JavaFX thread if possible
            final StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            final String trace = sw.toString();

            try {
                Platform.runLater(() -> {
                    Alert a = new Alert(AlertType.ERROR);
                    a.setTitle("Startup Error");
                    a.setHeaderText("Application failed to start");
                    a.setContentText(ex.getMessage() == null ? "See details" : ex.getMessage());
                    // show expandable stack trace
                    javafx.scene.control.TextArea ta = new javafx.scene.control.TextArea(trace);
                    ta.setEditable(false);
                    ta.setWrapText(true);
                    ta.setMaxWidth(Double.MAX_VALUE);
                    ta.setMaxHeight(Double.MAX_VALUE);
                    javafx.scene.layout.GridPane gp = new javafx.scene.layout.GridPane();
                    gp.setMaxWidth(Double.MAX_VALUE);
                    gp.add(ta, 0, 0);
                    a.getDialogPane().setExpandableContent(gp);
                    a.showAndWait();
                    // exit after showing
                    Platform.exit();
                });
            } catch (Exception ignored) {
                // If even Platform.runLater fails, print and exit
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
