package com.jayfella.devkit.controller.tree;

import com.jayfella.devkit.controller.tree.menu.SpatialContextMenu;
import com.jme3.scene.Geometry;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;

public class GeometryTreeItem extends SceneTreeItem {

    public GeometryTreeItem(Object value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.CUBE));
    }

    @Override
    public String getFriendlyName() {

        Geometry geometry = (Geometry) getValue();

        return String.format(SceneTreeItem.NAME_FORMAT,
                geometry.getName() == null ? "No Name" : geometry.getName(),
                getValue().getClass().getSimpleName());

    }

    @Override
    public ContextMenu getContextMenu() {
        return new SpatialContextMenu( (Geometry) getValue() );
    }
}
