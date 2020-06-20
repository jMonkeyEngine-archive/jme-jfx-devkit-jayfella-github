package com.jayfella.devkit.controller;

import com.jayfella.devkit.controller.tree.*;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Creates a tree view of the GUI and RootNode scene.
 * A "dummy" GuiNode and RootNode are created and added to their respective counterparts so we can separate the things
 * we do and don't want the TreeView to see.
 */
public class SceneTreeWindowController implements Initializable {

    private static final Logger log = Logger.getLogger(SceneTreeWindowController.class.getName());

    public static final String UNDELETABLE_FLAG = "DO_NOT_DELETE";
    private static final String LIGHTS_COLLECTION_NAME = "Lights";
    private static final String CONTROLS_COLLECTION_NAME = "Controls";

    // continually iterate over the sceneview to check for changes.
    private final Timer timer = new Timer();
    private final long timerDelay = 500L;

    @FXML private TreeView<Object> sceneTreeView;

    // These area "fake" nodes. They are added to their counterparts so we don't see things like "statsappstate".
    private Node guiNode;
    private Node rootNode;

    private TreeItem<Object> guiNodeTreeItem;
    private TreeItem<Object> rootNodeTreeItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TreeItem<Object> treeRoot = new TreeItem<>("Scene Root");

        sceneTreeView.setCellFactory(new SceneTreeCellFactory());

