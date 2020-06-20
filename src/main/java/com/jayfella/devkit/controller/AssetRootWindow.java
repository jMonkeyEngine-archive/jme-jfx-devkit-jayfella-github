package com.jayfella.devkit.controller;

import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jme3.asset.plugins.FileLocator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class AssetRootWindow implements Initializable {

    private static final Logger log = Logger.getLogger(AssetRootWindow.class.getName());

    @FXML private TextField textField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void onOkButtonAction(ActionEvent event) {

        if (textField.getText().strip().isBlank() || !new File(textField.getText()).exists()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Asset Root");
            alert.setHeaderText("Invalid Asset Root");
            alert.setContentText("Please specify a valid directory.");

            alert.showAndWait();
        }
        else {

            JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

            // unregister any existing locator.
            if (DevKitConfig.getInstance().getProjectConfig().getAssetRootDir() != null) {
                engineService.getAssetManager().unregisterLocator(DevKitConfig.getInstance().getProjectConfig().getAssetRootDir(), FileLocator.class);
            }

            DevKitConfig.getInstance().getProjectConfig().setAssetRootDir(textField.getText().strip());
            DevKitConfig.getInstance().save();

            // register the new locator
            engineService.getAssetManager().registerLocator(DevKitConfig.getInstance().getProjectConfig().getAssetRootDir(), FileLocator.class);

            // close the window
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

    @FXML
    private void onBrowseButtonAction(ActionEvent event) {

        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Asset Root Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File chosenDir = directoryChooser.showDialog(stage);

        if (chosenDir != null) {
            textField.setText(chosenDir.toString());
        }

    }

}
