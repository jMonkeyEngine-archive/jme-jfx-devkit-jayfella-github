package com.jayfella.devkit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jayfella.devkit.core.json.ColorRGBASerializer;
import com.jayfella.devkit.core.json.QuaternionSerializer;
import com.jayfella.devkit.core.json.Vector3fSerializer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DevKitConfig {

    private static final Logger log = Logger.getLogger(DevKitConfig.class.getName());

    private static DevKitConfig INSTANCE;

    private static final File storageDir = new File("devkit");
    private static final File configFile = Paths.get(storageDir.toString(), "devkit-config.json").toFile();

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new SimpleModule().addSerializer(ColorRGBA .class, new ColorRGBASerializer(ColorRGBA.class)))
            .registerModule(new SimpleModule().addSerializer(Vector3f .class, new Vector3fSerializer(Vector3f.class)))
            .registerModule(new SimpleModule().addSerializer(Quaternion.class, new QuaternionSerializer(Quaternion.class)));

    private ProjectConfig projectConfig = new ProjectConfig();
    private CameraConfig cameraConfig = new CameraConfig();
    private SceneConfig sceneConfig = new SceneConfig();

    private DevKitConfig() {
    }

    public static DevKitConfig getInstance() {
        if (INSTANCE == null) {

            if (!storageDir.exists()) {

                log.info("Creating devkit storage directory: " + storageDir);
                boolean created = storageDir.mkdir();

                if (!created) {
                    throw new RuntimeException("Unable to create config directory. Exiting.");
                }
            }

            if (!configFile.exists()) {

                log.info("Creating configuration file: " + configFile);

                INSTANCE = new DevKitConfig();
                INSTANCE.save();
            }
            else {
                log.info("Reading configuration file: " + configFile);

                try {
                    INSTANCE = objectMapper.readValue(configFile, DevKitConfig.class);
                } catch (IOException e) {
                    // we can't continue if we can't read the configuration file.
                    throw new RuntimeException(e);
                }

                // we save the configuration because new areas may have been added since the last save.
                if (INSTANCE != null) {
                    INSTANCE.save();
                }

            }

        }

        return INSTANCE;
    }

    public ProjectConfig getProjectConfig() { return projectConfig; }
    protected void setProjectConfig(ProjectConfig projectConfig) { this.projectConfig = projectConfig; }

    public CameraConfig getCameraConfig() {
        return cameraConfig;
    }
    protected void setCameraConfig(CameraConfig cameraConfig) {
        this.cameraConfig = cameraConfig;
    }

    public SceneConfig getSceneConfig() { return sceneConfig; }
    protected void setSceneConfig(SceneConfig sceneConfig) { this.sceneConfig = sceneConfig; }

    public void save() {

        log.info("Saving configuration: " + configFile);

        try {
            objectMapper.writeValue(configFile, this);
            log.info("Saved configuration: " + configFile);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Unable to write file: " + configFile, ex);
        }

    }

}
