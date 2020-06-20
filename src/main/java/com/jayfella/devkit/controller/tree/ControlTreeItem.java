package com.jayfella.devkit.controller.tree;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;

public class ControlTreeItem extends SceneTreeItem {

    public ControlTreeItem(Object value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.GAMEPAD));
    }

    @Override
    public String getFriendlyName() {
        return getValue().getClass().getSimpleName();
    }

    @Override
    public ContextMenu getContextMenu() {
        return null;
    }
}
