package de.luckydonald.pipboyserver.PipBoyServer;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class DBEntry {
    private Integer id = null;
    private Database database = null;
    boolean dirty = true;
    public EntryType getType() {
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
        content.put(this.getType().getByte()); // type
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
                "}";
    }

    /**
     * {@inheritDoc #toSimpleString}
     * @return
     */
    public String toSimpleString() {
        return toSimpleString(true);
    }

    /**
     * Presents the data in a simple non-recursive way, printing the ids instead.
     * This should only differ to the {@link #toString()} for {@link DBList} and {@link DBDict}.
     * @param showId
     * @return
     */
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

    /**********************************************************
     /* Public API, straight value access
     /**********************************************************
     */

    /**
     * @return True if this node represents a numeric JSON value, ignoring the size.
     */
    public final boolean isNumber() {
        switch (getType()) {
            case INT8:
            case INT32:
            case FLOAT:
                return true;
            default:
                return false;
        }
    }

    /**
     * @return True if this node represents a non-integral
     *   numeric JSON value
     */
    public boolean isFloatingPointNumber() {
        return getType() == EntryType.FLOAT;
    }


    // OMITED:
    // * Note, however, that even if this method returns false, it
    // * is possible that conversion would be possible from other numeric
    // * types -- to check if this is possible, use
    // * {@link #canConvertToInt()} instead.
    // OMITED!

    /**
     * Method that can be used to check whether contained value
     * is a number represented as Java <code>int</code>.
     *
     * @return True if the value contained by this node is stored as Java int
     */
    public boolean isInt() { return getType() == EntryType.INT32; }

    /**
     * Method that checks whether this node represents basic JSON String
     * value.
     */
    public final boolean isTextual() {
        return getType() == EntryType.STRING;
    }

    /**
     * Method that can be used to check if this node was created from
     * JSON boolean value (literals "true" and "false").
     */
    public final boolean isBoolean() {
        return getType() == EntryType.BOOLEAN;
    }

    /**
     * Method to use for accessing String values.
     * Does <b>NOT</b> do any conversions for non-String value nodes;
     * for non-String values (ones for which {@link #isTextual} returns
     * false) null will be returned.
     * For String values, null is never returned (but empty Strings may be)
     *
     * @return Textual value this node contains, iff it is a textual
     *   JSON node (comes from JSON String value entry)
     */
    public String textValue() { return null; }

    /**
     * Method to use for accessing JSON boolean values (value
     * literals 'true' and 'false').
     * For other types, always returns false.
     *
     * @return Textual value this node contains, iff it is a textual
     *   json node (comes from JSON String value entry)
     */
    public boolean booleanValue() { return false; }

    /**
     * Returns numeric value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true); otherwise
     * returns null
     *
     * @return Number value this node contains, if any (null for non-number
     *   nodes).
     */
    public Number numberValue() { return null; }

    /**
     * Returns 16-bit short value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to short operates.
     *
     * @return Short value this node contains, if any; 0 for non-number
     *   nodes.
     */
    public short shortValue() { return 0; }

    /**
     * Returns integer value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to int operates.
     *
     * @return Integer value this node contains, if any; 0 for non-number
     *   nodes.
     */
    public int intValue() { return 0; }

    /**
     * Returns 64-bit long value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to long operates.
     *
     * @return Long value this node contains, if any; 0 for non-number
     *   nodes.
     */
    public long longValue() { return 0L; }

    /**
     * Returns 32-bit floating value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.0.
     * For integer values, conversion is done using coercion; this means
     * that an overflow is possible for `long` values
     *
     * @return 32-bit float value this node contains, if any; 0.0 for non-number nodes.
     *
     * @since 2.2
     */
    public float floatValue() { return 0.0f; }

    /**
     * Returns 64-bit floating point (double) value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.0.
     * For integer values, conversion is done using coercion; this may result
     * in overflows with {@link BigInteger} values.
     *
     * @return 64-bit double value this node contains, if any; 0.0 for non-number nodes.
     *
     * @since 2.2
     */
    public double doubleValue() { return 0.0; }

}

/*
    DBBoolean
*/

class DBBoolean extends DBEntry {
    public static final EntryType TYPE = EntryType.BOOLEAN;
    private Boolean value = null;

