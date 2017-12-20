/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper.stats;

import com.maulss.minesweeper.GameSettings;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class ArchivedGames extends LinkedList<ArchivedGame> {

    public ArchivedGames() { }

    public ArchivedGames(final Collection<ArchivedGame> c) {
        super(c);
    }

    public Optional<String> getFavouriteDifficulty() {
        if (isEmpty()) return Optional.empty();

        Map<String, Integer> difficulties = new HashMap<>();
        for (ArchivedGame game : this) {
            String difficulty = game.getSettings().getDifficulty();
            if (difficulties.containsKey(difficulty)) {
                difficulties.replace(difficulty, difficulties.get(difficulty) + 1);
            } else {
                difficulties.put(difficulty, 1);
            }
        }

        Map.Entry<String, Integer> favourite = null;
        for (Map.Entry<String, Integer> entry : difficulties.entrySet()) {
            if (favourite == null || entry.getValue() > favourite.getValue()) {
                favourite = entry;
            }
        }

        return Optional.of(favourite.getKey());
    }

    public float getAverageMines() {
        float sum = 0f;
        float count = 0f;
        for (ArchivedGame game : this) {
            sum += game.getSettings().getMines();
            count++;
        }

        return sum / count;
    }

    public float getAverageDensity() {
        float sum = 0f;
        float count = 0f;
        for (ArchivedGame game : this) {
            sum += game.getSettings().getMineDensity();
            count++;
        }

        return sum / count;
    }

    public float getAverageCellCount() {
        float sum = 0f;
        float count = 0f;
        for (ArchivedGame game : this) {
            GameSettings settings = game.getSettings();
            sum += settings.getColumns() * settings.getRows();
            count++;
        }

        return sum / count;
    }

    public float getAverageTime(final TimeUnit unit) {
        float sum = 0f;
        float count = 0f;
        for (ArchivedGame game : this) {
            sum += game.getTime(unit);
            count++;
        }

        return sum / count;
    }

    public Optional<Date> getStartDate() {
        if (isEmpty()) return Optional.empty();

        long start = Long.MAX_VALUE;
        for (ArchivedGame game : this) {
            long time = game.getDate().getTime();
            if (time < start) {
                start = time;
            }
        }

        return Optional.of(new Date(start));
    }
}