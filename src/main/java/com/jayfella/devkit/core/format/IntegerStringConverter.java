package com.jayfella.devkit.core.format;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class IntegerStringConverter implements UnaryOperator<TextFormatter.Change> {

    @Override
    public TextFormatter.Change apply(TextFormatter.Change c) {

        if (c.getControlNewText().isEmpty()) {
            return null;
        }

        if (c.getControlNewText().matches("[0-9]+")) {
            return c;
        }
        return null;

    }

}
