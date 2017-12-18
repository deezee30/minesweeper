/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper;

import javafx.scene.layout.GridPane;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public final class MineGame {

    private boolean started = false;
    private boolean finished = false;
    private final Minesweeper minesweeper;
    private final GameSettings settings;
    private final MineField field;
    private final GridPane pane;
    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private int seconds = 0;
    private int flagsLeft;

    public MineGame(final Minesweeper minesweeper,
                    final GridPane pane,
                    final GameSettings settings) {
        this.minesweeper = requireNonNull(minesweeper, "minesweeper");
        this.settings = requireNonNull(settings, "settings");
        this.pane = requireNonNull(pane, "pane");

        pane.getChildren().clear();
        minesweeper.setFlags(flagsLeft = settings.getMines());
        minesweeper.setTime(0);

        field = new MineField(this, settings);

        minesweeper.setFace("face_game.png");
    }

    public void start() {
        if (finished) throw new IllegalStateException("Game has already finished");
        if (started) throw new IllegalStateException("Game has already started");

        started = true;
        field.generate();
        timer.scheduleAtFixedRate(() -> minesweeper.setTime(seconds++), 0L, 1L, TimeUnit.SECONDS);
    }

    public void win() {
        finish();
        minesweeper.setFace("face_win.png");
    }

    public void lose() {
        finish();
        field.showGrid();
        minesweeper.setFace("face_lose.png");
    }

    public void finish() {
        if (finished) return;

        finished = true;
        timer.shutdown();
    }

    public boolean hasStarted() {
        return started;
    }

    public boolean hasFinished() {
        return finished;
    }

    public Minesweeper getMinesweeper() {
        return minesweeper;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public MineField getField() {
        return field;
    }

    public GridPane getPane() {
        return pane;
    }

    public int getTime() {
        return seconds;
    }

    public int getFlagsLeft() {
        return flagsLeft;
    }

    public void adjustFlagsLeft(final int adjust) {
        flagsLeft += adjust;
        minesweeper.setFlags(flagsLeft);
    }
}