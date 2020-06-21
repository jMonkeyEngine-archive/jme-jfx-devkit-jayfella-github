package com.jayfella.devkit.service.impl;

import com.jayfella.devkit.config.CameraConfig;
import com.jayfella.devkit.config.DevKitConfig;
import com.jayfella.devkit.service.JmeEngineService;
import com.jayfella.jfx.embedded.jme.JmeOffscreenSurfaceContext;
import com.jme3.audio.AudioListenerState;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.material.TechniqueDef;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import org.apache.log4j.Logger;

public class JmeEngineServiceImpl extends JmeEngineService {

    private static final Logger log = Logger.getLogger(JmeEngineService.class);

    private FilterPostProcessor fpp;

    public JmeEngineServiceImpl() {
        super();

        AppSettings settings = new AppSettings(true);
        settings.setCustomRenderer(JmeOffscreenSurfaceContext.class);
        // settings.setFrameRate(60);
        // settings.setVSync(true);
        settings.setResizable(true);
        settings.setAudioRenderer(null);
        settings.setUseJoysticks(true);
        settings.setGammaCorrection(true);
        settings.setSamples(16);
        setSettings(settings);
        setPauseOnLostFocus(false);

        setSettings(settings);

        createCanvas();


    }

    @Override
    public void initApp() {

        DevKitConfig devKitConfig = DevKitConfig.getInstance();
        viewPort.setBackgroundColor(devKitConfig.getCameraConfig().getViewportColor());

        applyCameraFrustumSizes();

        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle(BaseStyles.GLASS);

        stateManager.attachAll(
                new EditorCameraState(),    // our camera movement in the editor
                new EnvironmentCamera(),    // used for probe generation. I'm not certain we want this right now...
                new AudioListenerState()   // required for positional audio.
                //new SpatialSelectorState(),
                //new SpatialToolState()      // select & transform spatials.
        );

        // Configure the scene for PBR
        getRenderManager().setPreferredLightMode(TechniqueDef.LightMode.SinglePassAndImageBased);
        getRenderManager().setSinglePassLightBatchSize(10);

        inputManager.setCursorVisible(true);

        // set the initial camera position and direction.
        // back a bit and slightly up because floors are commonly at zero Y and it looks ugly.
        // we're never going to get this right, but for most cases this is fine.
        cam.setLocation(new Vector3f(0, 5, 15));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        // set the default shadow mode for everything.
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        initialized = true;
    }

    @Override
    public void stop() {
        stop(true);
    }

    @Override
    public FilterPostProcessor getFilterPostProcessor() {
        return this.fpp;
    }

    /**
     * Removes any existing FilterPostProcessor and adds the given one.
     * Called whenever post processors are added or removed.
     * We require a new FPP because adding a post-processor that requires depth after it's been initialized causes
     * an error if none of the other post processors needed depth.
     * To get around this, we just create a new one with the new post-processors.
     * @param fpp the FilterPostProcessor to display, with all required filters already added.
     */
    @Override
    public void setFilterPostProcessor(FilterPostProcessor fpp) {

        if (this.fpp != null) {
            viewPort.removeProcessor(this.fpp);
        }

        this.fpp = fpp;

        if (this.fpp != null) {
            viewPort.addProcessor(this.fpp);
        }

    }

    @Override
    public void applyCameraFrustumSizes() {

        CameraConfig cameraConfig = DevKitConfig.getInstance().getCameraConfig();

        viewPort.getCamera().setFrustumPerspective(
                cameraConfig.getFieldOfView(),
                (float) viewPort.getCamera().getWidth() / (float) viewPort.getCamera().getHeight(),
                cameraConfig.getFrustumNear(),
                cameraConfig.getFrustumFar());

    }

}
