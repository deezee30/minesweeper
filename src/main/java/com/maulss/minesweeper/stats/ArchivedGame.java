/*
 * Part of minesweeper.
 */

package com.maulss.minesweeper.stats;

import com.maulss.minesweeper.GameSettings;
import com.maulss.minesweeper.MineGame;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ArchivedGame implements Serializable {

    private final GameSettings settings;
    private final long date;
    private final long time;
    private final TimeUnit unit;

    public ArchivedGame(final MineGame game) {
        this(game.getSettings(), System.currentTimeMillis(), game.getTime(), TimeUnit.SECONDS);
    }

    public ArchivedGame(final GameSettings settings,
                        final long date,
                        final long time,
                        final TimeUnit unit) {
        this.settings = settings;
        this.date = date;
        this.time = time;
        this.unit = unit;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public Date getDate() {
        return new Date(date);
    }

    public long getTime(final TimeUnit unit) {
        return unit.convert(time, TimeUnit.SECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArchivedGame that = (ArchivedGame) o;
        return time == that.time &&
                Objects.equals(settings, that.settings) &&
                Objects.equals(date, that.date) &&
                unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(settings, date, time, unit);
    }

    @Override
    public String toString() {
        return "ArchivedGame{" +
                "settings=" + settings +
                ", date=" + date +
                ", time=" + time +
                ", unit=" + unit +
                '}';
    }
}