    @Override
    public EntryType getType() {
        return TYPE;
    }

    public DBBoolean(boolean bool) {
        this(null, bool);
    }
    public DBBoolean(Database db, boolean bool) {
        super(db);
        this.value = bool;
    }

    public byte[] getBytes() {
        ByteBuffer content = ByteBuffer.allocate(1 + 4 + 1); //type=1, id=4, bool=1
        content.order(ByteOrder.LITTLE_ENDIAN);
        content.put(this.getType().getByte());
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

    /**
     * Method to use for accessing JSON boolean values (value
     * literals 'true' and 'false').
     * For other types, always returns false.
     *
     * @return Textual value this node contains, iff it is a textual
     * json node (comes from JSON String value entry)
     */
    @Override
    public boolean booleanValue() {
        return this.getValue();
    }
}

/*
    DBInteger8
*/

class DBInteger8 extends DBEntry {
    public static final EntryType TYPE = EntryType.INT8;
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
    public EntryType getType() {
        return TYPE;
    }

    public byte[] getBytes_() {
        ByteBuffer content = ByteBuffer.allocate(1 + 4 + 1); //type=1, id=4, bool=1
        content.order(ByteOrder.LITTLE_ENDIAN);
        content.put(this.getType().getByte());
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

    /**
     * Returns numeric value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true); otherwise
     * returns null
     *
     * @return Number value this node contains, if any (null for non-number
     * nodes).
     */
    @Override
    public Number numberValue() {
        return (this.getValue());
    }

    /**
     * Returns 16-bit short value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to short operates.
     *
     * @return Short value this node contains, if any; 0 for non-number
     * nodes.
     */
    @Override
    public short shortValue() {
        return ((short) this.getValue());
    }

    /**
     * Returns integer value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to int operates.
     *
     * @return Integer value this node contains, if any; 0 for non-number
     * nodes.
     */
    @Override
    public int intValue() {
        return ((int) this.getValue());
    }

    /**
     * Returns 64-bit long value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to long operates.
     *
     * @return Long value this node contains, if any; 0 for non-number
     * nodes.
     */
    @Override
    public long longValue() {
        return ((long) this.getValue());
    }

    /**
     * Returns 32-bit floating value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.0.
     * For integer values, conversion is done using coercion; this means
     * that an overflow is possible for `long` values
     *
     * @return 32-bit float value this node contains, if any; 0.0 for non-number nodes.
     * @since 2.2
     */
    @Override
    public float floatValue() {
        return ((float) this.getValue());
    }

    /**
     * Returns 64-bit floating point (double) value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.0.
     * For integer values, conversion is done using coercion; this may result
     * in overflows with {@link BigInteger} values.
     *
     * @return 64-bit double value this node contains, if any; 0.0 for non-number nodes.
     * @since 2.2
     */
    @Override
    public double doubleValue() {
        return ((double) this.getValue());
    }
}

/*
    DBInteger32
*/

class DBInteger32 extends DBEntry {
    public static final EntryType TYPE = EntryType.INT32;
    private int value;

    public DBInteger32(Database db, int i) {
        super(db);
        this.value = i;
    }

