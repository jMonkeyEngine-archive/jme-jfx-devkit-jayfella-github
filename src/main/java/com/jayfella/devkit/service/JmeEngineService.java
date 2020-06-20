package com.jayfella.devkit.service;

import com.jayfella.jfx.embedded.SimpleJfxApplication;
import com.jme3.post.FilterPostProcessor;

public abstract class JmeEngineService extends SimpleJfxApplication implements Service {

    public JmeEngineService() {
        super();
    }

    // public abstract FilterManager getFilterManager();
    public abstract FilterPostProcessor getFilterPostProcessor();
    public abstract void setFilterPostProcessor(FilterPostProcessor fpp);

    // public abstract void setView2d();
    // public abstract void setView3d();


}
