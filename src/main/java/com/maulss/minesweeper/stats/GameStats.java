/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper.stats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maulss.minesweeper.Minesweeper;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public final class GameStats {

    public static final String HOME_PATH = System.getProperty("user.home") + "/Documents/minesweeper";
    public static final String DATA_PATH = HOME_PATH + "/games.json";
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static GameStats instance = null;

    private ArchivedGames games = null;

    private GameStats() {
        try {
            setupStorage();
            games = readJson(GSON);
        } catch (IOException e) {
            Minesweeper.alertError(e);
        }
    }

    public ArchivedGames getGames() {
        return games;
    }

    public static void setupStorage() throws IOException {
        File main = new File(HOME_PATH);
        if (!main.exists())
            main.mkdirs();

        if (new File(DATA_PATH).createNewFile())
            Files.write(
                    Paths.get(DATA_PATH),
                    Collections.singletonList("[]"),
                    Charset.forName("UTF-8")
            );
    }

    public static void writeJson(final Gson gson,
                                 final ArchivedGames games) throws IOException {
        try (Writer writer = new FileWriter(DATA_PATH)) {
            gson.toJson(games, writer);
        }
    }

    public static ArchivedGames readJson(final Gson gson) throws IOException {
        try (Reader reader = new FileReader(DATA_PATH)) {
            return gson.fromJson(reader, ArchivedGames.class);
        }
    }

    public static void openDirectory(final String path) {
        try {
            Desktop.getDesktop().open(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameStats getInstance() {
        return instance == null
                ? instance = new GameStats()
                : instance;
    }
}