package de.luckydonald.pipboyserver.PipBoyServer;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class DBEntry{
    private Integer id = null;
    private Database database = null;
    boolean dirty = false;
    public int getType() {
        throw new NotImplementedException();
    }
    public int getID(){
        return this.id;
    }
    public void _setID(int nextFreeInt) {
        this.id = nextFreeInt;
    }
    public void _setDatabase(Database db) {
        this.database = db;
    }
    public Database getDatabase(){
        if (database == null) {
            throw new NullPointerException("this.database is null");
        }
        return this.database;
    }

    /**
     * This generates a byte array, ready to be pushed out via a socket to a client.
     * @return the package.
     */
    public byte[] getBytes(){
        ByteBuffer content = ByteBuffer.allocate(this.getRequiredBufferLength());
        content.order(ByteOrder.LITTLE_ENDIAN);
        content.put((byte) this.getType()); // type
        content.putInt(this.getID());       // id
        this.putValueIntoBuffer(content);
        return content.array();
        //return new byte[]{(byte) this.getType()};
    }

    /**
     * This writes this package content into a given {@code ByteBuffer}, ready to sent to the client.
     * @param b the {@code ByteBuffer} to fill.
     * @return the {@code ByteBuffer}.
     */
    public ByteBuffer putValueIntoBuffer(ByteBuffer b) {
        //b.put(this.value);
        return b;
    }

    /**
     * Calculate the needed byte length for this data as a sendable package. Only the raw data length without overhead (type, id).
     *
     * This should be overridden by implementing classes.
     * @return
     */
    public int getRequiredValueBufferLength() {
        return 0;
    }

    /**
     * Calculate the total needed buffer size of this object. Includes the length required for type and id, additionally to {@code getRequiredValueBufferLength()}
     * @return
     */
    public int getRequiredBufferLength() {
        return 1 + 4 + this.getRequiredValueBufferLength(); // type = 1, id = 4
    }

    @Override
    public String toString() {
            return this.toString(true);
    }
    public String toString(boolean showId) {
            return "DBEntry{" +
                "type=" + this.getType() + ", " +
                (showId?"id=" + this.getID() + ", ": "") +
                "database=" + this.getDatabase() +
                "}";
    }
    public String toSimpleString() {
        return toSimpleString(true);
    }
    public String toSimpleString(boolean showId) {
        return toString(showId);
    }

    public DBEntry() {
        this(null);
    }
    public DBEntry(Database db) {
        this.database = db;
    }

    /**
     * Whether or not this value was changed but not yet pushed as update to the client.
     * @return if it is changed.
     */
    public boolean isDirty() {
        return this.dirty;
    }
}

/*
    DBBoolean
*/

class DBBoolean extends DBEntry {
    private Boolean value = null;

    @Override
    public int getType() {
        return 0;
    }

    public DBBoolean(Database db, boolean bool) {
        super(db);
        this.value = bool;
    }

    public byte[] getBytes() {
        ByteBuffer content = ByteBuffer.allocate(1 + 4 + 1); //type=1, id=4, bool=1
        content.order(ByteOrder.LITTLE_ENDIAN);
        content.put((byte) this.getType());
        content.put((byte) (this.value ? 1 : 0));
        return content.array();
    }

    @Override
    public ByteBuffer putValueIntoBuffer(ByteBuffer b) {
        b.put((byte) (this.value ? 1 : 0));
        return b;
    }

    @Override
    public int getRequiredValueBufferLength() {
        return 1; // bool=1
    }

    @Override
    public String toString(boolean showID) {
        if (!showID || this.getID() == -1 && this.getDatabase() == null) {
            return "DBBoolean("+ this.value + ")";
        }
        String s = "DBBoolean(";
        if (this.getID() != -1) {
            s += "id=" + this.getID() + ", ";
        }
        if (this.getDatabase() == null) {
            s += "database=" + this.getDatabase() + ", ";
        }
        return s + "value=" + value + ")";
    }
    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
        this.dirty = false;
    }

}

/*
    DBInteger8
*/

class DBInteger8 extends DBEntry {
    private byte value;

    public DBInteger8(Database db, int i) {
        this(db, (byte) i);
        if (i > 255 || i < 0) {
            throw new IllegalArgumentException("Smaller as 0 or bigger as 255.");
        }
    }
    public DBInteger8(Database db, byte i) {
        super(db);
        this.value = i;
    }

    @Override
    public int getType() {
        return 2;
    }

    public byte[] getBytes_() {
        ByteBuffer content = ByteBuffer.allocate(1 + 4 + 1); //type=1, id=4, bool=1
        content.order(ByteOrder.LITTLE_ENDIAN);
        content.put((byte) this.getType());
        content.put(this.value);
        return content.array();
    }

    @Override
    public int getRequiredValueBufferLength() {
        return 1; // int8 = 1
    }

    @Override
    public ByteBuffer putValueIntoBuffer(ByteBuffer b) {
        b.put(this.value);
        return b;
    }

