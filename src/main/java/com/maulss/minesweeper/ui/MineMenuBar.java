/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper.ui;

import com.maulss.minesweeper.GameSettings;
import com.maulss.minesweeper.Minesweeper;
import com.maulss.minesweeper.stats.ArchivedGames;
import com.maulss.minesweeper.stats.GameStats;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MineMenuBar extends MenuBar {

    public MineMenuBar(final Minesweeper minesweeper) {

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> minesweeper.newGame());
        MenuItem close = new MenuItem("Close");
        close.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });
        fileMenu.getItems().addAll(newGame, close);

        // Settings Menu
        Menu settingsMenu = new Menu("Settings");
        MenuItem beginner = new MenuItem("Beginner");
        beginner.setOnAction(event -> minesweeper.setup(GameSettings.BEGINNER));
        MenuItem intermediate = new MenuItem("Intermediate");
        intermediate.setOnAction(event -> minesweeper.setup(GameSettings.INTERMEDIATE));
        MenuItem expert = new MenuItem("Expert");
        expert.setOnAction(event -> minesweeper.setup(GameSettings.EXPERT));
        MenuItem professional = new MenuItem("Professional");
        professional.setOnAction(event -> minesweeper.setup(GameSettings.PROFESSIONAL));
        MenuItem custom = new MenuItem("Custom");
        custom.setOnAction(event -> {
            Dialog<GameSettings> dialog = new Dialog<>();
            dialog.setTitle("Custom Settings");
            dialog.setHeaderText("Select Custom Settings");

            DialogPane pane = dialog.getDialogPane();
            pane.getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

            IntegerInput columns = new IntegerInput();
            columns.setMaxInts(2);
            columns.setMaxWidth(150);
            columns.setPromptText("Columns");

            IntegerInput rows = new IntegerInput();
            rows.setMaxInts(2);
            rows.setMaxWidth(150);
            rows.setPromptText("Rows");

            IntegerInput mines = new IntegerInput();
            mines.setMaxInts(4);
            mines.setMaxWidth(150);
            mines.setPromptText("Mines");
            mines.setDisable(true);

            CheckBox auto = new CheckBox("Auto Choose Mines");
            auto.setSelected(true);
            Runnable recalculate = () -> {
                if (auto.isSelected()) {
                    Optional<Integer> suggest = GameSettings.suggestMines(
                            columns.getInput(),
                            rows.getInput()
                    );
                    suggest.ifPresent(mines::input);
                }
            };
            columns.onUpdate(recalculate);
            rows.onUpdate(recalculate);
            auto.setOnAction(event1 -> {
                mines.setDisable(!mines.isDisabled());
                recalculate.run();
            });

            pane.setContent(new VBox(8,
                    new HBox(10, columns, new Label("Columns")),
                    new HBox(10, rows, new Label("Rows")),
                    new HBox(10, mines, auto)
            ));

            Platform.runLater(columns::requestFocus);
            dialog.setResultConverter((ButtonType button) -> {
                if (button == ButtonType.APPLY) {
                    // check and process
                    try {
                        GameSettings.checkConditions(columns.getInput(), rows.getInput(), mines.getInput());
                    } catch (Exception e) {
                        Alert error = new Alert(Alert.AlertType.ERROR);

                        error.setTitle("Incorrect Settings");
                        error.setHeaderText("Please check your settings!");
                        error.setContentText(e.getMessage());

                        error.showAndWait();
                        return null;
                    }
                    return new GameSettings(columns.getInput(), rows.getInput(), mines.getInput());
                } else {
                    // cancel
                    return null;
                }
            });

            dialog.showAndWait().ifPresent(minesweeper::setup);
        });
        settingsMenu.getItems().addAll(beginner, intermediate, expert,
                professional, new SeparatorMenuItem(), custom);

        Menu statsMenu = new Menu("Stats");
        MenuItem viewStats = new MenuItem("View Stats");
        viewStats.setOnAction(event -> {
            GameStats stats = minesweeper.getStats();
            ArchivedGames games = stats.getGames();

            // Stats stage modal
            Stage statsStage = new Stage();

            VBox statsPage = new VBox(20);
            statsPage.setPadding(new Insets(20));

            GridPane grid = new GridPane();

            Label top = new Label("You haven't played any games");
            top.setFont(Font.font(20));
            top.setAlignment(Pos.CENTER);
            statsPage.getChildren().add(top);

            if (!games.isEmpty()) {
                top.setText("Recorded " + games.size() + " games");

                grid.setHgap(150);
                grid.setMinWidth(140);
                grid.setMaxWidth(Double.MAX_VALUE);

                grid.add(new Label("First Game"), 0, 0);
                grid.add(new Label(games.getStartDate().get().toString()), 1, 0);

                grid.add(new Label("Favourite Difficulty"), 0, 1);
                grid.add(new Label(games.getFavouriteDifficulty().get()), 1, 1);

                grid.add(new Label("Average Cells"), 0, 2);
                grid.add(new Label(games.getAverageCellCount() + " cells"), 1, 2);

                grid.add(new Label("Average Mines"), 0, 3);
                grid.add(new Label(games.getAverageMines() + " mines"), 1, 3);

                grid.add(new Label("Average Mine Density"), 0, 4);
                grid.add(new Label(String.valueOf(games.getAverageDensity())), 1, 4);

                grid.add(new Label("Average Time"), 0, 5);
                grid.add(new Label(games.getAverageTime(TimeUnit.SECONDS) + " seconds"), 1, 5);

                statsPage.getChildren().add(grid);
            }

            // Clear stats button
            Button clear = new Button("Clear Stats");
            GridPane.setHgrow(clear, Priority.ALWAYS);
            clear.setOnAction(event1 -> {
                try {
                    GameStats.writeJson(GameStats.GSON, new ArchivedGames());
                    // only clear cache once storage has successfully been cleared
                    games.clear();

                    // update UI
                    statsPage.getChildren().remove(grid);
                    top.setText("You haven't played any games");
                    clear.setDisable(true);
                    statsStage.sizeToScene();
                } catch (IOException e) {
                    Minesweeper.alertError(e);
                }
            });
            if (games.isEmpty()) clear.setDisable(true);

            // Open home data folder
            Button data = new Button("Data Folder");
            GridPane.setHgrow(data, Priority.ALWAYS);
            data.setOnAction(event1 -> GameStats.openDirectory(GameStats.HOME_PATH));

            HBox hBox = new HBox(20, clear, data);
            hBox.setAlignment(Pos.CENTER);
            statsPage.getChildren().add(hBox);

            statsStage.setTitle("Minesweeper stats");
            statsStage.getIcons().addAll(Resources.getImage("flag.png"));
            statsStage.initOwner(getScene().getWindow());
            statsStage.initModality(Modality.APPLICATION_MODAL);
            statsStage.setScene(new Scene(statsPage));
            statsStage.showAndWait();
        });
        statsMenu.getItems().addAll(viewStats);

        getMenus().addAll(fileMenu, settingsMenu, statsMenu);
    }
}