    @Override
    public EntryType getType() {
        return TYPE;
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
        content.put(this.getType().getByte());
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

    /**
     * Returns numeric value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true); otherwise
     * returns null
     *
     * @return Number value this node contains, if any (null for non-number
     * nodes).
     */
    @Override
    public Number numberValue() {
        return ((Number) this.getValue());
    }

    /**
     * Returns 16-bit short value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to short operates.
     *
     * @return Short value this node contains, if any; 0 for non-number
     * nodes.
     */
    @Override
    public short shortValue() {
        return ((short) this.getValue());
    }

    /**
     * Returns integer value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to int operates.
     *
     * @return Integer value this node contains, if any; 0 for non-number
     * nodes.
     */
    @Override
    public int intValue() {
        return (this.getValue());
    }

    /**
     * Returns 64-bit long value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to long operates.
     *
     * @return Long value this node contains, if any; 0 for non-number
     * nodes.
     */
    @Override
    public long longValue() {
        return ((long) this.getValue());
    }

    /**
     * Returns 32-bit floating value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.0.
     * For integer values, conversion is done using coercion; this means
     * that an overflow is possible for `long` values
     *
     * @return 32-bit float value this node contains, if any; 0.0 for non-number nodes.
     * @since 2.2
     */
    @Override
    public float floatValue() {
        return ((float) this.getValue());
    }

    /**
     * Returns 64-bit floating point (double) value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.0.
     * For integer values, conversion is done using coercion; this may result
     * in overflows with {@link BigInteger} values.
     *
     * @return 64-bit double value this node contains, if any; 0.0 for non-number nodes.
     * @since 2.2
     */
    @Override
    public double doubleValue() {
        return ((double) this.getValue());
    }
}

class DBFloat extends DBEntry {
    public static final EntryType TYPE = EntryType.FLOAT;
    private float value;
    public DBFloat(int value){
        this(null, value);
    }
    public DBFloat(Database db, int value) {
        this(db, (float) value);
    }
    public DBFloat(double value) {
        this(null, value);
    }
    public DBFloat(Database db, double value) {
        this(db, (float) value);
    }
    public DBFloat(float value) {
        this(null, value);
    }
    public DBFloat(Database db, float value) {
        super(db);
        this.value = value;
    }

    @Override
    public EntryType getType() {
        return TYPE;
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

    /**
     * Returns 64-bit floating point (double) value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.0.
     * For integer values, conversion is done using coercion; this may result
     * in overflows with {@link BigInteger} values.
     *
     * @return 64-bit double value this node contains, if any; 0.0 for non-number nodes.
     * @since 2.2
     */
    @Override
    public double doubleValue() {
        return ((double) this.getValue());
    }

    /**
     * Returns integer value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to int operates.
     *
     * @return Integer value this node contains, if any; 0 for non-number
     * nodes.
     */
    @Override
    public int intValue() {
        return ((int) this.getValue());
    }

    /**
     * Returns 64-bit long value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to long operates.
     *
     * @return Long value this node contains, if any; 0 for non-number
     * nodes.
     */
    @Override
    public long longValue() {
        return ((long) this.getValue());
    }

    /**
     * Returns 32-bit floating value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.0.
     * For integer values, conversion is done using coercion; this means
     * that an overflow is possible for `long` values
     *
     * @return 32-bit float value this node contains, if any; 0.0 for non-number nodes.
     * @since 2.2
     */
    @Override
    public float floatValue() {
        return (this.getValue());
    }

    /**
     * Returns 16-bit short value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true). For other
     * types returns 0.
     * For floating-point numbers, value is truncated using default
     * Java coercion, similar to how cast from double to short operates.
     *
     * @return Short value this node contains, if any; 0 for non-number
     * nodes.
     */
    @Override
    public short shortValue() {
        return ((short) this.getValue());
    }

    /**
     * Returns numeric value for this node, <b>if and only if</b>
     * this node is numeric ({@link #isNumber} returns true); otherwise
     * returns null
     *
     * @return Number value this node contains, if any (null for non-number
     * nodes).
     */
    @Override
    public Number numberValue() {
        return ((Number) this.getValue());
    }
}

class DBString extends DBEntry {
    public static final EntryType TYPE = EntryType.STRING;
    private String value;

    public DBString(Database db, String value) {
        super(db);
        this.value = value;
    }

    @Override
    public EntryType getType() {
        return TYPE;
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

    /**
     * Method to use for accessing String values.
     * Does <b>NOT</b> do any conversions for non-String value nodes;
     * for non-String values (ones for which {@link #isTextual} returns
     * false) null will be returned.
     * For String values, null is never returned (but empty Strings may be)
     *
     * @return Textual value this node contains, iff it is a textual
     * JSON node (comes from JSON String value entry)
     */
    @Override
    public String textValue() {
        return this.getValue();
    }
}

class DBContainer extends DBEntry{
    public DBContainer(Database db) {
        super(db);
    }

    public DBContainer() {
    }
}

class DBList extends DBContainer {
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

