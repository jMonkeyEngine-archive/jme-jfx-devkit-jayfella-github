package com.jayfella.devkit.controller;

import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jayfella.devkit.service.impl.JmeEngineServiceImpl;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class SplashScreenController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(SplashScreenController.class);

    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;

    private Stage mainStage;
    private MainPageController mainPageController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        statusLabel.setText("Starting Engine...");
        progressBar.setProgress(-1);

        // LWJGL3 does not return after createCanvas().
        // As a result the JmeEngineService constructor will never complete its creation
        // To get around this we call the createCanvas() method AFTER the constructor.

        new Thread(new ThreadGroup("LWJGL"), () -> {
            ServiceManager.registerService(JmeEngineServiceImpl.class);
            ServiceManager.getService(JmeEngineService.class).start();
        }, "LWJGL Render").start();

        CompletableFuture
                .runAsync(() -> {

                    // let the engine go through its first iteration of a loop.

                    boolean ready = false;

                    while (!ready) {

                        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

                        // if (engineService != null && engineService.isStarted() && engineService.isInitialized()) {
                            // ready = true;
                        // }

                        if (engineService == null) {
                            Platform.runLater(() -> statusLabel.setText("Starting Engine..."));
                        }
                        else if (engineService.isStarted() && !engineService.isInitialized()) {
                            Platform.runLater(() -> statusLabel.setText("Initializing Engine..."));
                        }
                        else if (engineService.isInitialized()) {
                            Platform.runLater(() -> statusLabel.setText("Starting..."));
                            ready = true;
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                })

                .thenRunAsync(() -> Platform.runLater(() -> progressBar.setProgress(1d)))

                .thenRunAsync(() -> Platform.runLater(() -> {

                    Stage stage = (Stage) statusLabel.getScene().getWindow();
                    stage.hide();

                    mainPageController.start();
                    mainPageController.getMainStage().show();

                    // check if the asset root dir has been set and exists.
                    checkAssetRootDir();

                }));

    }

    public void setPrimaryController(MainPageController primaryController) {
        this.mainPageController = primaryController;
    }

    private void checkAssetRootDir() {

        // if the asset root dir is null, specify the default dir and notify the user.
        if (DevKitConfig.getInstance().getProjectConfig().getAssetRootDir() == null) {

            Path assetRoot = Paths.get("src", "main", "resources");
            DevKitConfig.getInstance().getProjectConfig().setAssetRootDir(assetRoot.toAbsolutePath().toString());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Asset Root");
            alert.setHeaderText("No Asset Root Specified.");
            alert.setContentText("Your Asset Root directory has not been set, and has been assigned a default value " +
                    "of ./src/main/resources/"
                    + System.lineSeparator()
                    + System.lineSeparator()
                    + "To change this value navigate to Edit -> Configuration.");

            alert.showAndWait();

            DevKitConfig.getInstance().save();
        }

        // notify the user that the asset root dir doesn't exist.
        if (!new File(DevKitConfig.getInstance().getProjectConfig().getAssetRootDir()).exists()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Asset Root");
            alert.setHeaderText("Asset Root directory does not exist.");
            alert.setContentText("The specified Asset Root directory does not exist. Please specify a valid Asset Root " +
                    "directory by navigating to Edit - Configuration");

        }

        else {

            // create a file locator for the asset root dir.

            AssetManager assetManager = ServiceManager.getService(JmeEngineService.class).getAssetManager();
            String assetRoot = DevKitConfig.getInstance().getProjectConfig().getAssetRootDir();

            assetManager.registerLocator(assetRoot, FileLocator.class);
            log.info("Registered Asset Root Directory: " + assetRoot);

        }



    }

}
