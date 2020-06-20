package com.jayfella.devkit.core.format;

import javafx.scene.control.TextFormatter;

public class IntegerTextFormatter extends TextFormatter<String> {

    public IntegerTextFormatter() {
        super(new IntegerStringConverter());
    }
}
