/*
 * Part of minesweeper.
 * Made on 11/11/2017
 */

package com.maulss.minesweeper;

import java.io.Serializable;
import java.util.Objects;

public final class GameSettings implements Serializable {

    private static final int SQUARE_SIZE = 30;

    public static final GameSettings
            BEGINNER        = new GameSettings(9,   9, 10),
            INTERMEDIATE    = new GameSettings(16, 16, 40),
            EXPERT          = new GameSettings(16, 30, 99);

    private final int rows;
    private final int columns;
    private final int mines;
    private final int squareSize;

    public GameSettings(final int rows,
                        final int columns,
                        final int mines) {
        if (mines >= rows * columns - 9)
            throw new IllegalArgumentException("Too many mines!");

        this.rows = rows;
        this.columns = columns;
        this.mines = mines;
        squareSize = SQUARE_SIZE;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getMines() {
        return mines;
    }

    public int getSquareSize() {
        return squareSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSettings that = (GameSettings) o;
        return rows == that.rows &&
                columns == that.columns &&
                mines == that.mines &&
                squareSize == that.squareSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rows, columns, mines, squareSize);
    }

    @Override
    public String toString() {
        return "GameSettings{"
                + "rows=" + rows
                + ", columns=" + columns
                + ", mines=" + mines
                + ", squareSize=" + squareSize
                + '}';
    }
}