package com.jayfella.devkit.controller.config;

import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.config.SceneConfig;
import com.jayfella.devkit.core.ColorConverter;
import com.jayfella.devkit.core.format.FloatTextFormatter;
import com.jayfella.devkit.core.format.IntegerTextFormatter;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Grid;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SceneConfigurationController extends ConfigurationSection implements Initializable  {

    // grid
    @FXML private TextField xGridSizeTextField;
    @FXML private TextField yGridSizeTextField;
    @FXML private TextField gridLineSpacingTextField;
    @FXML private ColorPicker gridColorPicker;

    public SceneConfigurationController() {
        super("Scene");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SceneConfig sceneConfig = DevKitConfig.getInstance().getSceneConfig();

        // grid
        xGridSizeTextField.setText("" + sceneConfig.getGridSize().x);
        yGridSizeTextField.setText("" + sceneConfig.getGridSize().y);
        gridLineSpacingTextField.setText("" + sceneConfig.getGridSize().z);
        gridColorPicker.setValue(ColorConverter.toColor(sceneConfig.getGridColor()));

        xGridSizeTextField.setTextFormatter(new IntegerTextFormatter());
        yGridSizeTextField.setTextFormatter(new IntegerTextFormatter());
        gridLineSpacingTextField.setTextFormatter(new FloatTextFormatter());

    }

    @Override
    public void applyToConfiguration() {

        SceneConfig sceneConfig = DevKitConfig.getInstance().getSceneConfig();

        // grid
        sceneConfig.setGridSize(new Vector3f(
                Float.parseFloat(xGridSizeTextField.getText()),
                Float.parseFloat(yGridSizeTextField.getText()),
                Float.parseFloat(gridLineSpacingTextField.getText())
        ));
        sceneConfig.setGridColor(ColorConverter.toColorRGBA(gridColorPicker.getValue()));

        // apply any changes that we can.
        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {

            Geometry gridGeometry = (Geometry) engineService.getRootNode().getChild("Debug Grid");

            if (gridGeometry != null) {
                Grid grid = new Grid((int) sceneConfig.getGridSize().x, (int) sceneConfig.getGridSize().y, sceneConfig.getGridSize().z);
                gridGeometry.setMesh(grid);
                gridGeometry.getMaterial().setColor("Color", sceneConfig.getGridColor());
            }

        });


    }

}
