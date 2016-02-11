package de.luckydonald.pipboyserver.PipBoyServer.types;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.EntryType;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyInsertedException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.logging.Logger;

public class DBEntry {
    private Logger logger = null;
    Logger getLogger() {
        if (this.logger == null) {
           this.logger =  Logger.getLogger(this.getClass().getCanonicalName());
        }
        return this.logger;
    }
    private Integer id = null;
    private Database database = null;
    boolean dirty = true;
    public EntryType getType() {
        throw new NotImplementedException();
    }
    public Integer getID(){
        return this.id;
    }
    public boolean hasID() {
        return this.id != null;
    }
    public DBEntry addToDB(Database db) throws AlreadyInsertedException {
        if (this.hasDatabase()) {
            // already is in some DB
            if (this.getDatabase() != db) {
                // that DB were in, is not the one we want to add it to.
                throw new AlreadyInsertedException("This Entry already is in some other database.");
            } else if (this.hasID()) {
                // we want to add us to the DB already set.
                //
                // So check whether the ids match.
                if (this.getDatabase().get(this.getID()) == this) { // Check ids
                    // already added in right DB, id here and in the DB are equal.
                    logger.info("Skipped adding to DB, as we are already are added correctly (IDs match).");
                } else {
                    throw new AlreadyInsertedException("This entry should already be inserted, but not at the stored id-location!");
                }
            } // else:
            // we are in the right DB, but don't have a ID set
            // will not throw anything, but continue to insert it.
        }
        // else: We have no DB set.
        // Now insert ourself into the DB.
        if (!(this instanceof DBContainer)) {
            db.add(this);
            // Just insert into the DB
        } else {
            // We do contain more Elements. Add them first.
            ((DBContainer) this).addChildrenToDB(db);
            // Now insert ourself into the DB
            db.add(this);
        }
        return this;
    }
    public void _setID(int nextFreeInt) {
        this.id = nextFreeInt;
    }
    public void _setDatabase(Database db) {
        this.database = db;
    }
    //TODO Doc
    public Database getDatabase(){
        if (database == null) {
            throw new NullPointerException("this.database is null");
        }
        return this.database;
    }
    //TODO Doc
    public boolean hasDatabase() {
        return (database != null);
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
     * This should be overridden by implementing public classes.
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
     * Method that can be used to check if this node is containing some other elements.
     * E.g. {@link DBList} or {@link DBDict}
     */
    public boolean isContainer() {
        switch (getType()) {
            case DICT:
            case LIST:
                return true;
            default:
                return false;
        }
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

    /**
     * Returns the {@link DBDict} for this node, <b>if and only if</b>
     * this node is a {@link DBDict}. For other
     * types returns {@code null}.
     *
     * @return {@link DBDict} if any, {@code null} else.
     */
    public DBDict dictValue() {
        return null;
    }
    /**
     * Returns the {@link DBList} for this node, <b>if and only if</b>
     * this node is a {@code DBList}. For other
     * types returns {@code null}.
     *
     * @return {@link DBList} if any, {@code null} else.
     */
    public DBList listValue() {
        return null;
    }
}

