package de.luckydonald.pipboyserver.PipBoyServer.types;

import de.luckydonald.pipboyserver.Messages.DataUpdate;
import de.luckydonald.pipboyserver.PipBoyServer.Database;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * All {@link DBEntry}s not containing references to other entries.
 * See {@link DBContainer} for those.
 *
 * @author luckydonald
 * @since 12.02.2016
 */
public class DBSimple<T> extends DBEntry {

    public DBSimple(Database db) {
        super(db);
    }
    /**
     * Parses and applies the value from a given string.
     *
     * @param s The string.
     * @return the updated {@link DBEntry entry} (type {@link DBSimple}).
     */
    public DBSimple setValueFromString(String s) {
        throw new NotImplementedException();
    }
    T value = null;
    void setValue(T value) {
        getLogger().finest("locking DB: write");
        this.getDatabase().getEntriesLock().writeLock().lock();
        this.value = value;
        this.getDatabase().getEntriesLock().writeLock().unlock();
        getLogger().finest("unlocked DB: write");
        this.getDatabase().queueDataUpdate(new DataUpdate(this));
    }
}
