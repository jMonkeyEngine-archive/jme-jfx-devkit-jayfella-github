package com.jayfella.devkit.controller.tree;

import javafx.scene.control.TreeCell;

public class SceneTreeCell extends TreeCell<Object> {

    @Override
    public void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {

            SceneTreeItem treeItem = (SceneTreeItem) getTreeItem();

            setText(treeItem.getFriendlyName());
            setGraphic(getTreeItem().getGraphic());
            setContextMenu(((SceneTreeItem) getTreeItem()).getContextMenu());

        }
    }



}
