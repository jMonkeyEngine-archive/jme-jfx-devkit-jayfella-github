package com.jayfella.devkit.controller.tree;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public abstract class SceneTreeItem extends TreeItem<Object> {

    public static String NAME_FORMAT = "%s [%s]";

    public SceneTreeItem(Object value, Node graphic) {
        super(value, graphic);
    }

    public abstract String getFriendlyName();
    public abstract ContextMenu getContextMenu();

}
