package com.jayfella.devkit.controller.tree;

import com.jayfella.devkit.controller.tree.menu.LightsContextMenu;
import com.jme3.light.Light;
import com.jme3.scene.Spatial;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;

public class LightTreeItem extends SceneTreeItem {

    private final Spatial parent;

    public LightTreeItem(Object value, Spatial parent, Node graphic) {
        super(value, graphic);
        this.parent = parent;
    }

    @Override
    public String getFriendlyName() {

        Light light = (Light) getValue();

        return String.format(SceneTreeItem.NAME_FORMAT,
                light.getName() == null ? "No Name" : light.getName(),
                getValue().getClass().getSimpleName());

    }

    @Override
    public ContextMenu getContextMenu() {
        return new LightsContextMenu(parent, (Light) getValue());
    }
}
