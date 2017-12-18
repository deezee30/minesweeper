/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper;

import com.maulss.minesweeper.ui.MineMenuBar;
import com.maulss.minesweeper.ui.Resources;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public final class Minesweeper extends Application {

    private Stage primaryStage = null;
    private final StackPane root = new StackPane();
    private GridPane grid = null;
    private MineGame game = null;
    private GameSettings settings = GameSettings.EXPERT;

    private HBox topBox = new HBox();
    private Label flags = new Label();
    private Button face = new Button();
    private Label time = new Label();

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        VBox box = new VBox(30);

        MenuBar menu = new MineMenuBar(this);

        // Top section
        topBox.setAlignment(Pos.BOTTOM_CENTER);

        // Top left (flags left)
        flags = new Label(String.valueOf(settings.getMines()));
        flags.setPadding(new Insets(10));
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
        time.setPadding(new Insets(10));
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

        box.getChildren().add(menu);
        box.getChildren().add(topBox);
        box.getChildren().add(grid);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxWidth(1500);
        scrollPane.setMaxHeight(1000);
        scrollPane.setContent(box);

        root.getChildren().add(scrollPane);

        setup(settings);

        primaryStage.setTitle("Mineweeper");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void setup(final GameSettings settings) {
        this.settings = settings;
        topBox.setSpacing((2.5f * settings.getColumns() * settings.getSquareSize()) / 10f);

        newGame();

        primaryStage.sizeToScene();
    }

    public void newGame() {
        if (game != null)
            game.finish();
        game = new MineGame(this, grid, settings);
    }

    public void setTime(final int seconds) {
        Platform.runLater(() -> time.setText(String.valueOf(seconds)));
    }

    public void setFlags(final int flags) {
        this.flags.setText(String.valueOf(flags));
    }

    public void setFace(final String faceResource) {
        face.setBackground(Resources.getAutoBackground(Resources.getImage(faceResource)));
    }

    public static void main(final String[] args) {
        // let JavaFX handle the launch
        launch(args);
    }
}