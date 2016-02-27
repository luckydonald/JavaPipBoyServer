package de.luckydonald.pipboyserver.PipBoyServer.types;

import de.luckydonald.pipboyserver.Messages.DataUpdate;
import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.ParserException;

/**
 * All {@link DBEntry}s not containing references to other entries.
 * See {@link DBContainer} for those.
 *
 * @author luckydonald
 * @since 12.02.2016
 */
public abstract class DBSimple<T> extends DBEntry {

    public DBSimple(Database db) {
        super(db);
    }
    /**
     * Parses and applies the {@code} value from a given {@code String}.
     *
     * @param s The string to parse.
     * @return the updated {@link DBEntry} entry.
     * @throws ParserException the parser failed.
     */
    abstract public DBSimple setValueFromString(String s) throws ParserException;
    T value = null;
    public void setValue(T value) {
        getLogger().finest("locking DB: write");
        this.getDatabase().getEntriesLock().writeLock().lock();
        this.value = value;
        this.getDatabase().getEntriesLock().writeLock().unlock();
        getLogger().finest("unlocked DB: write");
        this.getDatabase().queueDataUpdate(new DataUpdate(this));
    }
    public T getValue() {
        return this.value;
    }
}