    @Override
    public String toString(boolean showID) {
        if (!showID || this.getID() == -1 && this.getDatabase() == null) {
            return "DBInteger8(" + value + ")";
        }
        String s = "DBInteger8(";
        if (this.getID() != -1) {
            s += "id=" + this.getID() + ", ";
        }
        if (this.getDatabase() == null) {
            s += "database=" + this.getDatabase() + ", ";
        }
        return s + "value=" + value + ")";
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
        this.dirty = true;
    }
}

/*
    DBInteger32
*/

class DBInteger32 extends DBEntry {
    private int value;

    public DBInteger32(Database db, int i) {
        super(db);
        this.value = i;
    }

    @Override
    public int getType() {
        return 3;
    }

    public ByteBuffer putValueIntoBuffer(ByteBuffer b) {
        b.putInt(this.value);
        return b;
    }

    @Override
    public int getRequiredValueBufferLength() {
        return 4; //type=1, id=4,
    }

    public byte[] getBytes_() {
        ByteBuffer content = ByteBuffer.allocate(1 + 4 + 1); //type=1, id=4, bool=1
        content.order(ByteOrder.LITTLE_ENDIAN);
        content.put((byte) this.getType());
        content.putInt(this.value);
        return content.array();
    }

    @Override
    public String toString(boolean showID) {
        if (!showID || this.getID() == -1 && this.getDatabase() == null) {
            return "DBInteger32(" + value + ")";
        }
        String s = "DBInteger32(";
        if (this.getID() != -1) {
            s += "id=" + this.getID() + ", ";
        }
        if (this.getDatabase() == null) {
            s += "database=" + this.getDatabase() + ", ";
        }
        return s + "value=" + value + ")";

    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        this.dirty = true;
    }
}

class DBFloat extends DBEntry {
    private float value;

    public DBFloat(Database db, int value) {
        this(db, (float) value);
    }
    public DBFloat(Database db, double value) {
        this(db, (float) value);
    }
    public DBFloat(Database db, float value) {
        super(db);
        this.value = value;
    }

    @Override
    public int getType() {
        return 5;
    }

    @Override
    public ByteBuffer putValueIntoBuffer(ByteBuffer b) {
        b.putFloat(this.value);
        return b;
    }

    @Override
    public int getRequiredValueBufferLength() {
        return 4;
    }

    @Override
    public String toString(boolean showID) {
        if (!showID || this.getID() == -1 && this.getDatabase() == null) {
            return "DBFloat(" + value + ")";
        }
        String s = "DBFloat(";
        if (this.getID() != -1) {
            s += "id=" + this.getID() + ", ";
        }
        if (this.getDatabase() == null) {
            s += "database=" + this.getDatabase() + ", ";
        }
        return s + "value=" + value + ")";
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
        this.dirty = true;
    }
}

class DBString extends DBEntry {
    private String value;

    public DBString(Database db, String value) {
        super(db);
        this.value = value;
    }

    @Override
    public int getType() {
        return 6;
    }

    @Override
    public ByteBuffer putValueIntoBuffer(ByteBuffer b) {
        b.put(this.value.getBytes());
        b.put((byte) 0); // Null Termination
        return b;
    }

    @Override
    public int getRequiredValueBufferLength() {
        return this.value.length()+1; //+1 because Null-Termination.
    }

    @Override
    public String toString() {
        return "DBString(\"" + value + "\")";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.dirty = true;
    }
}

class DBList extends DBEntry {
    private List<DBEntry> value;

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
    public int getType() {
        return 7;
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
        String s = "DBList[";
        for (DBEntry item : this.value) {
            s += item.toString() + ", ";
        }
        s += "]";
        return s;
    }
    public DBList append(int id) {
        return this.append(this.getDatabase().get(id));
    }
    public DBList append(DBEntry entry) {
        //TODO: update notification
        this.value.add(entry);
        this.dirty = false;
        return this;
    }

    @Override
    public String toSimpleString(boolean showID) {
        //TODO: showID
        String s = "DBList[";
        for (DBEntry item : this.value) {
            s += item.getID() + ", ";
        }
        s += "]";
        return s;
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
}

class DBDict extends DBEntry {
    private HashMap<String, DBEntry> data;
    private HashMap<String, DBEntry> inserts;
    private List<DBEntry> removes;

    public DBDict(Database db) {
        super(db);
        this.data = new HashMap<>();
        this.inserts = new HashMap<>();
        this.removes = new ArrayList<>();
    }
    public DBDict(Database db, HashMap<String, DBEntry> data) {
        super(db);
        this.data = data;
        this.inserts = data;
        this.removes = new ArrayList<>();
    }
    public DBDict(Database db, String key, DBEntry data) {
        this(db);
        this.data.put(key, data);
        this.inserts.put(key, data);
    }

    @Override
    public int getType() {
        return 8;
    }

