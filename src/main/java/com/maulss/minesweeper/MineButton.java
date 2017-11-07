/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public final class MineButton {

    private static final Image MINE = MineButtons.getImage("mine.png");
    private static final Image MINE_WRONG = MineButtons.getImage("mine_wrong.png");
    private static final Image MINE_SOURCE = MineButtons.getImage("mine_source.png");
    private static final Image FLAG = MineButtons.getImage("flag.png");

    private static final Background MINE_BACKGROUND = MineButtons.getAutoBackground(MINE);
    private static final Background MINE_WRONG_BACKGROUND = MineButtons.getAutoBackground(MINE_WRONG);
    private static final Background MINE_SOURCE_BACKGROUND = MineButtons.getAutoBackground(MINE_SOURCE);
    private static final Background FLAG_BACKGROUND = MineButtons.getAutoBackground(FLAG);
    private static final Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));

    private static MineButton lastClicked = null;

    private final Button button;
    private final int x;
    private final int y;
    private boolean flagged = false;
    private boolean mine = false;
    private boolean visible = false;
    private Integer number = null;

    public MineButton(final int xPos,
                      final int yPos,
                      final int size) {
        this.x = xPos;
        this.y = yPos;

        button = new Button();
        button.setBackground(DEFAULT_BACKGROUND);
        BorderStroke stroke = new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, null, BorderStroke.THIN);
        button.setBorder(new Border(stroke, stroke, stroke, stroke));
        button.setMinSize(size, size);
        button.setMaxSize(size, size);
        button.setOnMouseClicked(event -> {
            if (visible) return;
            lastClicked = this;
            switch (event.getButton()) {
                default: return;
                case PRIMARY:
                    if (mine && !flagged) {
                        Minesweeper.gameOver();
                    } else {
                        activate();
                    }
                    break;
                case SECONDARY:
                    setFlagged(!isFlagged());
                    break;
            }
        });
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isMine() {
        return mine;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setMine(final boolean mine) {
        this.mine = mine;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public Button getButton() {
        return button;
    }

    public Integer getNumber() {
        if (number == null) throw new IllegalStateException(
                "(" + x + ", " + y + ") Does not have a number because it is a mine");

        return number;
    }

    public void setNumber(final int number) {
        if (mine) throw new IllegalArgumentException(
                "(" + x + ", " + y + ") Can not have a number because it is a mine");
        if (number > 8)
            throw new IllegalArgumentException("Number can not be greater than 8");
        this.number = number;
    }

    public Color getColor() {
        if (mine) throw new IllegalArgumentException(
                "(" + x + ", " + y + ") Can not have a color because it is a mine");
        return MineButtons.getColor(number);
    }

    public void setFlagged(final boolean flagged) {
        this.flagged = flagged;
        button.setBackground(flagged ? FLAG_BACKGROUND : DEFAULT_BACKGROUND);
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void activate() {
        if (visible) return;
        visible = true;

        if (mine) {
            if (equals(lastClicked)) {
                button.setBackground(MINE_SOURCE_BACKGROUND);
            } else if (!flagged) {
                button.setBackground(MINE_BACKGROUND);
            }
        } else {
            if (flagged) {
                button.setBackground(MINE_WRONG_BACKGROUND);
            } else {
                button.setBackground(null);

                if (number > 0) {
                    button.setText(String.valueOf(number));
                    button.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, 14));
                    button.setTextFill(getColor());
                }
            }

            MineField field = Minesweeper.getField();

            // expand region if there are no adjacent mines
            List<MineButton> adjacents = getAdjacent(field);
            boolean activate = true;
            for (MineButton adjacent : adjacents) {
                if (adjacent.mine) {
                    activate = false;
                    break;
                }
            }
            if (activate) {
                for (MineButton adjacent : adjacents) {
                    adjacent.activate();
                }
            }

            // check if the player won
            boolean won = true;
            for (int x = 0; x < field.getWidth(); x++) {
                for (int y = 0; y < field.getHeight(); y++) {
                    MineButton button = field.getGrid()[x][y];
                    if (!button.visible && !button.mine) {
                        won = false;
                    }
                }
            }
            if (won) Minesweeper.win();
        }
    }

    public List<MineButton> getAdjacent(final MineField field) {
        int width = field.getWidth();
        int height = field.getHeight();
        MineButton[][] grid = field.getGrid();

        MineButton top          = (                 y > 0         ) ? grid[x    ][y - 1] : null;
        MineButton topRight     = (x < width - 1 && y > 0         ) ? grid[x + 1][y - 1] : null;
        MineButton right        = (x < width - 1                  ) ? grid[x + 1][y    ] : null;
        MineButton bottomRight  = (x < width - 1 && y < height - 1) ? grid[x + 1][y + 1] : null;
        MineButton bottom       = (                 y < height - 1) ? grid[x    ][y + 1] : null;
        MineButton bottomLeft   = (x > 0         && y < height - 1) ? grid[x - 1][y + 1] : null;
        MineButton left         = (x > 0                          ) ? grid[x - 1][y    ] : null;
        MineButton topLeft      = (x > 0         && y > 0         ) ? grid[x - 1][y - 1] : null;

        List<MineButton> adjacent = new ArrayList<>(8);

        if (top         != null) adjacent.add(top);
        if (topRight    != null) adjacent.add(topRight);
        if (right       != null) adjacent.add(right);
        if (bottomRight != null) adjacent.add(bottomRight);
        if (bottom      != null) adjacent.add(bottom);
        if (bottomLeft  != null) adjacent.add(bottomLeft);
        if (left        != null) adjacent.add(left);
        if (topLeft     != null) adjacent.add(topLeft);

        return adjacent;
    }

    public int getAdjacentMines(final MineField field) {
        int adjacent = 0;
        for (MineButton button : getAdjacent(field))
            if (button.isMine())
                adjacent++;
        return adjacent;
    }
}