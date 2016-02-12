package de.luckydonald.pipboyserver.PipBoyServer.types;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.EntryType;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.ParserException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;

public class DBBoolean extends DBSimple<Boolean> {
    public static final EntryType TYPE = EntryType.BOOLEAN;

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

    /**
     * Parses and applies the {@code boolean} value from a given string.
     *
     * @param s The string.
     * @return the updated {@link DBBoolean} entry).
     * @throws ParserException if parsing as {@code float} fails.
     */
    @Override
    public DBBoolean setValueFromString(String s) throws ParserException {
        Boolean b = null;
        switch (s.trim().toLowerCase()) {
            case "true":
            case "yes":
            case "y":
            case "ja":  // german yes
            case "j":   // german y
            case "1":
                b = true;
                break;
            case "false":
            case "nein": // german no
            case "no":
            case "n":
            case "0":
                b = false;
                break;
            default:
                throw new ParserException("Seems to be no valid boolean information.");
        }
        this.setValue(b);
        return this;
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
