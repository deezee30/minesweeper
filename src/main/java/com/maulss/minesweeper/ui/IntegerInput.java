/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper.ui;

import javafx.scene.control.TextField;

public class IntegerInput extends TextField {

    private Integer maxInts = null;
    private Runnable update = null;

    public IntegerInput() {
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (maxInts != null && newValue.length() > maxInts) {
                setText(newValue.substring(0, maxInts));
            } else if (!newValue.matches("\\d*")) {
                setText(newValue.replaceAll("[^\\d]", ""));
            }

            if (update != null)
                update.run();
        });
    }

    public void onUpdate(final Runnable update) {
        this.update = update;
    }

    public void setMaxInts(final Integer maxInts) {
        this.maxInts = maxInts;
    }

    public void input(final Integer input) {
        if (input == null) return;
        setText(String.valueOf(input));
    }

    public Integer getInput() {
        String text = getText();
        return text == null || "".equals(text) ? null : Integer.parseInt(text);
    }
}