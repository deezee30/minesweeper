/*
 * Part of minesweeper.
 * Made on 18/12/2017
 */

package com.maulss.minesweeper.ui;

import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public final class Resources {

    private static final ClassLoader CLASS_LOADER = Thread
            .currentThread()
            .getContextClassLoader();

    public static final Background
            MINE        = getAutoBackground(getImage("mine.png")),
            MINE_WRONG  = getAutoBackground(getImage("mine_wrong.png")),
            MINE_SOURCE = getAutoBackground(getImage("mine_source.png")),
            FLAG        = getAutoBackground(getImage("flag.png")),
            DEFAULT_BG  = new Background(new BackgroundFill(Color.WHITE, null, null));

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

    private Resources() { }
}