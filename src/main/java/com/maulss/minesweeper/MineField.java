/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class MineField {

    private MineGame game;

    private boolean generated = false;

    private final int width;
    private final int height;
    private final int mines;

    private MineButton[][] grid = null;
    private MineButton lastClick = null;

    private final List<MineButton> possibleMines;

    public MineField(final MineGame game,
                     final GameSettings settings) {
        this.game = Objects.requireNonNull(game, "game");

        this.width = settings.getColumns();
        this.height = settings.getRows();
        this.mines = settings.getMines();

        // temporary storage for assigning mines
        possibleMines = new ArrayList<>(width * height);
        grid = new MineButton[width][height];

        // assign each button their own position
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MineButton button = new MineButton(this, x, y, settings.getSquareSize());
                grid[x][y] = button;
                possibleMines.add(button);
                game.getPane().add(button.getButton(), x, y);
            }
        }
    }

    public void generate() {
        if (generated) throw new IllegalStateException("Already generated");

        // first of all, remove the possibility of a mine being
        // generated at the clicked button or any button adjacent to it
        possibleMines.remove(lastClick);
        possibleMines.removeAll(lastClick.getAdjacent());

        int length = possibleMines.size();

        // compute whether or not each button should be a mine
        Random random = new Random();
        for (int x = 0; x < length; x++) {
            if (x == mines) break;
            int rand = random.nextInt(length - x);
            MineButton button = possibleMines.get(rand);
            button.setMine(true);
            possibleMines.remove(button);
        }

        // release resources
        possibleMines.clear();

        // in order for the first click to be in an empty region,
        // the initial button's number value must be 0 (and not be a mine)
        lastClick.setNumber(0);

        // compute how many mines are around each button
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MineButton button = grid[x][y];
                // do not set the number for mines or initial button
                if (button.isMine() || button.equals(lastClick)) {
                    // do not compute number for mines
                    continue;
                }

                button.setNumber(button.getAdjacentMines());
            }
        }

        generated = true;
    }

    public boolean isGenerated() {
        return generated;
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

    public MineButton getLastClick() {
        return lastClick;
    }

    public MineGame getGame() {
        return game;
    }

    public void setLastClick(final MineButton lastClick) {
        this.lastClick = Objects.requireNonNull(lastClick, "lastClick");
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