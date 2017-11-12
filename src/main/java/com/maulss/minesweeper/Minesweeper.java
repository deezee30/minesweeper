/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public final class Minesweeper extends Application {

    private static final GameSettings SETTINGS = GameSettings.EXPERT;

    private GridPane grid = null;
    private MineGame game = null;

    private Label flags = new Label();
    private Button face = new Button();
    private Label time = new Label();

    @Override
    public void start(final Stage primaryStage) throws Exception {
        StackPane root = new StackPane();

        VBox box = new VBox(30);
        box.setAlignment(Pos.BOTTOM_CENTER);

        // Top section
        HBox topBox = new HBox(250);
        topBox.setAlignment(Pos.BOTTOM_CENTER);

        // Top left (flags left)
        flags = new Label(String.valueOf(SETTINGS.getMines()));
        flags.setPadding(new Insets(10d));
        flags.setFont(Font.font("Courier New", FontWeight.BLACK, 20));
        flags.setTextFill(Color.ORANGERED);
        flags.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        flags.setMinWidth(60);
        flags.setAlignment(Pos.CENTER);
        topBox.getChildren().add(flags);

        // Top middle (face)
        face.setMaxSize(48, 48);
        face.setMinSize(48, 48);
        face.setOnMouseClicked(event -> newGame());
        topBox.getChildren().add(face);

        // Top right (time)
        time.setPadding(new Insets(10d));
        time.setFont(Font.font("Courier New", FontWeight.BLACK, 20));
        time.setTextFill(Color.ORANGERED);
        time.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        time.setMinWidth(60);
        time.setAlignment(Pos.CENTER);
        topBox.getChildren().add(time);

        grid = new GridPane();
        StackPane.setAlignment(grid, Pos.BOTTOM_CENTER);
        grid.setAlignment(Pos.BOTTOM_CENTER);
        grid.setPadding(new Insets(5));

        newGame();

        box.getChildren().add(topBox);
        box.getChildren().add(grid);

        root.getChildren().add(box);

        Scene scene = new Scene(
                root,
                SETTINGS.getSquareSize() * SETTINGS.getColumns(),
                SETTINGS.getSquareSize() * SETTINGS.getRows() + 100
        );

        primaryStage.setTitle("Mineweeper");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (game != null) {
            game.finish();
        }
    }

    public void newGame() {
        if (game != null)
            game.finish();
        game = new MineGame(this, grid, SETTINGS);
    }

    public void setTime(final int seconds) {
        Platform.runLater(() -> time.setText(String.valueOf(seconds)));
    }

    public void setFlags(final int flags) {
        this.flags.setText(String.valueOf(flags));
    }

    public void setFace(final String resource) {
        face.setBackground(MineButtons.getAutoBackground(MineButtons.getImage(resource)));
    }

    public static void main(final String[] args) {
        // let JavaFX handle the launch
        launch(args);
    }
}