        sceneTreeView.setRoot(treeRoot);
        sceneTreeView.setShowRoot(false);

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {

            // create the nodes on the JME thread.
            guiNode = new Node("Gui Node");
            rootNode = new Node("Root Node");

            // put some arbitrary data in the "root" nodes so we can reject deleting them.
            guiNode.setUserData(UNDELETABLE_FLAG, UNDELETABLE_FLAG);
            rootNode.setUserData(UNDELETABLE_FLAG, UNDELETABLE_FLAG);

            // These area "fake" nodes. They are added to their counterparts so we don't see things like "StatsAppState".
            engineService.getGuiNode().attachChild(guiNode);
            engineService.getRootNode().attachChild(rootNode);

            Platform.runLater(() -> {

                // create the TreeItems on the JavaFX thread.
                guiNodeTreeItem = new NodeTreeMenuItem(guiNode);
                rootNodeTreeItem = new NodeTreeMenuItem(rootNode);

                treeRoot.getChildren().add(guiNodeTreeItem);
                treeRoot.getChildren().add(rootNodeTreeItem);

            });

            // I think schedule is better than scheduleAtFixedRate.
            timer.schedule(sceneChangesTask, timerDelay, timerDelay);
        });


    }

    private final TimerTask sceneChangesTask = new TimerTask() {
        @Override
        public void run() {

            Platform.runLater(() -> {
                refreshGuiNode();
                refreshRootNode();
            });

        }
    };

    private void refreshGuiNode() {
        ServiceManager.getService(JmeEngineService.class).enqueue(() -> recurse(guiNodeTreeItem));
    }

    private void refreshRootNode() {
        ServiceManager.getService(JmeEngineService.class).enqueue(() -> recurse(rootNodeTreeItem));
    }

    private void recurse(TreeItem<Object> treeItem) {

        if (treeItem.getValue() instanceof Node) {

            Node node = (Node) treeItem.getValue();
            List<Spatial> nodeChildren = node.getChildren();

            // compare the scene children with the tree children

            // A list of treeItem children that are not in the scene.
            List<TreeItem<Object>> childrenToRemove = treeItem.getChildren().stream()
                    .filter(t -> t.getValue() instanceof Spatial)
                    .filter(t -> !nodeChildren.contains((Spatial) t.getValue()))
                    .collect(Collectors.toList());

            if (!childrenToRemove.isEmpty()) {
                // System.out.println("Remove: " + childrenToRemove.size());
                treeItem.getChildren().removeAll(childrenToRemove);
            }

            // A list of children that are in the scene but not currently children of the treeItem
            List<Spatial> childrenToAdd = nodeChildren.stream()
                    .filter(n -> treeItem.getChildren().stream().filter(child -> child.getValue().equals(n)).findFirst().orElse(null) == null)
                    .collect(Collectors.toList());

            // if (!childrenToAdd.isEmpty()) {
                // System.out.println("Add: " + childrenToAdd.size());
            // }

            for (Spatial childSpatial : childrenToAdd) {

                if (childSpatial instanceof Node) {

                    SceneTreeItem childTreeItem = new NodeTreeMenuItem(childSpatial);
                    treeItem.getChildren().add(childTreeItem);
                }
                else if (childSpatial instanceof Geometry) {

                    SceneTreeItem childTreeItem = new GeometryTreeItem(childSpatial);
                    treeItem.getChildren().add(childTreeItem);

                    SceneTreeItem meshTreeItem = new MeshTreeItem(((Geometry)childSpatial).getMesh());
                    childTreeItem.getChildren().add(meshTreeItem);
                }

            }

            // LIGHTS

            final TreeItem<Object> lightsCollection = treeItem.getChildren().stream()
                    .filter(t -> t instanceof CollectionTreeItem)
                    .filter(t -> t.getValue().equals(LIGHTS_COLLECTION_NAME))
                    .findFirst()
                    .orElse(null);

            LightList lightsList = node.getLocalLightList();
            List<Light> lights = new ArrayList<>();
            for (Light light : lightsList) {
                lights.add(light);
            }

            // if there is no lights collection, there were no lights in the last scan, so there is nothing to remove.
            // there is also no reason to check what to add. We would just add the entire light list.
            if (lightsCollection != null) {

                if (!lights.isEmpty()) {
                    List<TreeItem<Object>> lightsToRemove = lightsCollection.getChildren().stream()
                            .filter(t -> !lights.contains((Light) t.getValue()))
                            .collect(Collectors.toList());

                    if (!lightsToRemove.isEmpty()) {
                        lightsCollection.getChildren().removeAll(lightsToRemove);
                    }

                    List<Light> lightsToAdd = lights.stream()
                            .filter(n -> lightsCollection.getChildren().stream().filter(child -> child.getValue().equals(n)).findFirst().orElse(null) == null)
                            .collect(Collectors.toList());

                    for (Light light : lightsToAdd) {
                        LightTreeItem lightTreeItem = new LightTreeItem(light, node, null);
                        lightsCollection.getChildren().addAll(lightTreeItem);
                    }
                }
                else {
                    // lights collection is not null, and we have no lights, so just remove the collection.
                    treeItem.getChildren().remove(lightsCollection);
                }
            }
            else if (!lights.isEmpty()) {

                CollectionTreeItem newLightsCollection = new CollectionTreeItem(LIGHTS_COLLECTION_NAME);

                for (Light light : lights) {
                    LightTreeItem lightTreeItem = new LightTreeItem(light, node, null);
                    newLightsCollection.getChildren().add(lightTreeItem);
                }

                treeItem.getChildren().add(newLightsCollection);
            }

            // CONTROLS

            final TreeItem<Object> controlsCollection = treeItem.getChildren().stream()
                    .filter(t -> t instanceof CollectionTreeItem)
                    .filter(t -> t.getValue().equals(CONTROLS_COLLECTION_NAME))
                    .findFirst()
                    .orElse(null);

            List<Control> controls = new ArrayList<>();
            for (int i = 0; i < node.getNumControls(); i++) {
                controls.add(node.getControl(i));
            }

            // if there is no controls collection, there were no controls in the last scan, so there is nothing to remove.
            // there is also no reason to check what to add. We would just add the entire control list.
            if (controlsCollection != null) {

                if (!controls.isEmpty()) {
                    List<TreeItem<Object>> controlsToRemove = controlsCollection.getChildren().stream()
                            .filter(t -> !controls.contains((Control) t.getValue()))
                            .collect(Collectors.toList());

                    if (!controlsToRemove.isEmpty()) {
                        controlsCollection.getChildren().removeAll(controlsToRemove);
                    }

                    List<Control> controlsToAdd = controls.stream()
                            .filter(n -> controlsCollection.getChildren().stream().filter(child -> child.getValue().equals(n)).findFirst().orElse(null) == null)
                            .collect(Collectors.toList());

                    for (Control control : controlsToAdd) {
                        ControlTreeItem controlTreeItem = new ControlTreeItem(control);
                        controlsCollection.getChildren().add(controlTreeItem);
                    }
                }
                else {
                    // controls collection is not null, and we have no controls, so just remove the collection.
                    treeItem.getChildren().remove(controlsCollection);
                }
            }
            else if (!controls.isEmpty()) {

                CollectionTreeItem newControlsCollection = new CollectionTreeItem(CONTROLS_COLLECTION_NAME);

                for (Control control : controls) {
                    ControlTreeItem controlTreeItem = new ControlTreeItem(control);
                    newControlsCollection.getChildren().add(controlTreeItem);
                }

                treeItem.getChildren().add(newControlsCollection);
            }










            // we have to recurse the whole scene to check for changes.
            for (Spatial child : nodeChildren) {
                if (child instanceof Node) {
                    TreeItem<Object> childTreeItem = treeItem.getChildren().stream()
                            .filter(t -> t.getValue().equals(child))
                            .findFirst()
                            .orElse(null);

                    if (childTreeItem != null) {
                        recurse(childTreeItem);
                    }
                    else {
                        log.log(Level.WARNING, "Cannot find TreeItem for child node: " + child);
                    }

                }
            }

        }

    }

}
