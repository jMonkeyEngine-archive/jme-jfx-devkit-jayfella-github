package com.jayfella.devkit.core;

import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.config.SceneConfig;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jme3.material.Material;
import com.jme3.material.Materials;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Grid;

/**
 * Handles creation, manipulation and removal of the debug grid.
 * All methods **must** be run in the JME thread.
 */
public class DebugGrid {

    public static final String DEBUG_GRID_NAME = "Debug Grid";

    // we could check on any thread if it's attached.
    private static volatile boolean attached = false;

    public static void create() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
        Geometry gridGeometry = (Geometry) engineService.getRootNode().getChild(DEBUG_GRID_NAME);

        if (gridGeometry == null) {

            SceneConfig sceneConfig = DevKitConfig.getInstance().getSceneConfig();

            Grid grid = new Grid((int) sceneConfig.getGridSize().x, (int) sceneConfig.getGridSize().y, sceneConfig.getGridSize().z);
            gridGeometry = new Geometry(DEBUG_GRID_NAME, grid);

            gridGeometry.setMaterial(new Material(engineService.getAssetManager(), Materials.UNSHADED));
            gridGeometry.getMaterial().setColor("Color", sceneConfig.getGridColor());

            gridGeometry.setLocalTranslation(sceneConfig.getGridLocation());

            engineService.getRootNode().attachChild(gridGeometry);

            attached = true;
        }

    }

    public static void refreshMesh(boolean refreshMesh, boolean refreshColor) {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
        Geometry gridGeometry = (Geometry) engineService.getRootNode().getChild(DEBUG_GRID_NAME);

        if (gridGeometry != null) {
            SceneConfig sceneConfig = DevKitConfig.getInstance().getSceneConfig();

            if (refreshMesh) {
                Grid grid = new Grid((int) sceneConfig.getGridSize().x, (int) sceneConfig.getGridSize().y, sceneConfig.getGridSize().z);
                gridGeometry.setMesh(grid);
            }

            if (refreshColor) {
                gridGeometry.getMaterial().setColor("Color", sceneConfig.getGridColor());
            }

        }

    }

    public static void remove() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
        Geometry gridGeometry = (Geometry) engineService.getRootNode().getChild(DEBUG_GRID_NAME);

        if (gridGeometry != null) {
            gridGeometry.removeFromParent();
            attached = false;
        }

    }

    public static boolean isAttached() {
        return attached;
    }

}
