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
            s.append(item.toString()).append(", ");
        }
        return s.append("]").toString();
    }

    public DBList append(int id) throws AlreadyTakenException, AlreadyInsertedException {
        return this.append(this.getDatabase().get(id));
    }
    public DBList append(DBEntry entry) throws AlreadyInsertedException, AlreadyTakenException {
        //TODO: update notification
        addNewEntryToDB(entry);
        this.value.add(entry);
        this.dirty = true;
        return this;
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
}
