package de.luckydonald.pipboyserver.PipBoyServer.types;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.EntryType;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyInsertedException;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyTakenException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DBList extends DBContainer {
    public static final EntryType TYPE = EntryType.LIST;
    private List<DBEntry> value;

    public DBList() {
        this(null);
    }
    public DBList(Database db) {
        super(db);
        this.value = new ArrayList<>();
    }
    public DBList(Database db, DBEntry item) {
        this(db);
        this.value.add(item);
    }
    public DBList(Database db, List<DBEntry> value) {
        this(db);
        this.value = value;
    }
    /*public DBList add(DBEntry item) {
        this.value.add(item);
        return this;
    }*/

    @Override
    public EntryType getType() {
        return TYPE;
    }

    @Override
    public ByteBuffer putValueIntoBuffer(ByteBuffer b) {
        b.putShort((short) this.value.size());
        for (DBEntry item : this.value) {
            b.putInt(item.getID());
        }
        return b;
    }

    @Override
    public int getRequiredValueBufferLength() {
        //uint16 count; // 2
        //uint32[] ids; // 4*count
        return 2 + (this.value.size() * 4);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("DBList[");
        boolean notFirst = false;
        for (DBEntry item : this.value) {
            if (notFirst) {
                s.append(", ");
            } else {
                notFirst = true;
            }
            s.append(item.toString());
        }
        return s.append("]").toString();
    }

    public DBList append(int id) throws AlreadyTakenException, AlreadyInsertedException {
        return this.append(this.getDatabase().get(id));
    }

    /**
     * Appends the specified {@link DBEntry} element to the end of this list
     *
     * Note: DB-Thread-safe
     *
     * @param entry the element to add.
     * @return itself.
     * @throws AlreadyInsertedException See {@link DBContainer#addNewEntryToDB}.
     * @throws AlreadyTakenException See {@link DBContainer#addNewEntryToDB}.
     * @throws NullPointerException if the specified element is null
     */
    public DBList append(DBEntry entry) throws AlreadyInsertedException, AlreadyTakenException {
        //TODO: update notification
        if (entry == null) {
            throw new NullPointerException();
        }
        getLogger().finest("locking DB: write");
        this.getDatabase().getEntriesLock().writeLock().lock();
        addNewEntryToDB(entry);
        this.value.add(entry);
        this.dirty = true;
        this.getDatabase().getEntriesLock().writeLock().unlock();
        getLogger().finest("unlocked DB: write");
        return this;
    }
    /**
     * Returns the {@link DBEntry} at the specified position in this {@link DBList}.
     *
     * Lookup is done DB-Thread-safe
     *
     * @param index index of the element to return
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     * @return the element at the specified position in this list
     */
    public DBEntry get(int index) {
        getLogger().finest("locking DB: read");
        this.getDatabase().getEntriesLock().readLock().lock();
        DBEntry dbEntry = this.value.get(index);
        this.getDatabase().getEntriesLock().readLock().unlock();
        getLogger().finest("unlocked DB: read");
        return dbEntry;
    }

    @Override
    public String toSimpleString(boolean showID) {
        //TODO: showID
        StringBuilder sb = new StringBuilder("DBList[");
        boolean notFirst = false;
        for (DBEntry item : this.value) {
            if (notFirst) {
                sb.append(", ");
            } else {
                notFirst = true;
            }
            sb.append(item.getID());
        }
        return sb.append("]").toString();
    }

    public List<DBEntry> getValue() {
        return value;
    }

    public void setValue(List<DBEntry> value) {
        this.value = value;
        this.dirty = true;
    }

    @Override
    public boolean isDirty() {
        if (super.isDirty()) {
            return true;
        }
        for (DBEntry item : this.value) {
            if (item.isDirty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds each of our children to the {@link Database}.
     * (Via {@link DBEntry#addToDB(Database)})
     *
     * @param db The Database where we add to.
     * @return Itself
     * @throws AlreadyInsertedException See {@link DBEntry#addToDB(Database)}
     */
    @Override
    public DBContainer addChildrenToDB(Database db) throws AlreadyInsertedException {
        for (DBEntry entry : this.value) {
            entry.addToDB(db);
        }
        return this;
    }

    /**
     * Returns the {@link DBList} for this node, <b>if and only if</b>
     * this node is a {@code DBList}. For other
     * types returns {@code null}.
     *
     * @return {@link DBList} if any, {@code null} else.
     */
    @Override
    public DBList getDBList() {
        return this;
    }
}
