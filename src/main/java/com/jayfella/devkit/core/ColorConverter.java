package com.jayfella.devkit.core;

import com.jme3.math.ColorRGBA;
import javafx.scene.paint.Color;

public class ColorConverter {

    public static ColorRGBA toColorRGBA(Color color) {
        return new ColorRGBA(
                (float) color.getRed(),
                (float) color.getGreen(),
                (float) color.getBlue(),
                (float) color.getOpacity());
    }

    public static Color toColor(ColorRGBA color) {
        return new Color(color.r, color.g, color.b, color.a);
    }

}
