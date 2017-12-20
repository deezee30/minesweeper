/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper;

import com.maulss.minesweeper.ui.Resources;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MineButton {

    private final MineField field;
    private final Button button;
    private final int x;
    private final int y;
    private boolean flagged = false;
    private boolean mine = false;
    private boolean visible = false;
    private Integer number = null;

    public MineButton(final MineField field,
                      final int xPos,
                      final int yPos,
                      final int size) {
        this.field = Objects.requireNonNull(field, "field");
        this.x = xPos;
        this.y = yPos;

        button = new Button();
        button.setBackground(Resources.DEFAULT_BG);
        BorderStroke stroke = new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, null, BorderStroke.THIN);
        button.setBorder(new Border(stroke, stroke, stroke, stroke));
        button.setMinSize(size, size);
        button.setMaxSize(size, size);
        button.setOnMouseClicked(event -> {
            if (field.getGame().hasFinished() || visible) return;
            switch (event.getButton()) {
                default: return;
                case PRIMARY:
                	if (flagged) return;
                	if (field.getLastClick() == null) {
                        field.setLastClick(this);
                	    field.getGame().start();
                    } else {
                        field.setLastClick(this);
                    }
                    activate();
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
        field.getGame().adjustFlagsLeft(flagged ? -1 : 1);
        button.setBackground(flagged ? Resources.FLAG : Resources.DEFAULT_BG);
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void activate() {
        if (visible) return;
        visible = true;

        MineGame game = field.getGame();
        if (mine) {
            if (equals(field.getLastClick())) {
                game.lose();
                button.setBackground(Resources.MINE_SOURCE);
            } else if (!flagged) {
                button.setBackground(Resources.MINE);
            }
        } else {
            if (flagged && game.hasFinished()) {
                button.setBackground(Resources.MINE_WRONG);
            } else {
                button.setBackground(null);

                if (number > 0) {
                    button.setText(String.valueOf(number));
                    button.setPadding(new Insets(0));
                    button.setFont(Font.font("Courier New", FontWeight.BLACK, 18));
                    button.setTextFill(getColor());
                }
            }

            // expand region if there are no adjacent mines
            List<MineButton> adjacents = getAdjacent();
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
            if (!game.hasFinished()) {
                boolean won = true;
                for (int x = 0; x < field.getWidth(); x++) {
                    for (int y = 0; y < field.getHeight(); y++) {
                        MineButton button = field.getGrid()[x][y];
                        if (!button.visible && !button.mine) {
                            won = false;
                        }
                    }
                }
                if (won) game.win();
            }
        }
    }

    public List<MineButton> getAdjacent() {
        int width = field.getWidth();
        int height = field.getHeight();
        MineButton[][] grid = field.getGrid();

        //       | Position ---- | x-bound ------ | y-bound ------------ | x -- | y --
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

    public int getAdjacentMines() {
        int adjacent = 0;
        for (MineButton button : getAdjacent())
            if (button.isMine())
                adjacent++;
        return adjacent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MineButton that = (MineButton) o;
        return x == that.x &&
                y == that.y &&
                flagged == that.flagged &&
                mine == that.mine &&
                visible == that.visible &&
                Objects.equals(field, that.field) &&
                Objects.equals(button, that.button) &&
                Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, button, x, y, flagged, mine, visible, number);
    }

    @Override
    public String toString() {
        return "MineButton{" + "field=" + field
                + ", button=" + button
                + ", x=" + x
                + ", y=" + y
                + ", flagged=" + flagged
                + ", mine=" + mine
                + ", visible=" + visible
                + ", number=" + number
                + '}';
    }
}