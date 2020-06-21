package com.jayfella.devkit.controller;

import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.core.DebugGrid;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jayfella.jfx.embedded.jfx.EditorFxImageView;
import com.jme3.app.StatsAppState;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.SplitPane;
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

    @FXML private SplitPane splitPane;

    @FXML private AnchorPane anchorPaneLeft; // Scene TreeView
    @FXML private AnchorPane anchorPaneMiddle; // Scene
    @FXML private AnchorPane anchorPaneRight; // Properties

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

        // set the saved divider positions.
        splitPane.setDividerPositions(DevKitConfig.getInstance().getSdkConfig().getDividerPositions());

        // save any positions that were changed.
        for (int i = 0; i < splitPane.getDividers().size(); i++) {

            final int index = i;

            splitPane.getDividers().get(i).positionProperty().addListener((observable, oldValue, newValue) -> {
                DevKitConfig.getInstance().getSdkConfig().getDividerPositions()[index] = newValue.doubleValue();
                DevKitConfig.getInstance().save();
            });

        }

    }

    public void start() {
        mainStage.setTitle("JmonkeyEngine SDK");
        createView();
        applyConfiguration();
        addSceneControls();
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
            engineService.enqueue(DebugGrid::create);
        }

    }

    private void addSceneControls() {

        // load scene controls
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/SceneControls.fxml"));
        Parent root = null;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to load Scene Controls FXML", e);
        }

        if (root != null) {
            anchorPaneMiddle.getChildren().add(root);
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

}
