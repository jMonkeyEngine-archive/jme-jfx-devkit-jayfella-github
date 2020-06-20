package com.jayfella.devkit.controller.tree;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;

public class CollectionTreeItem extends SceneTreeItem {

    public CollectionTreeItem(String value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.LIST));
    }

    @Override
    public String getFriendlyName() {
        return (String) getValue();
    }

    @Override
    public ContextMenu getContextMenu() {
        return null;
    }
}
