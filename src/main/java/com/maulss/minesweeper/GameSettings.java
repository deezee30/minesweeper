/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper;

import javafx.beans.NamedArg;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public final class GameSettings implements Serializable {

    private static final int SQUARE_SIZE = 30;

    public static final GameSettings
            BEGINNER        = new GameSettings(9,   9, 10),
            INTERMEDIATE    = new GameSettings(16, 16, 40),
            EXPERT          = new GameSettings(30, 16, 99);

    private final int columns;
    private final int rows;
    private final int mines;
    private final int squareSize;

    public GameSettings(final int columns,
                        final int rows) {
        this(columns, rows, suggestMines(columns, rows).get());
    }

    public GameSettings(final int columns,
                        final int rows,
                        final int mines) {
        checkConditions(columns, rows, mines);

        this.columns = columns;
        this.rows = rows;
        this.mines = mines;
        squareSize = SQUARE_SIZE;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getMines() {
        return mines;
    }

    public int getSquareSize() {
        return squareSize;
    }

    public float getMineDensity() {
        return ((float) mines) / ((float) columns * rows);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSettings that = (GameSettings) o;
        return columns      == that.columns &&
                rows        == that.rows &&
                mines       == that.mines &&
                squareSize  == that.squareSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns, rows, mines, squareSize);
    }

    @Override
    public String toString() {
        return "GameSettings{"
                + "columns="        + columns
                + ", rows="         + rows
                + ", mines="        + mines
                +", squareSize="    + squareSize
                +'}';
    }

    public static void checkConditions(final Integer columns,
                                       final Integer rows,
                                       final Integer mines) {
        if (columns == null)
            throw new NullPointerException("Number of columns isn't provided");
        if (rows == null)
            throw new NullPointerException("Number of rows isn't provided");
        if (mines == null)
            throw new NullPointerException("Number of mines isn't provided");
        if (rows > 99 || columns > 99)
            throw new IllegalArgumentException("Grid too big!");
        if (mines >= columns * rows - 9)
            throw new IllegalArgumentException("Too many mines!");
    }

    public static Optional<Integer> suggestMines(final Integer columns,
                                                 final Integer rows) {
        if (columns == null || rows == null || columns < 9 || rows < 9)
            return Optional.empty();
        return Optional.of(suggestMines((columns) * (rows) - 8));
    }

    public static int suggestMines(@NamedArg("maxCells") final int x) {
        // Polynomial equation for mine suggestion generated from line of best fit
        // y = 10598.52 + (1.130322 - 10598.52)/(1 + (x/18173.86)^1.284253)
        return (int) Math.round(10598.52 - 10597.39 / (1 + Math.pow((float) x / 18173.86, 1.284)));
    }
}