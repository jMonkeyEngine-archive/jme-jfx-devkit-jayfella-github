package com.jayfella.devkit.controller;

import com.jayfella.devkit.config.DevKitConfig;
import com.simsilica.jmec.Convert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ImportModelWindowController implements Initializable {

    @FXML private TextField sourceModelTextField;
    @FXML private TextField targetPathTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void onImportButtonAction(ActionEvent event) {

        if (sourceModelTextField.getText().trim().isBlank()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Model Selected");
            alert.setHeaderText("No Model Selected");
            alert.setContentText("You must select a model to import.");

        }

        Convert convert = new Convert();
        File sourceModel = new File(sourceModelTextField.getText());
        convert.setSourceRoot(sourceModel.getParentFile());
        convert.setTargetRoot(new File(DevKitConfig.getInstance().getProjectConfig().getAssetRootDir()));
        convert.setTargetAssetPath(targetPathTextField.getText());

        try {
            convert.convert(new File(sourceModelTextField.getText()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String resultFile = Paths.get(targetPathTextField.getText(), sourceModel.getName() + ".j3o").toString();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Import Model");
        alert.setHeaderText("Import Successful");
        alert.setContentText("Model imported successfully to: " + resultFile);

        alert.showAndWait();

    }

    @FXML
    private void onCancelButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

}