    public DBList append(int id) {
        return this.append(this.getDatabase().get(id));
    }
    public DBList append(DBEntry entry) {
        //TODO: update notification
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

}

class DBDict extends DBContainer {
    public static final EntryType TYPE = EntryType.DICT;
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
    public EntryType getType() {
        return TYPE;
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
        //todo UPDATEs
        this.getDatabase().entriesLock.writeLock().lock();
        if (entry.getDBEntry() == null) {
            throw new NullPointerException("entry.getDBEntry() is null. Did you create it with a DBEntry?");
        }
        DBDict result = this.add(entry.name, entry.getDBEntry());
        this.getDatabase().entriesLock.writeLock().unlock();
        return result;
    }

    /**
     * Adds a element to the array.
     *
     * DB-Thread-safe
     * @param key The key.
     * @param value The {@link DBEntry}.
     * @returns itself, the {@link DBDict}.
     */
    public DBDict add(String key, DBEntry value) {
        //TODO: update events
        this.getDatabase().entriesLock.writeLock().lock();
        this.data.put(key, value);
        this.inserts.put(key, value);
        synchronized (this.getDatabase().entriesLock.writeLock()) {

        }
        this.getDatabase().entriesLock.writeLock().unlock();
        return this;
    }
    public DBDict remove(DictEntry entry) {
        return this.remove(entry.getName());
    }

    /**
     * removes a Entry by its name.
     *
     * DB-Thread-safe
     *
     * @param key
     * @return
     */
    public DBDict remove(String key) {
        //TODO: update events
        this.getDatabase().entriesLock.writeLock().lock();
        DBEntry deleted = this.data.get(key);
        this.data.remove(key);
        this.inserts.remove(key);
        this.removes.add(deleted);
        this.getDatabase().entriesLock.writeLock().unlock();
        return this;
    }

    /**
     * Removes a Entry by a given id.
     *
     * DB-Thread-safe *
     * @param id the id of the element.
     * @returns itself
     */
    public DBDict remove(int id) {
        //TODO: update events
        this.getDatabase().entriesLock.writeLock().lock();
        DBEntry deleted = null;
        String del_key = null;
        for (Map.Entry<String, DBEntry> entry  : this.data.entrySet()) {
            if (entry.getValue().getID() == id) {
                deleted = entry.getValue();
                del_key = entry.getKey();
            }
        }
        if(deleted == null || del_key == null) {
            this.getDatabase().entriesLock.writeLock().unlock();
            throw new NullPointerException("Could not find ID in array.");
        }
        DBDict result = this.remove(del_key);
        this.getDatabase().entriesLock.writeLock().unlock();
        return result;
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
        StringBuilder s = new StringBuilder("DBDict(");
        if (this.getID() <= 0){
            s.append("id=").append(this.getID()).append(", ");
        }
        s.append("data={");
        for (Map.Entry<String, DBEntry> entry  : this.data.entrySet()) {
            s.append("\"").append(entry.getKey()).append("\":").append(entry.getValue().toString()).append(", ");
        }
        s.append("}, inserts={");
        for (Map.Entry<String, DBEntry> entry  : this.inserts.entrySet()) {
            s.append("\"").append(entry.getKey()).append("\":").append(entry.getValue().toString()).append(", ");
        }
        s.append("}, removes=[");
        for (DBEntry entry  : this.removes) {
            s.append(entry.toString()).append(", ");
        }
        return s.append("]").toString();
    }

    @Override
    public String toSimpleString(boolean showID) {
        //TODO: showID
        StringBuilder sb = new StringBuilder("DBDict(");
        boolean notFirst = false;
        if (this.getID() <= 0){
            sb.append("id=").append(this.getID()).append(", ");
        }
        sb.append("data={");
        for (Map.Entry<String, DBEntry> entry  : this.data.entrySet()) {
            if (notFirst) {
                sb.append(", ");
            } else {
                notFirst = true;
            }
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue().getID());
        }
        sb.append("}, inserts={");
        notFirst = false;
        for (Map.Entry<String, DBEntry> entry  : this.inserts.entrySet()) {
            if (notFirst) {
                sb.append(", ");
            } else {
                notFirst = true;
            }
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue().getID());
        }
        sb.append("}, removes=[");
        notFirst = false;
        for (DBEntry entry  : this.removes) {
            if (notFirst) {
                sb.append(", ");
            } else {
                notFirst = true;
            }
            sb.append(entry.getID());
        }
        return sb.append("]").toString();
    }
}