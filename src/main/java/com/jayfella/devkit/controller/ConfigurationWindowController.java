package com.jayfella.devkit.controller;

import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.controller.config.ConfigurationSection;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigurationWindowController implements Initializable {

    private static final String[] sections = {
            "CameraConfiguration",
            // "SceneConfiguration",
    };

    @FXML private ListView<ConfigurationSection> configAreasListView;
    @FXML private AnchorPane areaAnchorPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (String section : sections) {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/Config/" + section + ".fxml"));

            Parent root = null;

            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (root != null) {
                ConfigurationSection configurationSection = fxmlLoader.getController();
                configurationSection.setRoot(root);
                configAreasListView.getItems().add(configurationSection);
            }

        }

        configAreasListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            areaAnchorPane.getChildren().clear();

            // set all anchors except bottom so it doesn't stretch.
            AnchorPane.setTopAnchor(newValue.getRoot(), 0d);
            AnchorPane.setLeftAnchor(newValue.getRoot(), 0d);
            AnchorPane.setRightAnchor(newValue.getRoot(), 0d);

            areaAnchorPane.getChildren().add(newValue.getRoot());

        });

        // select the first one in the list.
        configAreasListView.getSelectionModel().select(0);

    }

    @FXML
    private void onApplyChangesButtonAction(ActionEvent event) {

        for (ConfigurationSection section : configAreasListView.getItems()) {
            section.applyToConfiguration();
        }

        DevKitConfig devKitConfig = DevKitConfig.getInstance();
        devKitConfig.save();

        // try to apply any setting that we can.
        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.applyCameraFrustumSizes();
        engineService.getViewPort().setBackgroundColor(devKitConfig.getCameraConfig().getViewportColor());

    }

    @FXML
    private void onCloseButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

}

