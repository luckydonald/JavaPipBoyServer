package de.luckydonald.pipboyserver.PipBoyServer.types;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.EntryType;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DBInteger32 extends DBSimple<Integer> {
    public static final EntryType TYPE = EntryType.INT32;

    public DBInteger32(Database db, int i) {
        super(db);
        this.value = i;
    }

    public DBInteger32(int i) {
        this(null, i);
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

    /**
     * Parses and applies the value from a given string.
     *
     * @param s The string.
     * @return the updated {@link DBEntry entry} (type {@link DBSimple}).
     */
    @Override
    public DBInteger32 setValueFromString(String s) {
        this.setValue(Integer.parseInt(s));
        return this;
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
