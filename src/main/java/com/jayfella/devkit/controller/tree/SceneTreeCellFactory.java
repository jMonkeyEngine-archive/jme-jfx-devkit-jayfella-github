package com.jayfella.devkit.controller.tree;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class SceneTreeCellFactory implements Callback<TreeView<Object>, TreeCell<Object>> {

    private TreeItem<Object> draggedItem;

    @Override
    public TreeCell<Object> call(TreeView<Object> treeView) {

        TreeCell<Object> cell = new SceneTreeCell();
        return cell;
    }



}