    @Override
    public ByteBuffer putValueIntoBuffer(ByteBuffer b) {
        //uint16_t insert_count; //2 //short
        //DictEntry[insert_count];
        //uint32_t id
        //char_t *name //null terminated
        //uint16_t remove_count;
        //uint32_t references[remove_count];
        int insertCount = this.inserts.size();
        b.putShort((short) insertCount);
        for (Map.Entry<String, DBEntry> entry  : this.inserts.entrySet()) {
            DictEntry e = new DictEntry(this.getDatabase(), entry.getKey(), entry.getValue());
            e.putValueIntoByteBuffer(b);
        }
        int removeCount = this.removes.size();
        b.putShort((short) removeCount);
        for (DBEntry entry  : this.removes) {
            b.putInt(entry.getID());
        }
        return b;
    }

    @Override
    public int getRequiredValueBufferLength() {
        int length = 2; // insert_count
        for (Map.Entry<String, DBEntry> entry  : this.inserts.entrySet()) {
            DBEntry ent = entry.getValue();
            length += entry.getKey().getBytes().length + 1; //name string .length + null terminator
            length += 4; // id = 4
        }
        length += 2; // remove_count
        length += (this.removes.size() * 4); //id = 4; n-th times
        return length;
    }

    public DBDict add(DictEntry entry) {
        if (entry.getDBEntry() == null) {
            throw new NullPointerException("entry.getDBEntry() is null. Did you create it with a DBEntry?");
        }
        return this.add(entry.name, entry.getDBEntry());
    }
    public DBDict add(String key, DBEntry value) {
        //TODO: update events
        this.data.put(key, value);
        this.inserts.put(key, value);
        return this;
    }
    public DBDict remove(DictEntry entry) {
        return this.remove(entry.getName());
    }
    public DBDict remove(String key) {
        //TODO: update events
        DBEntry deleted = this.data.get(key);
        this.data.remove(key);
        this.inserts.remove(key);
        this.removes.add(deleted);
        return this;
    }
    public DBDict remove(int id) {
        DBEntry deleted = null;
        String del_key = null;
        for (Map.Entry<String, DBEntry> entry  : this.data.entrySet()) {
            if (entry.getValue().getID() == id) {
                deleted = entry.getValue();
            }
        }
        if(deleted == null || del_key == null) {
            throw new NullPointerException("Could not find ID in array.");
        }
        return this.remove(del_key);
    }
    static class DictEntry {
        private int id; //uint32_t //int
        private String name;
        private DBEntry value = null;

        public DictEntry(String name, int id) {
            this.id = id;
            this.name = name;
        }
        public DictEntry(int id, String name) {
            this(name, id);
        }
        public DictEntry(Database db, String name, DBEntry entry){
            this.id = entry.getID();
            this.name = name;
            this.value = entry;
        }
        public DictEntry(Database db, DBEntry entry, String name) {
            this(db, name, entry);
        }
        public int getID() {
            return this.id;
        }
        public String getName() {
            return this.name;
        }
        public DBEntry getDBEntry() {
            return this.value;
        }
        public int getRequiredValueBufferLength() {
            return 4 + this.name.length() + 1; // id = 4, string = s+1 //null termination
        }
        public ByteBuffer putValueIntoByteBuffer(ByteBuffer b) {
            b.putInt(this.id);
            b.put(this.name.getBytes());
            b.put((byte) 0); //Null-Termination of string
            return b;
        }
    }

    @Override
    public String toString() {
        String s = "DBDict(";
        if (this.getID() <= 0){
            s+="id=" + this.getID() + ", ";
        }
        s += "data={";
        for (Map.Entry<String, DBEntry> entry  : this.data.entrySet()) {
            s += "\"" + entry.getKey() + "\":" + entry.getValue().toString() + ", ";
        }
        s += "}, inserts={";
        for (Map.Entry<String, DBEntry> entry  : this.inserts.entrySet()) {
            s += "\"" + entry.getKey() + "\":" + entry.getValue().toString() + ", ";
        }
        s += "}, removes=[";
        for (DBEntry entry  : this.removes) {
            s += entry.toString() + ", ";
        }
        s += "]";
        return s;
    }

    @Override
    public String toSimpleString(boolean showID) {
        //TODO: showID
        String s = "DBDict(";
        if (this.getID() <= 0){
            s+="id=" + this.getID() + ", ";
        }
        s += "data={";
        for (Map.Entry<String, DBEntry> entry  : this.data.entrySet()) {
            s += "\"" + entry.getKey() + "\":" + entry.getValue().getID() + ", ";
        }
        s += "}, inserts={";
        for (Map.Entry<String, DBEntry> entry  : this.inserts.entrySet()) {
            s += "\"" + entry.getKey() + "\":" + entry.getValue().getID() + ", ";
        }
        s += "}, removes=[";
        for (DBEntry entry  : this.removes) {
            s += entry.getID() + ", ";
        }
        s += "]";
        return s;
    }
}