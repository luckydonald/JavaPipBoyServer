package de.luckydonald.pipboyserver.PipBoyServer.types;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.EntryType;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.ParserException;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class DBFloat extends DBSimple<Float> {
    public static final EntryType TYPE = EntryType.FLOAT;
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

    /**
     * Parses and applies the {@code float} value from a given string.
     *
     * @param s The string to parse.
     * @return the updated {@link DBFloat} entry.
     * @throws ParserException if parsing as {@code float} fails.
     */
    @Override
    public DBFloat setValueFromString(String s) throws ParserException {
        try {
            Float f = new Float(s);
            this.setValue(f);
        } catch (NumberFormatException e) {
            throw new ParserException(e);
        }
        return this;
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
        return this.getValue();
    }
}
