package com.jayfella.devkit.core.format;

import javafx.scene.control.TextFormatter;

/**
 * Enforce float-only values.
 */
public class FloatTextFormatter extends TextFormatter<String> {

    public FloatTextFormatter() {
        super(new FloatStringConverter());
    }

}
