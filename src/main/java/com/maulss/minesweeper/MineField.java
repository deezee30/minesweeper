/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper;

import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class MineField {

    private final int width;
    private final int height;
    private final int mines;

    private final MineButton[][] grid;

    public MineField(final GridPane pane,
                     final int width,
                     final int height,
                     final int size,
                     final int mines) {
        this.width = width;
        this.height = height;
        this.mines = mines;

        int length = width * height;

        // temporary storage for assigning mines
        List<MineButton> mineButtons = new ArrayList<>(length);
        grid = new MineButton[width][height];

        // assign each button their own position
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MineButton button = new MineButton(x, y, size);
                mineButtons.add(grid[x][y] = button);
                pane.add(button.getButton(), x, y);
            }
        }

        // compute whether or not each button should be a mine
        Random random = new Random();
        for (int x = 0; x < length; x++) {
            if (x == mines) break;
            int rand = random.nextInt(length - x);
            MineButton button = mineButtons.get(rand);
            button.setMine(true);
            mineButtons.remove(button);
        }

        // compute how many mines are around each button
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MineButton button = grid[x][y];
                if (button.isMine()) {
                    // do not compute number for mines
                    continue;
                }

                button.setNumber(button.getAdjacentMines(this));
            }
        }

        // test: show grid
        // showGrid();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMines() {
        return mines;
    }

    public MineButton[][] getGrid() {
        return grid;
    }

    public void showGrid() {
        if (grid == null) throw new IllegalStateException("Grid hasn't been set up yet");

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y].activate();
            }
        }
    }
}