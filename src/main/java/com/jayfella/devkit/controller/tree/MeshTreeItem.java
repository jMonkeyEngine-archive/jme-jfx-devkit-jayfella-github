package com.jayfella.devkit.controller.tree;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;

public class MeshTreeItem extends SceneTreeItem {

    public MeshTreeItem(Object value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.CIRCLE_THIN));
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
