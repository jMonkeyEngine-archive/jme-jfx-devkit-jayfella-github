package com.jayfella.devkit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SceneControlsController implements Initializable {

    private Popup debugLightsPopup = null;
    private Popup debugGridPopup = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void onDebugLightsToggleButtonAction(ActionEvent event) {

        ToggleButton toggleButton = (ToggleButton) event.getSource();
        Scene scene = toggleButton.getScene();
        Stage window = (Stage) toggleButton.getScene().getWindow();

        if (toggleButton.isSelected()) {

            if (debugLightsPopup == null) {

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/DebugLightsPopup.fxml"));

                Parent root = null;

                try {
                    root = fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (root != null) {

                    debugLightsPopup = new Popup();
                    debugLightsPopup.getContent().add(root);
                    debugLightsPopup.setX(toggleButton.getTranslateX());
                    debugLightsPopup.setY(toggleButton.getTranslateY());

                    Point2D windowCoords = new Point2D(window.getX(), window.getY());
                    Point2D sceneCoords = new Point2D(scene.getX(), scene.getY());

                    Point2D nodeCoords = toggleButton.localToScene(0.0, 0.0);

                    Point2D popupLocation = windowCoords.add(sceneCoords).add(nodeCoords);

                    debugLightsPopup.setX(popupLocation.getX());
                    debugLightsPopup.setY(popupLocation.getY() + toggleButton.getHeight());

                    debugLightsPopup.setAutoHide(false);

                }

            }


            debugLightsPopup.show(window);

        }
        else {

            if (debugLightsPopup != null) {
                debugLightsPopup.hide();
                debugLightsPopup = null;
            }

        }

    }

    @FXML
    private void onDebugGridToggleButtonAction(ActionEvent event) {

        ToggleButton toggleButton = (ToggleButton) event.getSource();
        Scene scene = toggleButton.getScene();
        Stage window = (Stage) toggleButton.getScene().getWindow();

        if (toggleButton.isSelected()) {

            if (debugGridPopup == null) {

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/DebugGridPopup.fxml"));

                Parent root = null;

                try {
                    root = fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (root != null) {

                    debugGridPopup = new Popup();
                    debugGridPopup.getContent().add(root);
                    debugGridPopup.setX(toggleButton.getTranslateX());
                    debugGridPopup.setY(toggleButton.getTranslateY());

                    Point2D windowCoords = new Point2D(window.getX(), window.getY());
                    Point2D sceneCoords = new Point2D(scene.getX(), scene.getY());

                    Point2D nodeCoords = toggleButton.localToScene(0.0, 0.0);

                    Point2D popupLocation = windowCoords.add(sceneCoords).add(nodeCoords);

                    debugGridPopup.setX(popupLocation.getX());
                    debugGridPopup.setY(popupLocation.getY() + toggleButton.getHeight());

                    debugGridPopup.setAutoHide(false);

                }

            }


            debugGridPopup.show(window);

        }
        else {

            if (debugGridPopup != null) {
                debugGridPopup.hide();
                debugGridPopup = null;
            }

        }

    }

}
