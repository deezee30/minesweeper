/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public final class Minesweeper extends Application {

    private static final int ROWS = 16;
    private static final int COLUMS = 30;
    private static final int MINES = 99;
    public static final int SQUARE_SIZE = 30;

    private static MineField field = null;
    private static GridPane grid = null;
    private static Button face = new Button();

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();

        VBox box = new VBox(40);
        box.setAlignment(Pos.BOTTOM_CENTER);

        // Top section
        face.setMaxSize(48, 48);
        face.setMinSize(48, 48);
        face.setOnMouseClicked(event -> newGame());

        grid = new GridPane();
        StackPane.setAlignment(grid, Pos.BOTTOM_CENTER);
        grid.setAlignment(Pos.BOTTOM_CENTER);
        grid.setPadding(new Insets(5));

        newGame();

        box.getChildren().add(face);
        box.getChildren().add(grid);

        root.getChildren().add(box);

        Scene scene = new Scene(root, SQUARE_SIZE * COLUMS, SQUARE_SIZE * ROWS + 120);

        scene.getStylesheets().add("style.css");

        primaryStage.setTitle("Minesweeper");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static MineField getField() {
        if (field == null)
            throw new IllegalStateException("Minefield hasn't been set up yet");
        return field;
    }

    public static void newGame() {
        grid.getChildren().clear();
        field = new MineField(grid, COLUMS, ROWS, SQUARE_SIZE, MINES);
        face.setBackground(MineButtons.getAutoBackground(MineButtons.getImage("face_game.png")));
    }

    public static void win() {
        face.setBackground(MineButtons.getAutoBackground(MineButtons.getImage("face_win.png")));
    }

    public static void gameOver() {
        field.showGrid();
        face.setBackground(MineButtons.getAutoBackground(MineButtons.getImage("face_lose.png")));
    }

    public static Button getFace() {
        return face;
    }

    public static void main(String[] args) {
        // let JavaFX handle the launch
        launch(args);
    }
}