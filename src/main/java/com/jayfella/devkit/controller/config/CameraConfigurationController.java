package com.jayfella.devkit.controller.config;

import com.jayfella.devkit.config.CameraConfig;
import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.core.ColorConverter;
import com.jayfella.devkit.core.format.FloatTextFormatter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CameraConfigurationController extends ConfigurationSection implements Initializable {

    @FXML private ColorPicker viewPortColorPicker;
    @FXML private TextField fovTextField;
    @FXML private TextField frustumNearTextField;
    @FXML private TextField frustumFarTextField;

    public CameraConfigurationController() {
        super("Camera");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        CameraConfig cameraConfig = DevKitConfig.getInstance().getCameraConfig();

        viewPortColorPicker.setValue(ColorConverter.toColor(cameraConfig.getViewportColor()));
        fovTextField.setText("" + cameraConfig.getFieldOfView());
        frustumNearTextField.setText("" + cameraConfig.getFrustumNear());
        frustumFarTextField.setText("" + cameraConfig.getFrustumFar());

        fovTextField.setTextFormatter(new FloatTextFormatter());
        frustumNearTextField.setTextFormatter(new FloatTextFormatter());
        frustumFarTextField.setTextFormatter(new FloatTextFormatter());
    }

    @Override
    public String getSectionName() {
        return "Camera";
    }

    @Override
    public void applyToConfiguration() {

        CameraConfig cameraConfig = DevKitConfig.getInstance().getCameraConfig();

        cameraConfig.setViewportColor(ColorConverter.toColorRGBA(viewPortColorPicker.getValue()));
        cameraConfig.setFieldOfView(Float.parseFloat(fovTextField.getText()));
        cameraConfig.setFrustumNear(Float.parseFloat(frustumNearTextField.getText()));
        cameraConfig.setFrustumFar(Float.parseFloat(frustumFarTextField.getText()));
    }



}
