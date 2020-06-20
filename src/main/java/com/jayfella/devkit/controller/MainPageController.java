package com.jayfella.devkit.controller;

import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.config.SceneConfig;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jayfella.jfx.embedded.jfx.EditorFxImageView;
import com.jme3.app.StatsAppState;
import com.jme3.material.Material;
import com.jme3.material.Materials;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainPageController implements Initializable {

    private static final Logger log = Logger.getLogger(MainPageController.class.getName());

    private Stage mainStage;

    @FXML private AnchorPane anchorPaneLeft; // Scene TreeView
    @FXML private AnchorPane anchorPaneMiddle; // Scene
    @FXML private AnchorPane anchorPaneRight; // Properties

    @FXML private CheckMenuItem showGridCheckMenuItem;
    @FXML private CheckMenuItem showStatisticsCheckMenuItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // load the scene tree window
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/SceneTreeWindow.fxml"));

        Parent root = null;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to load Scene Tree Window FXML", e);
        }

        if (root != null) {

            AnchorPane.setLeftAnchor(root, 0d);
            AnchorPane.setRightAnchor(root, 0d);
            AnchorPane.setTopAnchor(root, 0d);
            AnchorPane.setBottomAnchor(root, 0d);
            anchorPaneLeft.getChildren().add(root);

        }

    }

    public void start() {
        mainStage.setTitle("JmonkeyEngine SDK");
        createView();
        applyConfiguration();
    }

    private void createView() {

        // the imageview that will display the JME Scene
        EditorFxImageView imageView = ServiceManager.getService(JmeEngineService.class).getImageView();


        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(imageView);

        AnchorPane.setTopAnchor(stackPane, 0d);
        AnchorPane.setBottomAnchor(stackPane, 0d);
        AnchorPane.setLeftAnchor(stackPane, 0d);
        AnchorPane.setRightAnchor(stackPane, 0d);

        anchorPaneMiddle.getChildren().add(stackPane);

        stackPane.setFocusTraversable(true);
        anchorPaneMiddle.setFocusTraversable(true);

    }

    private void applyConfiguration() {

        DevKitConfig config = DevKitConfig.getInstance();
        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        if (config.getSceneConfig().isShowGrid()) {
            Geometry gridGeometry = createGridGeometry();
            engineService.enqueue(() -> engineService.getRootNode().attachChild(gridGeometry) );
            showGridCheckMenuItem.setSelected(true);
        }

    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    private void onShowStatsCheckMenuItem(ActionEvent event) {
        CheckMenuItem checkMenuItem = (CheckMenuItem) event.getSource();

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        if (checkMenuItem.isSelected()) {

            engineService.enqueue(() -> {

                StatsAppState statsAppState = engineService.getStateManager().getState(StatsAppState.class);

                if (statsAppState == null) {
                    engineService.getStateManager().attach(new StatsAppState());
                }

            });
        } else {

            StatsAppState statsAppState = engineService.getStateManager().getState(StatsAppState.class);

            if (statsAppState != null) {
                engineService.getStateManager().detach(statsAppState);
            }

        }

    }

    private Stage debugLights;

    @FXML
    private void onShowDebugLightCheckMenuItem(ActionEvent event) {

        CheckMenuItem checkMenuItem = (CheckMenuItem) event.getSource();

        if (checkMenuItem.isSelected()) {

            if (debugLights == null) {

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/DebugLightsWindow.fxml"));

                Parent root = null;

                try {
                    root = fxmlLoader.load();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (root != null) {

                    debugLights = new Stage();
                    debugLights.setTitle("Debug Lights");
                    debugLights.setScene(new Scene(root));
                    debugLights.sizeToScene();
                    debugLights.setResizable(false);
                    debugLights.initOwner(mainStage);

                    debugLights.setOnHiding(e -> {
                        checkMenuItem.setSelected(false);
                        debugLights = null;
                    });

                    debugLights.show();
                }

            }
            else {
                debugLights.show();
            }

        }
        else {
            if (debugLights != null) {
                debugLights.close();
                debugLights = null;
            }
        }

    }

    @FXML
    private void onExitMenuItemAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void onImportModelMenuItemAction(ActionEvent event) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/ImportModelWindow.fxml"));

        Parent root = null;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (root != null) {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Import Model...");
            stage.setScene(new Scene(root));
            stage.sizeToScene();
            stage.setResizable(false);

            stage.showAndWait();
        }

    }

    @FXML
    private void onConfigurationMenuItemAction(ActionEvent event) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/ConfigurationWindow.fxml"));

        Parent root = null;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (root != null) {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Configuration");
            stage.setScene(new Scene(root, 640, 480));



            stage.showAndWait();
        }

    }

    @FXML private void onShowGridCheckMenuItemAction(ActionEvent event) {

        CheckMenuItem checkMenuItem = (CheckMenuItem) event.getSource();
        final boolean isChecked = checkMenuItem.isSelected();

        DevKitConfig.getInstance().getSceneConfig().setShowGrid(isChecked);
        DevKitConfig.getInstance().save();

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {

            if (isChecked) {
                Geometry gridGeometry = createGridGeometry();
                engineService.getRootNode().attachChild(gridGeometry);
            }
            else {

                Spatial gridSpatial = engineService.getRootNode().getChild("Debug Grid");

                if (gridSpatial != null) {
                    gridSpatial.removeFromParent();
                }

            }

        });

    }

    private Geometry createGridGeometry() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
        SceneConfig sceneConfig = DevKitConfig.getInstance().getSceneConfig();

        Grid grid = new Grid( (int) sceneConfig.getGridSize().x, (int) sceneConfig.getGridSize().y, sceneConfig.getGridSize().z );
        Geometry gridGeometry = new Geometry("Debug Grid", grid);

        gridGeometry.setMaterial(new Material(engineService.getAssetManager(), Materials.UNSHADED));
        gridGeometry.getMaterial().setColor("Color", sceneConfig.getGridColor());
        gridGeometry.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        gridGeometry.setQueueBucket(RenderQueue.Bucket.Transparent);

        gridGeometry.setLocalTranslation(sceneConfig.getGridLocation());

        return gridGeometry;
    }

}
