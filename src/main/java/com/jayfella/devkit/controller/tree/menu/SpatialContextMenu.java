package com.jayfella.devkit.controller.tree.menu;

import com.jayfella.devkit.controller.SaveSpatialWindowController;
import com.jayfella.devkit.controller.SceneTreeWindowController;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * A menu that is added to Nodes and Geometries.
 */
public class SpatialContextMenu extends ContextMenu {

    private final Spatial spatial;

    private final Menu addMenu;

    public SpatialContextMenu(Spatial spatial) {
        super();

        this.spatial = spatial;

        addMenu = createAddMenu();
        getItems().add(addMenu);

        getItems().add(new SeparatorMenuItem());

        MenuItem saveItem = new MenuItem("Save...", new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        saveItem.setOnAction(event -> {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/SaveSpatialWindow.fxml"));

            Parent root = null;
            SaveSpatialWindowController controller = null;

            try {
                root = fxmlLoader.load();
                controller = fxmlLoader.getController();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (root != null) {

                controller.setSelectedSpatial(spatial);

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Save Spatial");
                stage.setScene(new Scene(root));
                stage.sizeToScene();
                stage.setResizable(false);

                stage.showAndWait();
            }

        });
        getItems().add(saveItem);

        getItems().add(new SeparatorMenuItem());

        MenuItem deleteItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.TIMES));
        deleteItem.setOnAction(event -> ServiceManager.getService(JmeEngineService.class).enqueue(() -> {

            String undeletable = spatial.getUserData(SceneTreeWindowController.UNDELETABLE_FLAG);

            if (undeletable == null) {
                spatial.removeFromParent();
            }
            else {
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Delete Rejected");
                    alert.setHeaderText("Unable To Delete Root Node");
                    alert.setContentText("You cannot delete a root node.");
                    alert.showAndWait();

                });
            }

        }));
        getItems().add(deleteItem);
    }

    public Menu getAddMenu() {
        return addMenu;
    }

    private Menu createAddMenu() {

        Menu menu = new Menu("Add...", new FontAwesomeIconView(FontAwesomeIcon.PLUS));

        Menu lightMenu = createLightMenu();
        menu.getItems().add(lightMenu);

        return menu;
    }

    private Menu createLightMenu() {

        Menu menu = new Menu("Light...", new FontAwesomeIconView(FontAwesomeIcon.LIGHTBULB_ALT));

        MenuItem ambLight = new MenuItem("Ambient Light");
        ambLight.setOnAction(event -> ServiceManager.getService(JmeEngineService.class).enqueue(() ->
                spatial.addLight(new AmbientLight())));
        menu.getItems().add(ambLight);

        MenuItem dirLight = new MenuItem("Directional Light");
        dirLight.setOnAction(event -> ServiceManager.getService(JmeEngineService.class).enqueue(() ->
                spatial.addLight(new DirectionalLight(new Vector3f(-1, -1, -1).normalizeLocal()))));
        menu.getItems().add(dirLight);

        MenuItem probeLight = new MenuItem("Light Probe");
        menu.getItems().add(probeLight);

        return menu;
    }

}
