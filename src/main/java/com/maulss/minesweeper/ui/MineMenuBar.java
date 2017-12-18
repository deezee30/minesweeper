/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper.ui;

import com.maulss.minesweeper.GameSettings;
import com.maulss.minesweeper.Minesweeper;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class MineMenuBar extends MenuBar {

    public MineMenuBar(final Minesweeper minesweeper) {
        Menu fileMenu = new Menu("File");
        MenuItem newGame = new MenuItem("New game");
        newGame.setOnAction(event -> minesweeper.newGame());
        MenuItem close = new MenuItem("Close");
        close.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });
        fileMenu.getItems().addAll(newGame, close);

        Menu settingsMenu = new Menu("Settings");
        MenuItem beginner = new MenuItem("Beginner");
        beginner.setOnAction(event -> minesweeper.setup(GameSettings.BEGINNER));
        MenuItem intermediate = new MenuItem("Intermediate");
        intermediate.setOnAction(event -> minesweeper.setup(GameSettings.INTERMEDIATE));
        MenuItem expert = new MenuItem("Expert");
        expert.setOnAction(event -> minesweeper.setup(GameSettings.EXPERT));
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
                    new HBox(10, columns,   new Label("Columns")),
                    new HBox(10, rows,      new Label("Rows")),
                    new HBox(10, mines,     auto)
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
        settingsMenu.getItems().addAll(beginner, intermediate, expert, new SeparatorMenuItem(), custom);

        getMenus().addAll(fileMenu, settingsMenu);
    }
}