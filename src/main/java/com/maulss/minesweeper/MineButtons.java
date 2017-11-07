/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper;

import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class MineButtons {

    private static final ClassLoader CLASS_LOADER = Thread
            .currentThread()
            .getContextClassLoader();

    private static final Color[] COLORS = {
            Color.TRANSPARENT,      // 0
            Color.BLUE,             // 1
            Color.GREEN,            // 2
            Color.RED,              // 3
            Color.NAVY,             // 4
            Color.DARKRED,          // 5
            Color.TURQUOISE,        // 6
            Color.BLACK,            // 7
            Color.GRAY              // 8
    };

    public static Color getColor(final int number) {
        if (number < 0 || number > 8)
            throw new IllegalArgumentException("Number must be between 0 and 8 inclusive");
        return COLORS[number];
    }

    public static Image getImage(final String resource) {
        return new Image(CLASS_LOADER.getResource(resource).toExternalForm());
    }

    public static Background getAutoBackground(final Image image) {
        return new Background(new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(
                        BackgroundSize.AUTO,
                        BackgroundSize.AUTO,
                        true,
                        true,
                        true,
                        false
                )
        ));
    }
}