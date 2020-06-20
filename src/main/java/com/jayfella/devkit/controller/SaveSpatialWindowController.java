package com.jayfella.devkit.controller;

import com.jayfella.devkit.config.DevKitConfig;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Spatial;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

public class SaveSpatialWindowController implements Initializable {

    @FXML private TextField directoryTextField;
    @FXML private TextField nameTextField;

    private Spatial selectedSpatial;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setSelectedSpatial(Spatial selectedSpatial) {
        this.selectedSpatial = selectedSpatial;
    }

    @FXML
    private void onBrowseButtonAction(ActionEvent event) {

    }

    @FXML
    private void onSaveButtonAction(ActionEvent event) {

        if (nameTextField.getText().strip().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Name Specified");
            alert.setHeaderText("No Name Specified.");
            alert.setContentText("You must specify a name.");
            alert.showAndWait();

        }
        else {

            String dir = directoryTextField.getText().strip();
            String name = nameTextField.getText().strip();

            if (!name.endsWith(".j3o")) {
                name += ".j3o";
            }

            File file = Paths.get(DevKitConfig.getInstance().getProjectConfig().getAssetRootDir(), dir, name).toFile();

            if (file.exists()) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "This file already exists. Overwrite?",
                        ButtonType.YES, ButtonType.NO);

                alert.setTitle("Overwrite File");
                alert.setHeaderText("File already exists.");

                Optional<ButtonType> response = alert.showAndWait();

                // if we didn't explicitly get the YES response, exit now.
                if (response.isEmpty() || response.get() != ButtonType.YES) {
                    return;
                }

            }

            try {
                BinaryExporter.getInstance().save(selectedSpatial, file);
            } catch (IOException e) {

                String spatialName = selectedSpatial.getName() == null ? "No Name" : selectedSpatial.getName();

                String errorString =
                        "An error occurred whilst attempting to save the spatial:"
                        + System.lineSeparator()
                        + spatialName
                        + System.lineSeparator()
                        + "to file"
                        + System.lineSeparator()
                        + file.toString()
                        + System.lineSeparator()
                        + e.getMessage();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unable to Save");
                alert.setHeaderText("Unable to save file.");
                alert.setContentText(errorString);

                alert.showAndWait();
            }

            String responseString = "Saved successfully to:"
                    + System.lineSeparator()
                    + file.toString().replace(DevKitConfig.getInstance().getProjectConfig().getAssetRootDir(), "");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File Saved");
            alert.setHeaderText("Save successful.");
            alert.setContentText(responseString);

            alert.showAndWait();

            // close the window.
            Button button = (Button) event.getSource();
            Stage stage = (Stage) button.getScene().getWindow();
            stage.close();

        }

    }

    @FXML
    private void onCancelButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

}
