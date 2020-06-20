package com.jayfella.devkit.controller;

import com.jayfella.devkit.core.ColorConverter;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jme3.light.*;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;

import java.net.URL;
import java.util.ResourceBundle;

public class DebugLightsWindowController implements Initializable {

    // All lights are created on, modified and removed on the JME thread.
    private AmbientLight ambientLight;
    private DirectionalLight directionalLight;
    private LightProbe lightProbe;

    @FXML private ColorPicker ambientColorPicker;
    @FXML private ColorPicker directionalColorPicker;

    @FXML private CheckBox ambientCheckBox;
    @FXML private CheckBox directionalCheckBox;
    @FXML private CheckBox probeCheckBox;
    @FXML private ChoiceBox<DemoProbe> probeChoiceBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        probeChoiceBox.getItems().addAll(DemoProbe.values());
        probeChoiceBox.getSelectionModel().select(0);

        createProbeChoiceBoxSelectionListener();
        querySceneForDebugLights();
    }

    private void querySceneForDebugLights() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {

            // read the lights from the JME thread.
            LightList lights = engineService.getRootNode().getLocalLightList();

            for (Light light : lights) {

                if (light instanceof AmbientLight) {

                    // set the ambientLight reference from the JME thread.
                    ambientLight = (AmbientLight) light;
                    final ColorRGBA colorRGBA = ambientLight.getColor();

                    // set the JavaFX value from the JavaFX thread.
                    Platform.runLater(() -> {
                        ambientCheckBox.setSelected(true);
                        ambientColorPicker.setValue(ColorConverter.toColor(colorRGBA));
                    } );
                }
                else if (light instanceof DirectionalLight) {

                    // set the ambientLight reference from the JME thread.
                    directionalLight = (DirectionalLight) light;
                    final ColorRGBA colorRGBA = directionalLight.getColor();

                    // set the JavaFX value from the JavaFX thread.
                    Platform.runLater(() -> {
                        directionalCheckBox.setSelected(true);
                        directionalColorPicker.setValue(ColorConverter.toColor(colorRGBA));
                    } );
                }
                else if (light instanceof LightProbe) {

                    // set the ambientLight reference from the JME thread.
                    lightProbe = (LightProbe) light;
                    final String name = lightProbe.getName();

                    if (name != null) {

                        // set the JavaFX value from the JavaFX thread.
                        Platform.runLater(() -> {
                            probeCheckBox.setSelected(true);
                            DemoProbe demoProbe = DemoProbe.fromResourcePath(name);
                            probeChoiceBox.getSelectionModel().select(demoProbe);
                        });

                    }

                }

            }

        });

    }

    private void createProbeChoiceBoxSelectionListener() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        probeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            final boolean isSelected = probeCheckBox.isSelected();

            // add/remove the probe on the JME thread.
            engineService.enqueue(() -> {

                if (lightProbe != null) {
                    engineService.getRootNode().removeLight(lightProbe);
                }

                if (isSelected) {
                    lightProbe = newValue.extractProbe();
                    engineService.getRootNode().addLight(lightProbe);
                }

            });

        });

    }

    @FXML
    private void onAmbientCheckBoxAction(ActionEvent event) {

        CheckBox checkBox = (CheckBox) event.getSource();

        final boolean isSelected = checkBox.isSelected();
        final ColorRGBA colorRGBA = ColorConverter.toColorRGBA(ambientColorPicker.getValue());

        final JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {

            if (isSelected) {

                if (ambientLight == null) {
                    ambientLight = new AmbientLight(colorRGBA);
                    engineService.getRootNode().addLight(ambientLight);
                }
                else {
                    ambientLight.setColor(colorRGBA);
                }
            }
            else {

                if (ambientLight != null) {
                    engineService.getRootNode().removeLight(ambientLight);
                    ambientLight = null;
                }
            }

        });

    }

    @FXML
    private void onDirectionalCheckBoxAction(ActionEvent event) {

        CheckBox checkBox = (CheckBox) event.getSource();

        final boolean isSelected = checkBox.isSelected();
        final ColorRGBA colorRGBA = ColorConverter.toColorRGBA(directionalColorPicker.getValue());

        final JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {

            if (isSelected) {

                if (directionalLight == null) {
                    directionalLight = new DirectionalLight(new Vector3f(-1, -1, -1).normalizeLocal(), colorRGBA);
                    engineService.getRootNode().addLight(directionalLight);
                }
                else {
                    directionalLight.setColor(colorRGBA);
                }
            }
            else {

                if (directionalLight != null) {
                    engineService.getRootNode().removeLight(directionalLight);
                    directionalLight = null;
                }
            }

        });

    }

    @FXML
    private void onProbeCheckBoxAction(ActionEvent event) {

        CheckBox checkBox = (CheckBox) event.getSource();

        final boolean isSelected = checkBox.isSelected();
        final JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {

            if (isSelected) {

                if (lightProbe != null) {
                    engineService.getRootNode().removeLight(lightProbe);
                }

                lightProbe = probeChoiceBox.getSelectionModel().getSelectedItem().extractProbe();
                engineService.getRootNode().addLight(lightProbe);

            }
            else {
                if (lightProbe != null) {
                    engineService.getRootNode().removeLight(lightProbe);
                    lightProbe = null;
                }
            }

        });

    }

    @FXML
    private void onAmbientColorPickerAction(ActionEvent event) {

        // Read the color value from the JavaFX thread.
        ColorPicker colorPicker = (ColorPicker) event.getSource();
        final ColorRGBA colorRGBA = ColorConverter.toColorRGBA(colorPicker.getValue());

        // Set the value in the JME thread.
        ServiceManager.getService(JmeEngineService.class).enqueue(() -> {
            if (ambientLight != null) {
                ambientLight.setColor(colorRGBA);
            }
        });

    }

    @FXML
    private void onDirectionalColorPickerAction(ActionEvent event) {

        // Read the color value from the JavaFX thread.
        ColorPicker colorPicker = (ColorPicker) event.getSource();
        final ColorRGBA colorRGBA = ColorConverter.toColorRGBA(colorPicker.getValue());

        // Set the value in the JME thread.
        ServiceManager.getService(JmeEngineService.class).enqueue(() -> {
            if (directionalLight != null) {
                directionalLight.setColor(colorRGBA);
            }
        });

    }

    private enum DemoProbe {

        Bathroom("Probes/bathroom.j3o"),
        City_Night_Lights("Probes/City_Night_Lights.j3o"),
        Corsica_Beach("Probes/corsica_beach.j3o"),
        Dresden_Station_Night("Probes/dresden_station_night.j3o"),
        Flower_Road("Probes/flower_road.j3o"),
        Glass_Passage("Probes/glass_passage.j3o"),
        Parking_Lot("Probes/Parking_Lot.j3o"),
        River_Road("Probes/River_Road.j3o"),
        Road_In_Tenerife_Mountain("Probes/road_in_tenerife_mountain.j3o"),
        Sky_Cloudy("Probes/Sky_Cloudy.j3o"),
        StoneWall("Probes/Stonewall.j3o"),
        Studio("Probes/studio.j3o");

        private final String resourcePath;

        DemoProbe(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        public String getResourcePath() {
            return resourcePath;
        }

        public LightProbe extractProbe() {

            Spatial probeHolder = ServiceManager.getService(JmeEngineService.class)
                    .getAssetManager()
                    .loadModel(getResourcePath());

            LightProbe lightProbe = (LightProbe) probeHolder.getLocalLightList().get(0);
            probeHolder.removeLight(lightProbe);

            lightProbe.getArea().setRadius(500);
            lightProbe.setName(resourcePath);

            return lightProbe;
        }

        public static DemoProbe fromResourcePath(String resourcePath) {

            for (DemoProbe demoProbe : values()) {
                if (demoProbe.getResourcePath().equals(resourcePath)) {
                    return demoProbe;
                }
            }

            return null;
        }

    }

}
