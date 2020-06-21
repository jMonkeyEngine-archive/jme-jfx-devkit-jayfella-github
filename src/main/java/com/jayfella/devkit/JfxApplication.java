package com.jayfella.devkit;

import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.controller.MainPageController;
import com.jayfella.devkit.controller.SplashScreenController;
import com.jayfella.devkit.core.LogUtil;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jayfella.jfx.embedded.jfx.EditorFxImageView;
import com.jme3.util.LWJGLBufferAllocator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.lwjgl.system.Configuration;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

public class JfxApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        LogUtil.initializeLogger(Level.INFO, true);

        Arrays.stream(new String[] {
                "org.reflections.Reflections"
        }).forEach(p -> LogManager.getLogger(p).setLevel(Level.ERROR));

        String vmVendor = ManagementFactory.getRuntimeMXBean().getVmVendor();

        if (!vmVendor.equals("AdoptOpenJDK")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Untested JVM");
            alert.setHeaderText("Using JVM: " + vmVendor);
            alert.setContentText("The recommended JVM is AdoptOpenJDK. All other JVMs are UNTESTED.");

            alert.showAndWait();
        }

        // compatibility settings
        Configuration.GLFW_CHECK_THREAD0.set(false); // need to disable to work on macos
        Configuration.MEMORY_ALLOCATOR.set("jemalloc"); // use jemalloc
        System.setProperty("prism.lcdtext", "false"); // JavaFx Font Anti-Aliasing
        System.setProperty(LWJGLBufferAllocator.PROPERTY_CONCURRENT_BUFFER_ALLOCATOR, "true");

        Platform.setImplicitExit(false);

        primaryStage.setTitle("JmonkeyEngine SDK");

        // Splash Screen
        FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/JavaFx/SplashScreen.fxml"));
        Parent splashRoot = splashLoader.load();
        SplashScreenController splashController = splashLoader.getController();

        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.setScene(new Scene(splashRoot));
        splashStage.sizeToScene();
        splashStage.centerOnScreen();

        FXMLLoader primaryLoader = new FXMLLoader(getClass().getResource("/JavaFx/MainPage.fxml"));
        Parent root = primaryLoader.load();
        MainPageController mainPageController = primaryLoader.getController();
        primaryStage.initStyle(StageStyle.DECORATED);

        // set window size
        primaryStage.setScene(new Scene(root,
                DevKitConfig.getInstance().getSdkConfig().getWindowSize()[0],
                DevKitConfig.getInstance().getSdkConfig().getWindowSize()[1]));

        // set window position
        primaryStage.setX(DevKitConfig.getInstance().getSdkConfig().getWindowLocation()[0]);
        primaryStage.setY(DevKitConfig.getInstance().getSdkConfig().getWindowLocation()[1]);

        primaryStage.setOnHidden(event -> Platform.exit());

        // window size
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            DevKitConfig.getInstance().getSdkConfig().getWindowSize()[0] = newValue.doubleValue();
            DevKitConfig.getInstance().save();
        });

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            DevKitConfig.getInstance().getSdkConfig().getWindowSize()[1] = newValue.doubleValue();
            DevKitConfig.getInstance().save();
        });

        // window position
        primaryStage.xProperty().addListener((observable, oldValue, newValue) -> {
            DevKitConfig.getInstance().getSdkConfig().getWindowLocation()[0] = newValue.doubleValue();
            DevKitConfig.getInstance().save();
        });

        primaryStage.yProperty().addListener((observable, oldValue, newValue) -> {
            DevKitConfig.getInstance().getSdkConfig().getWindowLocation()[1] = newValue.doubleValue();
            DevKitConfig.getInstance().save();
        });

        mainPageController.setMainStage(primaryStage);
        splashController.setPrimaryController(mainPageController);

        splashStage.show();

        addFocusHandler(primaryStage);

    }

    private void addFocusHandler(Stage primaryStage) {

        // Input handler for JME scene
        primaryStage.getScene().addEventFilter(MouseEvent.ANY, event -> {

            if (event.getTarget() instanceof EditorFxImageView) {
                if (event.getEventType() == MouseEvent.MOUSE_ENTERED_TARGET) {
                    JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
                    engineService.getImageView().requestFocus();
                }
            }
        });

    }

}
