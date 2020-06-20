package com.jayfella.devkit.controller;

import com.jayfella.devkit.config.DevKitConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AddModelWindowController implements Initializable {

    private final List<String> selectedModels = new ArrayList<>();

    @FXML private ListView<String> modelsListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<Path> modelFiles = null;

        try {
            modelFiles = Files.walk(new File(DevKitConfig.getInstance().getProjectConfig().getAssetRootDir()).toPath())
                    .filter(p -> p.toString().endsWith(".j3o"))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (modelFiles != null) {

            for (Path path : modelFiles) {

                String relativePath = path.toString().replace(DevKitConfig.getInstance().getProjectConfig().getAssetRootDir(), "");

                // remove any trailing slashes.
                if (relativePath.startsWith("/")) {
                    relativePath = relativePath.substring(1);
                }

                modelsListView.getItems().add(relativePath);
            }

        }

    }

    public List<String> getSelectedModels() {
        return selectedModels;
    }

    @FXML
    private void onAddModelsButtonAction(ActionEvent event) {

        selectedModels.addAll(modelsListView.getSelectionModel().getSelectedItems());

        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onCancelButtonAction(ActionEvent event) {

        selectedModels.clear();

        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

}
