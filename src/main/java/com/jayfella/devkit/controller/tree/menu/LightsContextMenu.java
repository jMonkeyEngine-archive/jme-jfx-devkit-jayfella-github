package com.jayfella.devkit.controller.tree.menu;

import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.devkit.service.ServiceManager;
import com.jme3.light.Light;
import com.jme3.scene.Spatial;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class LightsContextMenu extends ContextMenu {

    private final Spatial parent;
    private final Light light;

    public LightsContextMenu(Spatial parent, Light light) {
        this.parent = parent;
        this.light = light;

        MenuItem deleteItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.TIMES));
        deleteItem.setOnAction(event -> {
            ServiceManager.getService(JmeEngineService.class).enqueue(() -> parent.removeLight(light));
        });
        getItems().add(deleteItem);
    }
}
