package com.jayfella.devkit.controller.config;

import javafx.scene.Parent;

public abstract class ConfigurationSection {

    private final String sectionName;
    private Parent root;

    public ConfigurationSection(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }
    public abstract void applyToConfiguration();

    public void setRoot(Parent root) {
        this.root = root;
    }
    public Parent getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return getSectionName();
    }
}
