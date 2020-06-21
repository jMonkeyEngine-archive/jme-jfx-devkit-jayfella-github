package com.jayfella.devkit.config;

public class SdkConfig {

    private double[] dividerPositions = new double[] { 0.3, 0.75 }; // the divider positions of the scene (tree, canvas, props).
    private double[] windowSize = new double[] { 1280, 720 }; // the size of the main window.
    private double[] windowLocation = new double[] { 0.0, 0.0 }; // the starting location of the window.

    public SdkConfig() {

    }

    public double[] getDividerPositions() {
        return dividerPositions;
    }
    public void setDividerPositions(double[] dividerPositions) {
        this.dividerPositions = dividerPositions;
    }

    public double[] getWindowSize() { return windowSize; }
    public void setWindowSize(double[] windowSize) { this.windowSize = windowSize; }

    public double[] getWindowLocation() { return windowLocation; }
    public void setWindowLocation(double[] windowLocation) { this.windowLocation = windowLocation; }

}
