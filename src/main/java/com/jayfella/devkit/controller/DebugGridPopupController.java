package com.jayfella.devkit.controller;

import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.config.SceneConfig;
import com.jayfella.devkit.core.ColorConverter;
import com.jayfella.devkit.core.DebugGrid;
import com.jayfella.devkit.core.format.FloatTextFormatter;
import com.jayfella.devkit.core.format.IntegerTextFormatter;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jme3.math.Vector3f;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DebugGridPopupController implements Initializable {

    @FXML private CheckBox debugGridEnabledCheckBox;
    @FXML private TextField xGridSizeTextField;
    @FXML private TextField yGridSizeTextField;
    @FXML private TextField gridLineSpacingTextField;
    @FXML private ColorPicker gridColorPicker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SceneConfig sceneConfig = DevKitConfig.getInstance().getSceneConfig();

        // grid
        xGridSizeTextField.setText("" + sceneConfig.getGridSize().x);
        yGridSizeTextField.setText("" + sceneConfig.getGridSize().y);
        gridLineSpacingTextField.setText("" + sceneConfig.getGridSize().z);
        gridColorPicker.setValue(ColorConverter.toColor(sceneConfig.getGridColor()));

        debugGridEnabledCheckBox.setSelected(DebugGrid.isAttached());

        xGridSizeTextField.setTextFormatter(new IntegerTextFormatter());
        yGridSizeTextField.setTextFormatter(new IntegerTextFormatter());
        gridLineSpacingTextField.setTextFormatter(new FloatTextFormatter());

        createBindings();
    }

    private void createBindings() {

        SceneConfig sceneConfig = DevKitConfig.getInstance().getSceneConfig();

        xGridSizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            Vector3f gridSize = sceneConfig.getGridSize();
            sceneConfig.setGridSize(gridSize.setX(Float.parseFloat(newValue)));

            DevKitConfig.getInstance().save();
            ServiceManager.getService(JmeEngineService.class).enqueue(() -> DebugGrid.refreshMesh(true, false));
        });

        yGridSizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            Vector3f gridSize = sceneConfig.getGridSize();
            sceneConfig.setGridSize(gridSize.setY(Float.parseFloat(newValue)));

            DevKitConfig.getInstance().save();
            ServiceManager.getService(JmeEngineService.class).enqueue(() -> DebugGrid.refreshMesh(true, false));
        });

        gridLineSpacingTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            Vector3f gridSize = sceneConfig.getGridSize();
            sceneConfig.setGridSize(gridSize.setZ(Float.parseFloat(newValue)));

            DevKitConfig.getInstance().save();
            ServiceManager.getService(JmeEngineService.class).enqueue(() -> DebugGrid.refreshMesh(true, false));

        });

        gridColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {

            sceneConfig.setGridColor(ColorConverter.toColorRGBA(newValue));

            DevKitConfig.getInstance().save();
            ServiceManager.getService(JmeEngineService.class).enqueue(() -> DebugGrid.refreshMesh(false, true));

        });


    }

    @FXML
    private void onEnableDebugGridCheckBoxAction(ActionEvent event) {

        CheckBox checkBox = (CheckBox) event.getSource();
        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
        SceneConfig sceneConfig = DevKitConfig.getInstance().getSceneConfig();

        final boolean isSelected = checkBox.isSelected();

        if (isSelected) {
            engineService.enqueue(DebugGrid::create);
        }
        else {
            engineService.enqueue(DebugGrid::remove);
        }

        sceneConfig.setShowGrid(isSelected);
        DevKitConfig.getInstance().save();

    }

}
