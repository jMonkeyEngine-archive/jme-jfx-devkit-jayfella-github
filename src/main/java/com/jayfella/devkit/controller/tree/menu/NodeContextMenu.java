package com.jayfella.devkit.controller.tree.menu;

import com.jayfella.devkit.controller.AddModelWindowController;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.Materials;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class NodeContextMenu extends SpatialContextMenu {

    private final Node node;

    public NodeContextMenu(Node node) {
        super(node);

        this.node = node;

        Menu primitivesMenu = createShapesMenu();
        getAddMenu().getItems().add(primitivesMenu);

        // add -> Node
        MenuItem nodeSceneItem = new MenuItem("Node");
        nodeSceneItem.setOnAction(event -> {
            ServiceManager.getService(JmeEngineService.class).enqueue(() -> node.attachChild(new Node()));
        });
        getAddMenu().getItems().add(nodeSceneItem);

        // add -> Model...
        MenuItem modelsItem = new MenuItem("Model(s)...");
        modelsItem.setOnAction(event -> {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/AddModelWindow.fxml"));

            Parent root = null;
            AddModelWindowController controller = null;

            try {
                root = fxmlLoader.load();
                controller = fxmlLoader.getController();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (root != null) {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Add Model(s)...");
                stage.setScene(new Scene(root));
                stage.sizeToScene();
                stage.setResizable(false);

                stage.showAndWait();

                List<String> selectedModels = controller.getSelectedModels();

                if (!selectedModels.isEmpty()) {

                    AssetManager assetManager = ServiceManager.getService(JmeEngineService.class).getAssetManager();

                    for (String modelPath : selectedModels) {
                        Spatial model = assetManager.loadModel(modelPath);
                        node.attachChild(model);
                    }
                }
            }

        });
        getAddMenu().getItems().add(modelsItem);
    }

    private Geometry createShape(Mesh mesh, String name) {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        Geometry geometry = new Geometry(name, mesh);

        Material material = new Material(engineService.getAssetManager(), Materials.PBR);
        geometry.setMaterial(material);

        return geometry;
    }

    private Menu createShapesMenu() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        Menu menu = new Menu("Shape...");

        MenuItem cubeMenuItem = new MenuItem("Cube");
        cubeMenuItem.setOnAction(event -> {

            Geometry shape = createShape(new Box(1, 1, 1), "Cube");
            engineService.enqueue(() -> node.attachChild(shape));

        });
        menu.getItems().add(cubeMenuItem);

        MenuItem cylinderMenuItem = new MenuItem("Cylinder");
        cylinderMenuItem.setOnAction(event -> {

            Geometry shape = createShape(new Cylinder(32, 32, 1.0f, 1.0f, true), "Cylinder");
            engineService.enqueue(() -> node.attachChild(shape));

        });
        menu.getItems().add(cylinderMenuItem);

        MenuItem domeMenuItem = new MenuItem("Dome");
        domeMenuItem.setOnAction(event -> {

            Geometry shape = createShape(new Dome(32, 32, 1.0f), "Dome");
            engineService.enqueue(() -> node.attachChild(shape));

        });
        menu.getItems().add(domeMenuItem);

        MenuItem torusMenuItem = new MenuItem("PQTorus");
        torusMenuItem.setOnAction(event -> {

            Geometry shape = createShape(new Dome(32, 32, 1.0f), "PQTorus");
            engineService.enqueue(() -> node.attachChild(shape));

        });
        menu.getItems().add(torusMenuItem);

        MenuItem quadMenuItem = new MenuItem("Quad");
        quadMenuItem.setOnAction(event -> {

            Geometry shape = createShape(new Quad(1.0f, 1.0f), "Quad");
            engineService.enqueue(() -> node.attachChild(shape));

        });
        menu.getItems().add(quadMenuItem);

        MenuItem sphereMenuItem = new MenuItem("Sphere");
        sphereMenuItem.setOnAction(event -> {

            Geometry shape = createShape(new Sphere(32, 32, 1.0f), "Sphere");
            engineService.enqueue(() -> node.attachChild(shape));

        });
        menu.getItems().add(sphereMenuItem);

        return menu;
    }

}
