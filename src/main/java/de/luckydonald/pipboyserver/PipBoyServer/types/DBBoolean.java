package de.luckydonald.pipboyserver.PipBoyServer.types;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.EntryType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
    public Boolean getValue() {
        return value;
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
