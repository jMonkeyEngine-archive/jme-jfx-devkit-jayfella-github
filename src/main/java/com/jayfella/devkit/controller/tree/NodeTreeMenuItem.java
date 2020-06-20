package com.jayfella.devkit.controller.tree;

import com.jayfella.devkit.controller.tree.menu.NodeContextMenu;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;

public class NodeTreeMenuItem extends SceneTreeItem {

    public NodeTreeMenuItem(Object value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.CODE_FORK));
    }

    @Override
    public String getFriendlyName() {

        com.jme3.scene.Node node = (com.jme3.scene.Node)getValue();

        return String.format(SceneTreeItem.NAME_FORMAT,
                node.getName() == null ? "No Name" : node.getName(),
                getValue().getClass().getSimpleName());

    }

    @Override
    public ContextMenu getContextMenu() {
        return new NodeContextMenu((com.jme3.scene.Node) getValue());
    }

}
